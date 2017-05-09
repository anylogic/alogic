package com.alogic.xscript.doc.json;

import java.util.List;

import com.alogic.xscript.doc.XsPrimitive;
import com.alogic.xscript.doc.XsPrimitiveArray;

/**
 * 基于Json的XsPrimitiveArray
 * @author yyduan
 * @since 1.6.8.14
 */
public class JsonPrimitiveArray implements XsPrimitiveArray {
	protected List<Object> content = null;
	
	public JsonPrimitiveArray(List<Object> content) {
		this.content = content;
	}

	@Override
	public int getElementCount() {
		return this.content.size();
	}

	@Override
	public XsPrimitive get(int index) {
		return new JsonPrimitive(this.content.get(index));
	}

	@Override
	public void add(Number value) {
		this.content.add(value);
	}

	@Override
	public void add(String value) {
		this.content.add(value);
	}

	@Override
	public void add(Boolean value) {
		this.content.add(value);
	}

}
