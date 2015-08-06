package com.alogic.timer.core;

import com.alogic.timer.core.Task.State;

/**
 * 状态监听器
 * 
 * @author duanyy
 * @since 1.6.3.37
 */
public interface TaskStateListener {
	/**
	 * 报告任务状态
	 * @param task 任务
	 * @param state 状态
	 * @param percent 百分比
	 */
	public void reportState(Task task,State state,int percent);
	
	/**
	 * 报告任务状态
	 * @param id 任务ID
	 * @param state 状态
	 * @param percent 百分比
	 */
	public void reportState(String id,State state,int percent);
}
