package com.alogic.timer.core;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 基于ScheduledThreadPoolExecutor的提交实现
 * 
 * @author duanyy
 * @since 1.6.3.37
 */
public class ThreadPoolTaskCommitter extends DoerCommitter.Abstract{	
	public ThreadPoolTaskCommitter(){
		
	}
	
	public ThreadPoolTaskCommitter(boolean _async){
		async = _async;
	}
	
	protected ScheduledThreadPoolExecutor exec = new  ScheduledThreadPoolExecutor(5);
	protected void onCommit(Doer doer, Task task) {
		doer.setCurrentTask(task);
		
		if (async){
			exec.schedule(doer, 0, TimeUnit.MILLISECONDS);
		}else{
			doer.run();
		}
	}
	
	public void configure(Properties p) throws BaseException {
		super.configure(p);
		
		async = PropertiesConstants.getBoolean(p,"async",async,true);
	}
	
	protected boolean async = true;
}
