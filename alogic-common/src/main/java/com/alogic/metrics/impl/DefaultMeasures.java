package com.alogic.metrics.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.alogic.metrics.core.Fragment.Method;
import com.alogic.metrics.core.Measures;
import com.alogic.metrics.core.Value;

/**
 * 量度的缺省实现
 * 
 * @author duanyy
 *
 */
public class DefaultMeasures implements Measures{

	/**
	 * 量度值
	 */
	protected Map<String,Value> values = new HashMap<String,Value>(5);
	
	@Override
	public void toJson(Map<String, Object> json) {
		if (json != null){
			Iterator<Entry<String,Value>> iterator = values.entrySet().iterator();
			
			while (iterator.hasNext()){
				Entry<String,Value> entry = iterator.next();
				
				Map<String,Object> map = new HashMap<String,Object>();
				entry.getValue().toJson(map);
				json.put(entry.getKey(), map);
			}
		}
	}

	@Override
	public void fromJson(Map<String, Object> json) {
		if (json != null){
			Iterator<Entry<String,Object>> iter = json.entrySet().iterator();
			while (iter.hasNext()){
				Entry<String,Object> entry = iter.next();
				String key = entry.getKey();
				Object value = entry.getValue();
				if (value instanceof Map){
					@SuppressWarnings("unchecked")
					Map<String,Object> map = (Map<String,Object>)value;
					Value v = newValue(0L,Method.sum);
					v.fromJson(map);
					
					values.put(key, v);
				}
			}
		}
	}

	@Override
	public String getValue(String varName, Object context, String defaultValue) {
		Value found = values.get(varName);
		if (found == null){
			return defaultValue;
		}
		
		return found.value().toString();
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
	public Measures set(String key, String value) {
		Value v = newValue(value,Method.lst);
		values.put(key, v);
		return this;
	}

	@Override
	public Measures set(String key, long value, Method m) {
		Value v = newValue(value,m);
		values.put(key, v);
		return this;
	}

	@Override
	public Measures set(String key, long value) {
		return set(key,value,Method.sum);
	}

	@Override
	public Measures set(String key, double value, Method m) {
		Value v = newValue(value,m);
		values.put(key, v);
		return this;
	}

	@Override
	public Measures set(String key, double value) {
		return set(key,value,Method.sum);
	}

	@Override
	public long getAsLong(String key, long dftValue) {
		Value found = values.get(key);
		return found == null ? dftValue:found.asLong(dftValue);
	}

	@Override
	public String getAsString(String key, String dftValue) {
		Value found = values.get(key);
		return found == null ? dftValue:found.asString(dftValue);
	}

	@Override
	public double getAsDouble(String key, double dftValue) {
		Value found = values.get(key);
		return found == null ? dftValue:found.asDouble(dftValue);
	}

	@Override
	public Measures incr(Measures other) {
		
		return this;
	}

	@Override
	public Method getMethod(String key) {
		Value found = values.get(key);
		return found == null ? Method.lst:found.method();
	}

	@Override
	public boolean exist(String key) {
		return values.containsKey(key);
	}

	protected Value newValue(Object value,Method m){
		return new DefaultValue(value,m);
	}
}
