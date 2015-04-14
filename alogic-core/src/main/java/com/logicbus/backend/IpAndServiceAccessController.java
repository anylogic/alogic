package com.logicbus.backend;

import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.models.catalog.Path;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 基于IP和服务的访问控制器
 * 
 * <p>
 * 本AccessController实现表达了这样的场景：<br>
 * - 限定每个客户端对每个服务的访问并发数，通过client.maxThead环境变量控制，缺省为10;<br>
 * - 限定每个客户端对每个服务的在一分钟内的访问次数，通过client.maxTimesPerMin环境变量控制，缺省值为1000.<br>
 * 
 * @author duanyy
 * 
 * @version 1.0.1 [20140402 duanyy] <br>
 * - {@link com.logicbus.backend.AccessController AccessController}有更新
 * 
 * @version 1.6.3.18 [20150414 duanyy] <br>
 * - 方法{@link #getClientPriority(Path, ServiceDescription, Context, AccessStat) getClientPriority}
 * 增加参数sessionId <br>
 */
public class IpAndServiceAccessController extends AbstractAccessController {
	
	/**
	 * 最大并发数
	 */
	protected int maxThread = 10;
	
	/**
	 * 一分钟内访问次数限制
	 */
	protected int maxtimesPerMin = 1000;
	
	public IpAndServiceAccessController(Properties props){
		super(props);
		maxThread = PropertiesConstants.getInt(props, "acm.maxThread", maxThread);
		maxtimesPerMin = PropertiesConstants.getInt(props, "acm.maxTimesPerMin", maxtimesPerMin);
	}

	
	public String createSessionId(Path serviceId, ServiceDescription servant,
			Context ctx){
		return ctx.getClientIp() + ":" + serviceId.getPath();
	}
	
	protected int getClientPriority(String sessionId,Path serviceId,ServiceDescription servant,
			Context ctx,AccessStat stat){
		if (stat.thread > maxThread || stat.timesOneMin > maxtimesPerMin){
			return -1;
		}
		return 0;
	}
}
