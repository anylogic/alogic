package com.logicbus.backend.timer.matcher;

import java.util.Calendar;
import java.util.Date;

import com.anysoft.util.Properties;
import com.logicbus.backend.timer.Matcher;
import com.logicbus.backend.timer.util.SetValueMatcher;
import com.logicbus.backend.timer.util.parser.DayOfMonthItemParser;
import com.logicbus.backend.timer.util.parser.HourOfDayItemParser;
import com.logicbus.backend.timer.util.parser.MinuteItemParser;

/**
 * 日期调度器(每月调度)
 *  
 * <p>每月调度，所需参数从config中获取，包括：</p>
 * - minutes：时刻点（分钟），支持crontab中的minute语法
 * - hours:时刻点（小时），支持crontab中的hour语法
 * - days:时刻点（天），支持crontab中的day-of-month语法
 * 
 * <p>我们配置一个定时器，每月1日的8点30调度</p>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <timer matcher="com.logicbus.backend.timer.iterator.Monthly" minutes="30" hours="08" days="1"/>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * @see Crontab
 * @author duanyy
 *
 */
public class Monthly implements Matcher {
	public Monthly(){
		//缺省状态下，每小时0分钟时刻进行调度
		this("00","00","00");
	}
	protected SetValueMatcher minutes = null;
	protected SetValueMatcher hours = null;
	protected SetValueMatcher days = null;
	public Monthly(String _minutes,String _hours,String _days){
		minutes = new SetValueMatcher(_minutes,new MinuteItemParser());
		hours = new SetValueMatcher(_hours,new HourOfDayItemParser());
		days = new SetValueMatcher(_days,new DayOfMonthItemParser());
	}
	protected int lastMinute = -1;
	public boolean match(Date _last, Date _now, Properties _config) {
		if (_last == null){
			if (_config != null){
				minutes.parsePattern(_config.GetValue("minutes","00"));
				hours.parsePattern(_config.GetValue("hours","00"));
				days.parsePattern(_config.GetValue("days","00"));
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
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		return minutes.match(minute) && hours.match(hour) && days.match(day);
	}
	public boolean isTimeToClear(){
		return false;
	}
}