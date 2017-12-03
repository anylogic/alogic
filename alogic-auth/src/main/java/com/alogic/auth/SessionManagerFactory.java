package com.alogic.auth;

import java.io.InputStream;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
 * @since 1.6.10.10
 */
public class SessionManagerFactory extends Factory<SessionManager>{
	/**
	 * a logger of slf4j
	 */
	protected final static Logger LOG = LoggerFactory.getLogger(SessionManagerFactory.class);
	
	/**
	 * 静态缺省实例
	 */
	private static SessionManager INSTANCE = null;
	
	/**
	 * 缺省的配置文件路径
	 */
	public static final String DEFAULT = "java:///com/alogic/auth/session.default.xml";
	
	/**
	 * 获取缺省实现
	 * @return PrincipalManager实例
	 */
	public static SessionManager getDefault(){
		if (INSTANCE != null){
			synchronized (PrincipalManager.class){
				if (INSTANCE != null){
					Settings p = Settings.get();
					INSTANCE = loadFromPath(
							PropertiesConstants.getString(p,"auth.master",DEFAULT),
							PropertiesConstants.getString(p,"auth.secondary",DEFAULT),
							p);
				}
			}
		}
		return INSTANCE;
	}
	
	/**
	 * 从资源文件中创建PrincipalManager
	 * @param path 文件路径
	 * @param secondary 文件备用路径
	 * @param p 变量集
	 * @return PrincipalManager实例
	 */
	public static SessionManager loadFromPath(String path,String secondary,Properties p){
		ResourceFactory rf = Settings.getResourceFactory();
		SessionManager pm = null;
		InputStream in = null;		
		try {
			in = rf.load(path, secondary, null);
			Document doc = XmlTools.loadFromInputStream(in);
			if (doc != null){
				pm = loadFromElement(doc.getDocumentElement(),p);
			}
		}catch (Exception ex){
			LOG.error(ExceptionUtils.getStackTrace(ex));
		}finally{
			IOTools.close(in);
		}
		return pm;
	}
	
	/**
	 * 从XML配置节点中创建PrincipalManager
	 * 
	 * @param root 根节点
	 * @param p 变量集
	 * @return PrincipalManager实例
	 */
	public static SessionManager loadFromElement(Element root,Properties p){
		SessionManagerFactory f = new SessionManagerFactory();
		return f.newInstance(root, p, "module", DefaultPrincipalManager.class.getName());
	}
}
