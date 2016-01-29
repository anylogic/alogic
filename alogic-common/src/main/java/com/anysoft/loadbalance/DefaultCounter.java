package com.anysoft.loadbalance;

import java.text.DecimalFormat;
import java.util.Map;

import org.w3c.dom.Element;

import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;

/**
 * 缺省的LoadCounter
 * 
 * @author duanyy
 * 
 * @since 1.5.3
 * @version 1.6.4.31 [20160128 duanyy] <br>
 * - 增加活跃度和健康度接口 <br>
 * - 增加可配置性 <br>
 */
public class DefaultCounter implements LoadCounter {

	/**
	 * double数值格式化器
	 */
	private static DecimalFormat df = new DecimalFormat("#.00");

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


	public DefaultCounter(Properties p){
		configure(p);
	}
	

	@Override
	public int getActiveScore() {
		if (lastVisitedTime <= 0){
			return 0;
		}
		
		long d = System.currentTimeMillis() - lastVisitedTime;
		if (d < cycle){
			return 1000;
		}
		
		return Math.round(cycle * 100.0f / d);
	}


	@Override
	public int getHealthScore() {
		return -1;
	}

	@Override
	public void configure(Element e, Properties p) {
		XmlElementProperties props = new XmlElementProperties(e,p);
		configure(props);
	}

	@Override
	public void configure(Properties p) {
		cycle = PropertiesConstants.getLong(p,"loadbalance.cycle",cycle);
		maxErrorTimes = PropertiesConstants.getInt(p,"loadbalance.maxtimes",maxErrorTimes);
		retryInterval = PropertiesConstants.getInt(p,"loadbalance.retryinterval",retryInterval);
	} 	
	
	@Override
	public void count(long dur, boolean error) {
		long now = System.currentTimeMillis();

		if (now / cycle - lastVisitedTime / cycle == 0){
			//和上次记录处于同一个周期
			duration = (duration * times + dur) / (times + 1);
			
			times ++;
		}else{
			duration = dur;
			
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

	@Override
	public void report(Element xml) {
		if (xml != null){
			xml.setAttribute("module", getClass().getName());
			
			xml.setAttribute("times",String.valueOf(times));
			xml.setAttribute("duration", df.format(duration));
			xml.setAttribute("error", String.valueOf(errorTimes));
			
			xml.setAttribute("valid", Boolean.toString(isValid()));
		}
	}

	@Override
	public void report(Map<String, Object> json) {
		if (json != null){
			json.put("module", getClass().getName());
			
			json.put("times", times);
			json.put("duration", df.format(duration));
			json.put("error", String.valueOf(errorTimes));
			
			json.put("valid",isValid());
		}
	}

	@Override
	public long getTimes() {
		return times;
	}

	@Override
	public double getDuration() {
		return duration;
	}

	@Override
	public boolean isValid() {
		long now = System.currentTimeMillis();
		//下列条件下，认为该load是有效的：
		//当连续错误时间小于最大允许错误时间
		//或
		//已经到了重试的时间
		return errorTimes < maxErrorTimes || now - lastErrorTime > retryInterval;
	}
}
