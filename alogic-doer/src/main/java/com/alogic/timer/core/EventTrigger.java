package com.alogic.timer.core;

/**
 * 事件触发器
 * 
 * 事件触发器不是一个真正的逻辑处理者，而是和TaskSubmitter配合使用，
 * 将任务作为事件向异步队列触发.
 * 
 * @author yyduan
 *
 * @since 1.6.9.2
 * 
 * 
 */
public class EventTrigger extends Doer.Abstract{

	@Override
	public void execute(Task task) {
		LOG.error("I am just a trigger,i can not execute anything task.");
	}
}
