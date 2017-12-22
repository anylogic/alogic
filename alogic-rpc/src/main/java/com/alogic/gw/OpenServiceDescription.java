package com.alogic.gw;

import java.util.Map;

import org.w3c.dom.Element;

import com.alogic.load.Loadable;
import com.anysoft.util.Configurable;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 开放服务描述
 * 
 * @author yyduan
 * @since 1.6.11.4
 */
public interface OpenServiceDescription extends ServiceDescription,Loadable {
	
	/**
	 * 获取后端服务的appId
	 * @return appId
	 */
	public String getBackendApp();
	
	/**
	 * 获取后端服务路径
	 * @return 后端服务路径
	 */
	public String getBackendPath();
	
	/**
	 * 缺省实现
	 * @author yyduan
	 *
	 */
	public static class Default implements OpenServiceDescription,XMLConfigurable,Configurable{
		/**
		 * 服务id
		 */
		protected String id;
		
		/**
		 * 日志级别
		 */
		protected LogType logType = LogType.none;
		
		/**
		 * 访问控制器组id
		 */
		protected String acGroupId = "app";
		
		/**
		 * 权限项
		 */
		protected String privilege = "default";
		
		/**
		 * 可见性：
		 */
		protected String visible = "protected";
		
		/**
		 * 服务名
		 */
		protected String name = "";
		
		/**
		 * 说明
		 */
		protected String note = "";
		
		/**
		 * 后端AppId
		 */
		protected String backendApp;
		
		/**
		 * 后端服务路径
		 */
		protected String backendPath;
		
		/**
		 * 加载时间
		 */
		protected long timestamp = System.currentTimeMillis();
		
		/**
		 * 生存时间
		 */
		protected long ttl = 5 * 60 * 1000L;
		
		@Override
		public LogType getLogType() {
			return logType;
		}

		@Override
		public boolean guard() {
			return false;
		}

		@Override
		public String getServiceID() {
			return id;
		}

		@Override
		public String getVisible() {
			return visible;
		}

		@Override
		public String getAcGroup() {
			return acGroupId;
		}

		@Override
		public String getPrivilege() {
			return privilege;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public String getNote() {
			return note;
		}

		@Override
		public String getPath() {
			return id;
		}

		@Override
		public String getModule() {
			return id;
		}

		@Override
		public Properties getProperties() {
			return Settings.get();
		}

		@Override
		public String[] getModules() {
			return null;
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public long getTimestamp() {
			return timestamp;
		}

		@Override
		public boolean isExpired() {
			return System.currentTimeMillis() - timestamp > ttl;
		}

		@Override
		public void expire() {
			timestamp = System.currentTimeMillis() - ttl;
		}		
		
		@Override
		public String getBackendApp() {
			return this.backendApp;
		}

		@Override
		public String getBackendPath() {
			return this.backendPath;
		}
		
		@Override
		public void toXML(Element root) {
			if (root != null){
				XmlTools.setString(root,"id",getServiceID());
				XmlTools.setString(root,"name",getName());
				XmlTools.setString(root,"note",getNote());
				XmlTools.setString(root,"visible",getVisible());
				XmlTools.setString(root,"log", logType.toString());
				XmlTools.setString(root,"acGroupId",getAcGroup());
				XmlTools.setString(root,"privilege",getPrivilege());
				XmlTools.setString(root,"backendApp", getBackendApp());
				XmlTools.setString(root,"backendPath",getBackendPath());
			}
		}

		@Override
		public void fromXML(Element root) {
			if (root != null){
				id = XmlTools.getString(root,"id","");
				name = XmlTools.getString(root, "name", getName());
				note = XmlTools.getString(root,"note",getNote());
				visible = XmlTools.getString(root, "visible", getVisible());
				acGroupId = XmlTools.getString(root, "acGroupId", getAcGroup());
				privilege = XmlTools.getString(root,"privilege", getPrivilege());
				backendApp = XmlTools.getString(root, "backendApp", getBackendApp());
				backendPath = XmlTools.getString(root, "backendPath",getBackendPath());
				logType = this.parseLogType(XmlTools.getString(root, "logType", getLogType().toString()));
			}
		}

		@Override
		public void toJson(Map<String, Object> json) {
			if (json != null){
				JsonTools.setString(json,"id",getServiceID());
				JsonTools.setString(json,"name",getName());
				JsonTools.setString(json,"note",getNote());
				JsonTools.setString(json,"visible",getVisible());
				JsonTools.setString(json,"log", logType.toString());
				JsonTools.setString(json,"acGroupId",getAcGroup());
				JsonTools.setString(json,"privilege",getPrivilege());
				JsonTools.setString(json,"backendApp", getBackendApp());
				JsonTools.setString(json,"backendPath",getBackendPath());
			}
		}

		@Override
		public void fromJson(Map<String, Object> json) {
			if (json != null){
				id = JsonTools.getString(json,"id","");
				name = JsonTools.getString(json, "name", getName());
				note = JsonTools.getString(json,"note",getNote());
				visible = JsonTools.getString(json, "visible", getVisible());
				acGroupId = JsonTools.getString(json, "acGroupId", getAcGroup());
				privilege = JsonTools.getString(json,"privilege", getPrivilege());
				backendApp = JsonTools.getString(json, "backendApp", getBackendApp());
				backendPath = JsonTools.getString(json, "backendPath",getBackendPath());
				logType = parseLogType(JsonTools.getString(json, "logType", getLogType().toString()));
			}
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
		public void configure(Properties p) {
			id = PropertiesConstants.getString(p,"id","",true);
			name = PropertiesConstants.getString(p, "name", getName(),true);
			note = PropertiesConstants.getString(p,"note",getNote(),true);
			visible = PropertiesConstants.getString(p, "visible", getVisible(),true);
			acGroupId = PropertiesConstants.getString(p, "acGroupId", getAcGroup(),true);
			privilege = PropertiesConstants.getString(p,"privilege", getPrivilege(),true);
			backendApp = PropertiesConstants.getString(p, "backendApp", getBackendApp(),true);
			backendPath = PropertiesConstants.getString(p, "backendPath",getBackendPath(),true);
			logType = parseLogType(PropertiesConstants.getString(p, "logType", getLogType().toString(),true));			
		}

		@Override
		public void configure(Element e, Properties p) {
			Properties props = new XmlElementProperties(e,p);
			configure(props);
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
	}

}
