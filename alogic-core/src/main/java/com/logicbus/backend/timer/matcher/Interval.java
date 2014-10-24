package com.logicbus.backend.timer.matcher;

import java.util.Date;

import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.timer.Matcher;

/**
 * 调度时间匹配器(按时间间隔调度)
 *  
 * <p>按照时间间隔进行匹配，所需参数从_config中获取，具体包括：</p>
 * - interval:调度时间间隔，单位毫秒，缺省值为1000
 * 
 * <p>我们配置一个定时器，每5分钟调度一次</p>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <timer matcher="com.logicbus.backend.timer.matcher.Interval" interval="3000000"/>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * @author duanyy
 *
 */
public class Interval implements Matcher {
	protected long interval = 1000;
	public Interval(){
		
	}
	
	public Interval(long _interval){
		interval = _interval;
	}
	
	public boolean match(Date _last,Date _now, Properties _config) {
		if (_now == null){
			//读取参数
			if (_config != null){
				interval = PropertiesConstants.getLong(_config,"interval",interval);
			}
		}
		return _last == null? true :(_now.getTime() - _last.getTime() >= interval);
	}
	public boolean isTimeToClear(){
		return false;
	}
}
