package com.alogic.timer;

import com.anysoft.util.Reportable;

/**
 * 定时任务
 * 
 * @author duanyy
 *
 */
public interface Task extends Runnable,Reportable{
	
	/**
	 * 任务状态
	 * 
	 * @author duanyy
	 *
	 */
	public enum State{
		/**
		 * 空闲
		 */
		Idle,
		/**
		 * 已调度
		 */
		Scheduled,
		/**
		 * 工作中
		 */
		Working
	}
	
	/**
	 * 获取任务状态
	 * @return
	 */
	public State getState();
}
