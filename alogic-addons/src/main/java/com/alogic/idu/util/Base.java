package com.alogic.idu.util;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alogic.cache.context.CacheSource;
import com.alogic.cache.core.CacheStore;
import com.anysoft.util.Factory;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 服务实现的基类
 * 
 * @author duanyy
 *
 * @since 1.6.4.6
 * 
 * @version 1.6.4.23 [20160114 duanyy] <br>
 * - 修改权限模型 <br>
 * 
 * @deprecated
 */
public abstract class Base extends AbstractServant {
	/**
	 * 权限Guard
	 */
	protected Guard guard = null;
	
	/**
	 * 数据权限Guard
	 */
	protected DataGuard dataGuard = null;
	
	/**
	 * 本服务的权限项,可通过参数privilege配置，缺省为空
	 */
	protected String privilege = "";
	
	/**
	 * 相关的CacheId,可通过参数cache.id配置,缺省为空
	 */
	protected String cacheId = "";
		
	@Override
	protected void onDestroy() {

	}

	@Override
	protected void onCreate(ServiceDescription sd){
		Properties p = sd.getProperties();

		privilege = PropertiesConstants.getString(p, "privilege", "",true);
		cacheId = PropertiesConstants.getString(p,"cache.id",cacheId,true);
				
		//是否打开guard，缺省为false
		boolean guardOn = PropertiesConstants.getBoolean(p,"guard.on",true,true);
		if (guardOn){
			try {
				guard = createGuard(p);
				dataGuard = createDataGuard(p);
			}catch (Exception ex){
				logger.error("Can not create guard or dataguard instance",ex);
			}
		}
		onCreate(sd,p);
	}

	/**
	 * 处理Create事件
	 * @param sd 服务描述
	 * @param p 服务属性
	 */
	 protected abstract void onCreate(ServiceDescription sd, Properties p);	// NOSONAR
	

	@Override
	protected int onJson(Context ctx) {
		JsonMessage msg = (JsonMessage) ctx.asMessage(JsonMessage.class);
		return onJson(ctx,msg);
	}
	
	/**
	 * 处理Json协议的调用
	 * @param ctx 上下文
	 * @param msg 消息
	 * @
	 */
	abstract protected int onJson(Context ctx, JsonMessage msg)	;// NOSONAR
	
	/**
	 * 指定的id是否为空
	 * @param id id
	 * @return true|false
	 */
	protected boolean isNull(String id) {
		return StringUtils.isEmpty(id);
	}
	
	/**
	 * 在相关缓存中清除指定的对象
	 * @param id 对象ID
	 */
	protected void clearCache(String id){
		if (!isNull(cacheId)){
			CacheSource cs = CacheSource.get();
			
			CacheStore store = cs.get(cacheId);
			
			if (store != null){
				store.expire(id);
			}
		}
	}
	
	/**
	 * 获取相关的缓存
	 * @return 缓存
	 */
	protected CacheStore getCacheStore(){
		if (isNull(cacheId)){
			throw new ServantException("core.e1003","The relational cache is not defined");
		}
		
		CacheSource cs = CacheSource.get();
		
		CacheStore store = cs.get(cacheId);
		
		if (store == null){
			throw new ServantException("core.e1003","The cache is not found,cacheId=" + cacheId);
		}
		
		return store;
	}
	
	/**
	 * 检查本次操作的权限
	 * 
	 * @param userId 操作人员
	 * @return true|false
	 */	
	protected boolean checkPrivilege(String userId){
		boolean enable = true;
		
		if (guard != null){
			enable = guard.checkPrivilege(userId, privilege);
		}
		
		return enable;
	}
	
	/**
	 * 检查本次操作权限
	 * @param userId 操作人员
	 * @param objectId 所操作的对象ID
	 * @return true|false
	 */	
	protected boolean checkPrivilege(String userId,String objectId){
		boolean enable = true;
		
		if (guard != null){
			enable = guard.checkPrivilege(userId, privilege,objectId,dataGuard);
		}
		
		return enable;
	}	
	
	/**
	 * 检查指定用户在指定菜单上是否具有权限
	 * 
	 * @param userId
	 * @param menu
	 */
	protected void checkPrivilege(String userId,Map<String,Object> menu){
		if (guard != null){
			guard.checkPrivilege(userId, menu);
		}
	}
	
	/**
	 * 检查指定用户在菜单上是否具有权限
	 * 
	 * @param userId
	 * @param menu
	 * @param objectId
	 */
	public void checkPrivilege(String userId,Map<String,Object> menu,String objectId){
		if (guard != null){
			guard.checkPrivilege(userId, menu,objectId,dataGuard);
		}
	}
	
	/**
	 * 根据环境变量创建Guard实例
	 * @param p 环境变量
	 * @return Guard
	 */
	protected Guard createGuard(Properties p){
		Factory<Guard> factory = new Factory<Guard>();
		return factory.newInstance(
				PropertiesConstants.getString(p,"guard.module",Guard.Default.class.getName()), 
				p);
	}
	
	/**
	 * 根据环境变量创建DataGuard实例
	 * @param p 环境变量
	 * @return DataGuard
	 */
	protected DataGuard createDataGuard(Properties p){
		Factory<DataGuard> factory = new Factory<DataGuard>();
		return factory.newInstance(
				PropertiesConstants.getString(p,"dataGuard.module",DataGuard.Default.class.getName()), 
				p);
	}	
	
}
