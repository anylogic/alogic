package com.alogic.doer.core;

import org.w3c.dom.Element;

import com.alogic.doer.core.TaskReport.TaskState;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlTools;
import com.anysoft.xscript.CompileWatcher;
import com.anysoft.xscript.ExecuteWatcher;
import com.anysoft.xscript.Script;
import com.anysoft.xscript.ScriptLogInfo;
import com.anysoft.xscript.Statement;

/**
 * 脚本处理者
 * 
 * <p>
 * 通过xscript来实现任务
 * 
 * @author duanyy
 * 
 */
public class ScriptDoer extends TaskDoer.Abstract{
	
	public void dispatch(Task task) throws BaseException {
		currentTask = task;
		try {
			// 向队列报告任务已经开始
			queue.reportTaskState(task.id(), TaskState.Running, 0);
			if (script == null) {
				logger.error("Can not find script element");
				queue.reportTaskState(task.id(), TaskState.Failed, 10000);
			} else {
				Statement stmt = new TheScript(this, "script", null);
				stmt.compile(script, task.getParameters(),
						new CompileWatcher.Default());
				// 执行任务
				stmt.execute(task.getParameters(), new ExecuteWatcher.Default());
				// 任务完成
				queue.reportTaskState(task.id(), TaskState.Done, 10000);
			}
		} catch (Throwable t) {
			logger.error("Failed to execute script", t);
			// 任务失败
			queue.reportTaskState(task.id(), TaskState.Failed, 10000);
		} finally {
			currentTask = null;
		}
	}
	
	/**
	 * 当前处理的任务
	 */
	private Task currentTask = null;
	
	private Element script = null;
	
	public void onConfigure(Element _e, Properties p) {
		script = XmlTools.getFirstElementByPath(_e, "script");
	}
	
	/**
	 * 处理进度报告
	 * 
	 * @param logInfo 日志信息
	 */
	public void report(ScriptLogInfo logInfo) {
		if (currentTask != null){
			//当前有任务处理才有意义
			String id = logInfo.activity();
			String taskId = currentTask.id();
			
			if (id.equals(taskId)){
				//报告的是整个任务的进度
				int progress = logInfo.progress();
				
				if (progress >= -1){
					//百分比进度
					queue.reportTaskState(taskId, TaskState.Running, progress);
				}
			}
		}
	}	
	
	/**
	 * TheScript
	 * <p>
	 * 对xscript中的Script进行了重写，重点在截取日志信息。
	 * 
	 * @author duanyy
	 *
	 */
	protected static class TheScript extends Script{
		protected ScriptDoer doer = null;
		
		public TheScript(ScriptDoer _doer,String xmlTag, Statement _parent) {
			super(xmlTag, _parent);
			doer = _doer;
		}
		
		public void log(ScriptLogInfo logInfo){
			super.log(logInfo);
			if (doer != null){
				doer.report(logInfo);
			}			
		}
	}
}
