package com.alogic.doer.local;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alogic.doer.core.TaskCenter;
import com.alogic.doer.core.TaskQueue;
import com.alogic.doer.core.TaskReport;
import com.alogic.timer.core.Task;
import com.anysoft.util.BaseException;
import com.anysoft.util.Counter;
import com.anysoft.util.Properties;
import com.anysoft.util.SimpleCounter;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * JVM本地实现的TaskCenter
 * 
 * @author duanyy
 * 
 * @since 1.6.3.4
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 */
public class LocalTaskCenter implements TaskCenter {
	/**
	 * a logger of log4j
	 */
	protected static final Logger logger = LoggerFactory.getLogger(TaskCenter.class);
	
	public TaskReport getTaskReport(String id, String queue) {
		TaskQueue q = queues == null ? null:queues.get(queue);
		
		return q == null ? null : q.getTaskReport(id);
	}
	
	public void dispatch(Task task) throws BaseException {
		long now = System.currentTimeMillis();
		boolean error = false;
		try {
			TaskQueue q = queues == null ? null : queues.get(task.queue());
			
			if (q == null){
				throw new BaseException("core.queue_not_found","Can not find the queue:" + task.queue());
			}
			
			q.dispatch(task);
		}catch (BaseException ex){
			error = true;
			throw ex;
		}finally{
			if (counter != null){
				counter.count(System.currentTimeMillis() - now, error);
			}
		}
	}

	public void configure(Properties p) throws BaseException {

	}
	
	public void configure(Element _e, Properties _properties)
			throws BaseException {
		Properties p = new XmlElementProperties(_e,_properties);
		configure(p);
		//counter
		counter = new SimpleCounter(p);
		
		//queues
		queues = new Hashtable<String,TaskQueue>();
		{
			NodeList nodeList = XmlTools.getNodeListByPath(_e, "queue");
			
			for (int i = 0 ;i < nodeList.getLength() ; i ++){
				Node n = nodeList.item(i);
				
				if (Node.ELEMENT_NODE != n.getNodeType()){
					continue;
				}
				
				Element e = (Element)n;
				
				String id = e.getAttribute("id");
				if (id == null || id.length() <= 0){
					continue;
				}
				
				LocalTaskQueue q = new LocalTaskQueue();
				try {
					q.configure(e, p);
					queues.put(id, q);
				}catch (Exception ex){
					ex.printStackTrace();
					logger.error("Can not config TaskQueue,module:" + q.getClass().getName());
				}
			}
		}
	}

	public void report(Element xml) {
		if (xml != null){
			Document doc = xml.getOwnerDocument();
			
			Enumeration<TaskQueue> iterator = queues.elements();
			
			while (iterator.hasMoreElements()){
				TaskQueue obj = iterator.nextElement();
				Element _obj = doc.createElement("queue");
				obj.report(_obj);
				xml.appendChild(_obj);
			}
			
			if (counter != null){
				Element _counter = doc.createElement("counter");
				counter.report(_counter);
				xml.appendChild(_counter);
			}
		}
	}

	public void report(Map<String, Object> json) {
		if (json != null){
			List<Object> _objs = new ArrayList<Object>(queues.size());
			
			Enumeration<TaskQueue> iterator = queues.elements();
			while (iterator.hasMoreElements()){
				TaskQueue obj = iterator.nextElement();
				Map<String,Object> _obj = new HashMap<String,Object>();
				obj.report(_obj);
				_objs.add(_obj);
			}
			
			json.put("queue",_objs);
			
			if (counter != null){
				Map<String,Object> map = new HashMap<String,Object>();
				counter.report(map);
				json.put("counter", map);
			}
		}
	}

	protected Counter counter = null;
	protected Hashtable<String,TaskQueue> queues = null;


}
