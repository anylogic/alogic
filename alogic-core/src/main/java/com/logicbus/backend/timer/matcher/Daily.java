package com.logicbus.backend.timer.matcher;

import java.util.Calendar;
import java.util.Date;

import com.anysoft.util.Properties;
import com.logicbus.backend.timer.Matcher;
import com.logicbus.backend.timer.util.SetValueMatcher;
import com.logicbus.backend.timer.util.parser.HourOfDayItemParser;
import com.logicbus.backend.timer.util.parser.MinuteItemParser;

/**
 * 日期调度器(每天调度)
 *  
 * <p>每天调度，所需参数从config中获取，包括：</p>
 * - minutes：时刻点（分钟），支持crontab中的minute语法
 * - hours:时刻点（小时），支持crontab中的hour语法
 * 
 * <p>我们配置一个定时器，每天的8点30,12点30调度</p>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <timer matcher="com.logicbus.backend.timer.iterator.Daily" minutes="30" hours="08,12"/>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * 
 * @see Crontab
 * @author duanyy
 *
 */
public class Daily implements Matcher {
	public Daily(){
		//缺省状态下，每小时0分钟时刻进行调度
		this("00","00");
	}
	protected SetValueMatcher minutes = null;
	protected SetValueMatcher hours = null;
	public Daily(String _minutes,String _hours){
		minutes = new SetValueMatcher(_minutes,new MinuteItemParser());
		hours = new SetValueMatcher(_hours,new HourOfDayItemParser());
	}
	protected int lastMinute = -1;
	public boolean match(Date _last, Date _now, Properties _config) {
		if (_last == null){
			if (_config != null){
				minutes.parsePattern(_config.GetValue("minutes","00"));
				hours.parsePattern(_config.GetValue("hours","00"));
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
		return minutes.match(minute) && hours.match(hour);
	}
	public boolean isTimeToClear(){
		return false;
	}
}
