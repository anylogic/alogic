package com.alogic.remote.route.impl;

import java.util.List;

import com.anysoft.util.Properties;
import com.alogic.remote.backend.AppBackends;
import com.alogic.remote.backend.Backend;
import com.alogic.remote.backend.BackendProvider;
import com.alogic.remote.route.Route;

/**
 * 基于版本的路由模式
 * 
 * @author duanyy
 * @since 1.6.8.12
 */
public class Version extends Route.Indexed{
	protected static final String pattern = "${version}";

	public Version(){
		
	}
	
	public Version(String id,BackendProvider provider){
		super(id,provider);
	}	
		
	@Override
	protected String getRoute(String app, Properties p) {
		return p.transform(pattern);
	}

	@Override
	protected void rebuild(AppRoute ar, AppBackends app) {
		List<Backend> backends = app.getBackends();
		
		for (Backend b:backends){
			ar.add(b.getVersion(), b);
		}
	}

}
