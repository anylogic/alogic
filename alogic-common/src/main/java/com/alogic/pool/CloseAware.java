package com.alogic.pool;

/**
 * Close感知器
 * 
 * @author duanyy
 *
 * @since 1.6.6.8
 */
public interface CloseAware {
	/**
	 * 关闭Pooled对象
	 * 
	 * @param pooled 可关闭的缓冲对象
	 * 
	 */
	public void closeObject(Object pooled);
}
