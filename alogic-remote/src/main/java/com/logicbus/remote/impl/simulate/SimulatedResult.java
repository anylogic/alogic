package com.logicbus.remote.impl.simulate;

import java.util.HashMap;
import java.util.Map;

import com.anysoft.util.JsonTools;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.JsonProviderFactory;
import com.logicbus.remote.core.AbstractResult;

/**
 * 模拟的结果实现
 * 
 * @author duanyy
 * 
 * @version 1.2.9.3
 *
 */
public class SimulatedResult extends AbstractResult {
	protected Map<String,Object> root = null;
	protected Map<String,String> idPaths;
	
	@SuppressWarnings("unchecked")
	protected SimulatedResult(String data, Map<String, String> _idPaths){
		idPaths = _idPaths;
		
		if (data != null && data.length() > 0){
			JsonProvider provider = JsonProviderFactory.createProvider();
			Object rootObj = provider.parse(data);
			if (rootObj instanceof Map){
				root = (Map<String,Object>)rootObj;
			}
		}
		if (root == null){
			root = new HashMap<String,Object>();
		}
	}
	
	
	public Object getData(String id){
		if (idPaths == null){
			return root.get(id);
		}
		
		String path = idPaths.get(id);
		if (path == null){
			return root.get(id);
		}
		
		return JsonPath.read(root, path);
	}
	
	public Map<String, Object> getRoot() {
		return root;
	}
	
	
	public String getHost() {
		Map<String,Object> root = getRoot();
		return JsonTools.getString(root, "host", "");
	}

	
	public String getCode() {
		Map<String,Object> root = getRoot();
		return JsonTools.getString(root, "code", "");
	}

	
	public String getReason() {
		Map<String,Object> root = getRoot();
		return JsonTools.getString(root, "reason", "");
	}

	
	public String getGlobalSerial() {
		Map<String,Object> root = getRoot();
		return JsonTools.getString(root, "serial", "");
	}

	
	public long getDuration() {
		Map<String,Object> root = getRoot();
		return JsonTools.getLong(root, "duration", 0);
	}
}
