package com.logicbus.models.servant;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.util.DefaultProperties;
import com.anysoft.util.JsonSerializer;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlSerializer;
import com.anysoft.util.XmlTools;

/**
 * ServiceDescription的缺省实现
 * 
 * @author duanyy
 * 
 * @since 1.2.5.4
 * 
 * @version 1.2.8.2 [20141015 duanyy]
 * - 实现Reportable <br>
 * 
 * @version 1.6.7.20 <br>
 * - 改造ServantManager模型,增加服务配置监控机制 <br>
 * 
 * @version 1.6.7.25 <br>
 * - 增加配置参数规格功能 <br>
 * 
 * @version 1.6.8.7 [20170412 duanyy] <br>
 * - DefaultProperties容器由Hashtable更改为HashMap <br>
 */
public class DefaultServiceDescription implements ServiceDescription{
	/**
	 * 服务ID
	 */
	private String m_service_id;
	/**
	 * 服务名称
	 */
	private String m_name = "";
	/**
	 * 说明
	 */
	private String m_note = "";
	/**
	 * module
	 */
	private String m_module = "";
	/**
	 * 服务路径
	 */
	private String m_path = "";
	/**
	 * 服务参数
	 */
	private DefaultProperties m_properties;
	
	/**
	 * 服务参数规格
	 */
	private Map<String,PropertySpec> m_property_specs;
	
	/**
	 * 服务的可见性(public,login,limited)
	 */
	private String visible = "public";

	private boolean guard = false;
	
	/**
	 * 日志类型
	 */
	private LogType logType = LogType.none;
	
	/**
	 * constructor
	 * @param id 服务ID
	 */
	public DefaultServiceDescription(String id){
		m_service_id = id;
		m_properties = new DefaultProperties("Default",Settings.get());
		m_property_specs = new HashMap<String,PropertySpec>();
	}	
	
	/**
	 * 获取日志类型
	 * @return 日志类型
	 */
	public LogType getLogType(){return logType;}
	
	/**
	 * 设置日志类型
	 * @param type
	 * 
	 * @since 1.2.4.4
	 */
	public void setLogType(LogType type){logType = type;}
	
	/**
	 * 设置日志类型
	 * @param type
	 * 
	 * @since 1.2.4.4
	 */
	public void setLogType(String type){logType = parseLogType(type);}
	
	@Override
	public boolean guard() {
		return guard;
	}	
	
	/**
	 * 获得服务ID
	 * @return 服务ID
	 */
	public String getServiceID(){return m_service_id;}
	
	/**
	 * 设置服务ID
	 * @param id 服务ID
	 */
	public void setServiceID(String id){m_service_id = id;}
	
	/**
	 * 获得服务的可见性
	 * @return 可见性
	 */
	public String getVisible(){return visible;}
	
	/**
	 * 设置服务的可见性
	 * @param _visible
	 */
	public void setVisible(String _visible){visible = _visible;}
	
	/**
	 * 获得服务名称
	 * @return name
	 */
	public String getName(){return m_name;}
	
	/**
	 * 设置服务名称
	 * @param name name
	 */
	public void setName(String name){m_name = name;}
	
	/**
	 * 获取服务说明
	 * @return 服务说明
	 */
	public String getNote(){return m_note;}
	
	/**
	 * 设置服务说明
	 * @param note 服务说明
	 */
	public void setNote(String note){m_note = note;}
	
	/**
	 * 获得服务路径
	 * @return 服务路径
	 */
	public String getPath(){return m_path;}
	
	/**
	 * 设置服务路径
	 * @param path 
	 */
	public void setPath(String path){m_path = path;}
	
	/**
	 * 获得服务实现模块
	 * @return 实现模块的类名
	 */
	public String getModule(){return m_module;}
	
	/**
	 * 设置服务实现代码
	 * @param module 
	 */
	public void setModule(String module){m_module = module;}
	
	/**
	 * 获取参数变量集
	 * @return 参数变量集
	 */
	public Properties getProperties(){return m_properties;}
	
	/**
	 * 服务所依赖的库文件
	 */
	protected Vector<String> modulesMaster = null;
	
	/**
	 * 获取服务依赖库文件列表
	 * @return 库文件列表
	 */
	public String [] getModules(){return modulesMaster == null ? 
			null : modulesMaster.toArray(new String[0]);}
	
	/**
	 * 服务调用参数列表
	 * 
	 * @since 1.0.3
	 */
	protected HashMap<String,Argument> argumentList = null;
	
	/**
	 * 获取服务调用参数列表
	 * @return 参数列表
	 * @since 1.0.3
	 */
	public Argument [] getArgumentList(){
		if (argumentList == null)
			return null;
		
		return argumentList.values().toArray(new Argument[0]);
	}
	
	/**
	 * 获取指定ID的参数
	 * @param id 参数Id
	 * @return 指定ID的参数
	 */
	public Argument getArgument(String id){
		if (argumentList == null)
			return null;		
		return argumentList.get(id);
	}
	
	/**
	 * 设置ArgumentList
	 * @param list
	 * 
	 * @since 1.2.4.4
	 * 
	 */
	public void setArgumentList(Argument [] list){
		argumentList.clear();
		for (Argument argu:list){
			DefaultArgument newArgu = new DefaultArgument();
			newArgu.copyFrom((DefaultArgument)argu);
			argumentList.put(argu.getId(),argu);
		}
	}
	
	/**
	 * 设置Properties
	 * @param props
	 * 
	 * @since 1.2.4.4
	 */
	public void setProperties(DefaultProperties props){
		m_properties.copyFrom(props);
	}
	
	/**
	 * 输出到打印流
	 * @param out
	 */
	public void List(PrintStream out)
	{
		out.println("Service ID:" + m_service_id);
		out.println("Name:" + m_name);
		out.println("Module:" + m_module);
		out.println("Note:" + m_note);
		
		DefaultProperties props = (DefaultProperties)m_properties;
		out.println("Parameters:");
		props.list(out);
	}
	
	
	public void toXML(Element root){
		Document doc = root.getOwnerDocument();
			
		//id
		root.setAttribute("id",getServiceID());
		//name
		root.setAttribute("name", getName());
		//note
		root.setAttribute("note", getNote());
		//module
		root.setAttribute("module",getModule());
		//visible
		root.setAttribute("visible",getVisible());
		//path
		root.setAttribute("path",getPath());
		//Properties
		root.setAttribute("log", logType.toString());
		
		root.setAttribute("guard", Boolean.toString(guard));
		
		{
			DefaultProperties properties = (DefaultProperties) getProperties();
			Iterator<String> __keys = properties.keys().iterator();
			if (__keys.hasNext()){
				Element propertiesElem = doc.createElement("properties");
				while (__keys.hasNext()){
					String __name = (String)__keys.next();
					String __value = properties.GetValue(__name,"",false,true);
					
					Element e = doc.createElement("parameter");
					e.setAttribute("id",__name);
					e.setAttribute("value",__value);
					
					PropertySpec spec = m_property_specs.get(__name);
					if (spec != null){
						spec.toXML(e);
					}
					
					propertiesElem.appendChild(e);
				}
				root.appendChild(propertiesElem);
			}
		}
		if (modulesMaster != null && modulesMaster.size() > 0)
		{
			Element eModules = doc.createElement("modules");
			
			for (String module:modulesMaster){
				Element eModule = doc.createElement("module");
				eModule.setAttribute("url", module);
				eModules.appendChild(eModule);
			}
			
			root.appendChild(eModules);
		}

		if (argumentList != null && argumentList.size() > 0){
			Element eArguments = doc.createElement("arguments");
			Argument [] _argumentList = getArgumentList();
			for (Argument argu:_argumentList){
				Element eArgu = doc.createElement("argu");
				
				argu.toXML(eArgu);
				
				eArguments.appendChild(eArgu);
			}
			
			root.appendChild(eArguments);
		}
		
	}
	
	private LogType parseLogType(String type){
		LogType ret = LogType.none;
		
		if (type != null){
			if (type.equals("none")){
				ret = LogType.none;
			}else{
				if (type.equals("brief")){
					ret = LogType.brief;
				}else{
					if (type.equals("detail")){
						ret = LogType.detail;
					}else{
						ret = LogType.brief;
					}
				}
			}
		}
		
		return ret;
	}
	
	
	public void fromXML(Element root){
		setServiceID(root.getAttribute("id"));
		setName(root.getAttribute("name"));
		setModule(root.getAttribute("module"));
		setNote(root.getAttribute("note"));
		setVisible(root.getAttribute("visible"));
		setPath(root.getAttribute("path"));
		logType = parseLogType(root.getAttribute("log"));
		guard = Boolean.parseBoolean(root.getAttribute("guard"));
				
		NodeList eProperties = XmlTools.getNodeListByPath(root, "properties/parameter");
		if (eProperties != null){
			for (int i = 0 ; i < eProperties.getLength() ; i ++){
				Node n = eProperties.item(i);
				if (n.getNodeType() != Node.ELEMENT_NODE){
					continue;
				}
				Element e = (Element)n;
				String _id = e.getAttribute("id");
				String _value = e.getAttribute("value");
				if (StringUtils.isNotEmpty(_id) && StringUtils.isNotEmpty(_value)){
					PropertySpec spec = new PropertySpec();
					spec.fromXML(e);
					m_property_specs.put(_id, spec);
					
					getProperties().SetValue(_id,_value);
				}
			}
		}
		
		NodeList eModules = XmlTools.getNodeListByPath(root, "modules/module");
		if (eModules != null){
			if (modulesMaster == null){
				modulesMaster = new Vector<String>();
			}else{
				modulesMaster.clear();
			}
			
			for (int i = 0 ; i < eModules.getLength() ; i ++){
				Node n = eModules.item(i);
				if (n.getNodeType() != Node.ELEMENT_NODE){
					continue;
				}
				Element e = (Element)n;
				String url = e.getAttribute("url");
				if (url != null && url.length() > 0){
					modulesMaster.add(url);
				}
			}
		}
		
		NodeList eArguments = XmlTools.getNodeListByPath(root, "arguments/argu");
		if (eArguments != null){
			if (argumentList == null){
				argumentList = new HashMap<String,Argument>();
			}else{
				argumentList.clear();
			}
			
			for (int i = 0 ; i < eArguments.getLength() ; i ++){
				Node n = eArguments.item(i);
				if (n.getNodeType() != Node.ELEMENT_NODE){
					continue;
				}
				Element e = (Element) n;
				Argument argu = new DefaultArgument();
				argu.fromXML(e);
				if (argu.getId().length() <= 0){
					continue;
				}
				argumentList.put(argu.getId(),argu);
			}
		}
	}
	
	/**
	 * 从JSON对象中读入
	 * @param json 
	 */
	@SuppressWarnings("unchecked")
	public void fromJson(Map<String,Object> json){
		setServiceID((String)json.get("id"));
		setName((String)json.get("name"));
		setModule((String)json.get("module"));
		setNote((String)json.get("note"));
		setVisible((String)json.get("visible"));
		setPath((String)json.get("path"));
		guard = Boolean.parseBoolean((String)json.get("guard"));
		logType = parseLogType((String)json.get("log"));
		
		Object propertiesObj = json.get("properties");
		if (propertiesObj != null && propertiesObj instanceof List){
			List<Object> propsList = (List<Object>)propertiesObj;
			for (Object para:propsList){
				if (!( para instanceof Map)){
					continue;
				}
				try {
					Map<String,Object> paraMap = (Map<String,Object>)para;
					String id = (String)paraMap.get("id");
					String value = (String)paraMap.get("value");
					
					if (StringUtils.isNotEmpty(id) && StringUtils.isNotEmpty(value)){

						PropertySpec spec = new PropertySpec();
						spec.fromJson(paraMap);
						m_property_specs.put(id, spec);						
						
						getProperties().SetValue(id,value);
					}
				}catch (Exception ex){
					//如果类型转换错误,不管了
				}
			}
		}
		
		Object modulesObj = json.get("modules");
		if (modulesObj != null && modulesObj instanceof List){
			List<Object> modulesList = (List<Object>) modulesObj;
			for (Object module:modulesList){
				if (! (module instanceof String)){
					continue;
				}
				modulesMaster.add((String)module);
			}
		}
		
		Object argumentsObj = json.get("arguments");
		if (argumentsObj != null && argumentsObj instanceof List){
			List<Object> arguList = (List<Object>)argumentsObj;
			for (Object argumentObj : arguList){
				if (! (argumentObj instanceof Map)){
					continue;
				}
				Map<String,Object> argumentMap = (Map<String,Object>) argumentObj;
				Argument argu = new DefaultArgument();
				argu.fromJson(argumentMap);
				if (argu.getId().length() <= 0){
					continue;
				}
				argumentList.put(argu.getId(),argu);
			}
		}
	}
	
	/**
	 * 写出到JSON对象
	 * @param json
	 */
	public void toJson(Map<String,Object> json){
		json.put("type", "service");
		json.put("id", getServiceID());
		json.put("name",getName());
		json.put("module",getModule());
		json.put("note", getNote());
		json.put("visible", getVisible());
		json.put("path",getPath());
		json.put("log", logType.toString());
		json.put("guard",Boolean.toString(guard));
		{
			DefaultProperties properties = (DefaultProperties) getProperties();
			Iterator<String> __keys = properties.keys().iterator();
			if (__keys.hasNext()){
				
				List<Object> propertiesList = new Vector<Object>();				
				while (__keys.hasNext()){
					String __name = (String)__keys.next();
					String __value = properties.GetValue(__name,"",false,true);					
					Map<String,Object> pair = new HashMap<String,Object>();
					pair.put("id", __name);
					pair.put("value", __value);		
			
					PropertySpec spec = m_property_specs.get(__name);
					if (spec != null){
						spec.toJson(pair);
					}					
					
					propertiesList.add(pair);
				}
				
				json.put("properties", propertiesList);
			}
		}
		if (modulesMaster != null && modulesMaster.size() > 0)
		{
			List<Object> modulesList = new Vector<Object>();
			for (String module:modulesMaster){
				modulesList.add(module);
			}
			
			json.put("modules", modulesList);
		}		
		if (argumentList != null && argumentList.size() > 0){
			List<Object> arguList = new Vector<Object>();
			Argument [] _argumentList = getArgumentList();
			for (Argument argument:_argumentList){
				Map<String,Object> argumentMap = new HashMap<String,Object>();
				
				argument.toJson(argumentMap);
				
				arguList.add(argumentMap);
			}
			
			json.put("arguments", arguList);
		}
	}

	
	public void report(Element xml) {
		toXML(xml);
	}

	
	public void report(Map<String, Object> json) {
		toJson(json);
	}

	/**
	 * 参数规格
	 * @author yyduan
	 *
	 */
	public static class PropertySpec implements JsonSerializer,XmlSerializer{
		protected String editor = "Default";
		protected String name = "";
		protected String note = "";
		protected String template = "";
		@Override
		public void toJson(Map<String, Object> json) {
			if (json != null){
				JsonTools.setString(json,"editor",editor);
				JsonTools.setString(json,"name",name);
				JsonTools.setString(json,"note",note);
				JsonTools.setString(json,"template",template);
			}
		}
		@Override
		public void fromJson(Map<String, Object> json) {
			if (json != null){
				editor = JsonTools.getString(json,"editor","Default");
				name = JsonTools.getString(json,"name","");
				note = JsonTools.getString(json,"note","");
				template = JsonTools.getString(json,"template","");
			}
		}
		@Override
		public void toXML(Element e) {
			if (e != null){
				XmlTools.setString(e,"editor",editor);
				XmlTools.setString(e,"name",name);
				XmlTools.setString(e,"note",note);
				XmlTools.setString(e,"template",template);
			}
		}
		@Override
		public void fromXML(Element e) {
			if (e != null){
				editor = XmlTools.getString(e,"editor","Default");
				name = XmlTools.getString(e,"name","");
				note = XmlTools.getString(e,"note","");
				template = XmlTools.getString(e,"template","");
			}
		}
	}
}
