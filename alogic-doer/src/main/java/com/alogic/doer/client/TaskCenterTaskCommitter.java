package com.alogic.doer.client;

import com.alogic.doer.core.TaskCenter;
import com.alogic.timer.core.Doer;
import com.alogic.timer.core.DoerCommitter;
import com.alogic.timer.core.Task;

/**
 * 基于TaskCenter的任务提交者
 * 
 * @author duanyy
 *
 */
public class TaskCenterTaskCommitter extends DoerCommitter.Abstract{

	@Override
	protected void onCommit(Doer doer, Task task) {
		TaskCenter tc = TaskCenter.TheFactory.get();
		try {
			if (tc != null){
				tc.dispatch(task);
			}else{
				logger.error("Can not find a valid task center.Fail to submit this task.");
			}
		}catch (Exception ex){
			logger.error("Fail to commit task to task center,queue:" + task.queue());
		}finally{
			if (doer != null){
				//不去运行，直接complete，因为任务已经提交给了TaskCenter
				doer.complete();
			}
		}
	}

}
