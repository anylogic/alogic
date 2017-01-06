package com.alogic.metrics.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.alogic.metrics.core.Dimensions;

/**
 * 缺省的Dimensions实现
 * @author duanyy
 *
 */
public class DefaultDimensions implements Dimensions{
	
	/**
	 * 维度列表
	 */
	protected Map<String,String> dims = null;
	
	@Override
	public void toJson(Map<String, Object> json) {
		if (json != null){
			Map<String,String> dimensions = getDims();
			Iterator<Entry<String,String>> iterator = dimensions.entrySet().iterator();
			
			while (iterator.hasNext()){
				Entry<String,String> entry = iterator.next();					
				json.put(entry.getKey(), entry.getValue());
			}
		}
	}

	@Override
	public void fromJson(Map<String, Object> json) {
		if (json != null){
			Map<String,String> dimensions = getDims();
			Iterator<Entry<String,Object>> iter = json.entrySet().iterator();
			while (iter.hasNext()){
				Entry<String,Object> entry = iter.next();
				dimensions.put(entry.getKey(), entry.getValue().toString());
			}
		}
	}
	
	@Override
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		Map<String, String> dimensions = getDims();
		Iterator<Entry<String, String>> iterator = dimensions.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry<String, String> entry = iterator.next();
			buffer.append(entry.getKey()).append("$").append(entry.getValue());
			if (iterator.hasNext()) {
				buffer.append("$");
			}
		}

		return buffer.toString();
	}

	@Override
	public String getValue(String varName, Object context, String defaultValue) {
		return get(varName,defaultValue);
	}

	@Override
	public String getRawValue(String varName, Object context, String dftValue) {
		return getValue(varName,context,dftValue);
	}

	@Override
	public Object getContext(String varName) {
		return this;
	}

	@Override
	public Dimensions set(String key, String value, boolean overwrite) {
		if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)){
			return this;
		}
		
		Map<String,String> dimesions = getDims();			
		if (overwrite || !dimesions.containsKey(key)){
			dimesions.put(key, value);
		}
		return this;
	}

	@Override
	public String get(String key, String dftValue) {
		String value = dftValue;
		
		Map<String,String> dimesions = getDims();
		value = dimesions.get(key);
		if (value == null){
			value = dftValue;
		}
		return value;
	}
	
	@Override
	public boolean exist(String key){
		return getDims().containsKey(key);
	}
	
	private Map<String,String> getDims(){
		if (dims == null){
			synchronized (this){
				if (dims == null){
					dims = new HashMap<String,String>(5);
				}
			}
		}		
		
		return dims;
	}
	
	
}