package com.alogic.cache.core;

import java.util.Map;

import org.w3c.dom.Element;

import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;

/**
 * 过期策略
 * 
 * @author duanyy
 *
 * @since 1.6.3.3
 * 
 */
public interface ExpirePolicy extends XMLConfigurable,Reportable{
	
	/**
	 * 指定的值是否过期
	 * @param value 缓存对象值
	 * @param timestamp 对象的时间戳
	 * @param now 当前时间
	 * @return 是否过期
	 */
	public boolean isExpired(MultiFieldObject value,long timestamp,long now,int ttl);
	
	
	/**
	 * 缺省的过期策略
	 * @author duanyy
	 *
	 */
	public static class Default implements ExpirePolicy{

		public void configure(Element _e, Properties _properties)
				throws BaseException {
			// nothing to do
		}

		public void report(Element xml) {
			// nothing to do
		}

		public void report(Map<String, Object> json) {
			// nothing to do
		}

		public boolean isExpired(MultiFieldObject value,long timestamp,long now,int ttl) {
			return timestamp + ttl < now;
		}
	}
}
