package com.alogic.metrics.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alogic.metrics.Measures;
import com.alogic.metrics.Value;
import com.alogic.metrics.Fragment.Method;

/**
 * 量度的缺省实现
 * 
 * @author duanyy
 *
 * @since 1.6.6.13
 *
 *
 */
public class DefaultMeasures implements Measures{

	/**
	 * 量度值
	 */
	protected Map<String,Value> values = new HashMap<String,Value>(5);
	
	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer();
		
		Value[] vals = values();

		for (Value v:vals){
			buf.append(v).append(";");
		}		
		
		return buf.toString();
	}
	
	@Override
	public void toJson(Map<String, Object> json) {
		if (json != null){
			Value[] vals = values();
			
			List<Object> list = new ArrayList<Object>(vals.length);			
			for (Value v:vals){
				Map<String,Object> map = new HashMap<String,Object>();				
				v.toJson(map);				
				list.add(map);
			}
			
			json.put("meas", list);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void fromJson(Map<String, Object> json) {
		if (json != null){
			Object meas = json.get("meas");
			
			if (meas != null && meas instanceof List){
				List<Object> list = (List<Object>)meas;
				for (Object o:list){
					if (o instanceof Map){
						Map<String,Object> map = (Map<String,Object>)o;
						Value value = newValue(map);
						
						String key = value.key();
						if (StringUtils.isNotEmpty(key)){
							values.put(key, value);
						}
					}
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
		Value v = newValue(key,value,Method.lst);
		values.put(key, v);
		return this;
	}

	@Override
	public Measures set(String key, long value, Method m) {
		Value v = newValue(key,value,m);
		values.put(key, v);
		return this;
	}

	@Override
	public Measures set(String key, long value) {
		return set(key,value,Method.sum);
	}

	@Override
	public Measures set(String key, double value, Method m) {
		Value v = newValue(key,value,m);
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
		Value[] vals = other.values();
		
		for (Value val:vals){
			String key = val.key();
			
			Value found = values.get(key);
			if (found == null){
				Value added = newValue(key,val.value(),val.method());
				values.put(key, added);
			}else{
				found.incr(val);
			}
		}
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

	protected Value newValue(String key,Object value,Method m){
		return new DefaultValue(key,value,m);
	}
	
	protected Value newValue(Map<String,Object> json){
		return new DefaultValue(json);
	}

	@Override
	public Value[] values() {
		return values.values().toArray(new Value[values.size()]);
	}
}
