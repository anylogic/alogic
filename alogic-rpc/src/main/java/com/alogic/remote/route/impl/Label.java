package com.alogic.remote.route.impl;

import java.util.List;

import com.anysoft.util.Properties;
import com.alogic.remote.backend.AppBackends;
import com.alogic.remote.backend.Backend;
import com.alogic.remote.backend.BackendProvider;
import com.alogic.remote.route.Route;

/**
 * 基于标签的路由模式
 * 
 * @author duanyy
 * @since 1.6.8.12
 */
public class Label extends Route.Indexed{
	protected static final String pattern = "${label}";
	
	public Label(){
		
	}
	
	public Label(String id,BackendProvider provider){
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
			String [] labels = b.getLabels();
			for (String label:labels){
				ar.add(label, b);
			}
		}
	}
}