package com.alogic.event;

import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.alogic.load.Loadable;
import com.alogic.xscript.Script;
import com.anysoft.util.Configurable;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * 事件处理过程
 * @author yyduan
 *
 */
public interface Process extends XMLConfigurable,Configurable,Reportable,Loadable{
	/**
	 * 获取id
	 * @return id
	 */
	public String getId();
	
	/**
	 * 是否为空处理
	 * @return 是否为空处理
	 */
	public boolean isNull();
	
	/**
	 * 获取处理的脚本
	 * @return Script
	 */
	public Script getScript();
	
	/**
	 * 获取可重试次数
	 * @return 可重试次数
	 */
	public int getRetryCount();

	/**
	 * 缺省实现
	 * @author yyduan
	 *
	 */
	public static class Default implements Process{
		/**
		 * a logger of log4j
		 */
		protected static final Logger LOG = LoggerFactory.getLogger(Process.class);
		
		/**
		 * id
		 */
		protected String id;
		
		/**
		 * 可重试次数
		 */
		protected int retryCnt = 0;
		
		/**
		 * script
		 */
		protected Script script = null;
		
		/**
		 * 时间戳
		 */
		protected long timestamp = System.currentTimeMillis();
		
		/**
		 * 生存时间
		 */
		protected long ttl = 5 * 60 * 1000L;
		
		@Override
		public void configure(Element e, Properties p) {
			Properties props = new XmlElementProperties(e,p);
			Element scriptElem = XmlTools.getFirstElementByPath(e, "script");
			if (scriptElem != null){
				script = new Script("script",null);
				script.configure(scriptElem, props);
			}		
			configure(props);	
		}

		@Override
		public void configure(Properties p) {
			id = PropertiesConstants.getString(p,"id","");	
			retryCnt = PropertiesConstants.getInt(p,"retry",retryCnt);
			ttl = PropertiesConstants.getLong(p,"ttl",ttl);
			
			if (script == null){
				String src = PropertiesConstants.getString(p, "src","");
				if (StringUtils.isNotEmpty(src)) {
					script = Script.create(src, p);
				}
			}
			
			if (script == null){
				String scriptContent = PropertiesConstants.getString(p, "script","");
				if (StringUtils.isNotEmpty(scriptContent)){
					Document doc = null;
					try {
						doc = XmlTools.loadFromContent(scriptContent);
						script = new Script("script",null);
						script.configure(doc.getDocumentElement(),p);
					} catch (Exception ex) {
						LOG.error("Can not load script from " + scriptContent);
						LOG.error(ExceptionUtils.getStackTrace(ex));
					}
				}
			}			
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public int getRetryCount() {
			return retryCnt;
		}
		
		@Override
		public boolean isNull() {
			return StringUtils.isEmpty(id) || script == null;
		}

		@Override
		public Script getScript() {
			return script;
		}

		@Override
		public void report(Element xml) {
			if (xml != null){
				XmlTools.setString(xml,"module",getClass().getName());
				XmlTools.setString(xml,"id",id);
			}
		}

		@Override
		public void report(Map<String, Object> json) {
			if (json != null){
				JsonTools.setString(json,"module",getClass().getName());
				JsonTools.setString(json,"id",id);
			}
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
		public void expire(){
			timestamp = timestamp - ttl;
		}
	}
}
