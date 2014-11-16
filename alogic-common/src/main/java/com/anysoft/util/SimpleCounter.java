package com.anysoft.util;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * 简单统计模型
 * 
 * @author duanyy
 *
 * @since 1.5.2
 */
public class SimpleCounter implements Counter {
	/**
	 * 启动时间
	 */
	private long startTime = System.currentTimeMillis();
		
	/**
	 * 全部统计数据（自服务器启动开始）
	 */
	private CounterUnit total = new CounterUnit();
	
	public CounterUnit getTotal(){return total;}
	
	/**
	 * 当前统计数据（当前周期）
	 */
	private CounterUnit current = new CounterUnit();
	
	public CounterUnit getCurrent(){return current;}
	
	/**
	 * 上次访问时间
	 */
	public long lastVisitedTime = System.currentTimeMillis();
		
	/**
	 * 周期开始时间
	 */
	private long currentCycleStart = System.currentTimeMillis();
	
	/**
	 * 周期
	 */
	private long cycle = 5 * 60 * 1000L;
	
	
	public void count(long _duration, boolean error) {
		total.visited(_duration, error);
		
		long now = System.currentTimeMillis();

		if (now / cycle - lastVisitedTime / cycle == 0){
			//和上次记录处于同一个周期
			current.visited(_duration, error);
		}else{
			current.first(_duration, error);
			currentCycleStart = (now / cycle) * cycle;
		}
		lastVisitedTime = now;
	}
	
	public SimpleCounter(Properties p){
		cycle = getStatCycle(p);
	}
	
	public long getStatCycle(Properties p){
		return PropertiesConstants.getLong(p, "servant.stat.cycle", cycle);
	}
	
	
	public void report(Element root) {
		if (root != null){
			Document doc = root.getOwnerDocument();
			
			root.setAttribute("module", getClass().getName());
			root.setAttribute("start", DateUtil.formatDate(new Date(startTime), "yyyyMMddHHmmss"));
			root.setAttribute("lastVistiedTime", DateUtil.formatDate(new Date(lastVisitedTime), "yyyyMMddHHmmss"));
			root.setAttribute("cycleStart", DateUtil.formatDate(new Date(currentCycleStart), "yyyyMMddHHmmss"));
			
			if (total != null){
				Element stat = doc.createElement("total");
				
				total.report(stat);
				
				root.appendChild(stat);
			}
			
			if (current != null){
				Element stat = doc.createElement("current");
				
				current.report(stat);
				
				root.appendChild(stat);
			}			
		}
	}

	
	public void report(Map<String, Object> json) {
		if (json != null){
			json.put("module", getClass().getName());
			json.put("start", DateUtil.formatDate(new Date(startTime), "yyyyMMddHHmmss"));
			json.put("lastVistiedTime", DateUtil.formatDate(new Date(lastVisitedTime), "yyyyMMddHHmmss"));
			json.put("cycleStart", DateUtil.formatDate(new Date(currentCycleStart), "yyyyMMddHHmmss"));
			
			if (total != null){
				Map<String,Object> stat = new HashMap<String,Object>();
				
				total.report(stat);
				
				json.put("total", stat);
			}
			
			if (current != null){
				Map<String,Object> stat = new HashMap<String,Object>();
				
				current.report(stat);
				
				json.put("current", stat);
			}			
		}
	}
	
	public static final class CounterUnit implements Reportable{
		/**
		 * 服务次数（当前周期）
		 */
		public volatile long times = 0;
		
		/**
		 * 错误次数（当前周期）
		 */
		public volatile long errorTimes = 0;
		
		/**
		 * 最大时长
		 */
		public volatile long max = 0;
		
		/**
		 * 最小时长
		 */
		public volatile long min = 100000;
		
		/**
		 * 平均时长（当期周期）
		 */
		public volatile double avg = 0;
		
		/**
		 * 访问
		 * @param _duration 时长
		 * @param error 是否错误
		 */
		public void visited(long _duration,boolean error){
			//计算平均值
			if (times <= 0){
				avg =  _duration;
			}else{
				avg = (avg * times + _duration) / (times + 1);
			}
				
			//计算次数
			times += 1;
				
			//计算最小值
			if (min > _duration){
				min = _duration;
			}
			
			//计算最大值
			if (max < _duration){
				max = _duration;
			}
			
			errorTimes += (error?1:0);
		}
		
		/**
		 * 首次访问
		 * @param _duration 时长
		 * @param error 是否错误
		 */
		public void first(long _duration,boolean error){
			//计算平均值
			avg =  _duration;
				
			//计算次数
			times = 1;
				
			//计算最小值
			min = _duration;
			
			//计算最大值
			max = _duration;
			
			errorTimes = (error?1:0);
		}		
		
		
		public void report(Element xml) {
			if (xml != null){
				xml.setAttribute("times", String.valueOf(times));
				xml.setAttribute("error", String.valueOf(errorTimes));
				xml.setAttribute("max", String.valueOf(max));
				xml.setAttribute("min", String.valueOf(min));
				xml.setAttribute("avg", df.format(avg));
			}
		}

		
		public void report(Map<String, Object> json) {
			if (json != null){
				json.put("times", String.valueOf(times));
				json.put("error", String.valueOf(errorTimes));
				json.put("max", String.valueOf(max));
				json.put("min", String.valueOf(min));
				json.put("avg", df.format(avg));
			}
		}
		/**
		 * double数值格式化器
		 */
		private static DecimalFormat df = new DecimalFormat("#.00"); 
	}
}
