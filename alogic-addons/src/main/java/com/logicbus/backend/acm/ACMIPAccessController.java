package com.logicbus.backend.acm;
import com.logicbus.backend.Context;
import com.logicbus.models.catalog.Path;
import com.logicbus.models.servant.ServiceDescription;


/**
 * 基于IP的ACM访问控制
 * 
 * @author duanyy
 * @since 1.2.3
 * 
 * @version 1.6.4.35 [20160315 duanyy] <br>
 * - 实现XMLConfigurable和Configurable接口 <br>
 */
public class ACMIPAccessController extends ACMAccessController {

	public String createSessionId(Path id, ServiceDescription sd,
			Context ctx) {
		return ctx.getClientIp();
	}
}
