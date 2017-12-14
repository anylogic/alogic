package com.alogic.timer.matcher;

import java.util.Calendar;
import java.util.Date;

import com.alogic.timer.core.ContextHolder;
import com.alogic.timer.core.Matcher.Abstract;
import com.alogic.timer.matcher.util.SetValueMatcher;
import com.alogic.timer.matcher.util.parser.DayOfMonth;
import com.alogic.timer.matcher.util.parser.HourOfDay;
import com.alogic.timer.matcher.util.parser.Minute;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 日期匹配器Monthly
 * 
 * <p>
 * 日期匹配器用于在alogic-doer框架中用于确定timer的触发时机。Monthly指的是确定在每月的触发时机，支持下列参数：
 * - minutes，用于定义在哪些分钟触发，支持Crontab中minute语法；<br>
 * - hours，用于定义在哪些小时触发，支持Crontab中的hour语法；<br>
 * - days，用于定义在几号触发，支持Crontab中的day-of-month语法；<br>
 *  
 * <p>
 * 例如，可以定义一个匹配器，每月5日的8点30,12点30触发。<br>
 * {@code
 * 	<matcher module="Daily" minutes="30" hours="8,12" days="5"/>
 * }
 * 
 * @author zhangzundong
 * @since 1.6.3.40
 */
public class Monthly extends Abstract {
	public Monthly(){
		this("00 * * *");
	}
	protected SetValueMatcher minutes = null;
	protected SetValueMatcher hours = null;
	protected SetValueMatcher daysOfWeek = null;
	protected SetValueMatcher daysOfMonth = null;

	public Monthly(String _mothly){
		parseCrontab(_mothly);
	}
	
	protected void parseCrontab(String _monthly){
		String[] __items = _monthly.split(" ");
		String[] __monthly = new String[5];
		for (int i = 0 ;i < 3; i ++){
			if (i < __items.length){
				__monthly[i] = __items[i];
			}else{
				__monthly[i] = "*";
			}
		}
		
		minutes = new SetValueMatcher(__monthly[0],new Minute());
		hours = new SetValueMatcher(__monthly[1],new HourOfDay());
		daysOfMonth = new SetValueMatcher(__monthly[2],new DayOfMonth());
	}
	
	protected int lastMinute = -1;

	public boolean isTimeToClear(){
		return false;
	}

	public void configure(Properties p)  {
		parseCrontab(PropertiesConstants.getString(p,"minutes","00")
				+ " " +PropertiesConstants.getString(p,"hours","*")
				+ " " +PropertiesConstants.getString(p,"days","*")
				);
	}

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
		return minutes.match(minute) && hours.match(hour) && daysOfMonth.match(dayOfMonth);
	}
}