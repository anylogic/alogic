package com.alogic.rpc;

/**
 * Call回调
 * @author yyduan
 *
 */
public interface Callback {
	
	/**
	 * 调用完成
	 * @param id 调用id
	 * @param method 方法
	 * @param result 结果
	 * @param params 参数
	 * @param ctx 调用上下文
	 */
	public void onFinish(String id,String method,Result result,Parameters params,Object ctx);
	
	/**
	 * 调用异常
	 * @param id 调用id
	 * @param result 结果
 	 * @param method 方法
	 * @param params 参数
	 * @param ctx 上下文
	 * @param t 异常
	 */
	public void onException(String id,String method,Result result,Parameters params,Object ctx,Throwable t);
}
