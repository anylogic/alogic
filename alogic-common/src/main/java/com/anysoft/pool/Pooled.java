package com.anysoft.pool;

/**
 * 可Pool的对象
 * 
 * @author duanyy
 * @since 1.1.0
 * 
 * @version 1.2.2 [20140722 duanyy]
 * - 改为从AutoCloseable集成
 */
public interface Pooled extends AutoCloseable{
}
