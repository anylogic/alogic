package com.alogic.remote.attempt;

import com.alogic.remote.Attempt;
import com.alogic.remote.backend.Backend;
import com.alogic.remote.route.Route;
import com.anysoft.loadbalance.LoadBalance;
import com.anysoft.util.Properties;

/**
 * Failfast
 * @author yyduan
 *
 */
public class Failfast extends Attempt.Abstract{
	
	@Override
	public Backend retry(Route route,LoadBalance<Backend> lb,String app, String key, Properties p, long tryTimes) {
		//不再重试
		return null;
	}

	@Override
	public void configure(Properties p) {

	}
}
