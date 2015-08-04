package com.alogic.timer.matcher;

import java.util.Date;

import com.alogic.timer.Matcher.Abstract;
import com.anysoft.util.BaseException;
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
	
	@Override
	public void configure(Properties p) throws BaseException {
		//nothing to do
	}

	@Override
	public boolean match(Date _last, Date _now,Properties ctx) {
		count ++;
		return count <= 1;
	}

	@Override
	public boolean isTimeToClear() {
		return count > 1;
	}
}
