package com.anysoft.util;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;


import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;







import com.anysoft.util.resource.ResourceFactory;


/**
 * 全局的配置变量集
 * <p>包含两个方面的功能：</p>
 * <li>维护一个全局性的变量集
 * <li>维护一个全局性的对象列表
 * 
 * @author duanyy
 * @since 1.3.1 [20140808 duanyy] <br>
 * - 增加静态方法:{@link Settings#getClassLoader()} <br>
 * - 增加静态方法:{@link Settings#getResourceFactory()} <br>
 * 
 * @since 1.3.2 [20140814 duanyy] <br>
 * - 优化get函数的共享锁机制 <br>
 * 
 * @version 1.5.2 [20141017 duanyy] <br>
 * - 实现Reportable接口 <br>
 * 
 * @version 1.6.4.17 [20151216 duanyy] <br>
 * - 根据sonar建议优化代码 <br>
 * 
 * @version 1.6.5.4 [20160515 duanyy] <br>
 * - XML配置文件的变量可以写入到SystemProperties之中 <br>
 */
public class Settings extends DefaultProperties implements XmlSerializer,Reportable{
	/**
	 * logger of log4j
	 */
	protected static Logger logger = LogManager.getLogger(Settings.class);
	/**
	 * JRE环境变量集，作为本变量集的父节点
	 */
	protected static Properties system = new SystemProperties();
	
	/**
	 * 全局对象列表
	 */
	protected Hashtable<String, Object> objects = new Hashtable<String, Object>();
	
	/**
	 * 构造函数
	 */
	protected Settings(){
		super("Settings",system);
	}
	/**
	 * 全局唯一实例
	 */
	protected static Settings instance = null;
	
	protected static Object lock = new Object();
	/**
	 * 获取全局唯一实例
	 * @return 对象实例
	 */
	public static Settings get(){
		if (null == instance){
			synchronized (lock){
				if (null == instance){
					instance = new Settings();
				}
			}
		}
		return instance;
	}
	
	/**
	 * 获取当前可用的ClassLoader
	 * @return ClassLoader
	 */
	public static ClassLoader getClassLoader(){
		ClassLoader cl = null;
		if (instance != null){
			cl = (ClassLoader) instance.get("classLoader");
		}
		return cl == null ? Thread.currentThread().getContextClassLoader() : cl;
	}
	
	/**
	 * 获取可用的ResourceFactory
	 * @return ResourceFactory
	 */
	public static ResourceFactory getResourceFactory(){
		ResourceFactory rf = null;
		if (instance != null){
			rf = (ResourceFactory)instance.get("ResourceFactory");
		}
		
		return rf == null ? new ResourceFactory() : rf;
	}
	
	/**
	 * 装入指定的xrc文件，并读入xrc文件中的变量信息
	 * @param _url xrc文件的url
	 * @param secondary xrc文件的备用url
	 * @param _rm ResourceFactory实例
	 * @see #loadFromDocument(Document)
	 */
	public void addSettings(String _url,String secondary,ResourceFactory _rm){
		ResourceFactory rm = _rm;
		if (null == _rm){
			rm = new ResourceFactory();
		}
		
		InputStream in = null;
		try {
			in = rm.load(_url,secondary, null);
			Document doc = XmlTools.loadFromInputStream(in);		
			loadFromDocument(doc);			
		}catch (Exception ex){
			logger.error("Error occurs when load xml file,source=" + _url, ex);
		}finally {
			IOTools.closeStream(in);
		}
	}
	
	/**
	 * 从一个DefaultProperties复制变量列表
	 * @param p DefaultProperties实例
	 */
	public void addSettings(DefaultProperties p){
		Enumeration<String> keys = p.keys();
		while (keys.hasMoreElements()){
			String name = (String)keys.nextElement();
			String value = p.GetValue(name,"",false,true);
			if (value != null && value.length() > 0)
				SetValue(name, value);
		}
	}
	
	/**
	 * 从XML文档中读入变量信息
	 * @param doc XML文档实例
	 * @see #toXML(Element)
	 */
	protected void loadFromDocument(Document doc){
		if (doc == null){
			return ;
		}		
		Element root = doc.getDocumentElement();
		fromXML(root);
	}
	
	/**
	 * 将变量集写出到XML文档
	 * @param doc XML文档实例
	 * @see #toXML(Element)
	 */
	protected void saveToDocument(Document doc){
		if (doc == null)return ;
		Element root = doc.getDocumentElement();
		toXML(root);
	}

	@Override
	public void report(Element xml) {
		if (xml != null){
			Document doc = xml.getOwnerDocument();
			
			//parameters
			{
				Element _parameters = doc.createElement("parameters");
				toXML(_parameters);
				xml.appendChild(_parameters);
			}
			//objects
			{
				Element _objects = doc.createElement("objects");

				Enumeration<String> _keys = objects.keys();
				while (_keys.hasMoreElements()){
					String key = _keys.nextElement();
					Object obj = objects.get(key);
					
					Element _object = doc.createElement("object");
					_object.setAttribute("id", key);
					_object.setAttribute("content", obj.toString());
					_objects.appendChild(_object);
				}
				xml.appendChild(_objects);
			}
		}
	}

	@Override
	public void report(Map<String, Object> json) {
		if (json != null){
			//parameters
			{
				List<Object> _parameters = new ArrayList<Object>();

				Enumeration<String> keys = keys();
				while (keys.hasMoreElements()){
					String key = keys.nextElement();
					String value = _GetValue(key);
					
					Map<String,Object> _parameter = new HashMap<String,Object>(2);
					_parameter.put("id", key);
					_parameter.put("value", value);
					
					_parameters.add(_parameter);
				}
				
				json.put("parameter",_parameters);
			}
			//objects
			{
				List<Object> _objects = new ArrayList<Object>();

				Enumeration<String> _keys = objects.keys();
				
				while (_keys.hasMoreElements()){
					String key = _keys.nextElement();
					Object obj = objects.get(key);
					
					Map<String,Object> _object = new HashMap<String,Object>(2);
					
					_object.put("id", key);
					_object.put("content", obj.toString());
					
					_objects.add(_object);
				}
				json.put("object",_objects);
			}
		}
	}
	
	/**
	 * 输出到XML节点
	 * @param root 输出信息的根节点
	 */
	@Override
	public void toXML(Element root) {
		//为了输出文件的美观，添加一个\n文件节点
		Document doc = root.getOwnerDocument();
		root.appendChild(doc.createTextNode("\n"));
		Enumeration<?> ids = keys();
		
		while (ids.hasMoreElements()){
			String id = (String)ids.nextElement();
			String value = _GetValue(id);
			if (value.length() <= 0 || id.length() <= 0){
				continue;
			}
			Element e = doc.createElement("parameter");
			e.setAttribute("id",id);
			e.setAttribute("value",value);
			root.appendChild(e);
			//为了输出文件的美观，添加一个\n文件节点
			root.appendChild(doc.createTextNode("\n"));
		}		
	}

	/**
	 * 从XML节点中读入
	 * @param root 读入信息的根节点
	 */
	@Override
	public void fromXML(Element root) {
		NodeList nodeList = root.getChildNodes();	
		for (int i = 0 ; i < nodeList.getLength() ; i ++){
			Node node = nodeList.item(i);
			if (node.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			if ("parameter".equals(node.getNodeName())){
				Element e = (Element)node;
				String id = XmlTools.getString(e,"id","");
				String value = XmlTools.getString(e,"value","");
				if (StringUtils.isEmpty(id) || StringUtils.isEmpty(value)){
					continue;
				}
				//支持final标示,如果final为true,则不覆盖原有的取值
				boolean isFinal = XmlTools.getBoolean(e, "final", false);
				if (isFinal){
					String oldValue = GetValue(id, "", false,false);
					if (StringUtils.isEmpty(oldValue)){
						SetValue(id,value);
						boolean system = XmlTools.getBoolean(e, "system", false);
						if (system){
							System.setProperty(id, value);
							logger.info(String.format("Set system property:%s=%s", id,value));
						}
					}
				}else{
					SetValue(id,value);
					boolean system = XmlTools.getBoolean(e, "system", false);
					if (system){
						System.setProperty(id, value);
						logger.info(String.format("Set system property:%s=%s", id,value));
					}					
				}
			}
		}		
	}
	
	/**
	 * 获取对象列表中指定id的对象
	 * @param id
	 * @return Object
	 */
	public Object get(String id){
		return objects.get(id);
	}
	
	/**
	 * 向对象列表注册已经创建好的对象
	 * @param id 对象id
	 * @param obj 对象实例
	 */
	public void registerObject(String id,Object obj){
		if (obj != null){
			objects.put(id, obj);
		}
	}

	/**
	 * 创建指定的对象并注册到对象列表
	 * @param id 对象id
	 * @param className 对象的类名
	 */
	public void registerObject(String id, String className) {
		try {
			registerObject(id,Class.forName(className).newInstance());
		} catch (Exception ex){
			logger.error("Can not register object:" + className,ex);
		}
	}		
	
	/**
	 * 从对象列表中注销指定ID的对象
	 * @param id
	 */
	public void unregisterObject(String id){
		objects.remove(id);
	}

}
