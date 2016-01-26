package com.anysoft.metrics.core;

import java.util.Map;

import org.w3c.dom.Element;

/**
 * Measures的简单实现
 * 
 * @author duanyy
 * @since 1.2.8
 * 
 * @version 1.6.4.29 [20160126 duanyy] <br>
 * - 指标序列化的间隔符修改为$
 */
public class SimpleMeasures implements Measures {

	
	public void toXML(Element e) {
		if (e != null){
			String _values = toString();
			e.setAttribute("v", _values);
			e.setAttribute("m", method.name());
		}
	}

	
	public void fromXML(Element e) {
		if (e != null){
			String _m = e.getAttribute("m");
			if (_m != null && _m.length() > 0){
				method = Method.valueOf(_m);
			}
			
			String _v = e.getAttribute("v");
			if (_v != null && _v.length() > 0){
				String [] _values = _v.split("[$]");
				values = new Object[_values.length];
				for (int i = 0 ;i < _values.length ; i ++){
					values[i] = parse(_values[i]);
				}
			}
		}
	}

	
	public void toJson(Map<String, Object> json) {
		if (json != null){
			String _values = toString();
			json.put("v", _values);
			
			json.put("m", method.name());
		}
	}

	
	public void fromJson(Map<String, Object> json) {
		if (json != null){
			Object _m = json.get("m");
			if (_m != null && _m instanceof String){
				String m = (String)_m;
				method = Method.valueOf(m);
			}
			
			Object _v = json.get("v");
			if (_v != null && _v instanceof String){
				String v = (String)_v;
				String [] _values = v.split("[$]");
				values = new Object[_values.length];
				for (int i = 0 ;i < _values.length ; i ++){
					values[i] = parse(_values[i]);
				}
			}
		}
	}

	
	public Measures incr(Measures other) {
		if (other != null && values != null && other.values() != null){
			int length = values.length;
			
			for (int i = 0 ;i < length ; i ++){
				Object o = values[i];
				
				if (o != null){
					if (o instanceof String){
						String value = other.asString(i);
						if (value != null){
							values[i] = value;
						}
					}else{
						if (o instanceof Long){
							Long value = other.asLong(i);
							if (value != null){
								values[i] = incr((Long)values[i],value,other.method());
							}
						}else{
							if (o instanceof Double){
								Double value = other.asDouble(i);
								if (value != null){
									values[i] = incr((Double)values[i],value,other.method());
								}
							}
						}
					}
				}
			}
		}
		return this;
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

	
	public Method method() {
		return method;
	}

	
	public Measures method(Method _method) {
		method = _method;
		return this;
	}

	protected Method method = Method.sum;
	
	
	public Measures lpush(Object[] _values) {
		values = combine(values,_values);		
		return this;
	}

	
	public Measures rpush(Object[] _values) {
		values = combine(_values,values);
		return this;
	}
	
	
	private Object [] combine(Object [] left,Object [] right){
		if (left == null || left.length <= 0){
			return right;
		}
		if (right == null || right.length <= 0){
			return left;
		}
		
		Object [] result = new Object[left.length + right.length];
		
		for (int i = 0 ; i < left.length ; i ++){
			result[i] = left[i];
		}
		
		int offset = left.length;
		for (int i = 0 ; i < right.length ; i ++){
			result[offset + i] = right[i];
		}
		
		return result;
	}
	
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		
		if (values != null){
			for (int i = 0 ; i < values.length ; i ++){
				if (i != 0){
					buffer.append("$");
				}
				if (values[i] == null){
					buffer.append('N');
				}else{
					buffer.append(type(i)).append(values[i]);
				}
			}
		}
		
		return buffer.toString();
	}

	
	public int count() {
		return values.length;
	}

	
	public char[] types() {
		if (values == null){
			return new char[0];
		}
		
		char [] result = new char[values.length];
		
		for (int i = 0 ;i < result.length ; i ++){
			result[i] = type(i);
		}
		
		return result;
	}

	
	public String[] values() {
		if (values == null){
			return new String[0];
		}
		
		String [] result = new String[values.length];
		
		for (int i = 0 ;i < result.length ; i ++){
			result[i] = values[i] == null ? null : values[i].toString();
		}
		
		return result;
	}

	
	public Long asLong(int idx) {
		Object found = get(idx);
		
		if (found != null && found instanceof Long){
			return (Long)found;
		}
		return null;
	}

	
	public Double asDouble(int idx) {
		Object found = get(idx);
		
		if (found != null && found instanceof Double){
			return (Double)found;
		}
		
		return null;
	}

	
	public String asString(int idx) {
		Object found = get(idx);
		
		if (found != null && found instanceof String){
			return (String)found;
		}
		
		return null;
	}

	
	public char type(int idx) {
		return type(get(idx));
	}	

	private Object parse(String buffer){
		if (buffer == null || buffer.length() <= 1){
			return null;
		}
		
		char type = buffer.charAt(0);
		String value = buffer.substring(1);
		Object result = null;
		switch (type){
			case 'L':
				try {
					result = Long.parseLong(value);
				}catch (Exception ex){
					result = new Long(0);
				}
				break;
			case 'D':
				try {
					result = Double.parseDouble(value);
				}catch (Exception ex){
					result = new Double(0.0);
				}
				break;
			case 'S':
				result = value;
				break;
			default:
				result = null;
		}
		return result;
	}
	
	
	public Object get(int idx){
		if (values == null || idx < 0 || idx >= values.length){
			return null;
		}
		return values[idx];
	}	
	
	private char type(Object o){
		char type = 'N';
		if (o != null){
			if (o instanceof Long){
				type = 'L';
			}else{
				if (o instanceof Double){
					type = 'D';
				}else{
					if (o instanceof String){
						type = 'S';
					}
				}
			}
		}
		return type;
	}
	
	protected Object[] values = null;
}
