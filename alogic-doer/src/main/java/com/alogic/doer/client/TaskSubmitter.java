package com.alogic.doer.client;

import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alogic.doer.core.TaskCenter;
import com.alogic.doer.core.TaskReport;
import com.alogic.timer.core.Task;
import com.anysoft.util.DefaultProperties;


/**
 * 任务提交者
 * 
 * @author duanyy
 * 
 * @since 1.6.3.4
 * @version 1.6.3.6 [20150316 duanyy] <br>
 * - 增加方法{@link #getTaskReport(String, String)} <br>
 * 
 * @version 1.6.3.37 [20150806 duanyy] <br>
 * - 修改submit方法，增加任务ID的返回 <br>
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 */
public class TaskSubmitter{
	
	/**
	 * a logger of log4j
	 */
	public static Logger logger = LoggerFactory.getLogger(TaskSubmitter.class);
	
	/**
	 * 提交任务
	 * 
	 * @param id 任务ID
	 * @param queue 任务队列
	 * @param parameters 任务参数
	 * 
	 * @return 任务ID
	 */
	static public String submit(String id,String queue,Map<String,String> parameters){
		TaskCenter tc = TaskCenter.TheFactory.get();
		
		if (tc != null){
			Task task = new Task.Default(id, queue, parameters);
			tc.dispatch(task);
			return id;
		}else{
			logger.error("Can not find a valid task center.Fail to submit this task.");
			return null;
		}
	}
	
	/**
	 * 提交任务
	 * 
	 * @param queue 任务队列
	 * @param parameters 任务参数
	 * @return 任务ID
	 */
	static public String submit(String queue,Map<String,String> parameters){
		TaskCenter tc = TaskCenter.TheFactory.get();
		
		if (tc != null){
			String id = newTaskId();
			Task task = new Task.Default(id, queue, parameters);
			tc.dispatch(task);
			return id;
		}else{
			logger.error("Can not find a valid task center.Fail to submit this task.");
			return null;
		}
	}
	
	/**
	 * 提交任务
	 * 
	 * @param id 任务的ID
	 * @param queue 任务队列
	 * @param parameters 任务参数
	 * @return id
	 */	
	static public String submit(String id,String queue,DefaultProperties parameters){
		TaskCenter tc = TaskCenter.TheFactory.get();
		if (tc != null){
			Task task = new Task.Default(id, queue, parameters);
			tc.dispatch(task);
			return id;
		}else{
			logger.error("Can not find a valid task center.Fail to submit this task.");
			return null;
		}
	}
	
	/**
	 * 提交任务
	 * 
	 * @param queue 任务队列
	 * @param parameters 任务参数
	 * @return id
	 */
	static public String submit(String queue,DefaultProperties parameters){
		TaskCenter tc = TaskCenter.TheFactory.get();
		
		if (tc != null){
			String id = newTaskId();
			Task task = new Task.Default(id, queue, parameters);
			tc.dispatch(task);
			return id;
		}else{
			logger.error("Can not find a valid task center.Fail to submit this task.");
			return null;
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
	
	/**
	 * 生成一个任务id
	 * @return 任务id
	 */
	protected static String newTaskId(){
		return System.currentTimeMillis()+ randomString(6);
	}
	
	/**
	 * 字符表
	 */
	protected static final char[] Chars = {
	      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
	      'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
	      'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
	      'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
	      'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
	      'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
	      '8', '9'
	 };
	
	/**
	 * 按照指定宽度生成随机字符串
	 * @param _width 字符串的宽度
	 * @return 随机字符串
	 */
	static protected String randomString(int _width){
		int width = _width <= 0 ? 6 : _width;
		char [] ret = new char[width];
		Random ran = new Random();
		for (int i = 0 ; i < width ; i ++){
			int intValue = ran.nextInt(62) % 62;
			ret[i] = Chars[intValue];
		}
		
		return new String(ret);
	}
}
