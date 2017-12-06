package com.alogic.auth;

import com.alogic.load.Loadable;

/**
 * Principal
 * 
 * 代表当前用户
 * 
 * @author yyduan
 * @since 1.6.10.10
 */
public interface Principal extends Loadable{

	/**
	 * 获取id
	 * 
	 * <p>
	 * Principal的id可以是会话id,也可以是其他的全局性的id,比如说,在sso场景中的token.
	 * @return id
	 */
	public String getId();
	
	/**
	 * 虚基类
	 */
	public static abstract class Abstract implements Principal{
		/**
		 * 创建的时间戳
		 */
		protected long timestamp = System.currentTimeMillis();

		/**
		 * id
		 */
		protected String id;
		
		public Abstract(final String id){
			this.id = id;
		}
		
		@Override
		public long getTimestamp() {
			return timestamp;
		}

		@Override
		public boolean isExpired() {
			return false;
		}

		@Override
		public void expire() {
		}

		@Override
		public String getId() {
			return id;
		}
	}
}
