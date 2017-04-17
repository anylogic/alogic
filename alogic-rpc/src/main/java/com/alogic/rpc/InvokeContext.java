package com.alogic.rpc;

import java.util.HashMap;

/**
 * <p>
 * rcp invoke context
 * </p>
 * 
 * @author xkw
 * @since 1.6.7.15
 */
public interface InvokeContext {

	/**
	 * 设置属性
	 * 
	 * @param key
	 * @param value
	 */
	void setAttribute(String key, Object value);

	/**
	 * 获取属性
	 * 
	 * @param key
	 * @return 属性对象
	 */
	public Object getAttribute(String key);
	
	public boolean isEmpty();

	/**
	 * 缺省实现
	 * @author yyduan
	 *
	 */
	public static class Default extends HashMap<String,Object> implements InvokeContext {

		private static final long serialVersionUID = -3450580766769038106L;

		@Override
		public void setAttribute(String key, Object value) {
			super.put(key, value);
		}

		@Override
		public Object getAttribute(String key) {
			return super.get(key);
		}

		@Override
		public boolean isEmpty() {
			return super.isEmpty();
		}

	}
}
