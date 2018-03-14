package com.alogic.auth.service;

import com.alogic.auth.PrincipalManager;
import com.alogic.auth.SessionManagerFactory;
import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 处理身份验证扩展指令
 * 
 * @author yyduan
 * 
 * @since 1.6.11.22
 *
 */
public class Command extends AbstractServant{

	@Override
	protected void onDestroy() {

	}

	@Override
	protected void onCreate(ServiceDescription sd) {

	}

	@Override
	protected int onJson(Context ctx){
		PrincipalManager sm = (PrincipalManager)SessionManagerFactory.getDefault();
		sm.command(ctx);
		return 0;
	}

	@Override
	protected int onXml(Context ctx){
		throw new ServantException("core.e1000","Protocol XML is not suppurted.");	
	}
}