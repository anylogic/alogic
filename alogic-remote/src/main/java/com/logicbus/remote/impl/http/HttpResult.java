package com.logicbus.remote.impl.http;

import java.util.Map;

import com.anysoft.util.JsonTools;
import com.jayway.jsonpath.JsonPath;
import com.logicbus.remote.client.JsonBuffer;
import com.logicbus.remote.core.AbstractResult;

/**
 * 服务请求结果
 * 
 * @author duanyy
 *
 * @since 1.2.9
 * 
 * @version 1.2.9.3 [20141021 duanyy]
 * - 从AbstractResult进行扩展
 * 
 */
public class HttpResult extends AbstractResult {
	protected JsonBuffer buffer = null;
	protected Map<String,String> idPaths;
	protected HttpResult(JsonBuffer _buf, Map<String, String> _idPaths){
		buffer = _buf;
		idPaths = _idPaths;
	}
	
	
	public Object getData(String id){
		Map<String,Object> root = buffer.getRoot();
		
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
		return buffer.getRoot();
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
