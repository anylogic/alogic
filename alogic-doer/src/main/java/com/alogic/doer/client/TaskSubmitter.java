package com.alogic.doer.client;

import java.util.Map;

import com.alogic.doer.core.Task;
import com.alogic.doer.core.TaskCenter;
import com.alogic.doer.core.TaskReport;
import com.anysoft.util.DefaultProperties;


/**
 * 任务提交者
 * 
 * @author duanyy
 * 
 * @since 1.6.3.4
 * @version 1.6.3.6 [20150316 duanyy] <br>
 * - 增加方法{@link #getTaskReport(String, String)} <br>
 */
public class TaskSubmitter{
	/**
	 * 提交任务
	 * 
	 * @param id 任务的ID
	 * @param queue 任务队列
	 * @param parameters 任务参数
	 */
	static public void submit(String id,String queue,Map<String,String> parameters){
		TaskCenter tc = TaskCenter.TheFactory.get();
		if (tc != null){
			Task task = new Task.Default(id, queue, parameters);
			tc.dispatch(task);
		}
	}
	/**
	 * 提交任务
	 * 
	 * @param id 任务的ID
	 * @param queue 任务队列
	 * @param parameters 任务参数
	 */	
	static public void submit(String id,String queue,DefaultProperties parameters){
		TaskCenter tc = TaskCenter.TheFactory.get();
		if (tc != null){
			Task task = new Task.Default(id, queue, parameters);
			tc.dispatch(task);
		}
	}
	
	/**
	 * 获取指定任务的报告
	 * @param id 任务ID
	 * @param queue 队列ID
	 * @return 任务报告
	 */
	static public TaskReport getTaskReport(String id,String queue){
		TaskCenter tc = TaskCenter.TheFactory.get();
		if (tc != null){
			return tc.getTaskReport(id, queue);
		}
		return null;
	}	
}
