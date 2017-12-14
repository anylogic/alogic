package com.alogic.timer.matcher;

import java.util.Date;

import com.alogic.timer.core.ContextHolder;
import com.alogic.timer.core.Matcher.Abstract;
import com.anysoft.util.Properties;

/**
 * 调度时间匹配器(只调度一次)
 * 
 * @author duanyy
 * @since 1.6.3.37
 */
public class Once extends Abstract {
	/**
	 * 已调度次数
	 */
	protected int count = 0;

	public void configure(Properties p) {
		//nothing to do
	}

	public boolean match(Date _last, Date _now,ContextHolder ctx) {
		count ++;
		return count <= 1;
	}

	public boolean isTimeToClear() {
		return count > 1;
	}
}
