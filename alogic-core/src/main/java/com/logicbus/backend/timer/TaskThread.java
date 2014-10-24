package com.logicbus.backend.timer;

import com.anysoft.util.Properties;


/**
 * 任务进程
 * @author duanyy
 *
 */
final public class TaskThread extends Thread {
	
	/**
	 * 进程所运行的任务
	 */
	protected Task task = null;
	
	
	/**
	 * 任务所需的上下文
	 */
	protected Object context = null;

	/**
	 * 任务所需的配置信息
	 */
	protected Properties config = null;
	
	/**
	 * 任务监听器
	 */
	protected TaskListener taskListener = null;
	
	/**
	 * constructor
	 * @param _task 运行的任务
	 * @param _context 运行任务的上下文
	 * @param _config 参数变量集
	 * @param _taskListener 任务监听器
	 */
	public TaskThread(Task _task,Object _context,Properties _config,TaskListener _taskListener){
		task = _task;
		context = _context;
		config = _config;
		taskListener  = _taskListener;
	}
	
	
	public void run(){
		if (task != null){
			if (taskListener != null){
				taskListener.taskBegin(task);
			}
			try {
				task.start(context,config,taskListener);
			}finally{
				if (taskListener != null){
					taskListener.taskEnd(task);
				}
			}
		}
	}
}

