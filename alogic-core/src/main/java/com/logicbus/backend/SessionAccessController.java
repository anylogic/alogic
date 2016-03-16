package com.logicbus.backend;

import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.AbstractAccessController;
import com.logicbus.backend.Context;
import com.logicbus.backend.Session;
import com.logicbus.backend.SessionManager;
import com.logicbus.models.catalog.Path;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 基于登录会话的访问控制器
 * @author duanyy
 * @version 1.6.4.35 [20160315 duanyy] <br>
 * - 实现XMLConfigurable和Configurable接口 <br>
 */
public class SessionAccessController extends AbstractAccessController {
	/**
	 * 最大并发数，可通过环境变量acm.maxThread来配置，缺省为10
	 */
	protected int maxThread = 10;
	
	/**
	 * 一分钟内访问次数限制，可通过环境变量acm.maxTimesPerMin来配置，缺省为1000
	 */
	protected int maxtimesPerMin = 1000;
	
	/**
	 * 匿名用户的用户名，可通过环境变量acm.anonymous来配置，缺省为anonymous
	 */
	protected String anonymousUser = "anonymous";
	
	public SessionAccessController() {
	}
	
	@Override
	public void configure(Properties props) {
		maxThread = PropertiesConstants.getInt(props, "acm.maxThread", maxThread);
		maxtimesPerMin = PropertiesConstants.getInt(props, "acm.maxTimesPerMin", maxtimesPerMin);
		anonymousUser = PropertiesConstants.getString(props,"acm.anonymous",anonymousUser);
	}	
	
	@Override
	public String createSessionId(Path serviceId, ServiceDescription servant,
			Context ctx) {
		//获取当前的登录Session
		Session session = SessionManager.get().getSession(ctx, false);
		
		String currentId = getLoginIdFromSession(session);
		
		//设置到本次服务调用的Context中，以便服务获取当前用户id
		ctx.SetValue("user.id", currentId);
		return currentId;
	}

	protected String getLoginIdFromSession(Session session){
		//如果没有登录，当前用户设置为匿名用户
		//如果已经登录，从登录Session获取用户id		
		return session == null ? anonymousUser:session.hGet("user", "id", "anonymous");
	}
	
	@Override
	protected int getClientPriority(String sessionId,Path serviceId, ServiceDescription servant,
			Context ctx, AccessStat stat) {
		//当违反并发规则的时候，设置优先级为-1	
		int priority = (stat.thread > maxThread || stat.timesOneMin > maxtimesPerMin)? -1 :0;
		if (priority >= 0){
			//在没有违反并发控制规则的情况下，才考虑其他
			if (sessionId.startsWith(anonymousUser)){
				//匿名用户,只能访问public服务
				if (servant.getVisible().equals("public")){
					//是公共服务
					priority = 0;
				}else{
					//禁止访问
					priority = -1;
				}
			}else{
				//已经登录，查看这个用户的优先级
				priority = getServicePriority(sessionId,serviceId,servant);
			}
		}
		return priority;
	}
	
	/**
	 * 获取当前用户对服务的访问优先级
	 * 
	 * @param userId 当前用户id
	 * @param serviceId 要访问的服务
	 * @param servant 服务的描述
	 * @return 优先级
	 */
	protected int getServicePriority(String userId,Path serviceId, ServiceDescription servant){
		return 0;
	}

}
