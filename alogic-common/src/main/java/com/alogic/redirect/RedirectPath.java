package com.alogic.redirect;

import java.util.Map;

import org.w3c.dom.Element;

import com.alogic.load.Loadable;
import com.anysoft.util.Configurable;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;


/**
 * 重定向路径
 * 
 * @author yyduan
 * @since 1.6.11.26
 */
public interface RedirectPath extends Loadable{
	/**
	 * 获取目标路径
	 * @return 目标路径
	 */
	public String getTargetPath();
	
	/**
	 * 是否必须登录
	 */
	public boolean mustLogin();
	
	/**
	 * 是否需要携带登录证明
	 */
	public boolean withCredential();
	
	public static class Default extends Loadable.Abstract implements RedirectPath ,XMLConfigurable,Configurable{
		/**
		 * id
		 */
		protected String id;
		
		/**
		 * targetPath
		 */
		protected String targetPath;
		
		/**
		 * mustLogin
		 */
		protected boolean mustLogin = false;
		
		/**
		 * withCredential
		 */
		protected boolean withCredential = false;
		
		@Override
		public String getId() {
			return id;
		}

		@Override
		public void report(Element xml) {
			if (xml != null){
				XmlTools.setString(xml, "module", getClass().getName());
			}
		}

		@Override
		public void report(Map<String, Object> json) {
			if (json != null){
				JsonTools.setString(json,"module",getClass().getName());
			}
		}

		@Override
		public String getTargetPath() {
			return targetPath;
		}

		@Override
		public boolean mustLogin() {
			return this.mustLogin;
		}

		@Override
		public boolean withCredential() {
			return this.withCredential;
		}

		@Override
		public void configure(Properties p) {
			id = PropertiesConstants.getString(p, "id", id);
			targetPath = PropertiesConstants.getString(p, "targetPath", targetPath);
			mustLogin = PropertiesConstants.getBoolean(p, "mustLogin", mustLogin);
			withCredential = PropertiesConstants.getBoolean(p, "withCredential", withCredential);
		}

		@Override
		public void configure(Element e, Properties p) {
			Properties props = new XmlElementProperties(e,p);
			configure(props);
		}
		
	}
}
