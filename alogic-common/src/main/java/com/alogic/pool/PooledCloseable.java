package com.alogic.pool;

/**
 * 通过CloseAware来关闭自身
 * 
 * @author duanyy
 * @since 1.6.6.8
 */
public interface PooledCloseable {
	/**
	 * 关闭对象，替代close
	 */
	public void poolClose();
	
	/**
	 * 注册监听器
	 * @param listener 监听器
	 */
	public void register(CloseAware listener);
	
	/**
	 * 注销监听器
	 * @param listener 监听器
	 */
	public void unregister(CloseAware listener);
}
