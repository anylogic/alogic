package com.alogic.doer.local;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.w3c.dom.Element;

import com.alogic.doer.core.Task;
import com.alogic.doer.core.TaskDoer;
import com.alogic.doer.core.TaskQueue;
import com.alogic.doer.core.TaskReport;
import com.alogic.doer.core.TaskReport.TaskState;
import com.anysoft.util.BaseException;
import com.anysoft.util.Factory;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;

/**
 * JVM本地的任务队列
 * 
 * @author duanyy
 * @since 1.6.3.4
 * 
 */
public class LocalTaskQueue implements TaskQueue {
	
	public TaskReport getTaskReport(String id) {
		return reports.get(id);
	}

	public void reportTaskState(String id, TaskState state, int percent) {
		TaskReport.Default report = reports.get(id);
		if (report != null){
			report.reportTaskState(state, percent);
		}
	}	
	
	synchronized public void dispatch(Task task) throws BaseException {
		TaskReport.Default report = new TaskReport.Default(task);
		reports.put(task.id(), report);
		
		if (!queue.offer(task)){
			report.reportTaskState(TaskState.Queued, -1);
			throw new BaseException("core.queue_is_full","The queue is full.queue:" + id());
		}
	} 

	public void configure(Element _e, Properties _properties)
			throws BaseException {
		Properties p = new XmlElementProperties(_e,_properties);
		
		id = PropertiesConstants.getString(p, "id", "");
		
		doersCnt = PropertiesConstants.getInt(p,"cnt", doersCnt);
		doersCnt = doersCnt <= 0 ? 30:doersCnt;
		
		Factory<TaskDoer> factory = new Factory<TaskDoer>();
		
		doers = new ArrayList<TaskDoer>(doersCnt);
		for (int i = 0 ;i < doersCnt; i ++){
			TaskDoer doer = factory.newInstance(_e, _properties, "module", TaskDoer.Null.class.getName());
			doer.setTaskQueue(this);
			doer.start();
			doers.add(doer);
		}
		
		queue = new LinkedBlockingQueue<Task>();
		
		reports = new Hashtable<String,TaskReport.Default>();
	}

	public void report(Element xml) {
		if (xml != null){
			xml.setAttribute("module", getClass().getName());
		}
	}

	public void report(Map<String, Object> json) {
		if (json != null){
			JsonTools.setString(json,"module",getClass().getName());
		}
	}

	public String id() {
		return id;
	}

	public void askForTask(TaskDoer doer, long timeout) {
		try {
			Task task = queue.poll(timeout, TimeUnit.MILLISECONDS);
			if (task != null){
				reportTaskState(task.id(),TaskState.Polled,-1);
				doer.dispatch(task);
			}
		}catch (Exception ex){
			
		}
	}

	/**
	 * 队列的ID
	 */
	protected String id;
	
	/**
	 * 任务队列
	 */
	protected LinkedBlockingQueue<Task> queue = null;
	
	/**
	 * 任务处理者列表
	 */
	protected List<TaskDoer> doers = null;
	
	/**
	 * 任务处理者个数
	 */
	protected int doersCnt = 5;

	/**
	 * 报告列表
	 */
	protected Hashtable<String,TaskReport.Default> reports = null;
}
