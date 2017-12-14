package com.alogic.timer.matcher;

import java.util.Date;

import com.alogic.timer.core.ContextHolder;
import com.alogic.timer.core.Matcher.Abstract;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 调度时间匹配器(按次数匹配)
 * 
 * @author duanyy
 * @since 1.6.3.37
 */
public class Counter extends Abstract {
	
	/**
	 * 总的调度次数，可通过参数count配置
	 */
	protected long count = 1;
	
	/**
	 * 调度间隔，可通过参数interval配置
	 */
	protected long interval = 1000;
	
	/**
	 * 已调度次数
	 */
	protected long scheduled_count = 0;

	public Counter(){
		
	}
	
	public Counter(long _count,long _interval){
		count = _count;
		interval = _interval;
	}	
	
	public void configure(Properties p) {
		count = PropertiesConstants.getLong(p,"count",count,true);
		interval = PropertiesConstants.getLong(p,"interval",interval,true);
	}

	public boolean match(Date _last, Date _now,ContextHolder ctx) {
		if (count <= 0 || count >= scheduled_count){
			//还可以继续
			if (_last == null? true :(_now.getTime() - _last.getTime() >= interval)){
				scheduled_count ++;
				return true;
			}
		}
		return false;
	}

	public boolean isTimeToClear() {
		return scheduled_count > count;
	}

}
