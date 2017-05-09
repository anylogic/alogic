package com.alogic.xscript.doc;

/**
 * Primitive
 * @author yyduan
 * @since 1.6.8.14
 */
public interface XsPrimitive extends XsElement{
	
	public String getAsString();
	
	public boolean getAsBoolean(boolean dft);
		
	public int getAsInt(int dft);
	
	public long getAsLong(long dft);
	
	public float getAsFloat(float dft);
	
	public double getAsDouble(double dft);
}
