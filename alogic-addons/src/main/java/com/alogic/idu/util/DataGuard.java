package com.alogic.idu.util;

import com.anysoft.util.Properties;

/**
 * 数据权限接口
 * 
 * @author duanyy
 * @since 1.6.4.6
 */
public interface DataGuard {
	
	/**
	 * 检查指定的用户在指定的对象上是否具有指定的数据权限
	 * 
	 * @param userId 用户ID
	 * @param dataPrivilege 数据权限
	 * @param objectId 对象ID
	 * @return true|false
	 */
	public boolean checkPrivilege(String userId,String dataPrivilege,String objectId);
	
	/**
	 * 缺省实现
	 * 
	 * @author duanyy
	 *
	 */
	public static class Default implements DataGuard{
		public Default(Properties p){
			
		}
		public boolean checkPrivilege(String userId, String dataPrivilege,
				String objectId) {
			return true;
		}
		
	}
}