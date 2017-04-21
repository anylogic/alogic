package com.alogic.remote;

import java.util.Map;

import org.w3c.dom.Element;

import com.alogic.remote.backend.Backend;
import com.alogic.remote.cluster.Cluster;
import com.anysoft.loadbalance.LoadBalance;
import com.anysoft.loadbalance.LoadBalanceFactory;
import com.anysoft.loadbalance.impl.RoundRobin;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.esotericsoftware.minlog.Log;

/**
 * 虚基类
 * @author yyduan
 *
 */
public class AbstractClient implements Client {
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
	protected String routeId = "default";
	
	@Override
	public void report(Element xml) {
		if (xml != null){
			XmlTools.setString(xml, "module", getClass().getName());
			
			if (attempt != null){
				XmlTools.setString(xml, "rpc.ketty.attempt", attempt.getClass().getName());
			}
		}
	}

	@Override
	public void report(Map<String, Object> json) {
		if (json != null){
			JsonTools.setString(json, "module", getClass().getName());
			
			if (attempt != null){
				JsonTools.setString(json, "rpc.ketty.attempt", attempt.getClass().getName());
			}
		}
	}

	@Override
	public void configure(Properties p) {
		if (loadBalance == null){
			String lbModule = PropertiesConstants.getString(p,"rpc.ketty.loadbalance", RoundRobin.class.getName());		
			LoadBalanceFactory<Backend> f = new LoadBalanceFactory<Backend>();		
			try {
				loadBalance = f.newInstance(lbModule, p);
			}catch (Exception ex){
				Log.error(String.format("Can not create load balance [%s], Using Default.",lbModule));
				loadBalance = new RoundRobin<Backend>(p);
				Log.info("Current load balance is " + loadBalance.getClass().getName());
			}
		}
	}

	@Override
	public void configure(Element e, Properties p) {
		XmlElementProperties props = new XmlElementProperties(e,p);		
		configure(props);
	}

	@Override
	public Request build(String method) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Client addBackend(String appId, Backend backend) {
		// TODO Auto-generated method stub
		return null;
	}

}
