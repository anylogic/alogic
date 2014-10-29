package com.logicbus.backend.acm;

import com.anysoft.util.Properties;
import com.logicbus.backend.Context;
import com.logicbus.models.catalog.Path;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 基于客户的ACM访问控制
 * 
 * @author duanyy
 * @since 1.2.3
 */
public class ACMCustAccessController extends ACMAccessController {

	public ACMCustAccessController(Properties props) {
		super(props);
		
	}
	
	public String createSessionId(Path id, ServiceDescription sd,
			Context ctx) {
		return ctx.GetValue(appField, "default");
	}

}
