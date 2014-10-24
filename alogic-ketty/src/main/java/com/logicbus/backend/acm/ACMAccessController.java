package com.logicbus.backend.acm;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.metrics.core.Dimensions;
import com.anysoft.metrics.core.Fragment;
import com.anysoft.metrics.core.Measures;
import com.anysoft.metrics.core.MetricsCollector;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.logicbus.backend.AccessController;
import com.logicbus.backend.Context;
import com.logicbus.models.catalog.Path;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 基于ACM的访问控制器
 * 
 * @author duanyy
 * @since 1.2.3
 * 
 * @version 1.2.4.3 [20140709 duanyy]
 * - 在找不到ACM的情况下,使用缺省的ACM
 * 
 * @version 1.2.6.2 [20140814 duanyy]
 * - 优化缺省ACM模型的存储机制
 * 
 * @version 1.2.8.2 [20141011 duanyy] <br>
 * - AccessStat变更可见性为public
 * - 实现Reportable和MetricsReportable
 */
abstract public class ACMAccessController implements AccessController {
	/**
	 * 访问列表
	 */
	protected Hashtable<String,AccessStat> acl = new Hashtable<String,AccessStat>();

	/**
	 * 锁
	 */
	protected ReentrantLock lock = new ReentrantLock();
	
	/**
	 * ACM缓存管理器
	 */
	protected ACMCacheManager acmCache = null;
	
	/**
	 * 是否启用TokenCenter模式
	 */
	protected boolean tcMode = false;
		
	/**
	 * Token Holder
	 */
	protected TokenHolder tokenHolder = null;
	
	protected TokenCenterConnector tcc = null;
	
	protected String appField = "a";
	
	protected String defaultAcmId = "default";
	
	/**
	 * 指标ID
	 */
	protected String metricsId = "acm.stat";
	
	protected AccessControlModel defaultAcm = null;
	
	public ACMAccessController(Properties props){
		acmCache = getCacheManager();
		tcMode = PropertiesConstants.getBoolean(props, "acm.tcMode", false);
		defaultAcmId = PropertiesConstants.getString(props, "acm.default", defaultAcmId);
		defaultAcm = acmCache.get(defaultAcmId);
		if (tcMode){
			tokenHolder = new TokenHolder(props);
		}
		appField = props.GetValue("acm.appArguName", appField);		
		metricsId = PropertiesConstants.getString(props, "acm.metrics.id", metricsId);
	}
	
	public TokenHolder getTokenHolder(){
		return tokenHolder;
	}
	
	/**
	 * 创建CacheManager
	 * @return
	 */
	protected ACMCacheManager getCacheManager(){
		return ACMCacheManager.get();
	}
	
	protected String getACMObject(String sessionId,Path serviceId, ServiceDescription servant,
			Context ctx){
		return sessionId + ":" + serviceId.getPath();
	}
	
	
	public int accessStart(String sessionId,Path serviceId, ServiceDescription servant,
			Context ctx) {
		AccessControlModel acm = acmCache.get(sessionId);
		if (acm == null){
			acm = defaultAcm;
			if (acm == null)
				return -2;
		}
		
		if (!servant.getVisible().equals("public")){
			//仅对非public服务进行控制
			if (tcMode){
				//从参数中获取Token
				String t = ctx.GetValue("token", "");
				if (t == null || t.length() <= 0){
					//没有按照协议要求传递token参数
					return -1;
				}
				//看看TokenHolder中有没有缓存该Token
				boolean found = tokenHolder.exist(t);
				if (!found){
					//调用TokenCenter查询Token是否有效
					String app = ctx.GetValue(appField, "Default");
					if (tcc == null){
						tcc = new TokenCenterConnector(Settings.get());
					}
					boolean valid = tcc.tokenIsValid(app, t);
					if (!valid){
						//连TokenCenter都说是非法
						return -3;
					}
					tokenHolder.add(t);
				}
			}
		}
		
		lock.lock();
		try{
			String acmObject = getACMObject(sessionId,serviceId,servant,ctx);
			AccessStat current = acl.get(acmObject);	
			if (current == null){
				current = new AccessStat();
				acl.put(acmObject, current);
			}
			
			current.timesTotal ++;
			current.thread ++;
			current.waitCnt = lock.getQueueLength();
			long timestamp = System.currentTimeMillis();
			timestamp = (timestamp / 60000)*60000;
			if (timestamp != current.timestamp){
				//新的周期
				current.timesOneMin = 1;
				current.timestamp = timestamp;
			}else{
				current.timesOneMin ++;
			}
			
			return acm.getPriority(ctx.getClientIp(), serviceId.getPath(), current);
		}finally{
			lock.unlock();
		}
	}

	
	public int accessEnd(String sessionId,Path serviceId, ServiceDescription servant, Context ctx) {
		lock.lock();
		try{
			String acmObject = getACMObject(sessionId,serviceId,servant,ctx);
			AccessStat current = acl.get(acmObject);
			if (current != null){
				current.thread --;
			}
		}finally{
			lock.unlock();
		}
		return 0;
	}

	
	public void report(Element root) {
		if (root != null){
			Document doc = root.getOwnerDocument();
			
			Enumeration<String> keys = acl.keys();
			while (keys.hasMoreElements()){
				String key = keys.nextElement();
				AccessStat value = acl.get(key);
				Element eAcl = doc.createElement("acl");
				
				eAcl.setAttribute("session", key);
				eAcl.setAttribute("currentThread", String.valueOf(value.thread));
				eAcl.setAttribute("timesTotal", String.valueOf(value.timesTotal));
				eAcl.setAttribute("timesOneMin",String.valueOf(value.timesOneMin));
				eAcl.setAttribute("waitCnt", String.valueOf(value.waitCnt));
				
				root.appendChild(eAcl);
			}
			
			root.setAttribute("module", getClass().getName());
		}
	}

	
	public void report(Map<String,Object> json) {
		if (json != null){
			List<Object> acls = new ArrayList<Object>();
			
			Enumeration<String> keys = acl.keys();
			while (keys.hasMoreElements()){
				String key = keys.nextElement();
				AccessStat value = acl.get(key);
				
				Map<String,Object> mAcl = new HashMap<String,Object>();

				mAcl.put("session", key);
				mAcl.put("currentThread", String.valueOf(value.thread));
				mAcl.put("timesTotal", String.valueOf(value.timesTotal));
				mAcl.put("timesOneMin",String.valueOf(value.timesOneMin));
				mAcl.put("waitCnt", String.valueOf(value.waitCnt));
				
				acls.add(mAcl);
			}
			json.put("module", getClass().getName());
			json.put("acl", acls);
		}
	}
	
	public void report(MetricsCollector collector) {
		if (collector != null){
			Enumeration<String> keys = acl.keys();
			while (keys.hasMoreElements()){
				String key = keys.nextElement();
				AccessStat value = acl.get(key);
				
				Fragment f = new Fragment(metricsId);
				
				Dimensions dims = f.getDimensions();
				if (dims != null)
					dims.lpush(key);
				
				Measures meas = f.getMeasures();
				if (meas != null)
					meas.lpush(new Object[]{
							value.thread,
							value.timesTotal,
							value.timesOneMin,
							value.waitCnt
					});
				
				collector.metricsIncr(f);
			}			
		}
	}

}
