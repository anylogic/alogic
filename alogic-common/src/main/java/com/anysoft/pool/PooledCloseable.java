package com.anysoft.pool;

/**
 * CloseAware持有者
 * 
 * @author duanyy
 *
 * @since 1.3.7
 */
public interface PooledCloseable<pooled extends AutoCloseable> {
	
	/**
	 * 关闭对象，替代close
	 */
	public void poolClose();
	
	/**
	 * 注册监听器
	 * @param listener
	 */
	public void register(CloseAware<pooled> listener);
	
	/**
	 * 注销监听器
	 * @param listener
	 */
	public void unregister(CloseAware<pooled> listener);
}
