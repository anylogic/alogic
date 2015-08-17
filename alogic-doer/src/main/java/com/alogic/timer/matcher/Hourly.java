package com.alogic.timer.matcher;

import java.util.Calendar;
import java.util.Date;

import com.alogic.timer.core.ContextHolder;
import com.alogic.timer.core.Matcher.Abstract;
import com.alogic.timer.matcher.util.SetValueMatcher;
import com.alogic.timer.matcher.util.parser.Minute;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 *日期匹配器Hourly
 *
 *日期匹配器用于在alogic-doer框架中用于确定timer的触发时机。Hourly指的是确定在每个小时内的触发时机，支持下列参数：<br>
 *- minutes,该参数用于定义每个小时内在哪些分钟触发，支持Crontab中minute语法;<br>
 *<p>
 * 例如，可以定义一个匹配器，每个小时的第5，10，15，25分钟触发。<br>
 * {@code 
 * <matcher module="Hourly" minutes="5,10,15,25"/>
 * }
 * 
 * @author zhangzundong
 * @since 1.6.3.40
 */
public class Hourly extends Abstract {
	public Hourly(){
		this("00");
	}
	protected SetValueMatcher minutes = null;
	
	public Hourly(String _Hourly){
		parseCrontab(_Hourly);
	}
	
	protected void parseCrontab(String _Hourly){
		minutes = new SetValueMatcher(_Hourly,new Minute());
	}
	
	protected int lastMinute = -1;

	public boolean isTimeToClear(){
		return false;
	}

	public void configure(Properties p) throws BaseException {
		parseCrontab(PropertiesConstants.getString(p,"minutes","00"));
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
		return minutes.match(minute);
	}
}