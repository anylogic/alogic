package com.anysoft.metrics.core;

import java.util.Map;

import org.w3c.dom.Element;

/**
 * 简单的维度实现
 * <br>
 * 用于客户端做指标的采集
 * 
 * @author duanyy
 * 
 * @since 1.2.8
 *
 */
public class SimpleDimensions implements Dimensions {
	protected String [] dims = null;
	
	public SimpleDimensions(){
		
	}
	
	public SimpleDimensions(String[] _dims) {
		rpush(_dims);
	}

	
	public void toXML(Element e) {
		if (e != null){
			e.setAttribute("d",toString());
		}
	}

	
	public void fromXML(Element e) {
		if (e != null){
			String _dims = e.getAttribute("d");
			if (_dims != null && _dims.length() > 0){
				dims = _dims.split("[:]");
			}
		}
	}

	
	public void toJson(Map<String, Object> json) {
		if (json != null){
			json.put("d", toString());
		}
	}

	
	public void fromJson(Map<String, Object> json) {
		if (json != null){
			Object _dims = (Object) json.get("d");
			if (_dims != null && _dims instanceof String){
				dims = ((String)_dims).split("[:]");
			}
		}
	}

	
	public int count() {
		return dims.length;
	}

	
	public String toString(){
		
		StringBuffer buffer = new StringBuffer();
		
		if (dims != null){
			for (int i = 0 ; i < dims.length ; i ++){
				if (i != 0){
					buffer.append(":");
				}
				buffer.append(dims[i]);
			}
		}
		
		return buffer.toString();
	}
	
	
	public Dimensions lpush(String... _dims) {
		dims = combine(_dims,dims);
		return this;
	}

	
	public Dimensions rpush(String... _dims) {
		dims = combine(dims,_dims);
		return this;
	}
	
	private String [] combine(String [] left,String [] right){
		if (left == null || left.length <= 0){
			return right;
		}
		if (right == null || right.length <= 0){
			return left;
		}
		
		String [] result = new String[left.length + right.length];
		
		for (int i = 0 ; i < left.length ; i ++){
			result[i] = left[i];
		}
		
		int offset = left.length;
		for (int i = 0 ; i < right.length ; i ++){
			result[offset + i] = right[i];
		}
		
		return result;
	}

	
	public String get(int idx) {
		if (dims == null || idx < 0 || idx >= dims.length){
			return null;
		}
		
		return dims[idx];
	}

	
	public String[] get(int start, int count) {
		if (dims == null || start < 0 || start >= dims.length){
			return new String[0];
		}
		
		int end = ((start + count) >= dims.length) ? dims.length - 1 : start + count;
		String [] result = new String[end - start];
		
		for (int i = 0 ;i < result.length ; i ++){
			result[i] = dims[start + i];
		}
		
		return result;
	}

	
	public Dimensions sub(int start, int count) {
		String [] _dims = get(start,count);
		return new SimpleDimensions(_dims);
	}

	
	public String[] get() {
		if (dims == null) return new String[0];
		return dims;
	}

}
