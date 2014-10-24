package com.logicbus.backend.timer;


/**
 * 任务监听器 
 * @author duanyy
 *
 */
public interface TaskListener {
	
	/**
	 * 任务开始
	 * @param _task 任务
	 */
	public void taskBegin(Task _task);
	
	/**
	 * 任务处理进度
	 * @param _task 任务
	 * @param _percent 进度百分比
	 * @param _msg 消息
	 */
	public void taskProcess(Task _task,int _percent,String _msg);
	
	/**
	 * 任务处理信息
	 * @param _task 任务
	 * @param _msg 进度百分比
	 */
	public void taskMessage(Task _task,String _msg);
	
	/**
	 * 任务结束
	 * @param _task 任务
	 */
	public void taskEnd(Task _task);
}
