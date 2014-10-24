package com.anysoft.cache;

import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.Reportable;

/**
 * interface <code>DataStore</code>
 * 
 * @author duanyy
 * 
 * @param <data>
 * 
 * @since 1.3.0
 * 
 */
public interface DataStore<data extends Cacheable> extends Holder<data>,
		Provider<data>, AutoCloseable, Reportable {
	
	/**
	 * 创建DataStore
	 * @param props
	 * @throws BaseException
	 */
	void create(Properties props) throws BaseException;

	/**
	 * 刷新数据
	 * @throws BaseException
	 */
	void refresh() throws BaseException;
}