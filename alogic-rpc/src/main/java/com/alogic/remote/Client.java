package com.alogic.remote;

import com.alogic.remote.backend.Backend;
import com.anysoft.util.Configurable;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;

/**
 * Client
 *
 * @author yyduan
 *
 */
public interface Client extends Reportable,Configurable,XMLConfigurable{
	
	/**
	 * 创建新的Client
	 * @param method 请求方法
	 * @return Request
	 */
	public Request build(String method);
	
	/**
	 * 为指定的应用增加后端节点
	 * @param backend 后端节点
	 * @return Client
	 */
	public Client addBackend(String appId,Backend backend);
}
