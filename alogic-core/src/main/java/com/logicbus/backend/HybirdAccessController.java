package com.logicbus.backend;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alogic.metrics.stream.MetricsCollector;
import com.anysoft.util.Factory;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.logicbus.models.catalog.Path;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 混合模式的访问控制器
 * 
 * @author duanyy
 *
 */
public class HybirdAccessController implements AccessController {
	
	/**
	 * a logger of slf4j
	 */
	protected static final Logger LOG = LoggerFactory.getLogger(AccessController.class);
	
	/**
	 * 访问控制器分组
	 */
	protected Map<String,AccessController> groups = new HashMap<String,AccessController>();
	
	/**
	 * 缺省分组
	 */
	protected AccessController dftGroup = null;
	
	@Override
	public void report(Element xml) {
		if (dftGroup != null){
			dftGroup.report(xml);
		}
	}

	@Override
	public void report(Map<String, Object> json) {
		if (dftGroup != null){
			dftGroup.report(json);
		}
	}

	@Override
	public void report(MetricsCollector collector) {
		Iterator<AccessController> iter = groups.values().iterator();
		
		while (iter.hasNext()){
			AccessController ac = iter.next();
			ac.report(collector);
		}
	}

	@Override
	public void configure(Element e, Properties p) {
		Properties props = new XmlElementProperties(e,p);
		
		NodeList nodeList = XmlTools.getNodeListByPath(e, "group");
		
		Factory<AccessController> f = new Factory<AccessController>();
		
		for (int i = 0 ;i < nodeList.getLength() ; i ++){
			Node n = nodeList.item(i);
			if (Node.ELEMENT_NODE != n.getNodeType()){
				continue;
			}
			
			Element elem = (Element)n;
			
			String id = elem.getAttribute("id");
			if (StringUtils.isNotEmpty(id)){
				try {
					AccessController ac = f.newInstance(elem, props, "module");
					groups.put(id, ac);
					
					boolean asDefault = XmlTools.getBoolean(elem, "default", false);
					if (asDefault){
						dftGroup = ac;
					}
					
				}catch (Exception ex){
					LOG.error("Can not create access controller with " + XmlTools.node2String(elem));
					LOG.error(ExceptionUtils.getStackTrace(ex));
				}
			}
		}
		configure(props);
	}

	@Override
	public void configure(Properties p) {
		if (dftGroup == null){
			dftGroup = groups.get(PropertiesConstants.getString(p, "dft", "default"));
		}
	}

	@Override
	public void reload(String id) {
		if (dftGroup != null){
			dftGroup.reload(id);
		}
	}

	@Override
	public String createSessionId(Path serviceId, ServiceDescription servant,
			Context ctx) {
		AccessController ac = getGroup(servant.getAcGroup());
		return ((ac == null)?dftGroup:ac).createSessionId(serviceId, servant, ctx);
	}

	@Override
	public int accessStart(String sessionId, Path serviceId,
			ServiceDescription servant, Context ctx) {
		AccessController ac = getGroup(servant.getAcGroup());
		return ((ac == null)?dftGroup:ac).accessStart(sessionId, serviceId, servant, ctx);
	}

	@Override
	public int accessEnd(String sessionId, Path serviceId,
			ServiceDescription servant, Context ctx) {
		AccessController ac = getGroup(servant.getAcGroup());
		return ((ac == null)?dftGroup:ac).accessEnd(sessionId, serviceId, servant, ctx);
	}

	@Override
	public String[] getGroupList() {
		return groups.keySet().toArray(new String[groups.size()]);
	}

	@Override
	public AccessController getGroup(String id) {
		return groups.get(id);
	}

}
