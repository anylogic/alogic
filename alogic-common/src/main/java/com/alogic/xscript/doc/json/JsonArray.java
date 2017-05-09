package com.alogic.xscript.doc.json;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alogic.xscript.doc.XsArray;
import com.alogic.xscript.doc.XsObject;

/**
 * 基于Json的XsArray
 * @author yyduan
 * @since 1.6.8.14
 */
public class JsonArray implements XsArray {
	
	protected List<Object> content = null;
	
	public JsonArray(List<Object> content) {
		this.content = content;
	}

	public Object getContent() {
		return content;
	}

	@Override
	public int getElementCount() {
		return content.size();
	}

	@SuppressWarnings("unchecked")
	@Override
	public XsObject get(int index) {
		Object found = content.get(index);
		if (found instanceof Map){
			return new JsonObject("item" + index,(Map<String,Object>)found);
		}
		
		return null;
	}

	@Override
	public XsObject newObject() {
		return new JsonObject("item" + content.size(),new HashMap<String,Object>());
	}

	@Override
	public void add(XsObject data) {
		content.add(data.getContent());
	}

}
