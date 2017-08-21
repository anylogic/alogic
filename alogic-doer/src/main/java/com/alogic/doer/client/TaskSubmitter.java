package com.alogic.doer.client;

import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alogic.doer.core.TaskCenter;
import com.alogic.timer.core.Task;
import com.anysoft.util.DefaultProperties;
import com.anysoft.util.KeyGen;
import com.anysoft.util.PropertiesConstants;

/**
 * 任务提交者
 * 
 * @author duanyy
 * 
 * @since 1.6.3.4
 * @version 1.6.3.6 [20150316 duanyy] <br>
 * - 增加方法getTaskReport(String, String) <br>
 * 
 * @version 1.6.3.37 [20150806 duanyy] <br>
 * - 修改submit方法，增加任务ID的返回 <br>
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 * 
 * @version 1.6.9.2 [20170601 duanyy] <br>
 * - 改造TaskCenter模型，以便提供分布式任务处理支持; <br>
 * 
 * @version 1.6.9.3 [20170615 duanyy] <br>
 * - 修正taskId不规范问题 <br>
 * 
 * @version 1.6.9.8 [20170821] <br>
 * - 任务id修改为18位数字(当前时间戳+随机数字) <br>
 */
public class TaskSubmitter{
	
	/**
	 * a logger of log4j
	 */
	public static Logger logger = LoggerFactory.getLogger(TaskSubmitter.class);
	
	/**
	 * 提交任务
	 * 
	 * @param event 本次事件的id
	 * @param queue 提交的目标队列
	 * @param id 任务ID
	 * @param parameters 任务参数
	 * 
	 * @return 任务ID
	 */
	static public String submit(String event,String queue,String id,Map<String,String> parameters){
		TaskCenter tc = TaskCenter.TheFactory.get();
		
		if (tc != null){
			Task task = new Task.Default(id,event,parameters);
			tc.dispatch(queue,task);
			return id;
		}else{
			logger.error("Can not find a valid task center.Fail to submit this task.");
			return null;
		}
	}
	
	/**
	 * 提交任务
	 * 
	 * @param event 本次事件的id
	 * @param queue 提交的目标队列
	 * @param parameters 任务参数
	 * 
	 * @return 任务ID
	 */
	static public String submit(String event,String queue,Map<String,String> parameters){
		TaskCenter tc = TaskCenter.TheFactory.get();
		
		if (tc != null){
			String id = newTaskId();
			Task task = new Task.Default(id,event,parameters);
			tc.dispatch(queue,task);
			return id;
		}else{
			logger.error("Can not find a valid task center.Fail to submit this task.");
			return null;
		}
	}	
	
	/**
	 * 提交任务
	 * 
	 * @param event 本次事件的id
	 * @param parameters 任务参数
	 * 
	 * @return 任务ID
	 */
	static public String submit(String event,Map<String,String> parameters){
		TaskCenter tc = TaskCenter.TheFactory.get();		
		if (tc != null){
			String id = newTaskId();
			Task task = new Task.Default(id,event,parameters);
			String queue = parameters.get("queue");
			tc.dispatch(StringUtils.isEmpty(queue)?"default":queue,task);
			return id;
		}else{
			logger.error("Can not find a valid task center.Fail to submit this task.");
			return null;
		}
	}
	
	/**
	 * 提交任务
	 * 
	 * @param event 本次事件的id
	 * @param queue 提交的目标队列
	 * @param id 任务ID
	 * @param parameters 任务参数
	 * 
	 * @return 任务ID
	 */
	static public String submit(String event,String queue,String id,DefaultProperties parameters){
		TaskCenter tc = TaskCenter.TheFactory.get();
		if (tc != null){
			Task task = new Task.Default(id,event,parameters);
			tc.dispatch(queue,task);
			return id;
		}else{
			logger.error("Can not find a valid task center.Fail to submit this task.");
			return null;
		}
	}	
	
	/**
	 * 提交任务
	 * 
	 * @param event 本次事件的id
	 * @param queue 提交的目标队列
	 * @param parameters 任务参数
	 * 
	 * @return 任务ID
	 */
	static public String submit(String event,String queue,DefaultProperties parameters){
		TaskCenter tc = TaskCenter.TheFactory.get();
		if (tc != null){
			String id = newTaskId();
			Task task = new Task.Default(id,event,parameters);
			tc.dispatch(queue,task);
			return id;
		}else{
			logger.error("Can not find a valid task center.Fail to submit this task.");
			return null;
		}
	}		
	
	/**
	 * 提交任务
	 * 
	 * @param event 本次事件的id
	 * @param parameters 任务参数
	 * 
	 * @return 任务ID
	 */
	static public String submit(String event,DefaultProperties parameters){
		TaskCenter tc = TaskCenter.TheFactory.get();		
		if (tc != null){
			String id = newTaskId();
			Task task = new Task.Default(id,event,parameters);
			String queue = PropertiesConstants.getString(parameters,"queue","default");
			tc.dispatch(queue,task);
			return id;
		}else{
			logger.error("Can not find a valid task center.Fail to submit this task.");
			return null;
		}
	}		
	
	/**
	 * 生成一个任务id
	 * @return 任务id
	 */
	protected static String newTaskId(){
		return String.format("%d%s",System.currentTimeMillis(),KeyGen.uuid(5, 0, 9));
	}
}
