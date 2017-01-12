package com.alogic.metrics.impl;

import java.util.Map;

import com.alogic.metrics.Value;
import com.alogic.metrics.Fragment.DataType;
import com.alogic.metrics.Fragment.Method;
import com.anysoft.util.JsonTools;

/**
 * 缺省的量度实现
 * @author duanyy
 *
 * @since 1.6.6.13
 *
 */
public class DefaultValue implements Value{
	/**
	 * key
	 */
	protected String key;
	
	/**
	 * 量度值
	 */
	protected Object value;
	
	/**
	 * 汇聚方法
	 */
	protected Method method;
	
	/**
	 * 数据类型
	 */
	protected DataType type;
	
	public DefaultValue(String k,Object v,Method m){
		key = k;
		method = m;
		if (v instanceof Long || v instanceof Integer){
			type = DataType.L;
			value = v;
		}else{
			if (v instanceof Double || v instanceof Float){
				type = DataType.D;
				value = v;
			}else{
				type = DataType.S;
				value = v.toString();
			}
		}
	}
	
	public DefaultValue(Map<String,Object> json){
		fromJson(json);
	}
	
	@Override
	public Value incr(Value other) {
		if (DataType.L == type){
			value = incr(asLong(0),other.asLong(0),method);
		}else{
			if (DataType.D == type){
				value = incr(asDouble(0),other.asDouble(0),method);
			}else{
				value = other.value();
			}
		}
		return this;
	}

	@Override
	public Method method() {
		return method;
	}

	@Override
	public DataType type() {
		return type;
	}
	
	@Override
	public Object value(){
		return value;
	}

	@Override
	public String asString(String dftValue) {
		if (value != null && value instanceof String){
			return (String)value;
		}
		return dftValue;
	}

	@Override
	public double asDouble(double dftValue) {
		if (value != null && value instanceof Number){
			return ((Number)value).doubleValue();
		}
		return dftValue;
	}

	@Override
	public long asLong(long dftValue) {
		if (value != null && value instanceof Number){
			return ((Number)value).longValue();
		}
		return dftValue;
	}

	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer();
		
		buf.append(key).append(",");
		buf.append(type).append(",");
		buf.append(method).append(",");
		buf.append(value);
		
		return buf.toString();
	}
	
	@Override
	public void toJson(Map<String, Object> json) {
		if (json != null){
			JsonTools.setString(json,"v",String.valueOf(value));
			JsonTools.setString(json,"m",method.name());
			JsonTools.setString(json,"t",type.name());
			JsonTools.setString(json, "k", key);
		}
	}

	@Override
	public void fromJson(Map<String, Object> json) {
		if (json != null){
			method = Method.valueOf(JsonTools.getString(json, "m", Method.lst.name()));
			type = DataType.valueOf(JsonTools.getString(json,"t",DataType.S.name()));
			key = JsonTools.getString(json, "k", "");
			String v = JsonTools.getString(json, "v", "");
			if (type == DataType.L){
				try {
					value = Long.parseLong(v);
				}catch (NumberFormatException ex){
					value = 0;
				}
			}else{
				if (type == DataType.D){
					try {
						value = Double.parseDouble(v);
					}catch (NumberFormatException ex){
						value = 0;
					}					
				}else{
					value = v;
				}
			}
		}
	}
	
	private Long incr(Long object, Long value, Method m) {		
		if (m == Method.sum){
			return object + value;
		}
		
		if (m == Method.max){
			return object > value ? object : value;
		}
		
		if (m == Method.min){
			return object < value ? object : value;
		}
		
		if (m == Method.avg){
			return (object + value) / 2;
		}
		
		return value;
	}
	
	private Double incr(Double object, Double value, Method m) {		
		if (m == Method.sum){
			return object + value;
		}
		
		if (m == Method.max){
			return object > value ? object : value;
		}
		
		if (m == Method.min){
			return object < value ? object : value;
		}
		
		if (m == Method.avg){
			return (object + value) / 2;
		}
		
		return value;
	}

	@Override
	public String key() {
		return key;
	}
}