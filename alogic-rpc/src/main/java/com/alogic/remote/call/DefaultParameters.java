package com.alogic.remote.call;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import com.anysoft.util.DefaultProperties;
import com.anysoft.util.JsonSerializer;

/**
 * 缺省的参数实现
 * 
 * @author duanyy
 * @since 1.2.9.3
 * 
 * @version 1.6.8.13 [duanyy 20170427] <br>
 * - 从alogic-remote中迁移过来 <br>
 */
public class DefaultParameters extends DefaultProperties implements Parameters {
	protected static Object context = new Object();
	protected Hashtable<String,Object> jsonObjects = new  Hashtable<String,Object>();
	
	public String getValue(String varName, Object context, String defaultValue) {
		return GetValue(varName, defaultValue);
	}

	public Object getContext(String varName) {
		return context;
	}
	
	public Parameters param(String id, String value) {
		SetValue(id, value);
		return this;
	}

	public Parameters params(String... _params) {
		for (int i = 0 ; i < _params.length ; i = i + 2){
			if (i + 1 < _params.length){
				param(_params[i],_params[i + 1]);
			}else{
				param(_params[i],"");
			}
		}
		return this;
	}
	
	public <data extends JsonSerializer> Parameters param(String id, data value) {
		if (value != null){
			Map<String,Object> json = new HashMap<String,Object>();
			value.toJson(json);
			jsonObjects.put(id, json);
		}
		return this;
	}
	
	public <data> Parameters param(String id,data value,Builder<data> builder) {
		Object json = value;
		
		if (builder != null){
			json = builder.serialize(id, value);
		}
		
		if (json != null){
			jsonObjects.put(id, json);
		}
		return this;
	}

	public Parameters clean(){
		jsonObjects.clear();
		return this;
	}
	
	public Object getData(String id){
		return jsonObjects.get(id);
	}
}
