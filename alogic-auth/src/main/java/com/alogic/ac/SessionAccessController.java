package com.alogic.ac;


import org.w3c.dom.Element;
import com.alogic.auth.Principal;
import com.alogic.auth.PrincipalManager;
import com.alogic.auth.SessionManagerFactory;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlElementProperties;
import com.logicbus.backend.Context;
import com.logicbus.models.catalog.Path;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 基于登录会话的访问控制
 * 
 * @author yyduan
 * @since 1.6.11.1
 */
public class SessionAccessController extends AbstractACMAccessController{
	/**
	 * 匿名用户
	 */
	protected String dftUser = "anonymous";
	
	/**
	 * 所有登录用户采用同一个ACM
	 */
	protected AccessControlModel acm = null;
	
	@Override
	public void configure(Element e, Properties props) {
		XmlElementProperties p = new XmlElementProperties(e,props);
		
		acm = new AccessControlModel.Default();
		acm.configure(e, props);
		
		configure(p);
	}
	
	@Override
	public void reload(String id) {
		// nothing to reload
	}

	@Override
	public String createSessionId(Path serviceId, ServiceDescription servant,
			Context ctx) {
		PrincipalManager sm = (PrincipalManager)SessionManagerFactory.getDefault();
		Principal principal = sm.getCurrent(ctx);
		if (principal == null){
			//没有登录
			if (servant.getVisible().equals(ServiceDescription.PUBLIC)){
				//匿名状态下，仅允许访问public服务
				return String.format("%s@%s",dftUser,getClientIp(ctx));
			}else{
				throw new BaseException("core.e1010","Anonymous is not allowed to access protected service.");
			}
		}else{
			//已经登录
			if (principal.hasPrivilege(servant.getPrivilege())){
				return String.format("%s@%s", principal.getLoginId(),getClientIp(ctx));
			}else{
				throw new BaseException("core.e1010",
						String.format("User %s has no privelege [%s] to access the service", 
								principal.getLoginId(),
								servant.getPrivilege()));
			}
		}
	}

	@Override
	protected AccessControlModel getACM(String sessionId, Path serviceId,
			ServiceDescription servant, Context ctx) {
		return acm;
	}

}
