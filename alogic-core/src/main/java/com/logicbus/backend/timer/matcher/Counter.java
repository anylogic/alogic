package com.logicbus.backend.timer.matcher;

import java.util.Date;

import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.timer.Matcher;


/**
 * 调度时间匹配器(按次数匹配)
 *  
 * <p>按次数进行匹配，所需参数从config中获取，包括：</p>
 * - count:调度次数，缺省值为1,如果count小于等于0，则无限制调度
 * - interval:调度时间间隔，单位毫秒，缺省值为1000
 * 
 * 当count<=0时，和{@link Interval Interval}模式模式一致。</p>
 * 
 * <p>我们配置一个定时器，共调度10次，每次间隔为5分钟</p>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <timer matcher="com.logicbus.backend.timer.matcher.Counter" count="10" interval="3000000"/>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * 
 * @author duanyy
 *
 */
public class Counter implements Matcher {

	protected long count = 1;
	protected long interval = 1000;
	protected long scheduled_count = 0;
	
	public Counter(){
		
	}
	
	public Counter(long _count,long _interval){
		count = _count;
		interval = _interval;
	}
	
	public boolean match(Date _last,Date _now, Properties _config) {
		if (_last == null){
			//读取参数
			if (_config != null){
				count = PropertiesConstants.getLong(_config,"count",count);
				interval = PropertiesConstants.getLong(_config,"interval",interval);
			}
		}
		
		if (count <= 0 || count >= scheduled_count){
			//还可以继续
			if (_last == null? true :(_now.getTime() - _last.getTime() >= interval)){
				scheduled_count ++;
				return true;
			}
		}
		return false;
	}

	public boolean isTimeToClear(){
		return scheduled_count > count;
	}
}
