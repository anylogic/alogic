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
 * @version 1.6.9.2 [20170601 duanyy] <br>
 * - 改造TaskCenter模型，以便提供分布式任务处理支持; <br>
 */
public class TaskCenterTaskCommitter extends DoerCommitter.Abstract{

	@Override
	protected void onCommit(Doer doer, Task task) {
		if (doer != null){
			TaskCenter tc = TaskCenter.TheFactory.get();
			try {
				if (tc != null){
					tc.dispatch(doer.getQueue(),task);
				}else{
					logger.error("Can not find a valid task center.Fail to submit this task.");
				}
			}catch (Exception ex){
				logger.error("Fail to commit task to task center,queue:" + doer.getQueue());
			}
		}
	}

}
