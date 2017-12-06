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
		
		Map<String,Object> data = new HashMap<String,Object>();
		PrincipalManager sm = (PrincipalManager)SessionManagerFactory.getDefault();
		Session sess = sm.getSession(ctx, false);
		if (sess == null){
			JsonTools.setString(data,"isLoggedIn","false");
		}else{			
			Principal principal = sm.getCurrent(ctx);
			if (principal == null){
				JsonTools.setString(data,"isLoggedIn","false");
			}else{
				//用户已经登录
				JsonTools.setString(data,"isLoggedIn","true");
				String privilege = this.getArgument("privilege", "", ctx);
				String objectId = this.getArgument("objId","",ctx);
				String objectType = this.getArgument("objType","", ctx);
				
				boolean enable = sm.hasPrivilege(principal, privilege, objectId, objectType);
				
				JsonTools.setString(data,"privilege",privilege);
				JsonTools.setBoolean(data,"enable",enable);
				JsonTools.setString(data,"objId",objectId);
				JsonTools.setString(data,"objType",objectType);
			}
		}
		
		msg.getRoot().put("data", data);
		return 0;
	}
}