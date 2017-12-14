package com.alogic.timer.matcher;

import java.util.Calendar;
import java.util.Date;

import com.alogic.timer.core.ContextHolder;
import com.alogic.timer.core.Matcher.Abstract;
import com.alogic.timer.matcher.util.SetValueMatcher;
import com.alogic.timer.matcher.util.parser.HourOfDay;
import com.alogic.timer.matcher.util.parser.Minute;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 日期匹配器Daily
 * 
 * <p>日期匹配器在alogic-doer框架中用于确定timer的触发时机。Daily指的是确定在每天的触发时机，支持参数minutes和hours。<br>
 * - minutes，用于定义在哪些分钟触发，支持Crontab中minute语法；<br>
 * - hours，用于定义在哪些小时触发，支持Crontab中的hour语法。<br>
 * 
 * <p>例如，可以定义一个匹配器，每天的8点30,12点30触发。<br>
 * {@code
 * 		<matcher module="Daily" minutes="30" hours="8,12">
 * }
 * 
 * @author zhangzundong
 * @since 1.6.3.40
 */
public class Daily extends Abstract {
	public Daily(){
		this("00 *");
	}
	protected SetValueMatcher minutes = null;
	protected SetValueMatcher hours = null;
	
	public Daily(String _Daily){
		parseCrontab(_Daily);
	}
	
	protected void parseCrontab(String _Daily){
		String[] __items = _Daily.split(" ");
		String[] __Daily = new String[2];
		for (int i = 0 ;i < 2; i ++){
			if (i < __items.length){
				__Daily[i] = __items[i];
			}else{
				__Daily[i] = "*";
			}
		}
		
		minutes = new SetValueMatcher(__Daily[0],new Minute());
		hours = new SetValueMatcher(__Daily[1],new HourOfDay());
	}
	
	protected int lastMinute = -1;

	public boolean isTimeToClear(){
		return false;
	}

	public void configure(Properties p) {
		parseCrontab(PropertiesConstants.getString(p,"minutes", "00")
				+" "+PropertiesConstants.getString(p,"hours", "*"));
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
		return minutes.match(minute) && hours.match(hour);
	}
}