package com.alogic.doer.local;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alogic.doer.core.TaskCenter;
import com.alogic.doer.core.TaskDispatcher;
import com.alogic.timer.core.Doer;
import com.alogic.timer.core.DoerContext;
import com.alogic.timer.core.Task;
import com.alogic.timer.core.Task.State;
import com.anysoft.util.BaseException;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * 本地实现的任务中心
 * @author yyduan
 * @version 1.6.9.2 [20170601 duanyy] <br>
 * - 改造TaskCenter模型，以便提供分布式任务处理支持; <br>
 */
public class LocalTaskCenter implements TaskCenter{
	/**
	 * a logger of log4j
	 */
	protected static final Logger LOG = LoggerFactory.getLogger(TaskCenter.class);
	
	/**
	 * 为doer创造的全局上下文
	 */
	protected DoerContext ctx = new DoerContext();
	
	/**
	 * 任务队列
	 */
	protected Map<String,LinkedBlockingQueue<Task>> taskQueue 
		= new ConcurrentHashMap<String,LinkedBlockingQueue<Task>>();
	
	/**
	 * 任务处理器
	 */
	protected List<LocalTaskRobber> robbers = new ArrayList<LocalTaskRobber>();
	
	/**
	 * 线程
	 */
	protected Thread thread = null;
	
	@Override
	public void dispatch(String queue, Task task) {
		String eventId = task.getEventId();
		LinkedBlockingQueue<Task> found = taskQueue.get(eventId);
		if (found == null){
			synchronized (LocalTaskCenter.class){
				found = taskQueue.get(eventId);
				if (found == null){
					found = new LinkedBlockingQueue<Task>();
					taskQueue.put(eventId, found);
				}
			}
		}
		
		if (!found.offer(task)){
			onFinish(task.id(),State.Failed, 10000,"The queue is full.queue:" + task.id());
			throw new BaseException("core.queue_is_full","The queue is full.queue:" + task.id());
		}else{
			onQueued(task.id(),State.Queued, 10000,"");
		}
	}

	@Override
	public void configure(Element root, Properties props) {
		Properties p = new XmlElementProperties(root,props);
		configure(p);
		
		NodeList nodeList = XmlTools.getNodeListByPath(root, "event");
		
		for (int i = 0 ; i < nodeList.getLength() ; i ++){
			Node node = nodeList.item(i);
			if (node.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			
			Element e = (Element)node;
			
			LocalTaskRobber robber = new LocalTaskRobber.Default();
			robber.configure(e, p);
			robbers.add(robber);
		}
	}

	@Override
	public void configure(Properties p) {
		
	}

	@Override
	public void report(Element xml) {
		if (xml != null){
			XmlTools.setString(xml,"module",getClass().getName());
		}
	}

	@Override
	public void report(Map<String, Object> json) {
		if (json != null){
			JsonTools.setString(json, "module", getClass().getName());
		}
	}

	@Override
	public void onRunning(String id, State state, int percent,String note) {
		LOG.info(String.format("[%s]Task %s -> %d%%",state.name(),id,percent/100));
	}

	@Override
	public void onQueued(String id, State state, int percent,String note) {
		LOG.info(String.format("[%s]Task %s -> %d%%",state.name(),id,percent/100));
	}

	@Override
	public void onPolled(String id, State state, int percent,String note) {
		LOG.info(String.format("[%s]Task %s -> %d%%",state.name(),id,percent/100));
	}

	@Override
	public void onStart(String id, State state, int percent,String note) {
		LOG.info(String.format("[%s]Task %s -> %d%%",state.name(),id,percent/100));
	}

	@Override
	public void onFinish(String id, State state, int percent,String note) {
		LOG.info(String.format("[%s]Task %s -> %d%%",state.name(),id,percent/100));
	}		

	@Override
	public DoerContext getContext() {
		return ctx;
	}

	@Override
	public void saveContext(DoerContext ctx, Doer task) {
		// nothing to do
	}

	@Override
	public void start() {
		LOG.info("Task center is starting...");
		for (LocalTaskRobber robber:robbers){
			robber.start(this);
		}
		LOG.info("Task center has started.");
	}

	@Override
	public int askForTask(String queue, TaskDispatcher dispatcher, long timeout) {
		LinkedBlockingQueue<Task> found = taskQueue.get(queue);
		if (found == null){
			//没有事件要处理
			return 1;
		}
		
		try {
			Task task = found.poll(timeout, TimeUnit.MILLISECONDS);
			if (task != null && dispatcher != null){
				onPolled(task.id(),State.Polled,0,"");
				dispatcher.dispatch(queue,task);
				//有事件要处理，已经dispatch
				return 0;
			}else{
				//超时，没有事件要处理
				return 2;
			}
		} catch (InterruptedException e) {
			//进程被打断
			return -1;
		}
	}

	@Override
	public void stop() {
		for (LocalTaskRobber robber:robbers){
			robber.stop();
		}
	}

	@Override
	public void join(long timeout) {
		for (LocalTaskRobber robber:robbers){
			robber.join(timeout);
		}
	}
}
