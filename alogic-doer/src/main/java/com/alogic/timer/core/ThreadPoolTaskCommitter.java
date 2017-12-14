package com.alogic.timer.core;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 基于ScheduledThreadPoolExecutor的提交实现
 * 
 * @author duanyy
 * @since 1.6.3.37
 * 
 * @version 1.6.9.2 [20170601 duanyy] <br>
 * - 改造TaskCenter模型，以便提供分布式任务处理支持; <br>
 */
public class ThreadPoolTaskCommitter extends DoerCommitter.Abstract{		
	protected boolean async = true;
	
	public ThreadPoolTaskCommitter(){
		
	}
	
	public ThreadPoolTaskCommitter(boolean _async){
		async = _async;
	}
	
	protected ScheduledThreadPoolExecutor exec = new  ScheduledThreadPoolExecutor(5);
	protected void onCommit(Doer doer, Task task) {
		if (async){
			exec.schedule(new Wrapper(doer,task), 0, TimeUnit.MILLISECONDS);
		}else{
			doer.run(task);
		}
	}
	
	public void configure(Properties p) {
		super.configure(p);		
		async = PropertiesConstants.getBoolean(p,"async",async,true);
	}

	public static class Wrapper implements Runnable{
		protected Doer doer = null;
		protected Task task = null;
		
		public Wrapper(Doer doer,Task task){
			this.doer = doer;
			this.task = task;
		}
		
		@Override
		public void run() {
			if (this.doer != null && this.task != null){
				this.doer.run(task);
			}
		}
		
	}
}
