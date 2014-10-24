package com.logicbus.backend.timer.matcher;

import java.util.Date;

import com.anysoft.util.Properties;
import com.logicbus.backend.timer.Matcher;


/**
 * 调度时间匹配器（仅调度一次）
 * @author duanyy
 * 
 * <p>我们配置一个定时器，只调用一次.</p>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * <timer matcher="com.logicbus.backend.timer.matcher.Once"/>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
public class Once implements Matcher {
	protected int count = 0;
	public boolean match(Date _last, Date _now, Properties _config) {
		count ++;
		return count <= 1;
	}
	public boolean isTimeToClear(){
		return count > 1;
	}
}
