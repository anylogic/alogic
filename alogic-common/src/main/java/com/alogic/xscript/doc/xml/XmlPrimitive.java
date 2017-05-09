package com.alogic.xscript.doc.xml;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.alogic.xscript.doc.XsPrimitive;

/**
 * 基于xml的XsPrimitive
 * 
 * @author yyduan
 * @since 1.6.8.14
 */
public class XmlPrimitive implements XsPrimitive{
	protected Element content = null;
	
	public XmlPrimitive(Element content){
		this.content = content;
	}
	
	@Override
	public Object getContent() {
		return this.content;
	}

	@Override
	public String getAsString() {
		Node node = content.getFirstChild();
		if (node == null || node.getNodeType() != Node.TEXT_NODE){
			return null;
		}
		return node.getNodeValue();		
	}

	@Override
	public boolean getAsBoolean(boolean dft) {
		String found = getAsString();
		
		if (StringUtils.isEmpty(found)){
			return dft;
		}
		
		try {
			return BooleanUtils.toBoolean(found);
		}catch (NumberFormatException ex){
			return dft;
		}
	}

	@Override
	public int getAsInt(int dft) {
		String found = getAsString();
		
		if (StringUtils.isEmpty(found)){
			return dft;
		}
		
		try {
			return Integer.parseInt(found);
		}catch (NumberFormatException ex){
			return dft;
		}
	}

	@Override
	public long getAsLong(long dft) {
		String found = getAsString();
		
		if (StringUtils.isEmpty(found)){
			return dft;
		}
		
		try {
			return Long.parseLong(found);
		}catch (NumberFormatException ex){
			return dft;
		}
	}

	@Override
	public float getAsFloat(float dft) {
		String found = getAsString();
		
		if (StringUtils.isEmpty(found)){
			return dft;
		}
		
		try {
			return Float.parseFloat(found);
		}catch (NumberFormatException ex){
			return dft;
		}
	}

	@Override
	public double getAsDouble(double dft) {
		String found = getAsString();
		
		if (StringUtils.isEmpty(found)){
			return dft;
		}
		
		try {
			return Double.parseDouble(found);
		}catch (NumberFormatException ex){
			return dft;
		}
	}

}
