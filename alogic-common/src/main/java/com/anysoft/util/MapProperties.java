package com.anysoft.util;

import java.util.Map;

/**
 * 将Map转化为Properties
 * @author yyduan
 * @since 1.6.8.12
 */
public class MapProperties extends Properties{

	protected Map<String,Object> map = null;
	
	public MapProperties(Map<String,Object> map,Properties parent){
		super("default",parent);
		
		this.map = map;
	}
	
	@Override
	protected void _SetValue(String _name, String _value) {
		if (map != null){
			map.put(_name, _value);
		}
	}

	@Override
	protected String _GetValue(String _name) {
		if (map != null){
			Object found = map.get(_name);
			return found == null? null : found.toString();
		}
		return null;
	}

	@Override
	public void Clear() {
		if (map != null){
			map.clear();
		}
	}

}