package com.alogic.auth.service;

import java.util.HashMap;
import java.util.Map;

import com.alogic.auth.Principal;
import com.alogic.auth.PrincipalManager;
import com.alogic.auth.Session;
import com.alogic.auth.SessionManagerFactory;
import com.anysoft.util.JsonTools;
import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 检查单项权限
 * 
 * @author duanyy
 * @since 1.6.10.10
 * 
 * @version 1.6.11.59 [20180911 duanyy] <br>
 * - 优化权限接口 <br>
 */
public class CheckSinglePrivilege extends AbstractServant{

	@Override
	protected void onDestroy() {

	}

	@Override
	protected void onCreate(ServiceDescription sd) {

	}

	@Override
	protected int onJson(Context ctx){
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		
		@SuppressWarnings("unchecked")
		Map<String,Object> data = (Map<String,Object>)msg.getRoot().get("data");
		if (data == null){
			data = new HashMap<String,Object>();
			msg.getRoot().put("data", data);
		}
		
		PrincipalManager sm = (PrincipalManager)SessionManagerFactory.getDefault();
		Session sess = sm.getSession(ctx, false);
		if (sess == null){
			JsonTools.setString(data,"isLoggedIn","false");
		}else{			
			Principal principal = sm.getCurrent(ctx);
			if (principal != null){
				String privilege = this.getArgument("privilege", "", ctx);
				JsonTools.setString(data,"privilege",privilege);				
				sm.checkPrivilege(principal, data);
			}
			JsonTools.setBoolean(data,"isLoggedIn",sess.isLoggedIn());
		}
		
		return 0;
	}
}