package com.alogic.ac;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.alogic.metrics.Dimensions;
import com.alogic.metrics.Fragment;
import com.alogic.metrics.Measures;
import com.alogic.metrics.Fragment.Method;
import com.alogic.metrics.impl.DefaultFragment;
import com.alogic.metrics.stream.MetricsCollector;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.logicbus.backend.AccessController;
import com.logicbus.backend.Context;
import com.logicbus.models.catalog.Path;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 基于ACM访问控制器的虚基类
 * 
 * @author yyduan
 * 
 * @since 1.6.10.6
 * 
 * @version 1.6.10.7 [20171115 duanyy] <br>
 * - AccessStat中增加被Denied的统计信息 <br>
 */
public abstract class AbstractACMAccessController implements AccessController {
	/**
	 * a logger of slf4j
	 */
	protected static final Logger LOG = LoggerFactory.getLogger(AbstractACMAccessController.class);
	
	/**
	 * 访问统计
	 */
	protected Map<String,AccessStat> stats = new ConcurrentHashMap<String,AccessStat>();

	/**
	 * 锁
	 */
	protected ReentrantLock lock = new ReentrantLock();

	/**
	 * 指标ID
	 */
	protected String metricsId = "acm.stat";
	
	public AbstractACMAccessController(){
		
	}
	
	@Override
	public void configure(Element e, Properties props) {
		XmlElementProperties p = new XmlElementProperties(e,props);
		configure(p);
	}

	@Override
	public void configure(Properties props) {		
		metricsId = PropertiesConstants.getString(props, "acm.metrics.id", metricsId);
	}		
	
	/**
	 * 根据绘画获取ACM控制对象id
	 * @param sessionId 会话id
	 * @param serviceId 本次会话调用的服务
	 * @param servant 服务定义
	 * @param ctx 上下文
	 * @return 对象id
	 */
	protected String getACMObject(String sessionId,Path serviceId, ServiceDescription servant,
			Context ctx){
		return sessionId;
	}
	
	/**
	 * 根据会话获取ACM模型
	 * @param sessionId 会话id
	 * @param serviceId 本次会话调用的服务
	 * @param servant 服务定义
	 * @param ctx 上下文
	 * @return ACM模型
	 */
	abstract protected AccessControlModel getACM(String sessionId,Path serviceId, ServiceDescription servant,
			Context ctx);
	
	/**
	 * 获取当前调用者的ip
	 * @param ctx 上下文
	 * @return 调用者ip
	 */
	protected String getClientIp(Context ctx){
		return ctx.getClientIp();
	}
	
	@Override
	public int accessStart(String sessionId,Path serviceId, ServiceDescription servant,
			Context ctx) {
		AccessControlModel acm = getACM(sessionId,serviceId,servant,ctx);
		if (acm == null){
			return -2;
		}
		
		lock.lock();
		try{
			String acmObject = getACMObject(sessionId,serviceId,servant,ctx);
			AccessStat current = stats.get(acmObject);	
			if (current == null){
				current = new AccessStat();
				stats.put(acmObject, current);
			}
			
			current.timesTotal ++;
			current.thread ++;
			current.waitCnt = lock.getQueueLength();
			
			int priority = acm.getPriority(getClientIp(ctx), serviceId.getPath(), current);
			if (priority < 0){
				current.deniedTotal ++;
			}
			
			long timestamp = System.currentTimeMillis();
			timestamp = (timestamp / 60000)*60000;
			if (timestamp != current.timestamp){
				//新的周期
				current.timesOneMin = 1;
				current.deniedOneMin = priority < 0 ? 1 : 0;				
				current.timestamp = timestamp;
			}else{
				current.timesOneMin ++;
				if (priority < 0){
					current.deniedOneMin ++;
				}
			}
			return priority;
		}finally{
			lock.unlock();
		}
	}

	@Override
	public int accessEnd(String sessionId,Path serviceId, ServiceDescription servant, Context ctx) {
		lock.lock();
		try{
			String acmObject = getACMObject(sessionId,serviceId,servant,ctx);
			AccessStat current = stats.get(acmObject);
			if (current != null){
				current.thread --;
			}
		}finally{
			lock.unlock();
		}
		return 0;
	}
	
	@Override
	public void report(Element root) {
		if (root != null){
			int offset = XmlTools.getInt(root, "offset", 0);
			int limit = XmlTools.getInt(root,"limit",30);
			String keyword = XmlTools.getString(root, "keyword", "");
			Document doc = root.getOwnerDocument();
			Iterator<String> iter = stats.keySet().iterator();
			int current = 0;
			
			while (iter.hasNext()){
				String key = iter.next();
				
				boolean match = StringUtils.isEmpty(keyword) || key.contains(keyword);
				
				if (match){
					if (current >= offset && current < offset + limit){
						AccessStat value = stats.get(key);
						Element eAcl = doc.createElement("acl");
						
						eAcl.setAttribute("session", key);
						eAcl.setAttribute("currentThread", String.valueOf(value.thread));
						eAcl.setAttribute("timesTotal", String.valueOf(value.timesTotal));
						eAcl.setAttribute("timesOneMin",String.valueOf(value.timesOneMin));
						eAcl.setAttribute("waitCnt", String.valueOf(value.waitCnt));
						eAcl.setAttribute("deniedTotal", String.valueOf(value.deniedTotal));
						eAcl.setAttribute("deniedOneMin", String.valueOf(value.deniedOneMin));
						
						root.appendChild(eAcl);						
					}
					current ++;
				}
			}

			XmlTools.setInt(root, "total", current);
			XmlTools.setInt(root, "all", stats.size());
			XmlTools.setString(root,"module",getClass().getName());
		}
	}

	@Override
	public void report(Map<String,Object> json) {
		if (json != null){
			int offset = JsonTools.getInt(json, "offset", 0);
			int limit = JsonTools.getInt(json, "limit", 30);
			String keyword = JsonTools.getString(json,"keyword","");
			List<Object> acls = new ArrayList<Object>();
			Iterator<String> iter = stats.keySet().iterator();
			int current = 0;
			
			while (iter.hasNext()){
				String key = iter.next();
				
				boolean match = StringUtils.isEmpty(keyword) || key.contains(keyword);
				if (match){
						if (current >= offset && current < offset + limit){
							AccessStat value = stats.get(key);
							
							Map<String,Object> mAcl = new HashMap<String,Object>();

							mAcl.put("session", key);
							mAcl.put("currentThread", String.valueOf(value.thread));
							mAcl.put("timesTotal", String.valueOf(value.timesTotal));
							mAcl.put("timesOneMin",String.valueOf(value.timesOneMin));
							mAcl.put("waitCnt", String.valueOf(value.waitCnt));
							mAcl.put("deniedTotal", String.valueOf(value.deniedTotal));
							mAcl.put("deniedOneMin", String.valueOf(value.deniedOneMin));
							
							acls.add(mAcl);							
						}
						current ++;
				}
			}
			
			JsonTools.setInt(json, "total", current);
			JsonTools.setInt(json,"all",stats.size());
			JsonTools.setString(json, "module", getClass().getName());
			json.put("acl", acls);
		}
	}

	@Override
	public void report(MetricsCollector collector) {
		if (collector != null){
			Iterator<String> iter = stats.keySet().iterator();
			while (iter.hasNext()){
				String key = iter.next();
				AccessStat value = stats.get(key);
				
				Fragment f = new DefaultFragment(metricsId);
				
				Dimensions dims = f.getDimensions();
				if (dims != null){
					dims.set("session", key, true);
				}
				Measures meas = f.getMeasures();
				if (meas != null){
					meas.set("thread", value.thread, Method.lst);
					meas.set("timesTotal", value.timesTotal,Method.lst);
					meas.set("timesOneMin", value.timesOneMin,Method.lst);
					meas.set("waitCnt", value.waitCnt,Method.lst);
					meas.set("deniedTotal", value.deniedTotal,Method.lst);
					meas.set("deniedOneMin", value.deniedOneMin,Method.lst);
				}
				
				collector.metricsIncr(f);
			}			
		}
	}
}