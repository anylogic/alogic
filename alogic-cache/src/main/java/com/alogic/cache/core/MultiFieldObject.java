package com.alogic.cache.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.anysoft.cache.Cacheable;
import com.anysoft.formula.DataProvider;
import com.anysoft.util.JsonTools;

/**
 * 多field对象
 * 
 * @author duanyy
 * @since 1.6.3.3
 * @version 1.6.4.19 [duanyy 20151218] <br>
 * - 按照SONAR建议修改代码 <br>
 */
public interface MultiFieldObject extends Cacheable,DataProvider {
	/**
	 * 设置field
	 * @param key key of the field
	 * @param value value of the field
	 */
	public void setField(String key,String value);
	
	/**
	 * 获取field的值，当field不存在或为空时，返回dftValue
	 * @param key
	 * @param dftValue
	 * @return field的值
	 */
	public String getField(String key,String dftValue);
	
	/**
	 * 获取所有的field的key
	 * @return keys
	 */
	public String [] keys();
	
	/**
	 * 获取field的个数
	 * @return field的个数
	 */
	public int count();
	
	/**
	 * 缺省实现
	 * @author duanyy
	 * @version 1.6.3.24 [20150526 duanyy] <br>
	 * - 现在可以通过构造函数来设置id <br>
	 */
	public static class Default implements MultiFieldObject{
		protected String id;
		protected Map<String,String> keyvalues = new HashMap<String,String>(); // NOSONAR
		protected static Object context = new Object();
		
		public Default(){
			// nothing to do
		}
		
		public Default(String pId){
			id = pId;
		}
		
		@Override
		public String getId() {
			return id;
		}

		@Override
		public boolean isExpired() {
			return false;
		}

		@Override
		public void expire() {
			keyvalues.clear();
		}

		@Override
		public void toXML(Element e) {
			if (e != null){
				e.setAttribute("id", id);
				
				Iterator<Entry<String,String>> iter = keyvalues.entrySet().iterator();
				
				while (iter.hasNext()){
					Entry<String,String> keyvalue = iter.next();
					e.setAttribute(keyvalue.getKey(),keyvalue.getValue());
				}
			}
		}

		@Override
		public void fromXML(Element e) {
			if (e == null) {
				return;
			}
			id = e.getAttribute("id");

			NamedNodeMap attrs = e.getAttributes();

			for (int i = 0; i < attrs.getLength(); i++) {
				Node node = attrs.item(i);
				if (Node.ATTRIBUTE_NODE == node.getNodeType()) {
					Attr attr = (Attr) node;
					if ("id".equals(attr.getNodeName())) {
						continue;
					}

					keyvalues.put(attr.getNodeName(), attr.getNodeValue());
				}
			}
		}
		
		@Override
		public void toJson(Map<String, Object> json) {
			if (json != null){
				JsonTools.setString(json, "id", id);
				
				Iterator<Entry<String,String>> iter = keyvalues.entrySet().iterator();
				
				while (iter.hasNext()){
					Entry<String,String> keyvalue = iter.next();
					json.put(keyvalue.getKey(), keyvalue.getValue());
				}
			}
		}
		
		@Override
		public void fromJson(Map<String, Object> json) {
			if (json != null){
				id = JsonTools.getString(json, "id", "");
				
				Iterator<Entry<String,Object>> iter = json.entrySet().iterator();
				
				while (iter.hasNext()){
					Entry<String,Object> keyvalue = iter.next();
					keyvalues.put(keyvalue.getKey(), keyvalue.getValue().toString());
				}
			}
		}
		
		@Override
		public String getValue(String varName, Object context,
				String defaultValue) {
			String found = keyvalues.get(varName);
			return found == null || found.length() <= 0 ? defaultValue:found;
		}

		@Override
		public Object getContext(String varName) {
			return context;
		}
		
		@Override
		public void setField(String key, String value) {
			keyvalues.put(key, value);
		}

		@Override
		public String getField(String key, String dftValue) {
			String found = keyvalues.get(key);
			return found == null || found.length() <= 0 ? dftValue:found;
		}
		@Override
		public String[] keys() {
			return keyvalues.keySet().toArray(new String[keyvalues.size()]);
		}

		@Override
		public int count() {
			return keyvalues.size();
		}
		
		@Override
		public String toString(){
			StringBuilder value = new StringBuilder(id);
			
			Iterator<Entry<String,String>> iter = keyvalues.entrySet().iterator();
			
			value.append("(");
			while (iter.hasNext()){
				Entry<String,String> keyvalue = iter.next();
				value.append(keyvalue.getKey() + "=" + keyvalue.getValue());
				if (iter.hasNext()){
					value.append(";");
				}
			}
			value.append(")");
			return value.toString();
		}
	}
}
