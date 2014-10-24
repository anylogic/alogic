package com.anysoft.loadbalance;

import java.text.DecimalFormat;
import java.util.Map;

import org.w3c.dom.Element;

import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 缺省的LoadCounter
 * 
 * @author duanyy
 * 
 * @since 1.5.3
 * 
 */
public class DefaultCounter implements LoadCounter {

	public DefaultCounter(Properties p){
		cycle = PropertiesConstants.getLong(p,"loadbalance.cycle",cycle);
		maxErrorTimes = PropertiesConstants.getInt(p,"loadbalance.maxtimes",maxErrorTimes);
		retryInterval = PropertiesConstants.getInt(p,"loadbalance.retryinterval",retryInterval);
	}
	
	/**
	 * 数据统计周期，30分钟
	 */
	protected long cycle = 30 * 60 * 1000;
	
	/**
	 * 最大允许的连续错误次数
	 */
	protected int maxErrorTimes = 3;
	
	/**
	 * 重试间隔，缺省５分钟
	 */
	protected int retryInterval = 5 * 60 * 1000;	
	
	/**
	 * 使用次数
	 */
	protected volatile long times = 0;
	
	/**
	 * 平均时间
	 */
	protected volatile double duration = 0.0;
		
	/**
	 * 上次访问时间
	 */
	protected long lastVisitedTime = 0;
	
	/**
	 * 连续错误次数
	 */
	protected volatile int errorTimes = 0;
	
	/**
	 * 错误发生的时间
	 */
	protected volatile long lastErrorTime = 0;

	
	
	public void count(long _duration, boolean error) {
		long now = System.currentTimeMillis();

		if (now / cycle - lastVisitedTime / cycle == 0){
			//和上次记录处于同一个周期
			duration = (duration * times + _duration) / (times + 1);
			
			times ++;
		}else{
			duration = _duration;
			
			times = 1;
		}
		
		lastVisitedTime = now;
		
		if (error){
			errorTimes ++;
			lastErrorTime = now;
		}else{
			errorTimes = 0;
		}
	}

	
	public void report(Element xml) {
		if (xml != null){
			xml.setAttribute("module", getClass().getName());
			
			xml.setAttribute("times",String.valueOf(times));
			xml.setAttribute("duration", df.format(duration));
			xml.setAttribute("error", String.valueOf(errorTimes));
			
			xml.setAttribute("valid", Boolean.toString(isValid()));
		}
	}

	
	public void report(Map<String, Object> json) {
		if (json != null){
			json.put("module", getClass().getName());
			
			json.put("times", times);
			json.put("duration", df.format(duration));
			json.put("error", String.valueOf(errorTimes));
			
			json.put("valid",isValid());
		}
	}

	
	public long getTimes() {
		return times;
	}

	
	public double getDuration() {
		return duration;
	}

	
	public boolean isValid() {
		long now = System.currentTimeMillis();
		//下列条件下，认为该load是有效的：
		//当连续错误时间小于最大允许错误时间
		//或
		//已经到了重试的时间
		return errorTimes < maxErrorTimes || now - lastErrorTime > retryInterval;
	}

	/**
	 * double数值格式化器
	 */
	private static DecimalFormat df = new DecimalFormat("#.00"); 
}
