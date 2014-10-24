package com.logicbus.backend.timer;

import java.util.Date;

import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

final public class Timer implements TaskListener{

	public Timer(Matcher _matcher,Task _task,Properties _config,TimerLogListener _logListener){
		config = _config;
		matcher = _matcher;
		task = _task;
		
		if (config != null){
			fromDate = PropertiesConstants.getDate(config,"fromDate",fromDate);
			toDate = PropertiesConstants.getDate(config, "toDate", toDate);
		}
		timerHealth = new TimerHealth(this,_logListener);
	}

	/**
	 * 有效期起始时间
	 * <br>
	 * 定时器必须在有效期之内才会被调度，可以通过config中fromDate变量进行设置，缺省情况下，取当前时间。
	 */
	protected Date fromDate = new Date();
	/**
	 * 有效期结束时间
	 * <br>
	 * 定时器必须在有效期之内才会被调度，可以通过config中toDate变量进行设置，缺省情况下，取当前时间之后的50年。
	 */
	protected Date toDate = new Date(System.currentTimeMillis() + 50 * 365 * 24 * 60 * 60 * 1000);
	
	/**
	 * 上次调度时间
	 */
	protected Date lastDate = null;
	protected Task task = null;
	protected Matcher matcher = null;
	protected Properties config = null;
	protected TimerHealth timerHealth = null;
	protected Object context = null;
	
	public String id(){
		return timerHealth.getID();
	}
	public TimerHealth getTimerHealth(){return timerHealth;}
	public Properties getProperties(){return config;}
	public boolean isTimeToClear(){return matcher != null && matcher.isTimeToClear();}
	/**
	 * 调度任务
	 */
	final public void schedule(){
		synchronized(lock){
			if (!getState().equals("Running")){
				//当前定时器没有定义为Running
				return ;
			}

			if (task != null && matcher != null){
				Date now = new Date();
				if (now.before(fromDate) || now.after(toDate)){
					//必须在定时器的有效期之内才能调度
					return ;
				}
				
				if (task.getState().equals("Working")){
					//已经在工作，忽略本次
					return ;
				}
				boolean match = matcher.match(lastDate,now,config);
				if (match){
					if (context == null){
						context = task.createContext(config);
					}
					lastDate = now;
					TaskThread thread = new TaskThread(task, context, config, this);
					thread.setDaemon(true);
					thread.start();
				}
			}
		}
	}
	
	protected String state = "Running";
	/**
	 * 获取定时器状态
	 * <br>
	 * 定时器状态包括：Running|Paused 
	 * 
	 * @return state
	 */
	public String getState(){
		return state;
	}
	
	/**
	 * 暂停定时器
	 */
	public void pause(){
		state = "Pause";
		if (timerHealth != null){
			timerHealth.logOperation("pause");
		}
	}
	
	/**
	 * 恢复定时器状态为Running
	 */
	public void resume(){
		state = "Running";
		if (timerHealth != null){
			timerHealth.logOperation("resume");
		}
	}
	
	/**
	 * 预测1个月内下一次调度时间
	 * <br>
	 * 预测需要耗费资源，慎用
	 * @return 下一次调度时间
	 */
	public Date forecastNextDate(){
		long current = System.currentTimeMillis();
		Date __last = lastDate;
		Date __now = null;
		int step = 1000*60;
		int count = 60*24*31;
		for (; count > 0 ; current += step,count--){
			__now = new Date(current);
			if (matcher.match(__last, __now, null)){
				return __now;
			}
		}
		return null;
	}
	
	protected Object lock = new Object();
	
	/**
	 * 任务开始
	 * @param _task 任务
	 */
	public void taskBegin(Task _task){
		if (timerHealth != null){
			timerHealth.logProcess(0, "task begin.");
		}
	}
	
	/**
	 * 任务结束
	 * @param _task 任务
	 */
	public void taskEnd(Task _task){
		if (timerHealth != null){
			timerHealth.logProcess(100, "task end.");
		}
	}
	
	public void taskProcess(Task _task, int _percent, String _msg) {
		if (timerHealth != null){
			timerHealth.logProcess(_percent, _msg);
		}
	}

	public void taskMessage(Task _task, String _msg) {
		if (timerHealth != null){
			timerHealth.logProcess(-1, _msg);
		}
	}
}
