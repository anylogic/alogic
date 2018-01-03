package com.alogic.cache.provider;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.alogic.cache.core.MultiFieldObject;
import com.alogic.cache.core.MultiFieldObjectProvider;
import com.anysoft.util.Properties;
import com.anysoft.util.Watcher;
import com.anysoft.util.XmlTools;

/**
 * 内置XML数据的Provider
 * 
 * @author duanyy
 * @since 1.6.3.3
 * 
 * @version 1.6.3.3 <br>
 * - 改为从MultiFieldObjectProvider的虚基类继承 <br>
 * 
 * @version 1.6.4.19 [duanyy 20151218] <br>
 * - 按照SONAR建议修改代码 <br>
 * 
 * @deprecated
 */
public class XmlData extends MultiFieldObjectProvider.Abstract {
	protected Element dataElement = null;
	
	@Override
	protected MultiFieldObject loadObject(String id, boolean cacheAllowed) {
		if (dataElement == null) 
			return null;
		
		Node found = XmlTools.getNodeByPath(dataElement, "row[@id='" + id + "']");
		
		if (found == null || found.getNodeType() != Node.ELEMENT_NODE) 
			return null;
	
		Element row = (Element)found;
		
		MultiFieldObject.Default value = new MultiFieldObject.Default();
		value.fromXML(row);
		
		return value;
	}

	@Override
	public void addWatcher(Watcher<MultiFieldObject> watcher) {
		// nothing to do
	}

	@Override
	public void removeWatcher(Watcher<MultiFieldObject> watcher) {
		// nothing to do
	}

	@Override
	protected void onConfigure(Element element, Properties props){
		dataElement = XmlTools.getFirstElementByPath(element, "data");
	}

}
