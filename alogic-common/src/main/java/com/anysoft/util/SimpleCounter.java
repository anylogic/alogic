package com.anysoft.util;

import java.text.DecimalFormat;
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
 * 
 * @version 1.6.4.23 [20160114 duanyy] <br>
 * - 修正初始化的问题。 <br>
 * 
 * @version 1.6.4.31 [20160128 duanyy] <br>
 * - 增加活跃度和健康度接口 <br>
 * - 增加可配置性 <br>
 */
public class SimpleCounter implements Counter {
	/**
	 * 启动时间
	 */
	private long startTime = System.currentTimeMillis();
	
	/**
	 * 上次访问时间
	 */
	private long lastVisitedTime = 0;
	
	/**
	 * 全部统计数据（自服务器启动开始）
	 */
	private CounterUnit total = new CounterUnit();
	
	/**
	 * 当前统计数据（当前周期）
	 */
	private CounterUnit current = new CounterUnit();

	/**
	 * 周期开始时间
	 */
	private long currentCycleStart = System.currentTimeMillis();
	
	/**
	 * 周期
	 */
	private long cycle = 5 * 60 * 1000L;
	
	public SimpleCounter(){
		// Nothing to do
	}
	
	public SimpleCounter(Properties p){
		configure(p);
	}
	
	public CounterUnit getTotal(){return total;}	
	public CounterUnit getCurrent(){return current;}

	@Override
	public void configure(Element e, Properties p) {
		XmlElementProperties props = new XmlElementProperties(e,p);
		configure(props);
	}

	@Override
	public void configure(Properties p) {
		cycle = getStatCycle(p);
	}

	protected long getStatCycle(Properties p){
		return PropertiesConstants.getLong(p, "counter.cycle", 5 * 60 * 1000L);
	}	
	
	@Override
	public int getActiveScore() {
		if (lastVisitedTime <= 0){
			return 0;
		}
		
		long duration = System.currentTimeMillis() - lastVisitedTime;
		if (duration < cycle){
			return 100;
		}
		
		return Math.round(cycle * 100.0f / duration);
	}

	@Override
	public int getHealthScore() {
		return -1;
	}	
	
	@Override
	public void count(long duration, boolean error) {
		total.visited(duration, error);
		
		long now = System.currentTimeMillis();

		if (now / cycle - lastVisitedTime / cycle == 0){
			//和上次记录处于同一个周期
			current.visited(duration, error);
		}else{
			current.first(duration, error);
			currentCycleStart = (now / cycle) * cycle;
		}
		lastVisitedTime = now;
	}
	
	@Override
	public void report(Element root) {
		if (root != null){
			Document doc = root.getOwnerDocument();
			
			root.setAttribute("module", getClass().getName());
			root.setAttribute("start", String.valueOf(startTime));
			root.setAttribute("lastVistiedTime", String.valueOf(lastVisitedTime));
			root.setAttribute("cycleStart", String.valueOf(currentCycleStart));
			root.setAttribute("activeScore", String.valueOf(getActiveScore()));
			root.setAttribute("healthScore", String.valueOf(getHealthScore()));
			
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

	@Override
	public void report(Map<String, Object> json) {
		if (json != null){
			json.put("module", getClass().getName());
			json.put("start", startTime);
			json.put("lastVisitedTime", lastVisitedTime);
			json.put("cycleStart", currentCycleStart);
			json.put("activeScore", getActiveScore());
			json.put("healthScore", getHealthScore());
			
			if (total != null){
				Map<String,Object> stat = new HashMap<String,Object>(); // NOSONAR
				
				total.report(stat);
				
				json.put("total", stat);
			}
			
			if (current != null){
				Map<String,Object> stat = new HashMap<String,Object>(); // NOSONAR
				
				current.report(stat);
				
				json.put("current", stat);
			}			
		}
	}
	
	public static final class CounterUnit implements Reportable{
		/**
		 * 服务次数（当前周期）
		 */
		protected volatile long times = 0;
		
		/**
		 * 错误次数（当前周期）
		 */
		protected volatile long errorTimes = 0;
		
		/**
		 * 最大时长
		 */
		protected volatile long max = 0;
		
		/**
		 * 最小时长
		 */
		protected volatile long min = 100000;
		
		/**
		 * 平均时长（当期周期）
		 */
		protected volatile double avg = 0;
		
		/**
		 * double数值格式化器
		 */
		private static DecimalFormat df = new DecimalFormat("#.00"); 		
		
		/**
		 * 访问
		 * @param duration 时长
		 * @param error 是否错误
		 */
		public void visited(long duration,boolean error){
			//计算平均值
			if (times <= 0){
				avg =  duration;
			}else{
				avg = (avg * times + duration) / (times + 1);
			}
				
			//计算次数
			times += 1;
				
			//计算最小值
			if (min > duration){
				min = duration;
			}
			
			//计算最大值
			if (max < duration){
				max = duration;
			}
			
			errorTimes += (error?1:0);
		}
		
		/**
		 * 首次访问
		 * @param duration 时长
		 * @param error 是否错误
		 */
		public void first(long duration,boolean error){
			//计算平均值
			avg =  duration;
				
			//计算次数
			times = 1;
				
			//计算最小值
			min = duration;
			
			//计算最大值
			max = duration;
			
			errorTimes = error?1:0;
		}		
		
		@Override
		public void report(Element xml) {
			if (xml != null){
				xml.setAttribute("times", String.valueOf(times));
				xml.setAttribute("error", String.valueOf(errorTimes));
				xml.setAttribute("max", String.valueOf(max));
				xml.setAttribute("min", String.valueOf(min));
				xml.setAttribute("avg", df.format(avg));
			}
		}

		@Override
		public void report(Map<String, Object> json) {
			if (json != null){
				json.put("times", String.valueOf(times));
				json.put("error", String.valueOf(errorTimes));
				json.put("max", String.valueOf(max));
				json.put("min", String.valueOf(min));
				json.put("avg", df.format(avg));
			}
		}
	}
}
