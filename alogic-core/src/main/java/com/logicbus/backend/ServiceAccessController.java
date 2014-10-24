package com.logicbus.backend;

import com.anysoft.util.Properties;
import com.logicbus.models.catalog.Path;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 基于服务的访问控制器
 * 
 * <p>
 * 本AccessController实现表达了这样的场景：<br>
 * - 限定每个服务的访问并发数，通过client.maxThead环境变量控制，缺省为10;<br>
 * - 限定每个服务在一分钟内的访问次数，通过client.maxTimesPerMin环境变量控制，缺省值为1000.<br>
 * 
 * @author duanyy
 * 
 * @version 1.0.1 [20140402 duanyy] <br>
 * - {@link com.logicbus.backend.AccessController AccessController}有更新
 * 
 */
public class ServiceAccessController extends IpAndServiceAccessController {
	
	public ServiceAccessController(Properties props) {
		super(props);
	}

	
	public String createSessionId(Path serviceId, ServiceDescription servant,
			Context ctx){
		return serviceId.getPath();
	}
}
