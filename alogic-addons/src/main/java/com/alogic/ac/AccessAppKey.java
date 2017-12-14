package com.alogic.ac;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import com.alogic.load.Loadable;
import com.anysoft.util.Configurable;
import com.anysoft.util.JsonSerializer;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlSerializer;
import com.anysoft.util.XmlTools;

/**
 * 应用的Key
 * 
 * @author yyduan
 * @since 1.6.10.6
 */
public interface AccessAppKey extends Loadable,XmlSerializer,JsonSerializer,XMLConfigurable,Configurable{
	
	public String getAppId();
	
	public String getVerifier();
	
	public String getKeyContent();
	
	public static class Default implements AccessAppKey{
		/**
		 * a logger of slf4j
		 */
		public static final Logger LOG = LoggerFactory.getLogger(AccessAppKey.class);
		
		/**
		 * app key
		 */
		protected String key;
		
		/**
		 * 所属AppId
		 */
		protected String appId;
		
		/**
		 * 验证方法
		 */
		protected String verifier;
		
		/**
		 * key的内容
		 */
		protected String keyContent;
		
		/**
		 * 数据加载时间戳
		 */
		protected long timestamp = System.currentTimeMillis();
		
		/**
		 * 数据的生存周期:30分钟
		 */
		public static final long TTL = 30 * 60 * 1000L;	
	
		@Override
		public String getId() {
			return key;
		}
		
		@Override
		public long getTimestamp(){
			return timestamp;
		}
		
		@Override
		public String getAppId(){
			return appId;
		}
		
		@Override
		public String getVerifier(){
			return verifier;
		}
		
		@Override
		public String getKeyContent(){
			return keyContent;
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
			key = PropertiesConstants.getString(p, "id", "");
			appId = PropertiesConstants.getString(p, "appId", "");
			verifier = PropertiesConstants.getString(p, "verifier", "");
			keyContent = PropertiesConstants.getString(p, "keyContent", "");
		}
	
		@Override
		public void configure(Element e, Properties p) {
			XmlElementProperties props = new XmlElementProperties(e,p);
			configure(props);
		}
	
		@Override
		public void toJson(Map<String, Object> json) {
			if (json != null){
				JsonTools.setString(json, "id", key);
				JsonTools.setString(json,"appId",appId);
				JsonTools.setString(json, "verifier", verifier);
				JsonTools.setString(json, "keyContent", keyContent);
			}
		}
	
		@Override
		public void fromJson(Map<String, Object> json) {
			if (json != null){
				key = JsonTools.getString(json,"id","");
				appId = JsonTools.getString(json,"appId","");
				verifier = JsonTools.getString(json,"verifier","");
				keyContent = JsonTools.getString(json,"keyContent","");
			}
		}
	
		@Override
		public void toXML(Element e) {
			if (e != null){
				XmlTools.setString(e, "id", key);
				XmlTools.setString(e, "appId", appId);
				XmlTools.setString(e, "verifier", verifier);
				XmlTools.setString(e, "keyContent", keyContent);
			}
		}
	
		@Override
		public void fromXML(Element e) {
			if (e != null){
				key = XmlTools.getString(e,"id","");
				appId = XmlTools.getString(e,"appId","");
				verifier = XmlTools.getString(e,"verifier","");
				keyContent = XmlTools.getString(e,"keyContent","");
			}
		}
	
		@Override
		public boolean isExpired() {
			return System.currentTimeMillis() - timestamp > TTL;
		}
	
		@Override
		public void expire(){
			timestamp = timestamp - TTL;
		}
	}
}
