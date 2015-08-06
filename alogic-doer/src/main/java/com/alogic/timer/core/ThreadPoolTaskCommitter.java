package com.alogic.timer.core;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 基于ScheduledThreadPoolExecutor的提交实现
 * 
 * @author duanyy
 * @since 1.6.3.37
 */
public class ThreadPoolTaskCommitter extends DoerCommitter.Abstract{	
	protected ScheduledThreadPoolExecutor exec = new  ScheduledThreadPoolExecutor(5);
	protected void onCommit(Doer doer, Task task) {
		doer.setCurrentTask(task);
		exec.schedule(doer, 0, TimeUnit.MILLISECONDS);
	}
}
