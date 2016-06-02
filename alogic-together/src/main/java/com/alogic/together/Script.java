package com.alogic.together;

import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.together.plugins.Segment;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;

/**
 * Script
 * @author duanyy
 *
 */
public class Script extends Segment {
	protected static final String DEFAULT = "java:///com/alogic/together/together.default.xml#" + Script.class.getName();
	
	public Script(String tag, Logiclet p) {
		super(tag, p);
	}

	/**
	 * 根据XML创建服务脚本
	 * 
	 * @param root XML配置节点
	 * @param p 变量集
	 * @return 脚本实例
	 */
	public static Script create(Element root,Properties p){
		Script script = null;
		
		if (root != null){
			script = new Script("script",null);
			script.configure(root, p);
		}
		
		return script;
	}
	
	/**
	 * 根据XML配置文件的位置创建服务脚本
	 * @param src XML配置文件位置
	 * @param p 变量集
	 * @return 脚本实例
	 */
	public static Script create(String src,Properties p){
		Script script = null;
		
		if (StringUtils.isNotBlank(src)){
			ResourceFactory resourceFactory = Settings.getResourceFactory();
			InputStream in = null;
			try {
				String dft = PropertiesConstants.getString(p, "together.default", DEFAULT);
				in = resourceFactory.load(src,dft);
				Document doc = XmlTools.loadFromInputStream(in);
				
				if (doc != null){
					script = new Script("script",null);
					script.configure(doc.getDocumentElement(), p);
				}
			}catch (Exception ex){
				logger.error("The config file is not a valid file,url = " + src,ex);
			}finally{
				IOTools.close(in);
			}			
		}
		
		return script;		
	}
	
	public static Script create(String bootstrap,String path,Properties p){
		Script script = null;
		
		if (StringUtils.isNotBlank(bootstrap)){
			InputStream in = null;
			try {
				Class<?> clazz = Settings.getClassLoader().loadClass(bootstrap);
				in = clazz.getResourceAsStream(path);
				Document doc = XmlTools.loadFromInputStream(in);
				if (doc != null){
					script = new Script("script",null);
					script.configure(doc.getDocumentElement(), p);
				}
			}catch (Exception ex){
				logger.error("The config file is not a valid file,url = " + path + "#" + bootstrap,ex);
			}finally{
				IOTools.close(in);
			}			
		}
		
		return script;			
	}
}
