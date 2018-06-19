package com.anysoft.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 * 
 * @version 1.6.8.7 [20170412 duanyy] <br>
 * - DefaultProperties容器由Hashtable更改为HashMap <br>
 * 
 * @version 1.6.11.37 [20180619 duanyy] <br>
 * - 增加getToolkit方法,可以基于Settings来创建工具集 <br>
 */
public class Settings extends DefaultProperties implements XmlSerializer,Reportable{

	/**
	 * JRE环境变量集，作为本变量集的父节点
	 */
	protected static Properties system = new SystemProperties();
	
	/**
	 * 全局对象列表
	 */
	protected Map<String, Object> objects = new ConcurrentHashMap<String, Object>();
	
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
	 * 将变量集写出到XML文档
	 * @param doc XML文档实例
	 * @see #toXML(Element)
	 */
	protected void saveToDocument(Document doc){
		if (doc == null)return ;
		Element root = doc.getDocumentElement();
		toXML(root);
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

				Iterator<String> _keys = objects.keySet().iterator();
				while (_keys.hasNext()){
					String key = _keys.next();
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

				Iterator<String> keys = keys().iterator();
				while (keys.hasNext()){
					String key = keys.next();
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

				Iterator<String> _keys = objects.keySet().iterator();
				
				while (_keys.hasNext()){
					String key = _keys.next();
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
		Iterator<String> ids = keys().iterator();
		
		while (ids.hasNext()){
			String id = (String)ids.next();
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
		loadFrom(root);
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
	 * 根据当前环境变量生成指定的工具集
	 * @param clazz 工具的类
	 * @return 工具的实例
	 */
	@SuppressWarnings("unchecked")
	public <T> T getToolkit(Class<T> clazz){
		String id = clazz.getName();
		T found = (T) get(id);
		if (found == null){
			synchronized (clazz){
				found = (T)get(id);
				if (found == null){
					Factory<T> f = new Factory<T>();
					found = f.newInstance(id,this);
					registerObject(id, found);
				}
			}
		}
		
		return found;
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
