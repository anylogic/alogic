package com.alogic.cert;

import java.io.InputStream;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.cert.bc.CertificateStoreImpl;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;

/**
 * 工厂类
 * @author yyduan
 * 
 * @since 1.6.11.9
 * 
 * @version 1.6.11.10 [20180116 duanyy] <br>
 * - x509证书采用Bouncy Castle的类库来生成;
 */
public class CertificateStoreFactory extends Factory<CertificateStore>{
	/**
	 * a logger of slf4j
	 */
	protected static final Logger LOG = LoggerFactory.getLogger(CertificateStoreFactory.class);
	
	/**
	 * 缺省配置文件
	 */
	protected static final String DEFAULT = "java:///com/alogic/cert/default.xml#" + CertificateStore.class.getName();
	
	/**
	 * 缺省实例
	 */
	protected static CertificateStore INSTANCE = null;
	
	/**
	 * 获取一个缺省实例
	 * @return 缺省的实例
	 */
	public static CertificateStore getDefault(){
		if (INSTANCE == null){
			synchronized(CertificateStoreFactory.class){
				if (INSTANCE == null){
					INSTANCE = loadFrom(Settings.get());
				}
			}
		}
		
		return INSTANCE;
	}

	public static CertificateStore loadFrom(Properties p) {
		String master = PropertiesConstants.getString(p, "cert.master", DEFAULT);
		String secondary = PropertiesConstants.getString(p, "cert.secondary", DEFAULT);
		
		return loadFrom(master,secondary,p);
	}

	public static CertificateStore loadFrom(String master, String secondary,Properties p) {
		ResourceFactory rf = Settings.getResourceFactory();
		InputStream in = null;
		try {
			in = rf.load(master, secondary, null);
			Document doc = XmlTools.loadFromInputStream(in);
			if (doc != null){
				LOG.info("Load cert store from " + master);
				return loadFrom(doc.getDocumentElement(),p);
			}else{
				LOG.error("Can not load cert store from " + master);
				return null;
			}
		}catch (Exception ex){
			LOG.error("Can not load cert store from " + master);
			LOG.error(ExceptionUtils.getStackTrace(ex));
			return null;
		}finally{
			IOTools.close(in);
		}
	}

	public static CertificateStore loadFrom(Element root,Properties p) {
		CertificateStoreFactory f = new CertificateStoreFactory();
		return f.newInstance(root, p, "module", CertificateStoreImpl.class.getName());
	}


}
