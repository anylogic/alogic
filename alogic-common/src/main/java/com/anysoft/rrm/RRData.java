package com.anysoft.rrm;


import com.anysoft.util.Reportable;

/**
 * Round Robin Data,可以放在RRM中的数据
 * 
 * @author duanyy
 *
 */
public interface RRData extends Reportable {
	
	/**
	 * 在现有数据上做加法
	 * 
	 * @param fragment 新的数据片段
	 */
	public void incr(RRData fragment);
	
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
	 * 虚基类
	 * @author duanyy
	 *
	 */
	abstract public static class Abstract implements RRData{
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
