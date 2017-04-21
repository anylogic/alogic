package com.alogic.remote.attempt;

import com.alogic.remote.Attempt;
import com.alogic.remote.backend.Backend;
import com.alogic.remote.route.Route;
import com.anysoft.loadbalance.LoadBalance;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * Failover
 * @author yyduan
 *
 */
public class Failover extends Attempt.Abstract{
	/**
	 * 最大重试次数
	 */
	protected long maxTryTimes = 3;

	@Override
	public void configure(Properties p) {
		maxTryTimes = PropertiesConstants.getLong(p,"rpc.ketty.maxTryTimes", maxTryTimes);
	}	
	
	@Override
	public Backend retry(Route route,LoadBalance<Backend> lb,String app, String key, Properties p, long tryTimes) {
		if (tryTimes < maxTryTimes){
			LOG.warn("Retry to call a remote service,Cnt=" + tryTimes);
			return selectBackend(route,lb,app, key, p,true);
		}else{
			return null;
		}
	}
}
