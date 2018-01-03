package com.alogic.idu.util;

import java.util.Map;

import com.anysoft.util.Properties;

/**
 * 权限控制者
 * 
 * @author duanyy
 * @since 1.6.4.6
 * 
 * @version 1.6.4.23 [20160114 duanyy] <br>
 * - 修改权限模型 <br>
 * 
 * @deprecated
 */
public interface Guard{
	
	/**
	 * 检查指定用户是否具备指定权限
	 * 
	 * @param userId 用户ID
 	 * @param privilege 权限ID
	 * @return true|false
	 */
	public boolean checkPrivilege(String userId,String privilege);
	
	/**
	 * 检查指定用户在指定对象上是否具有指定权限
	 * @param userId 用户ID
	 * @param privilege 权限ID
	 * @param objectId 对象ID
	 * @param dataGuard 数据权限控制者
	 * @return true|false
	 */
	public boolean checkPrivilege(String userId,String privilege,String objectId,DataGuard dataGuard);
	
	/**
	 * 检查指定用户在指定菜单上是否具有权限
	 * 
	 * @param userId
	 * @param menu
	 */
	public void checkPrivilege(String userId,Map<String,Object> menu);
	
	/**
	 * 检查指定用户在指定菜单上是否具有权限
	 * 
	 * @param userId
	 * @param menu
	 * @param objectId
	 * @param dataGuard
	 */
	public void checkPrivilege(String userId,Map<String,Object> menu,String objectId,DataGuard dataGuard);
	
	/**
	 * 缺省实现
	 * @author duanyy
	 *
	 */
	public static class Default implements Guard{
		public Default(Properties p){
			
		}
		public boolean checkPrivilege(String userId, String privilege) {
			return true;
		}

		public boolean checkPrivilege(String userId, String privilege,
				String objectId, DataGuard dataGuard) {
			return true;
		}

		@Override
		public void checkPrivilege(String userId, Map<String, Object> menu) {
			
		}
		@Override
		public void checkPrivilege(String userId, Map<String, Object> menu,
				String objectId, DataGuard dataGuard) {
			
		}
		
	}
}