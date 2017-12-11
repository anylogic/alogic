package com.alogic.remote.call.impl;

import java.util.Map;

import com.alogic.remote.call.AbstractResult;
import com.anysoft.util.JsonTools;
import com.jayway.jsonpath.JsonPath;

/**
 * http result
 * @author yyduan
 * @version 1.6.8.13 [duanyy 20170427] <br>
 * - 从alogic-remote中迁移过来 <br>
 */
public class HttpResult extends AbstractResult {
	protected Map<String,String> idPaths;
	protected Map<String,Object> root;
	
	protected HttpResult(Map<String,Object> root, Map<String, String> idPaths){
		this.root = root;
		this.idPaths = idPaths;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <data> data getData(String id){
		if (idPaths == null){
			return (data) root.get(id);
		}
		
		String path = idPaths.get(id);
		if (path == null){
			return (data) root.get(id);
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
