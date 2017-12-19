package com.alogic.event;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.anysoft.stream.Flowable;
import com.anysoft.util.JsonSerializer;
import com.anysoft.util.JsonTools;

/**
 * 通用事件
 * 
 * @author yyduan
 * 
 * @version 1.6.11.3 [20171219 duanyy] <br>
 * - 增加isAsync方法，用来标记数据是否允许异步处理 <br>
 * 
 */
public interface Event extends Comparable<Event>,Flowable,JsonSerializer{
	
	/**
	 * 获取事件类型
	 * 
	 * @return 事件类型
	 */
	public String getEventType();
	
	/**
	 * 获取事件产生的时间
	 * 
	 * @return 生成时间
	 */
	public long getCreateTime();
	
	/**
	 * 设置事件属性
	 * 
	 * <p>
	 * 用于设置事件的属性值。当该属性已经存在时，如果overwrite为true,则更覆盖，反之放弃本次修改。
	 * 
	 * @param k 属性的key
	 * @param v 属性的值
	 * @param overwrite 是否覆盖
	 */
	public void setProperty(String k,String v,boolean overwrite);
	
	/**
	 * 获取事件的属性
	 * @param k 属性的key
	 * @param dftValue 缺省值
	 * @return 属性的值，当属性不存在时，返回为dftValue
	 */
	public String getProperty(String k,String dftValue);
	
	/**
	 * 清除指定的属性
	 * @param key 属性的key
	 */
	public void removeProperty(String key);
	
	/**
	 * 获取属性集的keys列表
	 * @return keys列表
	 */
	public String [] getKeys();
	
	/**
	 * clone自身到另外一个对象
	 * @return clone出的对象
	 */
	public Event copySelf();
	
	/**
	 * 拷贝自身到另外一个对象
	 * <p>
	 * 用于将自身的信息复制到另外一个对象，仅copy属性
	 * @param another 另外一个对象
	 * 
	 * @return another
	 */
	public Event copyTo(Event another);
	
	/**
	 * 虚基类
	 * @author yyduan
	 *
	 */
	public abstract static class Abstract implements Event{
		/**
		 * 事件id,全局唯一
		 */
		protected String id;
		
		/**
		 * 事件类型
		 */
		protected String eventType;
		
		/**
		 * 是否可异步处理
		 */
		protected boolean async = true;
		
		/**
		 * 生成日期
		 */
		protected long createTime = System.currentTimeMillis();
		
		protected Abstract(String id,String type,boolean async){
			this.id = id;
			this.eventType = type;
			this.async = async;
		}
		
		@Override
		public int compareTo(Event e) {
			return id().compareTo(e.id());
		}

		@Override
		public String getStatsDimesion() {
			return getEventType();
		}

		@Override
		public String id() {
			return id;
		}
		
		@Override
		public boolean isAsync(){
			return async;
		}
		
		public void setId(final String id){
			this.id = id;
		}
		
		@Override
		public String getEventType() {
			return eventType;
		}

		public void setEventType(String type){
			this.eventType = type;
		}
		
		@Override
		public long getCreateTime() {
			return createTime;
		}

		public void setCreateTime(long t){
			this.createTime = t;
		}

		@Override
		public String getValue(String varName, Object context,
				String defaultValue) {
			return getRawValue(varName, context, defaultValue);
		}

		@Override
		public String getRawValue(String varName, Object context,
				String dftValue) {
			return getProperty(varName, dftValue);
		}

		@Override
		public Object getContext(String varName) {
			return this;
		}
	}
	
	/**
	 * 缺省实现
	 * @author yyduan
	 *
	 */
	public static class Default extends Abstract{
		protected Map<String,String> properties = null;
		
		public Default(String id, String type,boolean async) {
			super(id, type,async);
		}

		protected Map<String,String> getMapObject(boolean create){
			if (properties == null){
				synchronized(this){
					if (properties == null && create){
						properties = new HashMap<String,String>(); 
					}
				}
			}
			
			return properties;
		}
		
		@Override
		public void setProperty(String k, String v, boolean overwrite) {
			Map<String,String> map = getMapObject(true);
			
			if (map.containsKey(k)){
				if (overwrite){
					map.put(k, v);
				}
			}else{
				map.put(k, v);
			}
		}

		@Override
		public String getProperty(String k, String dftValue) {
			Map<String,String> map = getMapObject(false);
			if (map != null){
				String value = map.get(k);
				return StringUtils.isNotEmpty(value) ? value : dftValue;
			}else{
				return dftValue;
			}
		}
		
		@Override
		public void removeProperty(String key){
			Map<String,String> map = getMapObject(false);
			if (map != null){
				map.remove(key);
			}
		}

		@Override
		public void toJson(Map<String, Object> json) {
			if (json != null){
				JsonTools.setString(json, "id", id());
				JsonTools.setString(json, "type", getEventType());
				JsonTools.setLong(json, "t", getCreateTime());
				
				Map<String,String> map = getMapObject(false);
				
				if (map != null){
					Iterator<Entry<String,String>> iter = map.entrySet().iterator();
					Map<String,Object> propertiesMap = new HashMap<String,Object>();
					
					while (iter.hasNext()){
						Entry<String,String> entry = iter.next();
						JsonTools.setString(propertiesMap, entry.getKey(), entry.getValue());
					}
					
					json.put("property", propertiesMap);
				}
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public void fromJson(Map<String, Object> json) {
			if (json != null){
				setId(JsonTools.getString(json, "id", EventBus.newId()));
				setEventType(JsonTools.getString(json, "type", ""));
				setCreateTime(JsonTools.getLong(json, "t", System.currentTimeMillis()));
				
				Object found = json.get("property");
				if (found != null && found instanceof Map){
					Map<String,String> map = (Map<String,String>)found;
					Iterator<Entry<String,String>> iter = map.entrySet().iterator();
					while (iter.hasNext()){
						Entry<String,String> entry = iter.next();
						setProperty(entry.getKey(), entry.getValue(), true);
					}
				}
			}
		}
		
		@Override
		public String [] getKeys(){
			Map<String,String> map = getMapObject(false);
			return map == null ? new String[0] : map.keySet().toArray(new String[0]);
		}
		
		@Override
		public Event copySelf(){
			Default copied = new Default(id(),getEventType(),this.isAsync());
			copied.setCreateTime(getCreateTime());
			return copyTo(copied);
		}
		
		@Override
		public Event copyTo(Event another){
			if (another != null){
				Map<String,String> map = getMapObject(false);
				
				if (map != null){
					Iterator<Entry<String,String>> iter = map.entrySet().iterator();
					
					while (iter.hasNext()){
						Entry<String,String> entry = iter.next();
						another.setProperty(entry.getKey(), entry.getValue(), true);
					}
				}
			}
			return another;
		}
		
		@Override
		public String toString(){
			StringBuffer buf = new StringBuffer(String.format("%s-%s-%d-[", id(),getEventType(),getCreateTime()));
			
			Map<String,String> map = getMapObject(false);
			
			if (map != null){
				Iterator<Entry<String,String>> iter = map.entrySet().iterator();
				
				while (iter.hasNext()){
					Entry<String,String> entry = iter.next();
					buf.append(String.format("%s=%s", entry.getKey(),entry.getValue()));
					if (iter.hasNext()){
						buf.append(";");
					}
				}
			}
			
			buf.append("]");
			return buf.toString();
		}
	}
}
