package com.alogic.timer.core;

import com.alogic.timer.core.Task.State;

/**
 * 状态监听器
 * 
 * @author duanyy
 * @since 1.6.3.37
 * 
 * @version 1.6.9.2 [20170601 duanyy] <br>
 * - 改造TaskCenter模型，以便提供分布式任务处理支持; <br>
 */
public interface TaskStateListener {
	/**
	 * 任务进度变化
	 * @param id 任务id
	 * @param state 任务状态
	 * @param percent 进度百分比
	 * @param note 说明信息
	 */
	public void onRunning(String id,State state,int percent,String note);

	/**
	 * 任务进入队列
	 * @param id 任务id
	 * @param state 任务状态
	 * @param percent 进度百分比
	 * @param note 说明信息
	 */
	public void onQueued(String id,State state,int percent,String note);
	
	/**
	 * 任务在队列中被拉出
	 * @param id 任务id
	 * @param state 任务状态
	 * @param percent 进度百分比
	 * @param note 说明信息
	 */
	public void onPolled(String id,State state,int percent,String note);
	
	/**
	 * 任务开始执行
	 * @param id 任务id
	 * @param state 任务状态
	 * @param percent 进度百分比
	 * @param note 说明信息
	 */
	public void onStart(String id,State state,int percent,String note);
	
	/**
	 * 任务执行完成
	 * @param id 任务id
	 * @param state 任务状态
	 * @param percent 进度百分比
	 * @param note 说明信息
	 */
	public void onFinish(String id,State state,int percent,String note);	
}
