package com.alogic.cache.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.anysoft.cache.Cacheable;
import com.anysoft.formula.DataProvider;
import com.anysoft.util.BaseException;
import com.anysoft.util.JsonTools;

/**
 * 多field对象
 * 
 * @author duanyy
 * @since 1.6.3.3
 * @version 1.6.4.19 [duanyy 20151218] <br>
 * - 按照SONAR建议修改代码 <br>
 * 
 * @version 1.6.4.23 [duanyy 20160113] <br>
 * - 扩展缓存模型 <br>
 * 
 * @version 1.6.5.31 [duanyy 20160721] <br>
 * - 增加set的exist接口 <br>
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
	
	public String hGet(String id,String field,String dftValue);
	
	public void hSet(String id,String field,String value);
	
	public boolean hExist(String id,String field);
	
	public Map<String,String> hGetAll(String id);

	public int hLen(String id);
	
	public String[] hKeys(String id);
	
	public String[] hValues(String id);
	
	public void sAdd(String id,String...member);
	
	public void sDel(String id,String...member);
	
	public int sSize(String id);
	
	public String[] sMembers(String id);
	
	public boolean sExist(String id,String member);
	
	public void del(String id);	
	
	public void copyTo(MultiFieldObject another);
	
	public long getLastVisitedTime();
	
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
	 * 
	 * @version 1.6.4.43 [20160411 duanyy] <br>
	 * - DataProvider增加获取原始值接口 <br>
	 */
	public static class Default implements MultiFieldObject{
		protected String id;
		protected long lastVisitedTime = System.currentTimeMillis();
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
		public String getRawValue(String varName, Object context, String dftValue) {
			return getValue(varName,context,dftValue);
		}		
		
		@Override
		public Object getContext(String varName) {
			return context;
		}
		
		@Override
		public void setField(String key, String value) {
			lastVisitedTime = System.currentTimeMillis();
			keyvalues.put(key, value);
		}

		@Override
		public String getField(String key, String dftValue) {
			lastVisitedTime = System.currentTimeMillis();
			String found = keyvalues.get(key);
			return found == null || found.length() <= 0 ? dftValue:found;
		}
		
		@Override
		public void del(String id) {
			keyvalues.remove(id);
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

		@Override
		public String hGet(String id, String field, String dftValue) {
			throw new BaseException("core.not_supported","Hash is not suppurted yet.");	
		}

		@Override
		public void hSet(String id, String field, String value) {
			throw new BaseException("core.not_supported","Hash is not suppurted yet.");	
		}

		@Override
		public boolean hExist(String id, String field) {
			throw new BaseException("core.not_supported","Hash is not suppurted yet.");	
		}

		@Override
		public Map<String, String> hGetAll(String id) {
			throw new BaseException("core.not_supported","Hash is not suppurted yet.");	
		}

		@Override
		public int hLen(String id) {
			throw new BaseException("core.not_supported","Hash is not suppurted yet.");	
		}

		@Override
		public String[] hKeys(String id) {
			throw new BaseException("core.not_supported","Hash is not suppurted yet.");	
		}

		@Override
		public String[] hValues(String id) {
			throw new BaseException("core.not_supported","Hash is not suppurted yet.");	
		}

		@Override
		public void sAdd(String id, String... member) {
			throw new BaseException("core.not_supported","Set is not suppurted yet.");	
		}

		@Override
		public void sDel(String id, String... member) {
			throw new BaseException("core.not_supported","Set is not suppurted yet.");	
		}

		@Override
		public int sSize(String id) {
			throw new BaseException("core.not_supported","Set is not suppurted yet.");	
		}
		
		@Override
		public boolean sExist(String id,String member){
			throw new BaseException("core.not_supported","Set is not suppurted yet.");	
		}

		@Override
		public String[] sMembers(String id) {
			throw new BaseException("core.not_supported","Set is not suppurted yet.");	
		}

		@Override
		public void copyTo(MultiFieldObject another) {
			if (another != null){
				Iterator<Entry<String,String>> iter = keyvalues.entrySet().iterator();
				
				while (iter.hasNext()){
					Entry<String,String> keyvalue = iter.next();
					another.setField(keyvalue.getKey(), keyvalue.getValue());
				}
			}
		}

		@Override
		public long getLastVisitedTime() {
			return lastVisitedTime;
		}

	}
	
	public static class Impl implements MultiFieldObject {
		/**
		 * id
		 */
		protected String id;
		
		/**
		 * 上次访问时间
		 */
		protected long lastVisitedTime = System.currentTimeMillis();
		
		/**
		 * 简单的keyvalue
		 */
		protected Map<String,String> stringValues = new HashMap<String,String>(); // NOSONAR
		
		/**
		 * Hash类型的values
		 */
		protected Map<String,Map<String,String>> hashValues = null;
		
		/**
		 * Set类型的values
		 */
		protected Map<String,Set<String>> setValues = null;
		
		protected static Object context = new Object();
		
		public Impl(){
			// nothing to do
		}
		
		public Impl(String pId){
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
			stringValues.clear();
			if (hashValues != null){
				hashValues.clear();
			}
			if (setValues != null){
				setValues.clear();
			}
		}

		@Override
		public void toXML(Element e) {
			// 暂不支持
		}

		@Override
		public void fromXML(Element e) {
			// 暂不支持
		}

		@Override
		public void toJson(Map<String, Object> json) {
			if (json == null){
				return ;
			}

			JsonTools.setString(json, "id", id);
			
			if (stringValues != null){
				json.put("strings", stringValues);
			}
			
			if (hashValues != null){
				json.put("hashs", hashValues);
			}
			
			if (setValues != null){
				Map<String,Object> hashs = new HashMap<String,Object>();
				
				Iterator<Entry<String,Set<String>>> iter = setValues.entrySet().iterator();
				
				while (iter.hasNext()){
					Entry<String,Set<String>> entry = iter.next();
					List<Object> values = new ArrayList<Object>();
					
					Iterator<String> iterator = entry.getValue().iterator();
					while (iterator.hasNext()){
						values.add(iterator.next());
					}
					
					hashs.put(entry.getKey(),values);
				}
			}
		}

		@Override
		public void fromJson(Map<String, Object> json) {
			// 暂不支持
		}

		@Override
		public String getValue(String varName, Object context,
				String defaultValue) {
			String found = stringValues.get(varName);
			return found == null || found.length() <= 0 ? defaultValue:found;
		}
		
		@Override
		public String getRawValue(String varName, Object context, String dftValue) {
			return getValue(varName,context,dftValue);
		}		

		@Override
		public Object getContext(String varName) {
			return context;
		}

		@Override
		public void setField(String key, String value) {
			lastVisitedTime = System.currentTimeMillis();
			stringValues.put(key, value);
		}

		@Override
		public String getField(String key, String dftValue) {
			lastVisitedTime = System.currentTimeMillis();
			String found = stringValues.get(key);
			return found == null || found.length() <= 0 ? dftValue:found;
		}
		

		@Override
		public String[] keys() {
			return stringValues.keySet().toArray(new String[stringValues.size()]);
		}

		@Override
		public int count() {
			return stringValues.size();
		}
				
		@Override
		public String hGet(String id, String field, String dftValue) {
			lastVisitedTime = System.currentTimeMillis();
			if (hashValues == null) 
				return dftValue;
			Map<String,String> found = hashValues.get(id);
			if (found == null){
				return dftValue;
			}
			return found.get(field);
		}

		@Override
		public void hSet(String id, String field, String value) {
			lastVisitedTime = System.currentTimeMillis();
			synchronized (context){
				if (hashValues == null){
					hashValues = new HashMap<String,Map<String,String>>();
				}
				
				Map<String,String> found = hashValues.get(id);
				if (found == null){
					found = new HashMap<String,String>();
					hashValues.put(id, found);
				}
				
				found.put(field, value);
			}
		}

		@Override
		public boolean hExist(String id, String field) {
			lastVisitedTime = System.currentTimeMillis();
			if (hashValues == null) 
				return false;
			Map<String,String> found = hashValues.get(id);
			if (found == null){
				return false;
			}
			return found.containsKey(field);
		}

		@Override
		public Map<String, String> hGetAll(String id) {
			lastVisitedTime = System.currentTimeMillis();
			if (hashValues == null) 
				return null;
			return hashValues.get(id);
		}

		@Override
		public int hLen(String id) {
			lastVisitedTime = System.currentTimeMillis();
			if (hashValues == null) 
				return 0;
			Map<String,String> found = hashValues.get(id);
			if (found == null){
				return 0;
			}
			return found.size();
		}

		@Override
		public String[] hKeys(String id) {
			lastVisitedTime = System.currentTimeMillis();
			if (hashValues == null) 
				return null;
			Map<String,String> found = hashValues.get(id);
			if (found == null){
				return null;
			}
			return found.keySet().toArray(new String[found.size()]);
		}

		@Override
		public String[] hValues(String id) {
			lastVisitedTime = System.currentTimeMillis();
			if (hashValues == null) 
				return null;
			Map<String,String> found = hashValues.get(id);
			if (found == null){
				return null;
			}
			return found.values().toArray(new String[found.size()]);
		}

		@Override
		public void sAdd(String id, String... member) {
			lastVisitedTime = System.currentTimeMillis();
			synchronized (context){
				if (setValues == null){
					setValues = new HashMap<String,Set<String>>();
				}
				
				Set<String> found = setValues.get(id);
				if (found == null){
					found = new HashSet<String>();
				}
				
				for (String m:member){
					found.add(m);
				}
			}
		}

		@Override
		public void sDel(String id, String... member) {
			lastVisitedTime = System.currentTimeMillis();
			synchronized (context){
				if (setValues == null){
					setValues = new HashMap<String,Set<String>>();
				}
				
				Set<String> found = setValues.get(id);
				if (found != null){
					for (String m:member){
						found.remove(m);
					}
				}
			}
		}

		@Override
		public int sSize(String id) {
			lastVisitedTime = System.currentTimeMillis();
			if (setValues == null){
				return 0;
			}
			
			Set<String> found = setValues.get(id);
			return found == null ? 0 : found.size();
		}

		@Override
		public String[] sMembers(String id) {
			lastVisitedTime = System.currentTimeMillis();
			if (setValues == null){
				return null;
			}
			
			Set<String> found = setValues.get(id);
			if (found == null){
				return null;
			}
			
			return found.toArray(new String[found.size()]);
		}
		
		@Override
		public boolean sExist(String id, String member) {
			lastVisitedTime = System.currentTimeMillis();
			if (setValues == null){
				return false;
			}
			
			Set<String> found = setValues.get(id);
			if (found == null){
				return false;
			}
			return found.contains(member);
		}		

		@Override
		public void del(String id) {
			stringValues.remove(id);
		}

		@Override
		public void copyTo(MultiFieldObject another) {
			if (another != null){
				if (stringValues != null){
					Iterator<Entry<String,String>> iter = stringValues.entrySet().iterator();
					
					while (iter.hasNext()){
						Entry<String,String> keyvalue = iter.next();
						another.setField(keyvalue.getKey(), keyvalue.getValue());
					}				
				}
				if (hashValues != null){
					Iterator<Entry<String,Map<String,String>>> iter = hashValues.entrySet().iterator();
					
					while (iter.hasNext()){
						Entry<String,Map<String,String>> entry = iter.next();
						
						String id = entry.getKey();
						Map<String,String> values = entry.getValue();
						
						Iterator<Entry<String,String>> iterator = values.entrySet().iterator();
						while (iterator.hasNext()){
							Entry<String,String> value = iterator.next();
							another.hSet(id, value.getKey(), value.getValue());
						}
					}
				}
				if (setValues != null){
					Iterator<Entry<String,Set<String>>> iter = setValues.entrySet().iterator();
					
					while (iter.hasNext()){
						Entry<String,Set<String>> entry = iter.next();
						String id = entry.getKey();
						Set<String> values = entry.getValue();
						another.sAdd(id, values.toArray(new String[values.size()]));
					}
				}
			}
		}

		@Override
		public long getLastVisitedTime() {
			return lastVisitedTime;
		}
	}	
}
