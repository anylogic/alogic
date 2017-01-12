package com.alogic.metrics.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.alogic.metrics.Dimensions;
import com.anysoft.util.JsonTools;

/**
 * 缺省的Dimensions实现
 * @author duanyy
 * 
 * @since 1.6.6.13
 *
 */
public class DefaultDimensions implements Dimensions{
	
	/**
	 * 维度列表
	 */
	protected Map<String,String> dims = new HashMap<String,String>(5);
	
	@Override
	public void toJson(Map<String, Object> json) {
		if (json != null){
			
			List<Object> list = new ArrayList<Object>(dims.size());
			
			Iterator<Entry<String,String>> iterator = dims.entrySet().iterator();			
			while (iterator.hasNext()){
				Entry<String,String> entry = iterator.next();
				
				Map<String,Object> map = new HashMap<String,Object>();
				JsonTools.setString(map,"k",entry.getKey());
				JsonTools.setString(map,"v",entry.getValue());				
				list.add(map);
			}
			
			json.put("dims", list);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void fromJson(Map<String, Object> json) {
		if (json != null){
			Object o = json.get("dims");
			
			if (o instanceof List){
				List<Object> list = (List<Object>)o;
				
				for (Object item:list){
					if (item instanceof Map){
						Map<String,Object> map = (Map<String,Object>)item;
						String key = JsonTools.getString(map, "k", "");
						String value = JsonTools.getString(map, "v", "");
						if (StringUtils.isNotEmpty(key)){
							dims.put(key, value);
						}
					}
				}
			}
		}
	}
	
	@Override
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		Iterator<Entry<String, String>> iterator = dims.entrySet().iterator();

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
				
		if (overwrite || !dims.containsKey(key)){
			dims.put(key, value);
		}
		return this;
	}

	@Override
	public String get(String key, String dftValue) {
		String value = dims.get(key);
		if (value == null){
			value = dftValue;
		}
		return value;
	}
	
	@Override
	public boolean exist(String key){
		return dims.containsKey(key);
	}

}