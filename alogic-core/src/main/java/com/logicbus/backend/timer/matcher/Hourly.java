package com.logicbus.backend.timer.matcher;

import java.util.Calendar;
import java.util.Date;

import com.anysoft.util.Properties;
import com.logicbus.backend.timer.Matcher;
import com.logicbus.backend.timer.util.SetValueMatcher;
import com.logicbus.backend.timer.util.parser.MinuteItemParser;

/**
 * 日期调度器(每小时调度)
 *  
 * <p>每小时调度，所需参数从config中获取，包括：</p>
 * - minutes：时刻点（分钟），支持crontab中的minute语法
 * 
 * <p>我们配置一个定时器，每个小时的第5，10，15，25分钟调度</p>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <timer matcher="com.logicbus.backend.timer.iterator.Hourly" minutes="05,10,15,25"/>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * 
 * @see Crontab
 * @author duanyy
 *
 */
public class Hourly implements Matcher {
	public Hourly(){
		//缺省状态下，每小时0分钟时刻进行调度
		this("0");
	}
	protected SetValueMatcher matcher = null;
	public Hourly(String _minutes){
		matcher = new SetValueMatcher(_minutes,new MinuteItemParser());
	}
	protected int lastMinute = -1;
	public boolean match(Date _last, Date _now, Properties _config) {
		if (_last == null){
			if (_config != null){
				matcher.parsePattern(_config.GetValue("minutes","00"));
			}
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(_now);
		int minute = calendar.get(Calendar.MINUTE);
		
		//在一分钟之内只允许一次
		if (lastMinute == minute){
			return false;
		}
		lastMinute = minute;
		
		return matcher.match(minute);
	}
	public boolean isTimeToClear(){
		return false;
	}
}
