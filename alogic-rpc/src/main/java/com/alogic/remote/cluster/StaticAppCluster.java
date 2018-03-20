package com.alogic.remote.cluster;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alogic.remote.backend.AppBackends;
import com.alogic.remote.backend.Backend;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * 基于多app的静态集群
 * 
 * @author yyduan
 *
 */
public class StaticAppCluster extends Cluster.Abstract{
	
	/**
	 * 后端集群
	 */
	protected Map<String,AppBackends> appBackends = new ConcurrentHashMap<String,AppBackends>();

	@Override
	public AppBackends load(String appId) {
		return appBackends.get(appId);
	}

	@Override
	public void configure(Element root,Properties p){
		super.configure(root, p);
		
		Properties props = new XmlElementProperties(root,p);
		
		NodeList nodeList = XmlTools.getNodeListByPath(root, "backend");
		for (int i = 0 ; i < nodeList.getLength(); i ++){
			Node n = nodeList.item(i);				
			if (Node.ELEMENT_NODE != n.getNodeType()){
				continue;
			}
			
			Element e = (Element)n;
			String app = e.getAttribute("app");
			if (StringUtils.isNotEmpty(app)){
				AppBackends backends = load(app);
				if (backends == null){
					backends = new AppBackends(app);
					appBackends.put(app, backends);
				}
				try {
					Backend.Default backend = new Backend.Default();
					backend.configure(e, props);
					backends.addBackend(backend);
				}catch (Exception ex){
					LOG.error("Can not create instance,Ignored.",ex);
				}
			}else{
				LOG.warn("Can not find app attr:" + XmlTools.node2String(e));
			}
		}		
	}
}
