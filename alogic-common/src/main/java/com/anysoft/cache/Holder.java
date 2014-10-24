package com.anysoft.cache;
import com.anysoft.util.BaseException;

/**
 * 缓存对象的持有者
 * 
 * @author duanyy
 *
 * @param <data> 缓存对象
 * 
 * @since 1.3.0
 * 
 */
public interface Holder<data extends Cacheable>{
	/**
	 * 保存缓存对象
	 * @param _data
	 */
	public void save(String id,data _data) throws BaseException;
}
