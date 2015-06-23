package com.logicbus.models.servant.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.logicbus.models.catalog.Path;
import com.logicbus.models.servant.ServantCatalogNode;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 目录节点实现
 * 
 * @author duanyy
 * 
 * @version 1.6.3.27 [20150623 duanyy] <br>
 * - 增加JSON序列化支持 <br>
 * 
 */
public class ServantCatalogNodeImpl implements ServantCatalogNode {

	/**
	 * Constructor
	 * @param _path 节点路径
	 */
	public ServantCatalogNodeImpl(Path _path,Object _data){
		path = _path;
		data = _data;
		services = new Hashtable<String, ServiceDescription>();
	}

	/**
	 * Constructor
	 * @param _name 节点名称
	 * @param _path 节点路径
	 */
	public ServantCatalogNodeImpl(String _name,String _path,Object _data){
		path = new Path(_path,_name);
		data = _data;
		services = new Hashtable<String, ServiceDescription>();
	}	

	/**
	 * 查找该目录节点所包含的指定ID的服务
	 * @param id 服务ID
	 */
	public ServiceDescription findService(String id) {
		return services.get(id);
	}

	/**
	 * 获取该节点的服务列表
	 */
	public ServiceDescription[] getServices() {
		return services.values().toArray(new ServiceDescription[0]);
	}

	
	public String getModule() {
		return "Node";
	}

	
	public String getName() {
		return path.getId();
	}
	
	
	public Path getPath() {
		return path;
	}
	
	
	public Object getData(){
		return data;
	}
	
	
	public void toXML(Element root) {
		toXML(root,true);
	}

	
	public void fromXML(Element e) {

	}
	
	public void toJson(Map<String, Object> json) {
		toJson(json,true);
	}

	public void fromJson(Map<String, Object> json) {

	}	
	
	public void toJson(Map<String,Object> json,boolean outputServices){
		if (json != null){
			json.put("name", getName());
			json.put("path", getPath().getPath());
			
			if (outputServices){
				ServiceDescription[] sds = getServices();
				List<Object> _services = new ArrayList<Object>();
				
				for (int i = 0 ; i < sds.length ; i ++){
					ServiceDescription sd = (ServiceDescription) sds[i];
					Map<String,Object> map = new HashMap<String,Object>();
					sd.toJson(map);
					_services.add(map);
				}
				
				json.put("service", _services);
			}
		}
	}
	
	/**
	 * 输出到XML节点
	 * @param root XML节点
	 * @param outputServices 是否输出目录下服务列表
	 */
	public void toXML(Element root,boolean outputServices) {
		Document doc = root.getOwnerDocument();
		//id
		root.setAttribute("name",getName());
		//path
		root.setAttribute("path",getPath().getPath());
		
		if (outputServices){
			ServiceDescription[] sds = getServices();
			for (int i = 0 ; i < sds.length ; i ++){
				ServiceDescription sd = (ServiceDescription) sds[i];
				Element service = doc.createElement("service");	
				sd.toXML(service);
				root.appendChild(service);
			}
		}
	}
	
	/**
	 * 向节点中增加服务
	 * @param id 服务ID
	 * @param _sd 服务描述
	 */
	public void addService(String id,ServiceDescription _sd){
		services.put(id, _sd);
	}
	
	/**
	 * 删除节点中指定的服务
	 * @param id 服务ID
	 */
	public void removeService(String id){
		services.remove(id);
	}
	
	/**
	 * 服务列表
	 */
	protected Hashtable<String, ServiceDescription> services;
	
	/**
	 * 路径
	 */
	protected Path path;
	
	/**
	 * 附加数据
	 */
	protected Object data;

}
