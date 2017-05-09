package com.alogic.xscript.doc.json;

import org.apache.commons.lang3.BooleanUtils;

import com.alogic.xscript.doc.XsPrimitive;

/**
 * 基于Json的XsPrimitive
 * @author yyduan
 * @since 1.6.8.14
 */
public class JsonPrimitive implements XsPrimitive {
	protected Object value = null;
	
	public JsonPrimitive(Object value) {
		this.value = value;
	}

	@Override
	public Object getContent() {
		return value;
	}
	
	@Override
	public String getAsString(){
		return value.toString();
	}
	
	@Override
	public boolean getAsBoolean(boolean dft) {
		if (value instanceof String){
			return BooleanUtils.toBoolean((String)value);
		}
		
		if (value instanceof Boolean){
			return (Boolean)value;
		}
		
		return dft;
	}

	@Override
	public int getAsInt(int dft) {
		if (value instanceof Number){
			return ((Number)value).intValue();
		}
		
		if (value instanceof String){
			String v = (String)value;
			try{
				return Integer.parseInt(v);
			}catch (NumberFormatException ex){
				return dft;
			}
		}
		
		return dft;
	}

	@Override
	public long getAsLong(long dft) {
		if (value instanceof Number){
			return ((Number)value).longValue();
		}
		
		if (value instanceof String){
			String v = (String)value;
			try{
				return Long.parseLong(v);
			}catch (NumberFormatException ex){
				return dft;
			}
		}
		
		return dft;
	}

	@Override
	public float getAsFloat(float dft) {
		if (value instanceof Number){
			return ((Number)value).floatValue();
		}
		
		if (value instanceof String){
			String v = (String)value;
			try{
				return Float.parseFloat(v);
			}catch (NumberFormatException ex){
				return dft;
			}
		}
		
		return dft;
	}

	@Override
	public double getAsDouble(double dft) {
		if (value instanceof Number){
			return ((Number)value).doubleValue();
		}
		
		if (value instanceof String){
			String v = (String)value;
			try{
				return Double.parseDouble(v);
			}catch (NumberFormatException ex){
				return dft;
			}
		}
		
		return dft;
	}

}
