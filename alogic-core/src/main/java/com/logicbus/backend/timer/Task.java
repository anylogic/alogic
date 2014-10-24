package com.logicbus.backend.timer;

import com.anysoft.util.Properties;

/**
 * 定时器任务
 * @author duanyy
 * 
 */
abstract public class Task {
	
	/**
	 * 创建上下文对象
	 * @param _config 配置信息
	 * @return 上下文对象
	 */
	public Object createContext(Properties _config){return null;}

	abstract public void run(Object _context,Properties _config,TaskListener _listener);

	/**
	 * 开始任务
	 * @param _context 上下文
	 * @param _config 配置信息
	 * @param _listener 监听器
	 * @return 是否执行成功
	 */
	final public void start(Object _context,Properties _config,TaskListener _listener){
		try {
			failed = false;
			state = "Working";
			run(_context,_config,_listener);
		}catch (Exception ex){
			failed = true;
			logNote = ex.getMessage();
		}finally {
			state = "Ready";
		}
	}
	
	/**
	 * 状态(Ready|Working)
	 */
	public String state = "Ready";
	
	/**
	 * 获取状态
	 * @return 状态
	 */
	public String getState(){return state;}
	
	protected boolean failed;

	/**
	 * 日志的描述信息
	 */
	protected String logNote;

	/**
	 * 是否执行失败
	 * @return 是否失败
	 */
	public boolean isFailed(){
		return failed;
	}
	
	/**
	 * 获取日志描述
	 * @return 日志的描述
	 */
	public String getLogNote(){
		return logNote;
	}
}
