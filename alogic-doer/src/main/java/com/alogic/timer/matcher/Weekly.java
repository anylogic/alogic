package com.alogic.timer.matcher;

import java.util.Calendar;
import java.util.Date;

import com.alogic.timer.core.ContextHolder;
import com.alogic.timer.core.Matcher.Abstract;
import com.alogic.timer.matcher.util.SetValueMatcher;
import com.alogic.timer.matcher.util.parser.DayOfWeek;
import com.alogic.timer.matcher.util.parser.HourOfDay;
import com.alogic.timer.matcher.util.parser.Minute;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 日期匹配器Weekly
 * 
 * <p>
 * 日期匹配器用于在alogic-doer框架中用于确定timer的触发时机。Weekly指的是确定在每周的触发时机，支持下列参数：
 * - minutes，用于定义在哪些分钟触发，支持Crontab中minute语法；<br>
 * - hours，用于定义在哪些小时触发，支持Crontab中的hour语法；<br>
 * - days，用于定义在周几触发，支持Crontab中的day-of-week语法；<br>
 * 
 * <p>
 * 例如，可以定义一个匹配器，每周星期一的8点30,12点30触发。<br>
 * {@code 
 * 	<matcher module="Daily" minutes="30" hours="8,12" days="Mon"/>
 * }
 * @author zhangzundong
 * @since 1.6.3.40
 */

public class Weekly extends Abstract {
	public Weekly(){
		this("00 * *");
	}
	
	protected SetValueMatcher minutes = null;
	protected SetValueMatcher hours = null;
	protected SetValueMatcher daysOfWeek = null;
	
	public Weekly(String _weekly){
		parseCrontab(_weekly);
	}
	
	protected void parseCrontab(String _weekly){
		String[] __items = _weekly.split(" ");
		String[] __weekly = new String[3];
		for (int i = 0 ;i < 3; i ++){
			if (i < __items.length){
				__weekly[i] = __items[i];
			}else{
				__weekly[i] = "*";
			}
		}
		
		minutes = new SetValueMatcher(__weekly[0],new Minute());
		hours = new SetValueMatcher(__weekly[1],new HourOfDay());
		daysOfWeek = new SetValueMatcher(__weekly[2],new DayOfWeek());
	}
	
	protected int lastMinute = -1;

	public boolean isTimeToClear(){
		return false;
	}

	public void configure(Properties p) throws BaseException {
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
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		
		return minutes.match(minute) && hours.match(hour) && daysOfWeek.match(dayOfWeek);
	}
}