package com.alogic.cache.provider;

import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.alogic.cache.core.MultiFieldObject;
import com.alogic.cache.core.MultiFieldObjectProvider;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.Watcher;
import com.anysoft.util.XmlTools;

/**
 * 内置XML数据的Provider
 * 
 * @author duanyy
 * @since 1.6.3.3
 */
public class XmlData implements MultiFieldObjectProvider {
	protected Element dataElement = null;
	
	public MultiFieldObject load(String id) {
		return load(id,true);
	}

	public MultiFieldObject load(String id, boolean cacheAllowed) {
		if (dataElement == null) return null;
		
		Node found = XmlTools.getNodeByPath(dataElement, "row[@id='" + id + "']");
		
		if (found == null || found.getNodeType() != Node.ELEMENT_NODE) return null;
	
		Element _row = (Element)found;
		
		MultiFieldObject.Default value = new MultiFieldObject.Default();
		value.fromXML(_row);
		
		return value;
	}

	public void addWatcher(Watcher<MultiFieldObject> watcher) {
		// nothing to do
	}

	public void removeWatcher(Watcher<MultiFieldObject> watcher) {
		// nothing to do
	}

	public void configure(Element _e, Properties _properties)
			throws BaseException {
		dataElement = XmlTools.getFirstElementByPath(_e, "data");
	}

	public void report(Element xml) {
		// nothing to do
	}

	public void report(Map<String, Object> json) {
		// nothing to do
	}
}
