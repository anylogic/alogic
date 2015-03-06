package com.alogic.doer.core;

import com.alogic.doer.core.TaskReport.TaskState;

/**
 * 任务队列
 * 
 * @author duanyy
 *
 */
public interface TaskQueue extends TaskDispatcher {
	
	/**
	 * 获取队列的ID
	 * @return id
	 */
	public String id();
	
	/**
	 * 请求任务
	 * 
	 * <p>向队列请求任务，本方法将阻塞直至有任务分发给该doer或者时间超时。
	 * 
	 * @param doer 任务处理人
	 */
	public void askForTask(TaskDoer doer,long timeout);
	
	/**
	 * 获取指定任务的报告
	 * 
	 * @param id 任务ID
	 * @return 任务报告
	 */
	public TaskReport getTaskReport(String id);
	
	/**
	 * 报告任务的状态
	 * @param state
	 * @param percent
	 */
	public void reportTaskState(String id,TaskState state,int percent);
}
