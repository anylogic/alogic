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
import com.logicbus.backend.Context;

/**
 * 访问验证器
 * 
 * @author yyduan
 * @since 1.6.10.6
 */
public interface AccessVerifier extends Loadable,XmlSerializer,JsonSerializer,XMLConfigurable,Configurable{
	/**
	 * 进行一次验证
	 * @param key 应用key的信息
	 * @param ctx 上下文
	 * @return 是否验证通过
	 */
	public boolean verify(AccessAppKey key,Context ctx);
	
	/**
	 * 虚基类
	 * @author yyduan
	 *
	 */
	public static abstract class Abstract implements AccessVerifier{
		/**
		 * a logger of slf4j
		 */
		public static final Logger LOG = LoggerFactory.getLogger(AccessVerifier.class);
		
		/**
		 * id
		 */
		protected String id;
		
		/**
		 * name
		 */
		protected String name;
	
		@Override
		public String getId() {
			return id;
		}

		@Override
		public long getTimestamp(){
			return System.currentTimeMillis();
		}
		
		@Override
		public void report(Element xml) {
			if (xml != null){
				XmlTools.setString(xml,"module",this.getClass().getName());
				XmlTools.setString(xml,"id",id);
				XmlTools.setString(xml, "name", name);
			}
		}
	
		@Override
		public void report(Map<String, Object> json) {
			if (json != null){
				JsonTools.setString(json,"module",this.getClass().getName());
				JsonTools.setString(json,"id",id);
				JsonTools.setString(json, "name", name);
			}
		}
	
		@Override
		public void configure(Properties p) {
			id = PropertiesConstants.getString(p, "id", "");
			name = PropertiesConstants.getString(p, "name", "");
		}
	
		@Override
		public void configure(Element e, Properties p) {
			XmlElementProperties props = new XmlElementProperties(e,p);
			configure(props);
		}
	
		@Override
		public void toJson(Map<String, Object> json) {
			if (json != null){
				JsonTools.setString(json, "id", id);
				JsonTools.setString(json, "name",name);
			}
		}
	
		@Override
		public void fromJson(Map<String, Object> json) {
			if (json != null){
				id = JsonTools.getString(json,"id","");
				name = JsonTools.getString(json, "name", "");
			}
		}
	
		@Override
		public void toXML(Element xml) {
			if (xml != null){
				XmlTools.setString(xml,"module",this.getClass().getName());
				XmlTools.setString(xml,"id",id);
				XmlTools.setString(xml, "name", name);
			}
		}
	
		@Override
		public void fromXML(Element e) {
			if (e != null){
				id = XmlTools.getString(e,"id","");
				name = XmlTools.getString(e, "name", "");
			}
		}
	
		@Override
		public boolean isExpired() {
			return false;
		}
	
		@Override
		public void expire() {
		}
	}
}
