package com.logicbus.backend.timer;

import java.util.LinkedList;
import java.util.Queue;

import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
/**
 * 定时器状况
 * @author user
 *
 */
public class TimerHealth {
	protected static int idCount = 1;
	
	protected String id = "timer"+ String.valueOf(idCount++);
	
	/**
	 * 获取定时器ID
	 * @return id
	 */
	public String getID(){
		return id;
	}
	
	/**
	 * 获取定时器名称
	 * @return name
	 */
	public String getName(){
		return name;
	}
	
	public String getNote(){
		return note;
	}
	
	/**
	 * 定时器的名称
	 */
	protected String name;
	
	/**
	 * 定时器说明
	 */
	protected String note;
	/**
	 * 定时器开始时间
	 */
	protected long startTime = 0;
	
	/**
	 * 上次调度时间
	 */
	protected long lastScheduleTime = 0;
	
	/**
	 * 调度成功次数
	 */
	protected long successCount = 0;
	
	/**
	 * 调度失败次数
	 */
	protected long failedCount = 0;
	
	/**
	 * 当前处理百分比
	 */
	protected int percent = 0;
	
	/**
	 * 获取当前处理百分比
	 * @return 百分比
	 */
	public int getPercent(){return percent;}
	/**
	 * 日志队列
	 */
	protected Queue<TimerLog> logs = new LinkedList<TimerLog>();
	
	/**
	 * 日志监听器
	 */
	protected TimerLogListener logListener = null;
	
	/**
	 * 仅仅纪录失败的调度
	 */
	protected boolean onlyLogFailed = true;
	
	public void putLogListener(TimerLogListener _listener){
		logListener = _listener;
	}
	/**
	 * 定时器实例
	 */
	protected Timer owner = null;
	
	/**
	 * 日志队列的长度
	 */
	protected long logQueueLength = 10;
	/**
	 * 构造函数 
	 * @param _timer 所有者 
	 * @param _listener 日志监听器
	 */
	public TimerHealth(Timer _timer,TimerLogListener _listener){
		owner = _timer;
		logListener = _listener;
		startTime = System.currentTimeMillis();
		successCount = 0;
		failedCount = 0;
		
		Properties config = owner.getProperties();
		if (config != null){
			id = config.GetValue("id", id);
			name = config.GetValue("name", id);
			note = config.GetValue("note", "");
			onlyLogFailed = PropertiesConstants.getBoolean(config, "onlyLogFailed", true);
			logQueueLength = PropertiesConstants.getLong(config, "logQueueLength", 10);
		}
	}
	
	public long getStartTime(){return startTime;}
	public long getlastScheduleTime(){return lastScheduleTime;}
	public long getSuccessCount(){return successCount;}
	public long getFailedCount(){return failedCount;}
	public TimerLog[] getLogs(){return logs.toArray(new TimerLog[0]);}
	
	/**
	 * 记录操作日志 
	 * @param _note 说明
	 */
	public void logOperation(String _note){
		TimerLog __newLog = new TimerLog();
		__newLog.createTime = System.currentTimeMillis();
		__newLog.note = _note;
		__newLog.context = "successCount=" + successCount + ";failedCount=" + failedCount;
		__newLog.type = "operation";
		logArrive(__newLog);
	}
	
	/**
	 * 设置当前处理百分比
	 * @param _percent 百分比
	 * @param _note 说明
	 */
	public void logProcess(int _percent,String _note){
		percent = _percent >= 0 ? _percent : percent;
		TimerLog __newLog = new TimerLog();
		__newLog.createTime = System.currentTimeMillis();
		__newLog.note = _note;
		__newLog.context = "percent=" + percent;
		__newLog.type = "process";
		logArrive(__newLog);		
	}
	
	/**
	 * 纪录调度日志
	 * @param _task 调度任务
	 */
	public void logSchedule(Task _task){
		lastScheduleTime = System.currentTimeMillis();
		if (_task.isFailed()){
			//执行失败
			failedCount ++;
			TimerLog __newLog = new TimerLog();
			__newLog.createTime = System.currentTimeMillis();
			__newLog.note = "Task execute failed.";
			__newLog.context = _task.getLogNote();
			__newLog.type = "schedule";
			logArrive(__newLog);
		}else{
			successCount ++;
		}
	}
	
	/**
	 * 纪录日志
	 * @param _log 日志
	 */
	synchronized protected void logArrive(TimerLog _log){
		if (logs.size() > logQueueLength){
			//队列长度大于定义的队列长度，需要将最早的日志记录flush掉
			TimerLog __flushLog = logs.poll();
			if (logListener != null && __flushLog != null){
					logListener.logArrived(owner, __flushLog);
					logListener.logFlush();
			}
		}
		
		logs.offer(_log);
	}
}
