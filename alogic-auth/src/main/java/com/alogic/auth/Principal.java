package com.alogic.auth;

import java.util.Map;

import org.w3c.dom.Element;

import com.alogic.load.Loadable;
import com.anysoft.util.JsonSerializer;
import com.anysoft.util.XmlSerializer;

/**
 * Principal
 * 
 * 代表当前用户
 * 
 * @author yyduan
 * @since 1.6.10.10
 */
public interface Principal extends JsonSerializer,XmlSerializer,Loadable{

	/**
	 * 获取id
	 * 
	 * <p>
	 * Principal的id可以是会话id,也可以是其他的全局性的id,比如说,在sso场景中的token.
	 * @return id
	 */
	public String getId();
	
	/**
	 * 获取当前会话id
	 * @return 会话id
	 */
	public String getSessionId();
	
	/**
	 * 判断当前是否已登录
	 * 
	 * <p>
	 * 对某些PrincipalManager实现来说,如果用户未登录,也可以返回一个Principal,
	 * 通过本方法来判断是否登录
	 * 
	 * @return 如果已经登录,返回为true,反正,返回为false
	 */
	public boolean isLoggedIn();
	
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
		public void report(Element xml) {
			this.toXML(xml);
		}

		@Override
		public void report(Map<String, Object> json) {
			this.toJson(json);
		}

		@Override
		public String getId() {
			return id;
		}
	}
}
