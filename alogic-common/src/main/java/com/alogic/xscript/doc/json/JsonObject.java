package com.alogic.xscript.doc.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;

import com.alogic.xscript.doc.XsArray;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.XsPrimitiveArray;
import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.JsonProviderFactory;

/**
 * 基于Json的XsObject
 * @author yyduan
 * @since 1.6.8.14
 * 
 * @version 1.6.9.3 [20170615 duanyy] <br>
 * - 增加判断文档是否为空的方法 <br>
 */
public class JsonObject implements XsObject {

	/**
	 * content
	 */
	protected Map<String,Object> content;
	protected String tag;
	
	public JsonObject(String tag,Map<String,Object> content) {
		this.content = content;
		this.tag = tag;
	}

	@Override
	public boolean isNull() {
		return content == null || content.isEmpty();
	}
	
	@Override
	public String getTag() {
		return tag;
	}
	
	@Override
	public Object getContent() {
		return this.content;
	}

	@Override
	public void addProperty(String name, String value) {
		this.content.put(name, value);
	}

	@Override
	public void addProperty(String name, Number value) {
		this.content.put(name, value);
	}

	@Override
	public void addProperty(String name, boolean value) {
		this.content.put(name, value);
	}

	@Override
	public boolean remove(String name) {
		return this.content.remove(name) != null;
	}
	
	@Override
	public boolean hasProperty(String name) {
		return this.content.containsKey(name);
	}

	@Override
	public String getProperty(String name, String dft) {
		Object found = this.content.get(name);
		if (found == null){
			return dft;
		}
		
		if (found instanceof String){
			return (String)found;
		}
		
		return found.toString();
	}

	@Override
	public long getProperty(String name, long dft) {
		Object found = this.content.get(name);
		if (found == null){
			return dft;
		}
		
		if (found instanceof Number){
			Number v = (Number)found;
			return v.longValue();
		}
		
		if (found instanceof String){
			String v = (String)found;
			try {
				return Long.parseLong(v);
			}catch (NumberFormatException ex){
				return dft;
			}
		}
		
		return dft;
	}

	@Override
	public int getProperty(String name, int dft) {
		Object found = this.content.get(name);
		if (found == null){
			return dft;
		}
		
		if (found instanceof Number){
			Number v = (Number)found;
			return v.intValue();
		}
		
		if (found instanceof String){
			String v = (String)found;
			try {
				return Integer.parseInt(v);
			}catch (NumberFormatException ex){
				return dft;
			}
		}
		
		return dft;
	}

	@Override
	public boolean getProperty(String name, boolean dft) {
		Object found = this.content.get(name);
		if (found == null){
			return dft;
		}
		
		if (found instanceof Boolean){
			return (Boolean)found;
		}
		
		if (found instanceof String){
			String v = (String)found;
			return BooleanUtils.toBoolean(v);
		}
		
		return dft;
	}

	@Override
	public float getProperty(String name, float dft) {
		Object found = this.content.get(name);
		if (found == null){
			return dft;
		}
		
		if (found instanceof Number){
			Number v = (Number)found;
			return v.intValue();
		}
		
		if (found instanceof String){
			String v = (String)found;
			try {
				return Float.parseFloat(v);
			}catch (NumberFormatException ex){
				return dft;
			}
		}
		
		return dft;
	}

	@Override
	public double getProperty(String name, double dft) {
		Object found = this.content.get(name);
		if (found == null){
			return dft;
		}
		
		if (found instanceof Number){
			Number v = (Number)found;
			return v.doubleValue();
		}
		
		if (found instanceof String){
			String v = (String)found;
			try {
				return Double.parseDouble(v);
			}catch (NumberFormatException ex){
				return dft;
			}
		}
		
		return dft;
	}

	@SuppressWarnings("unchecked")
	@Override
	public XsArray getArrayChild(String name,boolean create) {
		Object found = content.get(name);
		if (found == null){
			if (create){
				found = new ArrayList<Object>();
				content.put(name, found);
			}else{
				return null;
			}
		}
		
		if (!(found instanceof List)){
			return null;
		}
		
		return new JsonArray((List<Object>) found);
	}

	@SuppressWarnings("unchecked")
	@Override
	public XsObject getObjectChild(String name,boolean create) {
		Object found = content.get(name);
		if (found == null){
			if (create){
				found = new HashMap<String,Object>();
				content.put(name, found);
			}else{
				return null;
			}
		}
		
		if (!(found instanceof Map)){
			return null;
		}
		
		return new JsonObject(name,(Map<String, Object>) found);
	}

	@SuppressWarnings("unchecked")
	@Override
	public XsPrimitiveArray getPrimitiveArrayChild(String name, boolean create) {
		Object found = content.get(name);
		if (found == null){
			if (create){
				found = new ArrayList<Object>();
				content.put(name, found);
			}else{
				return null;
			}
		}
		
		if (!(found instanceof List)){
			return null;
		}
		
		return new JsonPrimitiveArray((List<Object>) found);
	}
	
	public static void main(String[] args){
		XsObject doc = new JsonObject("root",new HashMap<String,Object>());
		
		doc.addProperty("id", "alogic");
		doc.addProperty("name", "eason");
		
		XsObject child = doc.getObjectChild("child", true);
		child.addProperty("id", "child");
		child.addProperty("name", "ddd");
		
		XsArray array = doc.getArrayChild("array", true);
		
		XsObject item = array.newObject();
		item.addProperty("id", "ddd");
		array.add(item);
		
		item = array.newObject();
		item.addProperty("id", "dddd");
		array.add(item);
		
		XsPrimitiveArray array2 = doc.getPrimitiveArrayChild("array2", true);
		array2.add("dddd");
		array2.add(1000);
		
		JsonProvider provider = JsonProviderFactory.createProvider();
		System.out.println(provider.toJson(doc.getContent()));
	}

}
