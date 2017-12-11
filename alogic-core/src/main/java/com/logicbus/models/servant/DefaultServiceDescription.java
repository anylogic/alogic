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
 * 
 * @version 1.6.10.12 [20171211 duanyy] <br>
 * - 增加访问控制组和权限项属性 <br>
 */
public class DefaultServiceDescription implements ServiceDescription{
	/**
	 * 服务ID
	 */
	private String serviceId;
	/**
	 * 服务名称
	 */
	private String name = "";
	/**
	 * 说明
	 */
	private String note = "";
	/**
	 * module
	 */
	private String module = "";
	/**
	 * 服务路径
	 */
	private String path = "";
	/**
	 * 服务参数
	 */
	private DefaultProperties properties;
	
	/**
	 * 服务参数规格
	 */
	private Map<String,PropertySpec> propertySpecs;
	
	/**
	 * 服务的可见性(public,login,limited)
	 */
	private String visible = "public";

	/**
	 * 是否纳入监管
	 */
	private boolean guard = false;
	
	/**
	 * 日志类型
	 */
	private LogType logType = LogType.none;
	
	/**
	 * 访问控制组id
	 */
	private String acGroupId = "default";
	
	/**
	 * 所需要的权限项
	 */
	private String privilege = "default";
	
	/**
	 * 服务所依赖的库文件
	 */
	private Vector<String> modulesMaster = null;
	
	/**
	 * constructor
	 * @param id 服务ID
	 */
	public DefaultServiceDescription(String id){
		this.serviceId = id;
		this.properties = new DefaultProperties("Default",Settings.get());
		this.propertySpecs = new HashMap<String,PropertySpec>();
	}	
	
	/**
	 * 获取日志类型
	 * @return 日志类型
	 */
	@Override
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
	 * 设置Guard属性
	 * @param guard 是否监管
	 */
	public void setGuard(boolean guard){
		this.guard = guard;
	}
	
	/**
	 * 获得服务ID
	 * @return 服务ID
	 */
	@Override
	public String getServiceID(){return this.serviceId;}
	
	/**
	 * 设置服务ID
	 * @param id 服务ID
	 */
	public void setServiceID(String id){this.serviceId = id;}
	
	/**
	 * 获得服务的可见性
	 * @return 可见性
	 */
	@Override
	public String getVisible(){return visible;}
	
	/**
	 * 设置服务的可见性
	 * @param _visible
	 */
	public void setVisible(String v){this.visible = v;}
	
	/**
	 * 获得服务名称
	 * @return name
	 */
	@Override
	public String getName(){return name;}
	
	/**
	 * 设置服务名称
	 * @param name name
	 */
	public void setName(String n){name = n;}
	
	/**
	 * 获取服务说明
	 * @return 服务说明
	 */
	@Override
	public String getNote(){return note;}
	
	/**
	 * 设置服务说明
	 * @param note 服务说明
	 */
	public void setNote(String n){note = n;}
	
	/**
	 * 获得服务路径
	 * @return 服务路径
	 */
	@Override
	public String getPath(){return this.path;}
	
	/**
	 * 设置服务路径
	 * @param path 
	 */
	public void setPath(String p){this.path = p;}
	
	/**
	 * 获得服务实现模块
	 * @return 实现模块的类名
	 */
	@Override
	public String getModule(){return module;}
	
	/**
	 * 设置服务实现代码
	 * @param module 
	 */
	public void setModule(String m){module = m;}
	

	@Override
	public String getAcGroup() {
		return this.acGroupId;
	}

	@Override
	public String getPrivilege() {
		return this.privilege;
	}
	
	/**
	 * 设置权限控制分组id
	 * @param group groupId
	 */
	public void setAcGroup(String group){
		this.acGroupId = group;
	}
	
	/**
	 * 设置访问本服务所需的权限项
	 * @param p　权限项
	 */
	public void setPrivilege(String p){
		this.privilege = p;
	}
	
	/**
	 * 获取参数变量集
	 * @return 参数变量集
	 */
	@Override
	public Properties getProperties(){return this.properties;}
		
	/**
	 * 获取服务依赖库文件列表
	 * @return 库文件列表
	 */
	@Override
	public String [] getModules(){return modulesMaster == null ? 
			null : modulesMaster.toArray(new String[0]);}

	/**
	 * 设置Properties
	 * @param props
	 * 
	 * @since 1.2.4.4
	 */
	public void setProperties(DefaultProperties props){
		this.properties.copyFrom(props);
	}
	
	/**
	 * 输出到打印流
	 * @param out
	 */
	public void List(PrintStream out)
	{
		out.println("Service ID:" + serviceId);
		out.println("Name:" + name);
		out.println("Module:" + module);
		out.println("Note:" + note);
		
		DefaultProperties props = (DefaultProperties)this.properties;
		out.println("Parameters:");
		props.list(out);
	}
	
	
	public void toXML(Element root){
		Document doc = root.getOwnerDocument();
		
		XmlTools.setString(root,"id",getServiceID());
		XmlTools.setString(root,"name",getName());
		XmlTools.setString(root,"note",getNote());
		XmlTools.setString(root,"module",getModule());
		XmlTools.setString(root,"visible",getVisible());
		XmlTools.setString(root,"path",getPath());
		XmlTools.setString(root, "log", logType.toString());
		XmlTools.setString(root,"guard",Boolean.toString(guard));
		XmlTools.setString(root,"acGroupId",getAcGroup());
		XmlTools.setString(root,"privilege",getPrivilege());

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
					
					PropertySpec spec = this.propertySpecs.get(__name);
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
		setServiceID(XmlTools.getString(root,"id",""));
		setName(XmlTools.getString(root, "name", getName()));		
		setModule(XmlTools.getString(root, "module", getModule()));
		setNote(XmlTools.getString(root,"note",getNote()));
		setVisible(XmlTools.getString(root, "visible", visible));
		setPath(XmlTools.getString(root, "path", getPath()));
		setLogType(parseLogType(XmlTools.getString(root, "log", logType.toString())));
		setGuard(XmlTools.getBoolean(root, "guard", guard()));
		setAcGroup(XmlTools.getString(root,"acGroupId",getAcGroup()));
		setPrivilege(XmlTools.getString(root,"privilege",getAcGroup()));
		
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
					this.propertySpecs.put(_id, spec);					
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
	}
	
	/**
	 * 从JSON对象中读入
	 * @param json 
	 */
	@SuppressWarnings("unchecked")
	public void fromJson(Map<String,Object> json){
		setServiceID(JsonTools.getString(json, "id", ""));
		setName(JsonTools.getString(json, "name", getName()));
		setModule(JsonTools.getString(json, "module", getModule()));
		setNote(JsonTools.getString(json, "note", getNote()));
		setVisible(JsonTools.getString(json, "visible", getVisible()));
		setPath(JsonTools.getString(json, "path", getPath()));
		
		setAcGroup(JsonTools.getString(json,"acGroupId",getAcGroup()));
		setPrivilege(JsonTools.getString(json,"privilege",getAcGroup()));
		
		setGuard(JsonTools.getBoolean(json, "guard", guard()));
		setLogType(parseLogType(JsonTools.getString(json, "log", getLogType().toString())));
		
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
						this.propertySpecs.put(id, spec);						
						
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
	}
	
	/**
	 * 写出到JSON对象
	 * @param json
	 */
	public void toJson(Map<String,Object> json){
		JsonTools.setString(json,"type","service");
		JsonTools.setString(json, "id", getServiceID());
		JsonTools.setString(json, "acGroupId",getAcGroup());
		JsonTools.setString(json, "privilege", getPrivilege());
		
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
			
					PropertySpec spec = this.propertySpecs.get(__name);
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
