package com.anysoft.util.reloader;

import com.anysoft.util.Properties;

/**
 * 自动刷新接口定义
 * @author szduanyy
 *
 */
public interface AutoReloader {
	/**
	 * 是否进行刷新
	 * 根据一定的策略来决定是否进行刷新,策略的参数从props参数中获取。
	 * @param props 变量集
	 * @return 是否刷新
	 */
	public boolean reload(Properties _props);
}
