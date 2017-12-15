package com.alogic.auth.service;


import com.alogic.auth.PrincipalManager;
import com.alogic.auth.SessionManagerFactory;
import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 退出登录
 * 
 * @author yyduan
 * 
 * @since 1.6.10.10
 * 
 * @version 1.6.11.1 [20171215 duanyy] <br>
 * - 增加获取登录id的方法<br>
 */
public class Logout extends AbstractServant{

	@Override
	protected void onDestroy() {
	
	}

	@Override
	protected void onCreate(ServiceDescription sd) {
		
	}

	@Override
	protected int onJson(Context ctx){
		ctx.asMessage(JsonMessage.class);
		PrincipalManager sm = (PrincipalManager)SessionManagerFactory.getDefault();
		sm.logout(ctx);
		return 0;
	}

	@Override
	protected int onXml(Context ctx){
		ctx.asMessage(XMLMessage.class);
		PrincipalManager sm = (PrincipalManager)SessionManagerFactory.getDefault();
		sm.logout(ctx);
		return 0;
	}
}

