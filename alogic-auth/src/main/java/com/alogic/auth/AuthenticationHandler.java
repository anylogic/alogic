package com.alogic.auth;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.anysoft.util.BaseException;
import com.anysoft.util.Configurable;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.logicbus.backend.Context;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.server.http.HttpContext;


/**
 * 身份验证处理器
 * 
 * @author duanyy
 *
 * @since 1.6.10.10
 * 
 * @version 1.6.11.1 [20171215 duanyy] <br>
 * - 修正退出登录方法<br>
 * 
 * @version 1.6.11.7 [20180107 duanyy] <br>
 * - 优化Session管理 <br>
 * 
 * @version 1.6.11.14 [duanyy 20180129] <br>
 * - 优化AuthenticationHandler接口 <br>
 * 
 * @version 1.6.11.22 [duanyy 20180314] <br>
 * - 增加isLocalLoginMode(是否本地登录模式)的判断 <br>
 * - 增加common(扩展指令接口) <br>
 * 
 * @version 1.6.11.59 [20180911 duanyy] <br>
 * - 优化权限接口 <br>
 */
public interface AuthenticationHandler extends Configurable,XMLConfigurable{
	
	/**
	 * 是否本地登录模式
	 * @return 如果是本地登录模式，返回为true
	 */
	public boolean isLocalLoginMode();
	
	/**
	 * 通过Servlet请求获取Principal 
	 * 
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @return Principal实例，如果当前没有登录，返回为null.
	 */
	public Principal getCurrent(HttpServletRequest request,HttpServletResponse response);
	
	/**
	 * 通过Token来查找指定的Principal
	 * <p>
	 * 本方法用于SSO场景下的Token验证
	 * 
	 * @param app 应用id
	 * @param token token
	 * @param callback 退出时的回调服务地址
	 * @return Principal实例，如果没有找到指定的token，返回为null
	 */
	public Principal getPrincipal(String app,String token,String callback);
	
	/**
	 * 通过Servlet请求进行登录
	 * 
	 * @param request HttpServletRequest
	 * @return Principal实例，如果成功，返回登录后的Principal，反之，以异常的形式抛出.
	 */
	public Principal login(HttpServletRequest request,HttpServletResponse response);
	
	/**
	 * 通过Session获取Principal
	 * @param request HttpServletRequest
	 * @param session 会话
	 * @return Principal实例，如果当前没有登录，返回为null.
	 */
	public Principal getCurrent(HttpServletRequest request,HttpServletResponse response,Session session);
	
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
	 * 退出登录
	 */
	public void logout(HttpServletRequest request,HttpServletResponse response);	
	
	/**
	 * 退出登录
	 */
	public void logout(Context ctx);	
	
	/**
	 * 执行扩展指令
	 * @param ctx 上下文
	 */
	public void command(Context ctx);
	
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
		
		/**
		 * 支持ForwardedHeader
		 */
		protected String ForwardedHeader = "X-Forwarded-For";
		
		@Override
		public void configure(Element e, Properties p) {
			Properties props = new XmlElementProperties(e,p);
			configure(props);
		}
		
		@Override
		public void configure(Properties p){
			ForwardedHeader = PropertiesConstants.getString(p,"http.forwardedheader", ForwardedHeader);
		}
		
		/**
		 * 获取客户端的ip
		 * @param request HttpServletRequest
		 * @return 客户端ip
		 */
		public String getClientIp(HttpServletRequest request) {
			/**
			 * 支持负载均衡器的X-Forwarded-For
			 */
			String ip = request.getHeader(ForwardedHeader);
			if (StringUtils.isNotEmpty(ip)){
				String [] ips = ip.split(",");
				if (ips.length > 0){
					return ips[0];
				}else{
					return request.getRemoteHost();
				}
			}else{
				return request.getRemoteHost();
			}
		}
		
		@Override
		public boolean isLocalLoginMode(){
			return true;
		}
		
		@Override
		public Principal getCurrent(Context ctx) {
			if (!(ctx instanceof HttpContext)){
				throw new BaseException("core.e1002","The Context is not a HttpContext instance.");
			}
			
			HttpContext httpContext = (HttpContext)ctx;
			HttpServletRequest request = httpContext.getRequest();
			HttpServletResponse response = httpContext.getResponse();
			return getCurrent(request,response);
		}
		
		@Override
		public Principal login(Context ctx) {
			if (!(ctx instanceof HttpContext)){
				throw new BaseException("core.e1002","The Context is not a HttpContext instance.");
			}
			
			HttpContext httpContext = (HttpContext)ctx;
			HttpServletRequest request = httpContext.getRequest();
			HttpServletResponse response = httpContext.getResponse();
			return login(request,response);
		}
		
		@Override
		public void command(Context ctx){
			ctx.asMessage(JsonMessage.class);
		}
		
		@Override
		public void logout(Context ctx){
			if (!(ctx instanceof HttpContext)){
				throw new BaseException("core.e1002","The Context is not a HttpContext instance.");
			}
			
			HttpContext httpContext = (HttpContext)ctx;
			HttpServletRequest request = httpContext.getRequest();
			HttpServletResponse response = httpContext.getResponse();
			logout(request,response);			
		}
		
		public boolean hasPrivilege(Principal principal, String privilege) {
			if (principal != null){
				return principal.hasPrivilege(privilege);
			}
			return false;
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
	}
}
