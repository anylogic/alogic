package com.logicbus.backend.acm;

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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
 */
public abstract class ACMAccessController implements AccessController {
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
	protected Hashtable<String,AccessControlModel> acmCache = null;
	
	protected String appField = "a";
	
	protected String defaultAcmId = "default";
	
	/**
	 * 指标ID
	 */
	protected String metricsId = "acm.stat";
	
	protected AccessControlModel defaultAcm = null;
	
	public ACMAccessController(){

	}
	
	@Override
	public void configure(Element e, Properties props) {
		XmlElementProperties p = new XmlElementProperties(e,props);
		
		acmCache = new Hashtable<String,AccessControlModel>();		
		NodeList modules = XmlTools.getNodeListByPath(e, "model");

		for (int i = 0 ;i < modules.getLength() ; i ++){
			Node node = modules.item(i);
			
			if (Node.ELEMENT_NODE != node.getNodeType()){
				continue;
			}
			
			Element element = (Element)node;
			String id = element.getAttribute("id");
			if (StringUtils.isEmpty(id)){
				continue;
			}
			
			AccessControlModel acm = new AccessControlModel(id,element);
			acmCache.put(id, acm);
		}
		
		configure(p);
	}
	
	@Override
	public void configure(Properties props) {		
		defaultAcmId = PropertiesConstants.getString(props, "acm.default", defaultAcmId);
		defaultAcm = acmCache.get(defaultAcmId);
		appField = props.GetValue("acm.appArguName", appField);		
		metricsId = PropertiesConstants.getString(props, "acm.metrics.id", metricsId);
	}		
	
	protected String getACMObject(String sessionId,Path serviceId, ServiceDescription servant,
			Context ctx){
		return sessionId + ":" + serviceId.getPath();
	}
	
	protected AccessControlModel getACM(String sessionId,Path serviceId, ServiceDescription servant,
			Context ctx){
		return acmCache.get(sessionId);
	}
	
	protected int verify(AccessControlModel acm,Context ctx){
		return 0;
	}

	@Override
	public void reload(String id){
		// nothing to do
	}	
	
	@Override
	public int accessStart(String sessionId,Path serviceId, ServiceDescription servant,
			Context ctx) {
		AccessControlModel acm = getACM(sessionId,serviceId,servant,ctx);
		if (acm == null){
			acm = defaultAcm;
			if (acm == null)
				return -2;
		}
		
		/**
		 * 验证身份
		 */
		int verified = verify(acm,ctx);
		if (verified < 0){
			return verified;
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

	@Override
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

}
