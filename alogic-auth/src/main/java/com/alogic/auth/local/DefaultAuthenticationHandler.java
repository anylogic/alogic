package com.alogic.auth.local;


import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.w3c.dom.Element;

import com.alogic.auth.AuthenticationHandler;
import com.alogic.auth.Constants;
import com.alogic.auth.Principal;
import com.alogic.auth.Session;
import com.alogic.auth.SessionManager;
import com.alogic.auth.SessionPrincipal;
import com.alogic.auth.UserModel;
import com.alogic.auth.util.SimpleUser;
import com.alogic.load.Loader;
import com.anysoft.util.BaseException;
import com.anysoft.util.Factory;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.anysoft.util.code.Coder;
import com.anysoft.util.code.CoderFactory;
import com.logicbus.backend.Context;
import com.logicbus.backend.message.JsonMessage;

/**
 * AuthenticationHandler的缺省实现
 * 
 * <p>
 * 本实现从配置文件中读取用户信息进行验证.
 * 由于Principal通过Session进行存储，本实现不适合SSO场景.
 * 
 * @author duanyy
 * @since 1.6.10.10
 * 
 * @version 1.6.11.1 [20171215 duanyy] <br>
 * - 增加获取登录id的方法<br>
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
 */
public class DefaultAuthenticationHandler extends AuthenticationHandler.Abstract{
	
	/**
	 * 会话管理器
	 */
	protected SessionManager sessionManager = null;
	
	/**
	 * 用户模型装载器
	 */
	protected Loader<UserModel> loader = null;
	
	/**
	 * 用于前端传递过来的密码解密
	 */
	protected Coder encrypter = null;
	
	/**
	 * 用于数据库密码验证
	 */
	protected Coder md5 = null;
	
	protected Session getSession(SessionManager sm,HttpServletRequest request,HttpServletResponse response,boolean create){
		return sm.getSession(request,response,create);
	}
	
	@Override
	public boolean isLocalLoginMode(){
		return true;
	}
	
	@Override
	public void configure(Element e, Properties p) {
		Properties props = new XmlElementProperties(e,p);
		
		Element elem = XmlTools.getFirstElementByPath(e, "user-model");
		if (elem != null){
			Factory<Loader<UserModel>> f = new Factory<Loader<UserModel>>();
			try {
				loader = f.newInstance(elem, props, "loader",SimpleUser.LoadFromInner.class.getName());
			}catch (Exception ex){
				LOG.error("Can not create loader :" + XmlTools.node2String(elem));
				LOG.error(ExceptionUtils.getStackTrace(ex));
			}
		}
		configure(props);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		encrypter = CoderFactory.newCoder("DES3");
		md5 = CoderFactory.newCoder("MD5");
	}
	
	@Override
	public Principal getCurrent(HttpServletRequest request,HttpServletResponse response) {
		Session sess = getSession(sessionManager,request,response, false);
		return getCurrent(request,response,sess);
	}

	@Override
	public Principal getCurrent(HttpServletRequest request,HttpServletResponse response,Session session) {
		return (session != null && session.isLoggedIn()) ? new SessionPrincipal(session.getId(),session):null;
	}
	
	@Override
	public Principal getPrincipal(String app,String token,String callback) {
		throw new BaseException("core.e1000","In default mode,it's not supported to get principal by token.");
	}
	
	@Override
	public Principal login(HttpServletRequest request,HttpServletResponse response) {
		Session sess = getSession(sessionManager,request,response, true);
		if (sess.isLoggedIn()){
			//已经登录了，删除前一个登录者的用户信息和权限信息
			sess.hDel(Session.USER_GROUP);
			sess.sDel(Session.PRIVILEGE_GROUP);
			sess.setLoggedIn(false);
		}
		
		//登录id
		String userId = getParameter(request,"loginId");
		//登录密码
		String password = getParameter(request,"pwd");
		//验证码
		String authCode = getParameter(request,"loginCode");
		
		try {
			String innerAuthCode = sess.hGet(Session.DEFAULT_GROUP,Constants.AUTH_CODE, "");
			if (StringUtils.isEmpty(innerAuthCode)){
				throw new BaseException("clnt.e2003","The auth code does not exist.");				
			}
			
			if (!authCode.equals(innerAuthCode)){
				throw new BaseException("clnt.e2002",String.format("The auth code %s is not correct", authCode));		
			}
			
			UserModel user = loadUserModel(userId);
			if (user == null){
				throw new BaseException("clnt.e2001",
						String.format("User %s does not exist or the password is not correct.", userId));
			}
			
			String pwd = encrypter.decode(password, authCode);
			pwd = md5.encode(pwd, userId);
			
			if (!pwd.equals(user.getPassword())){
				throw new BaseException("clnt.e2001",
						String.format("User %s does not exist or the password is not correct.", userId));				
			}
			
			Principal newPrincipal = new SessionPrincipal(sess.getId(),sess);
			user.copyTo(newPrincipal);
			//设置登录时间
			newPrincipal.setProperty(Constants.LOGIN_TIME, String.valueOf(System.currentTimeMillis()), true);
			//设置客户端ip
			newPrincipal.setProperty(Constants.FROM_IP, getClientIp(request), true);
			
			/**
			 * 设置已登录标记
			 */			
			sess.setLoggedIn(true);
			LOG.info(String.format("User %s has logged in.",user.getId()));
			return newPrincipal;
		}catch (Exception ex){
			LOG.error(String.format("User %s tried to login ,but %s",userId,ex.getMessage()));
			throw ex;
		}finally{
			//登录完成,验证码过期
			sess.hDel(Session.DEFAULT_GROUP,Constants.AUTH_CODE);
		}
	}

	@Override
	public boolean hasPrivilege(Principal principal, String privilege) {
		if (principal != null){
			SessionPrincipal thePrincipal = (SessionPrincipal)principal;
			return thePrincipal.hasPrivilege(privilege);
		}
		return false;
	}
	
	@Override
	public void setSessionManager(SessionManager sm){
		this.sessionManager = sm;
	}
	
	/**
	 * 装入指定id的User Model
	 * @param loginId 登录id
	 * @return UserModel实例，如果无法找到，返回为null
	 */
	protected UserModel loadUserModel(String loginId){
		return loader == null ? null : loader.load(loginId, true);
	}
	
	/**
	 * 从Request中获取指定的参数
	 * @param request　HttpServletRequest
	 * @param id　参数id
	 * @return 参数值，如果参数不存在，抛出clnt.e2000异常
	 */
	protected String getParameter(HttpServletRequest request,String id){
		String value = request.getParameter(id);
		if (StringUtils.isEmpty(value)){
			throw new BaseException("clnt.e2000",String.format("Can not find parameter %s",id));
		}
		return value;
	}
	
	/**
	 * 从Request中获取指定的参数
	 * @param request HttpServletRequest
	 * @param id 参数id
	 * @param dftValue 缺省值，当参数不存在时，返回
	 * @return　参数值，如果参数不存在，返回缺省值
	 */
	protected String getParameter(HttpServletRequest request,String id,String dftValue){
		String value = request.getParameter(id);
		return StringUtils.isEmpty(value)?dftValue:value;
	}

	@Override
	public void logout(HttpServletRequest request,HttpServletResponse response) {
		Session session = getSession(sessionManager,request,response, false);

		if (session != null && session.isLoggedIn()){
			Principal principal = new SessionPrincipal(session.getId(),session);
			LOG.info(String.format("User %s has logged out.",principal.getLoginId()));						
			principal.expire();
		}
	}
	
	@Override
	public void command(Context ctx){
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		
		String cmdId = PropertiesConstants.getString(ctx, "cmd", "help");
		
		if (cmdId.equals("GetUser")){
			String userId = PropertiesConstants.getString(ctx, "userId", "");
			if (StringUtils.isNotEmpty(userId)){
				UserModel user = loadUserModel(userId);
				Map<String,Object> map = new HashMap<String,Object>();
				user.report(map);
				msg.getRoot().put("data", map);
			}
			return ;
		}
	}

}
