package com.alogic.rpc;

import java.util.HashMap;
import java.util.Map;

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

	public static class Default implements InvokeContext {

		private Map<String, Object> attrs = new HashMap<String, Object>();

		@Override
		public void setAttribute(String key, Object value) {
			attrs.put(key, value);
		}

		@Override
		public Object getAttribute(String key) {
			return attrs.get(key);
		}

		@Override
		public boolean isEmpty() {
			return attrs.isEmpty();
		}

	}
}
