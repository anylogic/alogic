package com.alogic.load;

import com.anysoft.util.Reportable;

/**
 * 可装入接口
 * 
 * @author duanyy
 *
 */
public interface Loadable extends Reportable{
	/**
	 * 获取缓存对象的ID
	 * 
	 * @return ID
	 */
	public String getId();
	
	/**
	 * 获取对象的时间戳
	 * @return 时间戳
	 */
	public long getTimestamp();
	
	/**
	 * 是否已经过期
	 * @return true if expired, or not return false
	 */
	public boolean isExpired();	
	
	/**
	 * 将该对象失效
	 */
	public void expire();
	
	/**
	 * 虚基类
	 * @author duanyy
	 * @since 1.6.11.7
	 */
	public abstract static class Abstract implements Loadable{
		/**
		 * 时间戳
		 */
		private long timestamp = System.currentTimeMillis();

		/**
		 * 设置时间戳
		 * @param t 当前时间戳
		 */
		protected void setTimestamp(long t){
			this.timestamp = t;
		}
		
		/**
		 * 获取生存时间(毫秒)
		 * @return 生存时间
		 */
		protected long getTTL(){
			return 5 * 60 * 1000L;
		}
		
		@Override
		public long getTimestamp() {
			return timestamp;
		}

		@Override
		public boolean isExpired() {
			return System.currentTimeMillis() - this.timestamp > getTTL();
		}

		@Override
		public void expire() {
			this.timestamp = System.currentTimeMillis() - getTTL();
		}
		
	}
}
