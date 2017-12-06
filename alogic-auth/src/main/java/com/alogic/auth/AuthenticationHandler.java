package com.alogic.auth;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.anysoft.util.BaseException;
import com.anysoft.util.Configurable;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.logicbus.backend.Context;
import com.logicbus.backend.server.http.HttpContext;


/**
 * 身份验证处理器
 * 
 * @author duanyy
 *
 * @since 1.6.10.10
 */
public interface AuthenticationHandler extends Configurable,XMLConfigurable{
	/**
	 * 通过Servlet请求获取Principal 
	 * 
	 * @param request HttpServletRequest
	 * @return Principal实例，如果当前没有登录，返回为null.
	 */
	public Principal getCurrent(HttpServletRequest request);
	
	/**
	 * 通过Servlet请求进行登录
	 * 
	 * @param request HttpServletRequest
	 * @return Principal实例，如果成功，返回登录后的Principal，反之，以异常的形式抛出.
	 */
	public Principal login(HttpServletRequest request);
	
	/**
	 * 通过Session获取Principal
	 * @param session 会话
	 * @return Principal实例，如果当前没有登录，返回为null.
	 */
	public Principal getCurrent(Session session);
	
	/**
	 * 通过服务上下文获取Principal 
	 * 
	 * @param ctx Context
	 * @return Principal实例，如果当前没有登录，返回为null.
	 */
	public Principal getCurrent(Context ctx);
	
	/**
	 * 通过服务上下文进行登录
	 * @param ctx Context
	 * @return Principal实例，如果成功，返回登录后的Principal，反之，以异常的形式抛出.
	 */
	public Principal login(Context ctx);	
	
	/**
	 * 判断当前用户是否具备指定的权限
	 * @param privilege 权限id
	 * @return 如果具备该权限，返回为true,反之为false
	 */
	public boolean hasPrivilege(Principal principal,String privilege);
	
	/**
	 * 退出登录
	 */
	public void logout(Principal principal);	
	
	/**
	 * 判断当前用户是否对指定的业务对象具备操作权限
	 * @param privilege 权限id
	 * @param objectId 业务对象id
	 * @param objectType 业务对象的类型
	 * @return 如果具备该权限，返回为true,反之为false
	 */
	public boolean hasPrivilege(Principal principal,String privilege,String objectId,String objectType);
	
	/**
	 * 根据菜单树，逐一检查当前用户是否具备操作权限
	 * <p>
	 * 所谓菜单树是一个Json对象，将对Json对象的item节点进行检查，在叶子节点上输出属性enable,如果
	 * enable为true，则表示该菜单项具备权限。
	 * <p>
	 * 每个菜单项需要具备属性privilege，用来标明该菜单项所对应的权限项。
	 * 
	 * @param menu 菜单树
	 */
	public void checkPrivilege(Principal principal,Map<String,Object> menu);
	
	/**
	 * 根据菜单树，逐一检查当前用户是否对指定的业务对象具备操作权限
	 * <p>
	 * 所谓菜单树是一个Json对象，将对Json对象的每一个节点进行检查，在叶子节点上输出属性enable,如果
	 * enable为true，则表示该菜单项具备权限。
	 * <p>
	 * 每个菜单项需要具备属性privilege，用来标明该菜单项所对应的权限项。
	 * 
	 * @param menu 菜单树
	 * @param objectId 业务对象id
	 * @param objectType 业务对象的类型
	 */
	public void checkPrivilege(Principal principal,Map<String,Object> menu,String objectId,String objectType);
	
	/**
	 * 设置所需的会话管理器
	 * @param sm SessionManager
	 */
	public void setSessionManager(SessionManager sm);
	/**
	 * 虚基类
	 * @author yyduan
	 *
	 */
	public abstract static class Abstract implements AuthenticationHandler{
		/**
		 * a logger of slf4j
		 */
		protected final Logger LOG = LoggerFactory.getLogger(AuthenticationHandler.class);
		
		@Override
		public void configure(Element e, Properties p) {
			Properties props = new XmlElementProperties(e,p);
			configure(props);
		}
		
		@Override
		public void configure(Properties p){
			
		}
		
		@Override
		public Principal getCurrent(Context ctx) {
			if (!(ctx instanceof HttpContext)){
				throw new BaseException("core.nothttpcontext","The Context is not a HttpContext instance.");
			}
			
			HttpContext httpContext = (HttpContext)ctx;
			HttpServletRequest request = httpContext.getRequest();
			return getCurrent(request);
		}
		
		@Override
		public Principal login(Context ctx) {
			if (!(ctx instanceof HttpContext)){
				throw new BaseException("core.nothttpcontext","The Context is not a HttpContext instance.");
			}
			
			HttpContext httpContext = (HttpContext)ctx;
			HttpServletRequest request = httpContext.getRequest();
			return login(request);
		}
		
		@Override
		public boolean hasPrivilege(Principal principal,String privilege,String objectId,String objectType){
			//缺省不做数据权限控制
			return hasPrivilege(principal,privilege);
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public void checkPrivilege(Principal principal,Map<String,Object> menu){
			if (menu != null){
				//从privilege属性获取到权限项
				String privilege = JsonTools.getString(menu,"privilege","");
				if (StringUtils.isNotEmpty(privilege)){
					JsonTools.setBoolean(menu, "enable", this.hasPrivilege(principal, privilege));
				}
				
				Object item = menu.get("item");
				if (item != null){
					if (item instanceof Map){
						this.checkPrivilege(principal, (Map<String,Object>)item);
					}else{
						if (item instanceof List){
							List<Object> listItem = (List<Object>)item;
							for (Object o:listItem){
								if (o != null && o instanceof Map){
									this.checkPrivilege(principal, (Map<String,Object>)o);
								}
							}
						}
					}
				}
			}
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public void checkPrivilege(Principal principal,Map<String,Object> menu,String objectId,String objectType){
			if (menu != null){
				//从privilege属性获取到权限项
				String privilege = JsonTools.getString(menu,"privilege","");
				if (StringUtils.isNotEmpty(privilege)){
					JsonTools.setBoolean(menu, "enable", this.hasPrivilege(principal, privilege,objectId,objectType));
				}
				
				Object item = menu.get("item");
				if (item != null){
					if (item instanceof Map){
						this.checkPrivilege(principal, (Map<String,Object>)item);
					}else{
						if (item instanceof List){
							List<Object> listItem = (List<Object>)item;
							for (Object o:listItem){
								if (o != null && o instanceof Map){
									this.checkPrivilege(principal, (Map<String,Object>)o,objectId,objectType);
								}
							}
						}
					}
				}
			}
		}
	}
}
