package com.alogic.ac;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alogic.load.Loadable;
import com.anysoft.util.Configurable;
import com.anysoft.util.JsonSerializer;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.StringMatcher;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlSerializer;
import com.anysoft.util.XmlTools;

/**
 * 访问控制模型
 * 
 * @author yyduan
 * @since 1.6.10.6
 */
public interface AccessControlModel extends Loadable,XmlSerializer,JsonSerializer,XMLConfigurable,Configurable{
	
	/**
	 * 获取最大并发数
	 * @return 最大并发数
	 */
	public int getMaxThread();
	
	/**
	 * 获取一分钟之内最大流量
	 * @return 一分钟之内最大流量
	 */
	public int getMaxTimesPerMin();
	
	/**
	 * 增加访问控制项
	 * @param ip 指定的ip
	 * @param service 指定的服务
	 * @param maxThread 并发数
	 * @param maxTimesPerMin 一分钟流量
	 * @param priority 优先级
	 */
	public void addACI(String ip,String service,int maxThread,int maxTimesPerMin,int priority);
	
	/**
	 * 获取本次调用的优先级
	 * @param ip 调用IP
	 * @param service 调用的服务
	 * @param stat 访问统计信息
	 * @return 优先级
	 */
	public int getPriority(String ip,String service,AccessStat stat);
	
	/**
	 * 缺省实现
	 * @author yyduan
	 *
	 */
	public static class Default implements AccessControlModel{
		/**
		 * id
		 */
		protected String id = "";
		
		/**
		 * 并发个数
		 */
		protected int maxThread = 1;
		
		/**
		 * 一分钟之内的最大访问次数
		 */
		protected int maxTimesPerMin = 10;
		
		/**
		 * 优先级
		 */
		protected int priority = -1;
		
		/**
		 * 访问控制列表
		 */
		protected List<AcctControlItem> acl = new ArrayList<AcctControlItem>();	
		
		/**
		 * 数据加载时间戳
		 */
		protected long timestamp = System.currentTimeMillis();
		
		/**
		 * 数据的生存周期:30分钟
		 */
		public static final long TTL = 30 * 60 * 1000L;
		
		public Default(){
			
		}
		
		public Default(String _id) {
			id = _id;
		}
	
		public Default(String _id,Map<String,Object> data){
			id = _id;
			fromJson(data);
		}
		
		public Default(String _id,Element e){
			id = _id;
			fromXML(e);
		}
		
		@Override
		public String getId() {
			return id;
		}
		
		@Override
		public long getTimestamp(){
			return timestamp;
		}
	
		public int getMaxThread(){
			return maxThread;
		}
		
		public int getMaxTimesPerMin(){
			return maxTimesPerMin;
		}
	
		@Override
		public boolean isExpired() {
			return System.currentTimeMillis() - timestamp > TTL;
		}
	
		@Override
		public void expire(){
			timestamp = timestamp - TTL;
		}
		
		@Override
		public void addACI(String ip,String service,int maxThread,int maxTimesPerMin,int priority){
			AcctControlItem aci = new AcctControlItem(ip,service);
			aci.maxThread = maxThread;
			aci.maxTimesPerMin = maxTimesPerMin;
			aci.priority = priority;
			acl.add(aci);		
		}
		
		@Override
		public void report(Element xml) {
			toXML(xml);
		}
	
		@Override
		public void report(Map<String, Object> json) {
			toJson(json);
		}
	
		@Override
		public void configure(Properties props) {
			id = PropertiesConstants.getString(props,"id","");
			maxThread = PropertiesConstants.getInt(props, "maxThread", maxThread);
			maxTimesPerMin = PropertiesConstants.getInt(props, "maxTimesPerMin", maxTimesPerMin);
			priority = PropertiesConstants.getInt(props, "priority", priority);
		}
	
		@Override
		public void configure(Element root, Properties p) {
			XmlElementProperties props = new XmlElementProperties(root,p);
			
			configure(props);
	
			NodeList aclsNodeList = XmlTools.getNodeListByPath(root, "acls/acl");
			if (aclsNodeList.getLength() > 0){
				for (int i = 0 ,length = aclsNodeList.getLength(); i < length ; i ++){
					Node n = aclsNodeList.item(i);
					if (n.getNodeType() != Node.ELEMENT_NODE){
						continue;
					}
					
					Element e = (Element)n;
					XmlElementProperties eProps = new XmlElementProperties(e,props);
					
					AcctControlItem aci = new AcctControlItem(
							PropertiesConstants.getString(eProps, "ip", "*"),
							PropertiesConstants.getString(eProps, "service", "*"));
					
					aci.maxThread = PropertiesConstants.getInt(eProps, "maxThread", maxThread);
					aci.maxTimesPerMin = PropertiesConstants.getInt(eProps, "maxTimesPerMin", maxTimesPerMin);
					aci.priority = PropertiesConstants.getInt(eProps, "priority", priority);
					
					acl.add(aci);
				}
			}		
		}
		
		@Override
		public void toXML(Element root) {
			root.setAttribute("id", id);
			root.setAttribute("maxThread", String.valueOf(maxThread));
			root.setAttribute("maxTimesPerMin", String.valueOf(maxTimesPerMin));
			root.setAttribute("priority", String.valueOf(priority));
			
			if (! acl.isEmpty()){
				Document doc = root.getOwnerDocument();
				Element aclsElement = doc.createElement("acls");
				
				for (AcctControlItem aci:acl){
					Element aclElement = doc.createElement("acl");
					
					aclElement.setAttribute("ip", aci.ip);
					aclElement.setAttribute("service", aci.service);
					aclElement.setAttribute("maxThread", String.valueOf(aci.maxThread));
					aclElement.setAttribute("maxTimesPerMin", String.valueOf(aci.maxTimesPerMin));
					aclElement.setAttribute("priority", String.valueOf(aci.priority));
					
					aclsElement.appendChild(aclElement);
				}
				root.appendChild(aclsElement);
			}
		}
	
		@Override
		public void toJson(Map<String,Object> json) {
			json.put("id", id);
			json.put("maxThread",String.valueOf(maxThread));
			json.put("maxTimesPerMin", String.valueOf(maxTimesPerMin));
			json.put("priority", String.valueOf(priority));
			
			if (! acl.isEmpty()){
				ArrayList<Object> list = new ArrayList<Object>();
				for (AcctControlItem aci:acl){
					Map<String,Object> map = new HashMap<String,Object>();
					
					map.put("ip", aci.ip);
					map.put("service", aci.service);
					map.put("maxThread", String.valueOf(aci.maxThread));
					map.put("maxTimesPerMin", String.valueOf(aci.maxTimesPerMin));
					map.put("priority", String.valueOf(aci.priority));
					
					list.add(map);
				}
				
				json.put("acls", list);
			}
		}
	
		@Override
		public void fromXML(Element root) {
			XmlElementProperties props = new XmlElementProperties(root,null);
			
			id = PropertiesConstants.getString(props,"id","");
			maxThread = PropertiesConstants.getInt(props, "maxThread", maxThread);
			maxTimesPerMin = PropertiesConstants.getInt(props, "maxTimesPerMin", maxTimesPerMin);
			priority = PropertiesConstants.getInt(props, "priority", priority);
	
			NodeList aclsNodeList = XmlTools.getNodeListByPath(root, "acls/acl");
			
			if (aclsNodeList.getLength() > 0){
				for (int i = 0 ,length = aclsNodeList.getLength(); i < length ; i ++){
					Node n = aclsNodeList.item(i);
					if (n.getNodeType() != Node.ELEMENT_NODE){
						continue;
					}
					
					Element e = (Element)n;
					XmlElementProperties eProps = new XmlElementProperties(e,props);
					
					AcctControlItem aci = new AcctControlItem(
							PropertiesConstants.getString(eProps, "ip", "*"),
							PropertiesConstants.getString(eProps, "service", "*"));
					
					aci.maxThread = PropertiesConstants.getInt(eProps, "maxThread", maxThread);
					aci.maxTimesPerMin = PropertiesConstants.getInt(eProps, "maxTimesPerMin", maxTimesPerMin);
					aci.priority = PropertiesConstants.getInt(eProps, "priority", priority);
					
					acl.add(aci);
				}
			}
		}
	
		@SuppressWarnings("unchecked")
		@Override
		public void fromJson(Map<String,Object> json) {
			id = JsonTools.getString(json,"id","");
			maxThread = JsonTools.getInt(json, "maxThread", maxThread);
			maxTimesPerMin = JsonTools.getInt(json, "maxTimesPerMin", maxTimesPerMin);
			priority = JsonTools.getInt(json, "priority", priority);
	
			Object _acls = json.get("acls");
			if (_acls != null && _acls instanceof List){
				List<Object> _aclsList = (List<Object>) _acls;
				for (Object _acl:_aclsList){
					if (! (_acl instanceof Map)){
						continue;
					}
					Map<String,Object> _aclMap = (Map<String,Object>)_acl;
					
					AcctControlItem aci = new AcctControlItem(
							JsonTools.getString(_aclMap, "ip", "*"),
							JsonTools.getString(_aclMap, "service","*"));
					
					aci.maxThread = JsonTools.getInt(_aclMap, "maxThread", maxThread);
					aci.maxTimesPerMin = JsonTools.getInt(_aclMap, "maxTimesPerMin", maxTimesPerMin);
					aci.priority = JsonTools.getInt(_aclMap, "priority", priority);
					
					acl.add(aci);
				}
			}
		}
	
		@Override
		public int getPriority(String ip,String service,AccessStat stat){		
			int _maxThread = maxThread;
			int _maxTimesPerMin = maxTimesPerMin;
			int _priority = priority;
			
			AcctControlItem aci = findACI(ip,service);
			if (aci != null){
				//如果找到匹配的,则使用匹配的ACL的参数
				_maxThread = aci.maxThread;
				_maxTimesPerMin = aci.maxTimesPerMin;
				_priority = aci.priority;
			}
			
			if (stat.thread > _maxThread || stat.timesOneMin > _maxTimesPerMin){
				//如果超过并发数，或者超多一分钟调用次数
				return -1;
			}
			return _priority;
		}
		
		/**
		 * 查找匹配的ACL
		 * @param ip 
		 * @param service
		 * @return ACL
		 */
		protected AcctControlItem findACI(String ip,String service){
			for (AcctControlItem aci:acl){
				if (aci.match(ip, service)){
					return aci;
				}
			}
			return null;
		}
		
		/**
		 * 访问控制项目
		 * @author duanyy
		 *
		 */
		public static class AcctControlItem {
			public String ip;
			public String service;
			public int maxThread = 1;
			public int maxTimesPerMin = 10;
			public int priority = -1;
			
			protected StringMatcher ipMatcher = null;
			protected StringMatcher serviceMatcher = null;
			
			public AcctControlItem(String ip,String service){
				this.ip = ip;
				this.service = service;
				ipMatcher = new StringMatcher(ip);
				serviceMatcher = new StringMatcher(service);
			}
			
			/**
			 * 是否和指定的IP和服务匹配
			 * @param ip IP地址，支持*通配符
			 * @param service 服务，支持*通配符
			 * @return 是否匹配
			 */
			public boolean match(String ip,String service){
				boolean matched = ipMatcher.match(ip);
				if (matched){
					matched = serviceMatcher.match(service);
				}
				
				return matched;
			}
		}
	}
}
