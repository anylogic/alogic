package com.alogic.remote;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.remote.httpclient.HttpClient;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;


/**
 * Client工厂类
 * 
 * @author yyduan
 * 
 * @since 1.6.8.15
 */
public class ClientFactory {
	
	/**
	 * logger of log4j
	 */
	protected static final Logger LOG = LoggerFactory.getLogger(ClientFactory.class);
	
	/**
	 * 唯一实例，作为缺省的实例
	 */
	protected static Client instance = null;
	
	/**
	 * 缺省配置文件地址
	 */
	protected static final String DEFAULT = "java:///com/alogic/remote/client.default.xml#" + Client.class.getName();
	
	/**
	 * 不能实例化
	 */
	protected ClientFactory(){
		
	}
	
	/**
	 * 获取缺省的Client实现
	 * 
	 * @return client实现
	 */
	public static Client getDefault(){
		if (instance == null){
			synchronized (ClientFactory.class){
				if (instance == null){
					Settings p = Settings.get();
					String master = PropertiesConstants.getString(p, "client.master", DEFAULT);
					String secondary = PropertiesConstants.getString(p, "client.secondary", DEFAULT);
					instance = loadFrom(master,secondary,Settings.getResourceFactory());
				}
			}
		}
		
		return instance;
	}

	public static Client loadFrom(String master,String secondary,ResourceFactory resourceFactory) {
		ResourceFactory rf = resourceFactory;
		if (rf == null){
			rf = Settings.getResourceFactory();
		}
	
		InputStream in = null;
		try {
			in = rf.load(master, secondary, null);
			Document doc = XmlTools.loadFromInputStream(in);
			if (doc != null){
				return loadFrom(doc,Settings.get());
			}
		}catch (Exception ex){
			LOG.error("The config file is not a valid file,url = "
					+ master);			
		}finally{
			IOTools.close(in);
		}
		return null;
	}

	public static Client loadFrom(Document doc,Properties p) {
		return loadFrom(doc.getDocumentElement(),"module",p);
	}
	
	public static Client loadFrom(Element doc,Properties p) {
		return loadFrom(doc,"module",p);
	}	

	public static Client loadFrom(Element root,String moduleAttr,Properties p) {
		Client client = null;		
		try {
			Factory<Client> f = new Factory<Client>();
			client = f.newInstance(root, p, moduleAttr, HttpClient.class.getName());
		}catch (Exception ex){
			LOG.error(String.format("Can not remote client with %s", XmlTools.node2String(root)));
			client = new HttpClient();
			client.configure(root, p);
			LOG.info(String.format("Using default,Current remote client is %s",client.getClass().getName()));
		}
		return client;
	}
}
