package com.alogic.auth.service;

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
 * 检查菜单权限
 * 
 * @author duanyy
 *
 * @since 1.6.10.10
 */
public class CheckMenuPrivilege extends AbstractServant{

	@Override
	protected void onDestroy() {

	}

	@Override
	protected void onCreate(ServiceDescription sd) {

	}

	@Override
	protected int onJson(Context ctx){
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		Object data = msg.getRoot().get("data");
		if (data != null && data instanceof Map){
			@SuppressWarnings("unchecked")
			Map<String,Object> map = (Map<String,Object>)msg.getRoot().get("data");
			PrincipalManager sm = (PrincipalManager)SessionManagerFactory.getDefault();
			Session sess = sm.getSession(ctx, false);
			if (sess == null){
				JsonTools.setString(map,"isLoggedIn","false");
			}else{			
				Principal principal = sm.getCurrent(ctx);
				if (principal != null){
					sm.checkPrivilege(principal,map,
							this.getArgument("objId","",ctx),
							this.getArgument("objType","", ctx)
							);					
				}
				JsonTools.setBoolean(map,"isLoggedIn",sess.isLoggedIn());
			}
		}
		return 0;
	}
}