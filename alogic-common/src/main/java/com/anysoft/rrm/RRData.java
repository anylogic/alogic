package com.anysoft.rrm;


import com.alogic.metrics.stream.MetricsReportable;
import com.anysoft.util.Reportable;

/**
 * Round Robin Data,可以放在RRM中的数据
 * 
 * @author duanyy
 * 
 * @version 1.6.4.42 [duanyy 20160407] <br>
 * - 对接指标处理器 <br>
 * 
 * @version 1.6.6.13 [20170109 duanyy] <br>
 * - 采用新的指标接口
 */
public interface RRData extends Reportable,MetricsReportable {
	
	/**
	 * 在现有数据上做加法
	 * 
	 * @param fragment 新的数据片段
	 */
	public void incr(RRData fragment);
	
	/**
	 * 克隆自身
	 * @return 新的片段
	 */
	public RRData copy();
	
	/**
	 * 获取时间戳
	 * @return 时间戳
	 */
	public long timestamp();
	
	/**
	 * 设置时间戳
	 * @param t 时间
	 */
	public void timestamp(long t);
	
	/**
	 * 获取指标id
	 * @return 指标id
	 */
	public String id();
	
	/**
	 * 虚基类
	 * @author duanyy
	 *
	 */
	public abstract static class Abstract implements RRData{
		protected String id = "unknown";
		
		public Abstract(String metricsId){
			id = metricsId;
		}
		
		public String id(){
			return id;
		}
		
		@Override
		public long timestamp() {
			return timestamp;
		}

		@Override
		public void timestamp(long t) {
			timestamp = t;
		}
		
		protected long timestamp;
	}
}
