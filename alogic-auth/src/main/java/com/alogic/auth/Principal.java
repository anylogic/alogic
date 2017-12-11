package com.alogic.auth;

import java.util.List;

import com.alogic.load.Loadable;
import com.alogic.load.Store;
import com.anysoft.util.JsonSerializer;

/**
 * Principal
 * 
 * 代表当前用户
 * 
 * @author yyduan
 * @since 1.6.10.10
 */
public interface Principal extends Loadable,Constants,JsonSerializer{
	
	/**
	 * 获取id
	 * 
	 * <p>
	 * Principal的id可以是会话id,也可以是其他的全局性的id,比如说,在sso场景中的token.
	 * @return id
	 */
	public String getId();
	
	/**
	 * 获取登录时间
	 * @return　登录时间，毫秒数
	 */
	public String getLoginTime();
	
	/**
	 * 获取登录ip
	 * @return 登录ip，客户端地址
	 */
	public String getLoginIp();
	
	/**
	 * 设置属性
	 * 
	 * <p>用来设置Principal的扩展属性，当该属性已经存在时，如果overwrite为true,则覆盖，反之则忽略
	 * 
	 * @param id 属性id
	 * @param value 属性值
	 * @param overwrite 是否覆盖
	 */
	public void setProperty(String id,String value,boolean overwrite);
	
	/**
	 * 获取属性
	 * 
	 * <p>用来获取Principal的扩展属性，如果该属性不存在，则返回dftValue.
	 * @param id 属性id
	 * @param dftValue 缺省值
	 * @return 属性值
	 */
	public String getProperty(String id,String dftValue);
	
	/**
	 * 清除属性
	 */
	public void clearProperties();
	
	/**
	 * 获取用户的权限列表
	 * @return 权限列表
	 */
	public List<String> getPrivileges();
	
	/**
	 * 是否具备指定的权限
	 * @param privilege 权限项
	 * @return　如果具备该权限，返回为true，反之为false
	 */
	public boolean hasPrivilege(String privilege);
	
	/**
	 * 增加权限列表
	 * @param privileges 权限列表
	 */
	public void addPrivileges(String...privileges);
	
	/**
	 * 清除权限列表
	 */
	public void clearPrivileges();
	
	/**
	 * 复制本Principal的数据到另外一个Principal实例
	 * @param another 另外一个Principal实例
	 */
	public void copyTo(Principal another);
	
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
	
	/**
	 * 本地缓存实现
	 * 
	 * @author yyduan
	 *
	 */
	public static class LocalCacheStore extends Store.HashStore<Principal>{
		
	}
}
