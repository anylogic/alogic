package com.alogic.timer.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.util.DefaultProperties;
import com.anysoft.util.JsonSerializer;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Reportable;
import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.JsonProviderFactory;

/**
 * 任务
 * 
 * @author duanyy
 * @since 1.6.3.37
 * 
 * @version 1.6.9.2 [20170601 duanyy] <br>
 * - 改造TaskCenter模型，以便提供分布式任务处理支持; <br>
 */
public interface Task extends JsonSerializer,Reportable{
	
	/**
	 * 任务状态
	 * 
	 * @author duanyy
	 *
	 */
	public static enum State{
		/**
		 * 新建
		 */
		New,
		/**
		 * 已进入队列
		 */
		Queued,
		/**
		 * 已从队列中领取
		 */
		Polled,
		/**
		 * 正在运行
		 */
		Running,
		/**
		 * 运行失败
		 */
		Failed,
		/**
		 * 已完成
		 */
		Done
	}
	
	/**
	 * 获取任务ID
	 * @return id
	 */
	public String id();
	
	/**
	 * 获取事件id
	 * @return 事件id
	 */
	public String getEventId();
		
	/**
	 * 任务参数
	 * @return 任务参数
	 */
	public DefaultProperties getParameters();
	
	/**
	 * 从json形式的string中解析任务
	 * @param jsonString json形式的string
	 */
	public void fromJsonString(String jsonString);
		
	/**
	 * 生成json形式的string
	 * @return json形式的string
	 */
	public String toJsonString();	
	
	/**
	 * 缺省实现
	 * 
	 * @author duanyy
	 *
	 */
	public static class Default implements Task{
		/**
		 * id
		 */
		protected String id;
		
		/**
		 * event
		 */
		protected String event;
		
		/**
		 * 属性
		 */
		protected DefaultProperties p;
		/**
		 * Json provider
		 */
		protected static JsonProvider provider = JsonProviderFactory.createProvider();
		
		public Default(String id,String event){
			this(id,event,(DefaultProperties)null);
		}
		
		public Default(String id,String event,Map<String,String> _p){
			this.id = id;
			this.event = event;
			this.p = new DefaultProperties();
			if (_p != null){
				Iterator<Entry<String,String>> iter = _p.entrySet().iterator();
				
				while (iter.hasNext()){
					Entry<String,String> entry = iter.next();
					p.SetValue(entry.getKey(), entry.getValue());
				}
			}
		}
		
		public Default(String id,String event,DefaultProperties p){
			this.id = id;
			this.event = event;
			this.p = p;
		}
		
		public Default(Map<String,Object> json){
			fromJson(json);
		}
		
		@Override
		public String getEventId() {
			return event;
		}
		
		@Override
		public void toJson(Map<String, Object> json) {
			if (json != null){
				json.put("id", id);
				json.put("event", event);
				Map<String,Object> parameters = new HashMap<String,Object>();
				p.toJson(parameters);
				json.put("parameters", parameters);
			}
		}

		@Override
		@SuppressWarnings("unchecked")
		public void fromJson(Map<String, Object> json) {
			if (json != null){
				id = JsonTools.getString(json, "id", "");
				event = JsonTools.getString(json, "event", "");
				if (p == null){
					p = new DefaultProperties();
				}
				
				Object parameters = json.get("parameters");
				if (parameters instanceof Map){
					p.fromJson((Map<String,Object>)parameters);
				}
			}
		}

		public void toXML(Element root) {
			if (root != null){
				root.setAttribute("id", id);		
				root.setAttribute("event", event);
				if (p != null){
					Document doc = root.getOwnerDocument();
					
					Element _parameters = doc.createElement("parameters");
					p.toXML(_parameters);
					
					root.appendChild(_parameters);
				}
			}
		}

		@Override
		public void report(Element xml) {
			toXML(xml);
		}

		@Override
		public void report(Map<String, Object> json) {
			toJson(json);
		}

		@Override
		public String id() {
			return id;
		}
		
		@Override
		public DefaultProperties getParameters() {
			return p;
		}

		@Override
		public void fromJsonString(String jsonString) {
			Object jsonObj = provider.parse(jsonString);
			if (jsonObj instanceof Map){
				@SuppressWarnings("unchecked")
				Map<String,Object> json = (Map<String,Object>)jsonObj;
				fromJson(json);
			}
		}

		@Override
		public String toJsonString() {
			Map<String,Object> json = new HashMap<String,Object>();			
			toJson(json);			
			return provider.toJson(json);
		}
	}
}
