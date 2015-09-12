package com.alogic.idu.util;

import java.util.List;
import java.util.Map;

import com.anysoft.util.Properties;

/**
 * 权限控制者
 * 
 * @author duanyy
 * @since 1.6.4.6
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
	 * 检查指定用户在指定button列表上是否具有权限
	 * 
	 * @param userId
	 * @param buttons
	 */
	public void checkPrivilege(String userId,List<Map<String,Object>> buttons);
	
	/**
	 * 检查指定用户在指定button列表上是否具有权限
	 * 
	 * @param userId
	 * @param buttons
	 * @param objectId
	 * @param dataGuard
	 */
	public void checkPrivilege(String userId,List<Map<String,Object>> buttons,String objectId,DataGuard dataGuard);
	
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

		public void checkPrivilege(String userId,
				List<Map<String, Object>> buttons) {
			
		}

		public void checkPrivilege(String userId,
				List<Map<String, Object>> buttons, String objectId,
				DataGuard dataGuard) {
		}
		
	}
}