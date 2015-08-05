package com.alogic.timer;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;

/**
 * 基于ScheduledThreadPoolExecutor的提交实现
 * 
 * @author duanyy
 * @since 1.6.3.37
 */
public class ThreadPoolTaskCommitter extends TaskCommitter.Abstract{

	protected void onCommit(Task task, Timer timer) {
		exec.schedule(task, 0, TimeUnit.MILLISECONDS);
	}
	
	public void configure(Properties p) throws BaseException {
		super.configure(p);
	}
	
	protected ScheduledThreadPoolExecutor exec = new  ScheduledThreadPoolExecutor(5);
}
