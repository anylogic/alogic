package com.logicbus.backend.timer;

import java.util.Date;

import com.anysoft.util.Properties;

/**
 * 调度时间匹配器
 * @author duanyy
 *
 */
public interface Matcher {
	/**
	 * 按照规则匹配时间
	 * @param _last 上次调度时间
	 * @param _now 当前时间
	 * @param _config 配置信息
	 * @return 是否匹配
	 */
	public boolean match(Date _last,Date _now,Properties _config);
	
	/**
	 * 是否可以清除
	 * 
	 * <br>
	 * 如果返回为true，框架将从列表中清除该Timer
	 * @return true/false
	 */
	public boolean isTimeToClear();
}
