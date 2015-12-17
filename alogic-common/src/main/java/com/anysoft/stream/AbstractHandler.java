package com.anysoft.stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Reportable;
import com.anysoft.util.XmlElementProperties;

/**
 * 数据处理器基类 
 * 
 * @author duanyy
 * @since 1.4.0
 * 
 * @version 1.4.3 [20140903 duanyy] <br>
 * - 增加pause,resume实现  <br>
 * 
 * @version 1.4.4 [20140917 duanyy] <br>
 * - Handler:handle和flush方法增加timestamp参数，以便进行时间同步 <br>
 * 
 * @version 1.6.0.3 [20141114 duanyy] <br>
 * - 修正队列可能为空的异常 <br>
 * 
 * @version 1.6.4.17 [20151216 duanyy] <br>
 * - 根据sonar建议优化代码 <br>
 */
abstract public class AbstractHandler<data extends Flowable> implements Handler<data> {
	
	protected static final Logger LOG = LogManager.getLogger(AbstractHandler.class);
	
	/**
	 * 当前的时间周期，缺省半个小时
	 */
	protected long currentCycle = 30 * 60 * 1000;
	
	/**
	 * 周期时间戳
	 */
	protected long lastVisitedTime;
	
	/**
	 * 是否开启Report
	 */
	protected boolean enableReport = false;
	
	/**
	 * 全部的Report数据
	 */
	protected Hashtable<String,Measure> total_items = null;
	
	/**
	 * 当前周期之内的Report数据
	 */
	protected Hashtable<String,Measure> current_items = null;
	
	/**
	 * 异步模式
	 */
	protected boolean async = false;
	
	/**
	 * 异步模式下工作线程
	 */
	protected Worker<data> asyncWorker = null;	
	
	protected String id;
	
	
	public String getId(){
		return id;
	}
	
	
	public void configure(Element _e, Properties _properties)
			throws BaseException {
		XmlElementProperties p = new XmlElementProperties(_e,_properties);
		
		id = PropertiesConstants.getString(p,"id", "",true);
		
		enableReport = PropertiesConstants.getBoolean(p, "report.enable", enableReport,false);
		if (enableReport){
			currentCycle = PropertiesConstants.getLong(p, "report.cycle", currentCycle,false);
			total_items = new Hashtable<String,Measure>();
			current_items = new Hashtable<String,Measure>();
		}
		
		async = PropertiesConstants.getBoolean(p,"async",async);
		if (async){
			asyncWorker = new Worker<data>(this,p);
		}
		
		onConfigure(_e, p);
	}

	
	public void close() throws Exception {
		if (async && asyncWorker != null){
			asyncWorker.close();
		}
	}

	private void stat(String group,Hashtable<String,Measure> items,data item){
		Measure found = items.get(group);
		if (found == null){
			synchronized (items){
				found = items.get(group);
				if (found == null){
					found = new Measure(0);
					items.put(group, found);
				}				
			}
		}
		found.incr(1);	
	}	
	
	
	public void report(Element xml) {
		if (xml == null) return;
		if (enableReport){
			Document doc = xml.getOwnerDocument();
			Element current = doc.createElement("current");
			report(current_items,current);
			xml.appendChild(current);
			
			Element total = doc.createElement("total");
			report(total_items,total);
			xml.appendChild(total);
			
			xml.setAttribute("module", getClass().getName());
		}
		xml.setAttribute("async", Boolean.toString(async));
		if (async && asyncWorker != null){
			asyncWorker.report(xml);
		}
		
		xml.setAttribute("isRunning", Boolean.toString(isRunning));
	}
	
	private void report(Hashtable<String,Measure> _items,Element root){
		Document doc = root.getOwnerDocument();
		long total = 0;
		
		Iterator<Entry<String,Measure>> iterator = _items.entrySet().iterator();
		
		while (iterator.hasNext()){
			Entry<String,Measure> entry = iterator.next();
			Element dimElem = doc.createElement("group");
			dimElem.setAttribute("dim",entry.getKey());
			dimElem.setAttribute("amount", String.valueOf(entry.getValue().value()));
			total += entry.getValue().value();
			root.appendChild(dimElem);
		}
		root.setAttribute("total", String.valueOf(total));
	}	

	
	public void report(Map<String, Object> json) {
		if (json == null) return ;
		if (enableReport){
			report(current_items,json,"current");
			report(total_items,json,"total");
			json.put("module", getClass().getName());
		}
		
		json.put("async", async);
		if (async && asyncWorker != null){
			asyncWorker.report(json);
		}
		
		json.put("isRunning",isRunning);
	}
	
	private void report(Hashtable<String,Measure> _items,Map<String,Object> json,String name){
		long total = 0;
		List<Object> items = new ArrayList<Object>(_items.size());
		
		Iterator<Entry<String,Measure>> iterator = _items.entrySet().iterator();
		while (iterator.hasNext()){
			Entry<String,Measure> entry = iterator.next();
			Map<String,Object> itemObj = new HashMap<String,Object>(2);
			
			itemObj.put("dim", entry.getKey());
			itemObj.put("amount", entry.getValue().value());
			
			items.add(itemObj);
			total += entry.getValue().value();
		}
		json.put("item", items);
		json.put("total", total);
	}	

	
	public void handle(data _data,long timestamp) {
		if (enableReport){
			String group = _data.getStatsDimesion();
			//当前时间
			long current = System.currentTimeMillis();
			if (current / currentCycle - lastVisitedTime / currentCycle != 0){
				//新的周期
				synchronized(current_items){
					current_items.clear();
				}
			}
			stat(group,current_items,_data);
			stat(group,total_items,_data);
			lastVisitedTime = current;
		}
		if (isRunning){
			//只有在running状体下才执行
			if (async && asyncWorker != null){
				asyncWorker.handle(_data,timestamp);
			}else{
				onHandle(_data,timestamp);
			}
		}
	}

	
	public void flush(long timestamp) {
		if (isRunning){
			if (async && asyncWorker != null){
				asyncWorker.flush(timestamp);
			}else{
				onFlush(timestamp);
			}
		}
	}
	
	
	public String getHandlerType() {
		return "handler";
	}
	
	/**
	 * 暂停
	 */
	public void pause(){
		isRunning = false;
	}
	
	/**
	 * 恢复
	 */
	public void resume(){
		isRunning = true;
	}
	
	protected boolean isRunning = true;
	
	/**
	 * 处理Handle事件
	 * @param _data
	 */
	abstract protected void onHandle(data _data,long timestamp);
	
	/**
	 * 处理Flush事件
	 */
	abstract protected void onFlush(long timestamp);
	
	abstract protected void onConfigure(Element e, Properties p);

	public static class Measure {
		protected long value = 0;
		
		public long value(){return value;}
		public void value(final long _value){value = _value;}
		
		public Measure(long _value){
			value = _value;
		}
		
		synchronized public void incr(final long increment){
			value += increment;
		}
	}
	
	public static class Worker<data extends Flowable> implements Runnable,Reportable,AutoCloseable{
		/**
		 * 异步模式下的时间间隔
		 */
		protected long interval = 1000;
		
		/**
		 * 异步模式下的缓冲队列
		 */
		protected ConcurrentLinkedQueue<data> queue = null;
		
		/**
		 * 异步模式下的最大队列长度
		 */
		protected volatile int maxQueueLength = 1000;
		
		protected AbstractHandler<data> handler = null;
		
		/**
		 * 当前队列长度
		 */
		protected int currentQueueLength = 0;
		
		private boolean stopped = false;
		
		private Thread thread = null;
		
		public Worker(AbstractHandler<data> _handler, Properties p){
			handler = _handler;
			interval = PropertiesConstants.getLong(p,"async.interval", interval,true);
			maxQueueLength = PropertiesConstants.getInt(p,"async.maxQueueLength", maxQueueLength,true);
			currentQueueLength = 0;
			queue = new ConcurrentLinkedQueue<data>();
			
			thread = new Thread(this);
			thread.start();
		}
		
		public void run() {
			while (!stopped){
				try {
					flush(System.currentTimeMillis());
					Thread.sleep(interval);
				}catch (Exception ex){
					LOG.error("Thread is interruppted",ex);
				}
			}
		}
		
		public void report(Element xml) {
			if (xml != null){
				xml.setAttribute("interval", String.valueOf(interval));
				xml.setAttribute("maxQueueLength", String.valueOf(maxQueueLength));
				xml.setAttribute("currentQueueLength", String.valueOf(currentQueueLength));
			}
		}
		
		public void report(Map<String, Object> json) {
			if (json != null){
				json.put("interval", interval);
				json.put("maxQueueLength", String.valueOf(maxQueueLength));
				json.put("currentQueueLength", String.valueOf(currentQueueLength));
			}
		}
		
		public void handle(data _data,long timestamp){
			queue.offer(_data);
		
			synchronized (this){
				currentQueueLength ++;
				if (currentQueueLength > maxQueueLength){
					//如果缓冲区满了，同步处理
					data item = queue.poll();
					if (item != null){
						handler.onHandle(item,timestamp);
						currentQueueLength --;
					}
				}
			}
		}
		
		public void flush(long timestamp){
			if (!queue.isEmpty()){
				data item = queue.poll();		
				int count = 0;
				while (item != null){
					handler.onHandle(item,timestamp);
					item = queue.poll();
					count ++;
				}
				
				currentQueueLength -= count;
				handler.onFlush(timestamp);
			}
		}
		
		public void close(){
			stopped = true;
			if (thread != null && thread.isAlive()){
				thread.interrupt();
			}
		}
	}	
	
}
