package com.alogic.remote.cluster;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alogic.remote.backend.AppBackends;
import com.alogic.remote.backend.Backend;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * 静态配置的集群
 * 
 * @author yyduan
 * @since 1.6.8.12
 */
public class StaticCluster extends Cluster.Abstract{
	/**
	 * 模拟的后端节点
	 */
	protected AppBackends appBackends = new AppBackends("all");

	@Override
	public AppBackends load(String appId) {
		return appBackends;
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
			
			try {
				Backend.Default backend = new Backend.Default();
				backend.configure(e, props);
				appBackends.addBackend(backend);
			}catch (Exception ex){
				LOG.error("Can not create instance,Ignored.",ex);
			}
		}		
	}
}
