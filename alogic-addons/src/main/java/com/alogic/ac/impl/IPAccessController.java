package com.alogic.ac.impl;

import com.alogic.ac.ACMAccessController;
import com.logicbus.backend.Context;
import com.logicbus.models.catalog.Path;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 基于IP的访问控制器
 * 
 * @author yyduan
 * @since 1.6.10.6
 */
public class IPAccessController extends ACMAccessController{
	@Override
	public String createSessionId(Path serviceId, ServiceDescription servant,
			Context ctx) {
		return getClientIp(ctx);
	}
}
