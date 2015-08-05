package com.alogic.timer.matcher;

import java.util.Calendar;
import java.util.Date;

import com.alogic.timer.ContextHolder;
import com.alogic.timer.Matcher.Abstract;
import com.alogic.timer.matcher.util.SetValueMatcher;
import com.alogic.timer.matcher.util.parser.DayOfMonth;
import com.alogic.timer.matcher.util.parser.DayOfWeek;
import com.alogic.timer.matcher.util.parser.HourOfDay;
import com.alogic.timer.matcher.util.parser.Minute;
import com.alogic.timer.matcher.util.parser.MonthOfYear;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * Crontab
 * <p>按crontab调度，所需参数从配置参数中获取，包括：</p>
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
 * @author duanyy
 *
 */
public class Crontab extends Abstract {
	public Crontab(){
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
		
		minutes = new SetValueMatcher(__crontabs[0],new Minute());
		hours = new SetValueMatcher(__crontabs[1],new HourOfDay());
		daysOfMonth = new SetValueMatcher(__crontabs[2],new DayOfMonth());
		monthsOfYear = new SetValueMatcher(__crontabs[3],new MonthOfYear());
		daysOfWeek = new SetValueMatcher(__crontabs[4],new DayOfWeek());
	}
	
	protected int lastMinute = -1;

	public boolean isTimeToClear(){
		return false;
	}

	@Override
	public void configure(Properties p) throws BaseException {
		parseCrontab(PropertiesConstants.getString(p,"crontab", "00 * * * *"));
	}

	@Override
	public boolean match(Date _last, Date _now,ContextHolder ctx) {
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
}