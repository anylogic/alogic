package com.alogic.remote;

import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import com.alogic.remote.attempt.Failfast;
import com.alogic.remote.backend.Backend;
import com.alogic.remote.cluster.Cluster;
import com.alogic.remote.cluster.ClusterManager;
import com.alogic.remote.route.Route;
import com.alogic.rpc.CallException;
import com.anysoft.loadbalance.LoadBalance;
import com.anysoft.loadbalance.LoadBalanceFactory;
import com.anysoft.loadbalance.impl.RoundRobin;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.anysoft.util.Factory;

/**
 * 虚基类
 * @author yyduan
 * @since 1.6.8.12
 */
public abstract class AbstractClient implements Client {
	/**
	 * a logger of slf4j
	 */
	protected final static Logger LOG = LoggerFactory.getLogger(Client.class);
	
	/**
	 * 服务尝试策略
	 */
	protected Attempt attempt = null;
	
	/**
	 * 负载均衡策略
	 */
	protected LoadBalance<Backend> loadBalance = null;
	
	/**
	 * 后端集群
	 */
	protected Cluster cluster = null;
	
	/**
	 * 缺省的路由策略
	 */
	protected String dftRouteId = "default";
	
	/**
	 * 缺省的应用id
	 */
	protected String dftAppId = "default";
	
	/**
	 * URL的scheme
	 */
	protected String scheme = "http";
	
	/**
	 * 根据路由策略，负载均衡策略从Cluster中获取合适的后端节点
	 * @param key 服务调用的关键字（某些负载均衡算法需要）
	 * @param p 环境变量
	 * @param tryTimes 已经重试的次数（某些Attempt需要）
	 * @return 可用的后端节点
	 */
	public Backend getBackend(String key,Properties p,long tryTimes){		
		String routeId = PropertiesConstants.getString(p,"$route",dftRouteId,true);
		Route route = cluster.getRoute(routeId);
		if (route == null){
			throw new CallException("client.route_not_found","Can not find the route,id=" + routeId);
		}
		
		String app = PropertiesConstants.getString(p,"$app",dftAppId,true);
		return attempt.getBackend(route, loadBalance, app, key, p, tryTimes);
	}
	
	public String getInvokeURL(Backend backend,String path){
		StringBuffer url = new StringBuffer();			
		url.append(scheme).append("://").append(backend.getIp()).append(":").append(backend.getPort());
		String contextPath = backend.getContextPath();
		if(StringUtils.isNotEmpty(contextPath)){
			url.append('/').append(contextPath);	
		}
		
		url.append(path);
		return url.toString();
	}
	
	@Override
	public void report(Element xml) {
		if (xml != null){
			XmlTools.setString(xml, "module", getClass().getName());
		}
	}

	@Override
	public void report(Map<String, Object> json) {
		if (json != null){
			JsonTools.setString(json, "module", getClass().getName());
		}
	}

	@Override
	public void configure(Properties p) {
		dftRouteId = PropertiesConstants.getString(p,"rpc.ketty.route",dftRouteId);
		dftAppId = PropertiesConstants.getString(p,"rpc.ketty.app",dftAppId);
		scheme = PropertiesConstants.getString(p, "rpc.ketty.scheme", scheme);
		
		if (loadBalance == null){
			String lbModule = PropertiesConstants.getString(p,"rpc.ketty.loadbalance", RoundRobin.class.getName());		
			LoadBalanceFactory<Backend> f = new LoadBalanceFactory<Backend>();		
			try {
				loadBalance = f.newInstance(lbModule, p);
			}catch (Exception ex){
				LOG.error(String.format("Can not create load balance [%s], Using Default.",lbModule));
				loadBalance = new RoundRobin<Backend>(p);
				LOG.info("Current load balance is " + loadBalance.getClass().getName());
			}
		}
		if (attempt == null){
			String module =  PropertiesConstants.getString(p,"rpc.ketty.attempt", Failfast.class.getName());
			Factory<Attempt> f = new Factory<Attempt>();
			try {
				attempt = f.newInstance(module, p);
			}catch (Exception ex){
				LOG.error(String.format("Can not create attempt [%s], Using Default.",module));
				attempt = new Failfast();
				attempt.configure(p);
				LOG.info("Current attempt is " + attempt.getClass().getName());
			}
		}
		if (cluster == null){
			String clusterId = PropertiesConstants.getString(p,"rpc.ketty.cluster", "default");			
			ClusterManager cm = ClusterManager.TheFactory.get();
			cluster = cm.getCluster(clusterId);
		}
	}

	@Override
	public void configure(Element e, Properties p) {
		XmlElementProperties props = new XmlElementProperties(e,p);
		
		//支持内置的cluster定义
		Element clusterElem = XmlTools.getFirstElementByPath(e, "cluster");
		if (clusterElem != null){
			Factory<Cluster> factory = new Factory<Cluster>();
			try {
				cluster = factory.newInstance(clusterElem, props, "module");
			}catch (Exception ex){
				LOG.error(String.format("Can not create cluster with %s", XmlTools.node2String(clusterElem)));
			}
		}
		
		configure(props);
	}
}
