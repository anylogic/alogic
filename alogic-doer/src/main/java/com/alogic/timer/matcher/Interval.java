package com.alogic.timer.matcher;

import java.util.Date;

import com.alogic.timer.Matcher.Abstract;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 调度时间匹配器(间隔)
 * 
 * @author duanyy
 * @since 1.6.3.37
 */
public class Interval extends Abstract {
	/**
	 * 间隔时间，可通过参数interval配置
	 */
	protected long interval = 1000;
	
	public Interval(){
	}
	
	public Interval(long _interval){
		interval = _interval;
	}
	
	@Override
	public void configure(Properties p) throws BaseException {
		interval = PropertiesConstants.getLong(p,"interval",interval);
	}

	@Override
	public boolean match(Date _last, Date _now,Properties ctx) {
		return (_now.getTime() - _last.getTime() >= interval);
	}

	@Override
	public boolean isTimeToClear() {
		//never clear
		return false;
	}

}
