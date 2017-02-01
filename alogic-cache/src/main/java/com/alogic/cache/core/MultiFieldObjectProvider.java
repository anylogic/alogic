package com.alogic.cache.core;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.cache.Provider;
import com.anysoft.util.Counter;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Reportable;
import com.anysoft.util.SimpleCounter;
import com.anysoft.util.Watcher;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;

/**
 * MultiFieldObject专用Provider
 * 
 * @author duanyy
 * @since 1.6.3.3
 * 
 * @version 1.6.3.3 增加虚基类
 * 
 * @version 1.6.4.9 [duanyy 20151023] <br>
 * - 增加Null实现
 * 
 * @version 1.6.4.19 [duanyy 20151218] <br>
 * - 按照SONAR建议修改代码 <br>
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 */
public interface MultiFieldObjectProvider extends Provider<MultiFieldObject>,XMLConfigurable,Reportable {
	
	/**
	 * Null实现
	 * @author duanyy
	 *
	 * @since 1.6.4.9 
	 */
	public static class Null implements MultiFieldObjectProvider {

		@Override
		public MultiFieldObject load(String id, boolean cacheAllowed) {
			//本provider不提供任何对象
			return null;
		}

		@Override
		public void addWatcher(Watcher<MultiFieldObject> watcher) {
			//无需watcher
		}

		@Override
		public void removeWatcher(Watcher<MultiFieldObject> watcher) {
			//无需watcher
		}

		@Override
		public void configure(Element e, Properties props){
			//没有
		}

		@Override
		public void report(Element xml) {
			if (xml != null){
				xml.setAttribute("module", getClass().getName()); // NOSONAR
			}
		}

		@Override
		public void report(Map<String, Object> json) {
			if (json != null){
				json.put("module", getClass().getName());
			}
		}
		
	}
	/**
	 * 虚基类
	 * 
	 * @author duanyy
	 * @since 1.6.3.3
	 */
	 public abstract static class Abstract implements MultiFieldObjectProvider {
		protected Counter counter = null;
		protected static final Logger LOG = LoggerFactory.getLogger(MultiFieldObjectProvider.class);
		
		@Override
		public MultiFieldObject load(String id, boolean cacheAllowed) {
			MultiFieldObject found = null;
			long now = System.currentTimeMillis();
			try {
				found = loadObject(id,cacheAllowed);
			}finally{
				if (counter != null){
					counter.count(System.currentTimeMillis() - now, found == null);
				}
			}
			return found;
		}

		protected abstract MultiFieldObject loadObject(String id,boolean cacheAllowed);
		
		@Override
		public void configure(Element element, Properties props) {
			Properties p = new XmlElementProperties(element,props);
			
			Counter.TheFactory factory = new Counter.TheFactory();
			
			try {
				counter = factory.newInstance(element, props, "counter", SimpleCounter.class.getName());
			}catch (Exception ex){
				LOG.error("Can not create the Counter.Use Default.",ex);
				counter = new SimpleCounter(p);
			}
			
			onConfigure(element,p);
		}
		
		protected abstract void onConfigure(Element e,Properties p);

		@Override
		public void report(Element xml) {
			if (xml != null){
				xml.setAttribute("module", getClass().getName());
				if (counter != null){
					Document doc = xml.getOwnerDocument();
					
					Element stat = doc.createElement("stat");
					counter.report(stat);
					xml.appendChild(stat);
				}
			}
		}

		@Override
		public void report(Map<String, Object> json) {
			if (json != null){
				JsonTools.setString(json,"module",getClass().getName());
				if (counter != null){
					Map<String,Object> stat = new HashMap<String,Object>(); // NOSONAR
					counter.report(stat);
					json.put("stat", stat);
				}
			}
		}
		
		@Override
		public void addWatcher(Watcher<MultiFieldObject> watcher) {
			// nothing to do
		}

		@Override
		public void removeWatcher(Watcher<MultiFieldObject> watcher) {
			// nothing to do
		}
	}
}
