package com.alogic.remote;

import com.alogic.remote.backend.Backend;
import com.anysoft.util.Configurable;
import com.anysoft.util.Properties;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;

/**
 * Client
 *
 * @author yyduan
 * @since 1.6.8.12
 */
public interface Client extends Reportable,Configurable,XMLConfigurable{
	
	/**
	 * 创建新的Client
	 * @param method 请求方法
	 * @return Request
	 */
	public Request build(String method);
	
	/**
	 * 根据路由策略，负载均衡策略从Cluster中获取合适的后端节点
	 * @param key 服务调用的关键字（某些负载均衡算法需要）
	 * @param p 环境变量
	 * @param tryTimes 已经重试的次数（某些Attempt需要）
	 * @return 可用的后端节点
	 */
	public Backend getBackend(String key,Properties p,long tryTimes);
}
