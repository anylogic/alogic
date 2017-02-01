package com.anysoft.stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.util.BaseException;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Reportable;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

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
 * 
 * @version 1.6.4.44 [20160414 duanyy] <br>
 * - 修正Report输出的bug，并增加统计数据的分页功能 <br>
 * - 不再保存全部数据，只保存当前周期数据 <br>
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 */
public abstract class AbstractHandler<data extends Flowable> implements Handler<data> {
	
	/**
	 * a logger of log4j
	 */
	protected static final Logger LOG = LoggerFactory.getLogger(AbstractHandler.class);
	
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
		xml.setAttribute("module", getClass().getName());
		xml.setAttribute("async", Boolean.toString(async));
		xml.setAttribute("isRunning", Boolean.toString(isRunning));
		
		if (enableReport){
			report(current_items,xml,"stat");
		}

		if (async && asyncWorker != null){
			asyncWorker.report(xml);
		}
	}
	
	private void report(Hashtable<String,Measure> _items,Element root,String name){
		Document doc = root.getOwnerDocument();
		Element child = doc.createElement(name);
		long total = 0;
		
		int current = 0;
		int offset = XmlTools.getInt(root, "offset", 0);
		int limit = XmlTools.getInt(root,"limit",30);
		String keyword = XmlTools.getString(root, "keyword", "");
		
		Iterator<Entry<String,Measure>> iterator = _items.entrySet().iterator();
		
		while (iterator.hasNext()){
			Entry<String,Measure> entry = iterator.next();
			
			String dim = entry.getKey();
			boolean match = StringUtils.isEmpty(keyword) || dim.contains(keyword);
			if (match){
				if (current >= offset && current < offset + limit){
					
					Element dimElem = doc.createElement("group");
					dimElem.setAttribute("dim",dim);
					dimElem.setAttribute("amount", String.valueOf(entry.getValue().value()));
					total += entry.getValue().value();
					child.appendChild(dimElem);
					
				}
				current ++;
			}
		}
		XmlTools.setLong(child, "times",total);
		XmlTools.setInt(child, "total", current);
		XmlTools.setInt(child, "all", _items.size());
		XmlTools.setLong(child, "lastVisitedTime", lastVisitedTime);
		XmlTools.setLong(child, "cycle", currentCycle);		
		root.appendChild(child);
	}	

	
	public void report(Map<String, Object> json) {
		if (json == null) return ;
		json.put("module", getClass().getName());
		json.put("async", async);
		json.put("isRunning",isRunning);
		
		if (enableReport){
			report(current_items,json,"stat");
		}
		
		if (async && asyncWorker != null){
			asyncWorker.report(json);
		}
	}
	
	private void report(Hashtable<String,Measure> _items,Map<String,Object> json,String name){
		Map<String,Object> child = new HashMap<String,Object>();
		long total = 0;
		int current = 0;
		int offset = JsonTools.getInt(json, "offset", 0);
		int limit = JsonTools.getInt(json, "limit", 30);
		String keyword = JsonTools.getString(json,"keyword","");
		
		List<Object> items = new ArrayList<Object>(_items.size());
		
		Iterator<Entry<String,Measure>> iterator = _items.entrySet().iterator();
		while (iterator.hasNext()){
			Entry<String,Measure> entry = iterator.next();
			
			String dim = entry.getKey();
			boolean match = StringUtils.isEmpty(keyword) || dim.contains(keyword);
			if (match){
				if (current >= offset && current < offset + limit){
					Map<String,Object> itemObj = new HashMap<String,Object>(2);			
					itemObj.put("dim", entry.getKey());
					itemObj.put("amount", entry.getValue().value());			
					items.add(itemObj);
					total += entry.getValue().value();
				}
				current ++;
			}
		}
		JsonTools.setLong(child, "times",total);
		JsonTools.setInt(child, "total", current);
		JsonTools.setInt(child, "all", _items.size());
		JsonTools.setLong(child, "lastVisitedTime", lastVisitedTime);
		JsonTools.setLong(child, "cycle", currentCycle);
		child.put("item", items);
		json.put(name, child);
	}	

	
	public void handle(data _data,long timestamp) {
		if (enableReport){
			String group = _data.getStatsDimesion();
			//当前时间
			long current = System.currentTimeMillis();
			if (current / currentCycle - lastVisitedTime / currentCycle > 0){
				//新的周期
				synchronized(current_items){
					current_items.clear();
				}
			}
			stat(group,current_items,_data);
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
