package com.anysoft.pool;

/**
 * Close感知器
 * 
 * @author duanyy
 *
 * @since 1.3.7
 * 
 */
public interface CloseAware<pooled extends AutoCloseable> {

	/**
	 * 关闭Pooled对象
	 * 
	 * @param _pooled
	 * 
	 */
	public void closeObject(pooled _pooled);
}
