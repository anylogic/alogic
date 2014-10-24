package com.logicbus.backend.timer.matcher;

import java.util.Calendar;
import java.util.Date;

import com.anysoft.util.Properties;
import com.logicbus.backend.timer.Matcher;
import com.logicbus.backend.timer.util.SetValueMatcher;
import com.logicbus.backend.timer.util.parser.DayOfMonthItemParser;
import com.logicbus.backend.timer.util.parser.DayOfWeekItemParser;
import com.logicbus.backend.timer.util.parser.HourOfDayItemParser;
import com.logicbus.backend.timer.util.parser.MinuteItemParser;
import com.logicbus.backend.timer.util.parser.MonthOfYearItemParser;

/**
 * 日期调度器(crontab调度)
 *  
 * <p>按crontab调度，所需参数从config中获取，包括：</p>
 * - crontab：crontab表，支持crontab语法
 * <p>crontab语法如下：</p>
 * 
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * minute hour day-of-month month-of-year day-of-week
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * 
 * <p>crontab语法包括5个配置项，如果缺失，则认为是*,例如"30 08"则自动补充为"30 08 * * *"</p>
 * <p>对于day-of-week，支持缩写，大小写不敏感，例如：Mon,Tue,Web,Thu,Fri,Sat,Sun等</p>
 * <p>对于month-of-year，支持缩写，大小写不敏感，例如：Jan,Feb,Mar,Apr,May,June,July,Aug,Sept,Oct,Nov,Dec等</p>
 * 
 * <p>我们配置一个定时器，每月1日的8点30调度</p>
 * 
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <timer matcher="com.logicbus.backend.timer.iterator.Crontab" crontab="30 08 01 * *"/>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * 
 * @author duanyy
 *
 */
public class Crontab implements Matcher {
	public Crontab(){
		//缺省状态下，每小时0分钟时刻进行调度
		this("00 * * * *");
	}
	protected SetValueMatcher minutes = null;
	protected SetValueMatcher hours = null;
	protected SetValueMatcher daysOfWeek = null;
	protected SetValueMatcher daysOfMonth = null;
	protected SetValueMatcher monthsOfYear = null;
	
	public Crontab(String _crontab){
		parseCrontab(_crontab);
	}
	
	protected void parseCrontab(String _crontab){
		String[] __items = _crontab.split(" ");
		String[] __crontabs = new String[5];
		for (int i = 0 ;i < 5; i ++){
			if (i < __items.length){
				__crontabs[i] = __items[i];
			}else{
				__crontabs[i] = "*";
			}
		}
		
		minutes = new SetValueMatcher(__crontabs[0],new MinuteItemParser());
		hours = new SetValueMatcher(__crontabs[1],new HourOfDayItemParser());
		daysOfMonth = new SetValueMatcher(__crontabs[2],new DayOfMonthItemParser());
		monthsOfYear = new SetValueMatcher(__crontabs[3],new MonthOfYearItemParser());
		daysOfWeek = new SetValueMatcher(__crontabs[4],new DayOfWeekItemParser());
	}
	protected int lastMinute = -1;
	public boolean match(Date _last, Date _now, Properties _config) {
		if (_last == null){
			if (_config != null){
				parseCrontab(_config.GetValue("crontab", "00 * * * *"));
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
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		int monthOfYear = calendar.get(Calendar.MONTH) + 1;
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		return minutes.match(minute) && hours.match(hour) && daysOfMonth.match(dayOfMonth)
				&& monthsOfYear.match(monthOfYear)&& daysOfWeek.match(dayOfWeek);
	}
	public boolean isTimeToClear(){
		return false;
	}
}