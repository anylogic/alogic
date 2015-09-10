package com.alogic.cache.core;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.cache.Provider;
import com.anysoft.util.BaseException;
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
 */
public interface MultiFieldObjectProvider extends Provider<MultiFieldObject>,XMLConfigurable,Reportable {
	
	/**
	 * 虚基类
	 * 
	 * @author duanyy
	 * @since 1.6.3.3
	 */
	abstract public static class Abstract implements MultiFieldObjectProvider {
		protected Counter counter = null;
		
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

		abstract protected MultiFieldObject loadObject(String id,boolean cacheAllowed);
		
		public void configure(Element _e, Properties _properties)
				throws BaseException {
			Properties p = new XmlElementProperties(_e,_properties);
			
			Counter.TheFactory factory = new Counter.TheFactory();
			
			try {
				counter = factory.newInstance(_e, _properties, "counter", SimpleCounter.class.getName());
			}catch (Exception ex){
				counter = new SimpleCounter(p);
			}
			
			onConfigure(_e,p);
		}
		
		abstract protected void onConfigure(Element _e,Properties p);

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

		public void report(Map<String, Object> json) {
			if (json != null){
				JsonTools.setString(json,"module",getClass().getName());
				if (counter != null){
					Map<String,Object> stat = new HashMap<String,Object>();
					counter.report(stat);
					json.put("stat", stat);
				}
			}
		}
		
		public void addWatcher(Watcher<MultiFieldObject> watcher) {
			// nothing to do
		}

		public void removeWatcher(Watcher<MultiFieldObject> watcher) {
			// nothing to do
		}
	}
}
