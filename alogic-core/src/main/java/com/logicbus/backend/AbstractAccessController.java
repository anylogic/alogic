package com.logicbus.backend;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;
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
import com.logicbus.models.catalog.Path;
import com.logicbus.models.servant.ServiceDescription;

/**
 * AccessController的实现
 * 
 * <p>本实现提供了基于SessionID的访问控制方式，提供了并发数，一分钟之内的调用次数等变量.
 * 
 * <p>本类是一个虚类，需要子类做进一步细化，包括：<br>
 * - SessionID如何组成？<br>
 * - 如何根据等待队列长度，最近一分钟之内的调用次数等变量判断访问权限<br>
 * 
 * @author duanyy
 * 
 * @version 1.0.1 [20140402 duanyy] <br>
 * - {@link com.logicbus.backend.AccessController AccessController}有更新
 * 
 * @version 1.2.1 [20140613 duanyy] <br>
 * - 共享锁由synchronized改为ReentrantLock
 * 
 * @version 1.2.8.2 [20141011 duanyy] <br>
 * - AccessStat变更可见性为public <br>
 * - 实现Reportable和MetricsReportable <br>
 * 
 * @version 1.6.3.18 [20150414 duanyy] <br>
 * - 方法getClientPriority增加参数sessionId <br>
 * 
 * @version 1.6.4.35 [20160315 duanyy] <br>
 * - 实现XMLConfigurable和Configurable接口 <br>
 * 
 * @version 1.6.4.41 [20160401 duanyy] <br>
 * - Report增加分页功能 <br>
 * 
 * @version 1.6.5.5 [20160515 duanyy] <br>
 * - 增加reload接口 <br>
 * 
 * @version 1.6.7.4 [20170118 duanyy] <br>
 * - 淘汰com.anysoft.metrics包 ，改用新的指标框架<br>
 * 
 * @version 1.6.10.12 [20171211 duanyy] <br>
 * - 兼容混合模式 <br>
 */
public abstract class AbstractAccessController implements AccessController {
	/**
	 * 访问列表
	 */
	protected Hashtable<String,AccessStat> acl = new Hashtable<String,AccessStat>();

	/**
	 * 锁
	 */
	protected ReentrantLock lock = new ReentrantLock();
	/**
	 * 指标ID
	 */
	protected String metricsId = "acm.stat";
	
	public AbstractAccessController(){
	}
	
	@Override
	public void reload(String id){
		// nothing to do
	}
	
	@Override
	public void configure(Element e, Properties props) {
		XmlElementProperties p = new XmlElementProperties(e,props);
		configure(p);
	}
	
	@Override
	public void configure(Properties p) {
		metricsId = PropertiesConstants.getString(p, "acm.metrics.id", metricsId);
	}	
	
	@Override
	public String [] getGroupList(){
		return new String[]{"default"};
	}
	
	@Override
	public AccessController getGroup(String id){
		return this;
	}	
	
	@Override
	public int accessEnd(String sessionId,Path serviceId, ServiceDescription servant,
			Context ctx) {
		lock.lock();
		try {
			AccessStat current = acl.get(sessionId);
			if (current != null){
				current.thread --;
			}
		}finally{
			lock.unlock();
		}
		return 0;
	}

	@Override
	public int accessStart(String sessionId,Path serviceId, ServiceDescription servant,
			Context ctx) {
		lock.lock();
		try {
			AccessStat current = acl.get(sessionId);	
			if (current == null){
				current = new AccessStat();
				acl.put(sessionId, current);
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
			
			return getClientPriority(sessionId,serviceId,servant,ctx,current);
		}finally{
			lock.unlock();
		}
	}
		
	/**
	 * 获取控制优先级
	 * @param sessionId 会话ID
	 * @param serviceId 服务ID
	 * @param servant 服务描述
	 * @param ctx 上下文
	 * @param stat 当前Session的访问统计
	 * @return 优先级
	 */
	protected abstract int getClientPriority(String sessionId,Path serviceId,ServiceDescription servant,
			Context ctx,AccessStat stat);
	
	
	@Override
	public void report(Element root) {
		if (root != null){
			int offset = XmlTools.getInt(root, "offset", 0);
			int limit = XmlTools.getInt(root,"limit",30);
			String keyword = XmlTools.getString(root, "keyword", "");

			
			Document doc = root.getOwnerDocument();
			
			Enumeration<String> keys = acl.keys();
			
			int current = 0;
			
			while (keys.hasMoreElements()){
				String key = keys.nextElement();
				
				boolean match = StringUtils.isEmpty(keyword) || key.contains(keyword);
				
				if (match){
					if (current >= offset && current < offset + limit){
						AccessStat value = acl.get(key);
						Element eAcl = doc.createElement("acl");
						
						eAcl.setAttribute("session", key);
						eAcl.setAttribute("currentThread", String.valueOf(value.thread));
						eAcl.setAttribute("timesTotal", String.valueOf(value.timesTotal));
						eAcl.setAttribute("timesOneMin",String.valueOf(value.timesOneMin));
						eAcl.setAttribute("waitCnt", String.valueOf(value.waitCnt));
						
						root.appendChild(eAcl);						
					}
					current ++;
				}
			}

			XmlTools.setInt(root, "total", current);
			XmlTools.setInt(root, "all", acl.size());
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
			
			Enumeration<String> keys = acl.keys();
			
			int current = 0;
			
			while (keys.hasMoreElements()){
				String key = keys.nextElement();
				
				boolean match = StringUtils.isEmpty(keyword) || key.contains(keyword);
				if (match){
						if (current >= offset && current < offset + limit){
							AccessStat value = acl.get(key);
							
							Map<String,Object> mAcl = new HashMap<String,Object>();

							mAcl.put("session", key);
							mAcl.put("currentThread", String.valueOf(value.thread));
							mAcl.put("timesTotal", String.valueOf(value.timesTotal));
							mAcl.put("timesOneMin",String.valueOf(value.timesOneMin));
							mAcl.put("waitCnt", String.valueOf(value.waitCnt));
							
							acls.add(mAcl);							
						}
						current ++;
				}
			}
			
			JsonTools.setInt(json, "total", current);
			JsonTools.setInt(json,"all",acl.size());
			JsonTools.setString(json, "module", getClass().getName());
			json.put("acl", acls);
		}
	}
	
	@Override
	public void report(MetricsCollector collector) {
		if (collector != null){
			Enumeration<String> keys = acl.keys();
			while (keys.hasMoreElements()){
				String key = keys.nextElement();
				AccessStat value = acl.get(key);
				
				Fragment f = new DefaultFragment(metricsId);
				
				Dimensions dims = f.getDimensions();
				if (dims != null){
					dims.set("session", key, true);
				}
				Measures meas = f.getMeasures();
				if (meas != null){
					meas.set("thread", value.thread, Method.avg);
					meas.set("timesTotal", value.timesTotal,Method.avg);
					meas.set("timesOneMin", value.timesOneMin,Method.avg);
					meas.set("waitCnt", value.waitCnt,Method.avg);
				}
				
				collector.metricsIncr(f);
			}			
		}
	}
	
	/**
	 * 访问统计
	 * @author duanyy
	 *
	 */
	public static class AccessStat {
		/**
		 * 总调用次数
		 */
		public long timesTotal = 0;
		/**
		 * 最近一分钟调用次数
		 */
		public int timesOneMin = 0;
		/**
		 * 当前接入进程个数
		 */
		public int thread = 0;
		/**
		 * 时间戳(用于定义最近一分钟)
		 */
		public long timestamp = 0;
		
		/**
		 * 等待进程数
		 * @since 1.2.1
		 */
		public int waitCnt = 0;
	}
}
