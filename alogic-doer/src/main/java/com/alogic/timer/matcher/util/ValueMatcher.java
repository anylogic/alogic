package com.alogic.timer.matcher.util;

/**
 * 日期值匹配器
 * 
 * @author duanyy
 * @since 1.6.3.37
 */
public interface ValueMatcher {
	
	/**
	 * 是否匹配
	 * @param value 日期值
	 * @return true|false
	 */
	public boolean match(int value);
}
