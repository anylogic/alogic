package com.alogic.together;

/**
 * 执行监视器 
 * 
 * @author duanyy
 *
 */
public interface ExecuteWatcher {
	
	/**
	 * 执行完毕
	 * @param logiclet 微服务单元
	 * @param ctx 上下文
	 * @param error 是否出错
	 * @param start 开始时间
	 * @param duration 执行时长
	 */
	public void executed(Logiclet logiclet,LogicletContext ctx,boolean error,long start,long duration);
	
	/**
	 * 安静模式
	 * @author duanyy
	 *
	 */
	public static class Quiet implements ExecuteWatcher{

		@Override
		public void executed(Logiclet logiclet, LogicletContext ctx, boolean error,
				long start, long duration) {
			// nothing to do
		}
		
	}
}
