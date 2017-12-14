package com.alogic.remote;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.alogic.remote.backend.Backend;
import com.alogic.remote.route.Route;
import com.alogic.rpc.CallException;
import com.anysoft.loadbalance.LoadBalance;
import com.anysoft.util.Configurable;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * 尝试接口
 * @author yyduan
 * @since 1.6.8.12
 */
public interface Attempt extends Reportable,Configurable,XMLConfigurable{
	
	/**
	 * 获取后端节点
	 * 
	 * @param route 路由策略
	 * @param lb 负载均衡策略
	 * @param app 应用id
	 * @param key 关键字
	 * @param p 变量集
	 * @param tryTimes 已经重试的次数
	 * @return 可用的后端节点
	 */
	public Backend getBackend(Route route,LoadBalance<Backend> lb,String app,String key,Properties p,long tryTimes);
	
	/**
	 * 虚基类
	 * @author yyduan
	 *
	 */
	public abstract static class Abstract implements Attempt{
		/**
		 * a logger of slf4j
		 */
		protected static final Logger LOG = LoggerFactory.getLogger(Attempt.class);
		
		@Override
		public void report(Element xml) {
			if (xml != null){
				XmlTools.setString(xml, "module", getClass().getName());
			}
		}

		@Override
		public void report(Map<String, Object> json) {
			if (json != null){
				JsonTools.setString(json,"module",getClass().getName());
			}
		}
		
		@Override
		public void configure(Element e, Properties p) {
			Properties props = new XmlElementProperties(e,p);
			configure(props);
		}

		@Override
		public Backend getBackend(Route route,LoadBalance<Backend> lb,String app,String key,Properties p,long tryTimes) {
			if (tryTimes <= 0){
				return selectBackend(route,lb,app,key,p,false);
			}else{
				return retry(route,lb,app,key,p,tryTimes);
			}
		}
		
		protected Backend selectBackend(Route route,LoadBalance<Backend> lb,String app,String key,Properties p,boolean excludeNotValid){	
			List<Backend> backends = route.select(app, p);
			if (backends == null){
				throw new CallException("core.e1600","Can not find valid backends.");
			}
			
			List<Backend> list = backends;
			if (excludeNotValid){
				list = new ArrayList<Backend>();
				for (Backend b:backends){
					if (b.isValid()){
						list.add(b);
					}
				}
				if (list.isEmpty()){
					throw new CallException("core.e1600","Can not find valid backends,Not all backends is valid");
				}
			}
			
			Backend backend = lb.select(key, p, list);
			if (backend == null){
				throw new CallException("core.e1600","Can not find valid backends,Not all backends is valid");
			}
			
			return backend;
		}
		
		public abstract Backend retry(Route route,LoadBalance<Backend> lb,String app, String key, Properties p, long tryTimes);
	}
}
