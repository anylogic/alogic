package com.alogic.ha;

/**
 * 主备切换监听器
 * 
 * @author yyduan
 * @since 1.6.8.3
 */
public interface FailoverListener {
	
	/**
	 * 变成active
	 */
	public void becomeActive();
	
	/**
	 * 变成standby
	 */
	public void becomeStandby();
}
