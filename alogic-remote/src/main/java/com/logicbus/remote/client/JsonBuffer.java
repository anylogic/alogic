package com.logicbus.remote.client;

import java.util.HashMap;
import java.util.Map;

import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.JsonProviderFactory;

public class JsonBuffer extends Buffer {
	
	/**
	 * Json结构的根节点
	 */
	protected Map<String,Object> root = null;
	
	public Map<String,Object> getRoot(){return root;}
	
	public JsonBuffer(){
		root = new HashMap<String,Object>();
	}
	
	public JsonBuffer(Map<String,Object> json){
		root = json;
	}
	
	@SuppressWarnings("unchecked")
	
	public void prepareBuffer(boolean flag){
		if (flag){
			StringBuffer buf = getBuffer();
			buf.setLength(0);
			JsonProvider provider = JsonProviderFactory.createProvider();
			String data = provider.toJson(root);
			buf.append(data);
		}else{
			StringBuffer _buf = getBuffer();
			String data = _buf.toString();
			if (data != null && data.length() > 0){
				JsonProvider provider = JsonProviderFactory.createProvider();
				Object rootObj = provider.parse(_buf.toString());
				if (rootObj instanceof Map){
					root = (Map<String,Object>)rootObj;
				}
			}
			if (root == null){
				root = new HashMap<String,Object>();
			}
		}
	}
}
