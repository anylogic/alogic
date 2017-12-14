package com.anysoft.rrm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.metrics.Fragment;
import com.alogic.metrics.stream.MetricsCollector;
import com.alogic.metrics.stream.MetricsHandlerFactory;
import com.anysoft.stream.Handler;
import com.anysoft.util.Configurable;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;

/**
 * Round Robin Model
 * @author duanyy
 * @version 1.6.4.42 [duanyy 20160407] <br>
 * - 对接指标处理器 <br>
 * 
 * @version 1.6.6.13 [20170109 duanyy] <br>
 * - 采用新的指标接口 <br>
 * 
 */
public class RRModel<data extends RRData> implements XMLConfigurable,Configurable,Reportable,MetricsCollector{
	
	protected static final Logger LOG = LoggerFactory.getLogger(RRModel.class);

	/**
	 * id
	 */
	private String id;
	
	/**
	 * 模型下的多个RRA
	 */
	private Map<String,RRArchive<data>> rras = new HashMap<String,RRArchive<data>>();
	
	/**
	 * 指标处理器
	 */
	private Handler<Fragment> metricsHandler = null;
	
	/**
	 * 是否向指标处理器输出指标
	 */
	private boolean metricsOutput = true;
	
	/**
	 * 获取id
	 * @return id
	 */
	public String getId(){
		return id;
	}
	
	public RRModel(String _id){
		id = _id;
	}
	
	/**
	 * 获取指定id的rra
	 * @param id id
	 * @return rra
	 */
	public RRArchive<data> getRRA(String id){
		return rras.get(id);
	}
	
	/**
	 * 更新数据
	 * @param timestamp
	 * @param fragment
	 */
	public void update(long timestamp,data fragment){
		Iterator<RRArchive<data>> iterator = rras.values().iterator();
		while (iterator.hasNext()){
			RRArchive<data> rra = iterator.next();
			rra.update(timestamp, fragment);
		}
		
		if (metricsOutput){
			fragment.report(this);
		}
	}
	
	@Override
	public void report(Element xml) {
		if (xml != null){
			xml.setAttribute("id", id);
			
			String cycle = xml.getAttribute("cycle");
			String current = xml.getAttribute("current");
			
			Document doc = xml.getOwnerDocument();
			
			if (cycle != null && cycle.length() > 0){
				RRArchive<data> found = rras.get(cycle);
				if (found != null){
					Element _rra = doc.createElement("rra");
					if (current == null || !current.equals("true")){
						_rra.setAttribute("hist", "true");
					}
					found.report(_rra);
					xml.appendChild(_rra);
				}
			}else{	
				Iterator<RRArchive<data>> iterator = rras.values().iterator();
				while (iterator.hasNext()){
					RRArchive<data> rra = iterator.next();
					Element _rra = doc.createElement("rra");
					rra.report(_rra);
					xml.appendChild(_rra);
				}
			}
		}
	}

	@Override
	public void report(Map<String, Object> json) {
		if (json != null){
			json.put("id", id);
			
			String cycle = JsonTools.getString(json, "cycle", "");
			boolean current = JsonTools.getBoolean(json, "current", false);
			if (cycle != null && cycle.length() > 0){
				RRArchive<data> found = rras.get(cycle);
				if (found != null){
					Map<String,Object> _rra = new HashMap<String,Object>();
					if (!current){
						_rra.put("hist", true);
					}
					found.report(_rra);
					json.put("rra", _rra);
				}
			}else{
				List<Object> _rras = new ArrayList<Object>();
				
				Iterator<RRArchive<data>> iterator = rras.values().iterator();
				while (iterator.hasNext()){
					RRArchive<data> rra = iterator.next();
					Map<String,Object> _rra = new HashMap<String,Object>();
					rra.report(_rra);
					_rras.add(_rra);
				}
				
				json.put("rra", _rras);
			}
		}
	}

	@Override
	public void configure(Element _e, Properties _properties) {
		XmlElementProperties p = new XmlElementProperties(_e,_properties);		
		configure(p);
	}

	@Override
	public void configure(Properties p) {
		String[] _rras = PropertiesConstants.getString(p,"rrm.rras","minute,halfhour,hour").split(",");
		for (int i = 0 ;i < _rras.length ; i ++){
			String id = _rras[i];
			if (id != null && id.length() > 0){
				RRArchive<data> newRRA = new RRArchive<data>(id);
				newRRA.configure(p);
				rras.put(id, newRRA);
			}
		}
		
		metricsOutput = PropertiesConstants.getBoolean(p,getId() + ".output",metricsOutput);
		metricsHandler = MetricsHandlerFactory.getClientInstance();
	}

	@Override
	public void metricsIncr(Fragment fragment) {
		if (metricsHandler != null){
			metricsHandler.handle(fragment,System.currentTimeMillis());
		}
	}
}
