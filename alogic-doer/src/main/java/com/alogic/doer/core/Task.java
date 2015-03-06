package com.alogic.doer.core;

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

/**
 * 任务
 * @author duanyy
 *
 */
public interface Task extends JsonSerializer,Reportable{
	
	/**
	 * 获取任务ID
	 * @return id
	 */
	public String id();
	
	/**
	 * 获取任务队列
	 * @return 队列ID
	 */
	public String queue();
		
	/**
	 * 任务参数
	 * @return 任务参数
	 */
	public DefaultProperties getParameters();
	
	/**
	 * 缺省实现
	 * 
	 * @author duanyy
	 *
	 */
	public static class Default implements Task{
		protected String id;
		protected String queue;
		protected DefaultProperties p;
		
		public Default(String _id,String _queue){
			this(_id,_queue,(DefaultProperties)null);
		}
		
		public Default(String _id,String _queue,Map<String,String> _p){
			id = _id;
			queue = _queue;
			p = new DefaultProperties();
			if (_p != null){
				Iterator<Entry<String,String>> iter = _p.entrySet().iterator();
				
				while (iter.hasNext()){
					Entry<String,String> entry = iter.next();
					
					p.SetValue(entry.getKey(), entry.getValue());
				}
			}
		}
		
		public Default(String _id,String _queue,DefaultProperties _p){
			id = _id;
			queue = _queue;
			p = _p;
		}
		
		public Default(Map<String,Object> json){
			fromJson(json);
		}
		
		public void toJson(Map<String, Object> json) {
			if (json != null){
				json.put("id", id);
				json.put("queue", queue);
				
				Map<String,Object> parameters = new HashMap<String,Object>();
				p.toJson(parameters);
				json.put("parameters", parameters);
			}
		}

		@SuppressWarnings("unchecked")
		public void fromJson(Map<String, Object> json) {
			if (json != null){
				id = JsonTools.getString(json, "id", "");
				queue = JsonTools.getString(json, "queue", id);
				
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
				root.setAttribute("queue", queue);
				
				if (p != null){
					Document doc = root.getOwnerDocument();
					
					Element _parameters = doc.createElement("parameters");
					p.toXML(_parameters);
					
					root.appendChild(_parameters);
				}
			}
		}

		public void report(Element xml) {
			toXML(xml);
		}

		public void report(Map<String, Object> json) {
			toJson(json);
		}

		public String id() {
			return id;
		}

		public String queue() {
			return queue;
		}

		public DefaultProperties getParameters() {
			return p;
		}
	}
}
