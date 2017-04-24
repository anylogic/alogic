package com.alogic.remote.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * 集群管理器实现
 * @author yyduan
 * @since 1.6.8.12
 */
public class ClusterManagerImpl implements ClusterManager {
	/**
	 * a logger of log4j
	 */
	protected static final Logger LOG = LoggerFactory.getLogger(ClusterManager.class);
	
	/**
	 * 缺省的集群类名
	 */
	protected String dftClusterClazz = null;
	
	/**
	 * 缺省集群的id
	 */
	protected String dftClusterId = "default";
	
	/**
	 * 集群列表
	 */
	protected Map<String,Cluster> clusters = new ConcurrentHashMap<String,Cluster>();
	
	@Override
	public void report(Element xml) {
		if (xml != null){	
			XmlTools.setString(xml,"module",getClass().getName());
			XmlTools.setString(xml,"dftClusterClass",dftClusterClazz);
			XmlTools.setString(xml,"dftClusterId",dftClusterId);
			
			if (!clusters.isEmpty()){
				Document doc = xml.getOwnerDocument();
				
				Cluster[] list = getClusters();
				for (Cluster c:list){
					Element cluster = doc.createElement("cluster");
					c.report(cluster);
					xml.appendChild(cluster);
				}
			}
		}
	}

	@Override
	public void report(Map<String, Object> json) {
		if (json != null){
			JsonTools.setString(json,"module",getClass().getName());
			JsonTools.setString(json,"dftClusterClass",dftClusterClazz);
			JsonTools.setString(json,"dftClusterId",dftClusterId);
			
			if (!clusters.isEmpty()){
				List<Object> clusterList = new ArrayList<Object>();
				
				Cluster[] list = getClusters();
				for (Cluster c:list){
					Map<String,Object> map = new HashMap<String,Object>();
					c.report(map);
					clusterList.add(map);
				}
				
				json.put("cluster", clusterList);
			}			
		}
	}

	@Override
	public void configure(Properties p) {
		dftClusterClazz = PropertiesConstants.getString(p,"dftClusterClass",dftClusterClazz);
		dftClusterId = PropertiesConstants.getString(p,"dftClusterId",dftClusterId);
	}

	@Override
	public void configure(Element root, Properties p) {
		Properties props = new XmlElementProperties(root,p);		
		configure(props);
		
		NodeList nodeList = XmlTools.getNodeListByPath(root, "cluster");
		Factory<Cluster> factory = new Factory<Cluster>();
		
		for (int i = 0 ;i < nodeList.getLength() ;i ++){
			Node n = nodeList.item(i);
			if (Node.ELEMENT_NODE != n.getNodeType()){
				continue;
			}
			Element e = (Element)n;
			
			try {
				Cluster instance = factory.newInstance(e, props, "module", dftClusterClazz);
				
				if (instance != null){
					String id = instance.getId();
					if (StringUtils.isNotEmpty(id)){
						clusters.put(id, instance);
					}
				}
			}catch (Exception ex){
				LOG.error("Failed to create cluster.",ex);
			}
		}
	}

	@Override
	public void close() throws Exception {
		Cluster [] list = getClusters();
		for (Cluster c:list){
			IOTools.close(c);
		}
		clusters.clear();
	}

	@Override
	public Cluster getCluster(String id) {
		return clusters.get(id);
	}

	@Override
	public Cluster[] getClusters() {
		return clusters.values().toArray(new Cluster[clusters.size()]);
	}

	@Override
	public Cluster getDefaultCluster() {
		return getCluster(dftClusterId);
	}

}
