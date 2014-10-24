package com.logicbus.kvalue.common;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.util.BaseException;
import com.anysoft.util.Factory;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * 分区器的虚基类 
 * 
 * @author duanyy
 *
 */
abstract public class AbstractPartitioner implements Partitioner {
	/**
	 * a logger of log4j
	 */
	protected final static Logger logger = LogManager.getLogger(Partitioner.class);
	
	/**
	 * 缺省的分区
	 */
	protected Partition defaultPartition = null;

	/**
	 * 是否从Key中提取出domain(用于改变分区规则)
	 */
	protected boolean domainInKey = true;
	
	/**
	 * 分区信息
	 */
	protected Hashtable<String,Partition> partitions = new Hashtable<String,Partition>();
	
	abstract protected String getPartitionCase(String key);
	
	abstract protected void onConfigure(Element _e,Properties _p);
	
	
	public void configure(Element _e, Properties _properties)
			throws BaseException {
		XmlElementProperties p = new XmlElementProperties(_e,_properties);
				
		TheFactory factory = new TheFactory();
		{
			//default partition
			Element dftElement = XmlTools.getFirstElementByPath(_e, "partition");
			if (dftElement != null){
				defaultPartition = factory.newInstance(
						dftElement, 
						p,
						"module",
						DefaultPartition.class.getName()
						);
			}
		}
		{
			//partitions
			NodeList nodeList= XmlTools.getNodeListByPath(_e, "partitions/partition");
			
			for (int i = 0 ;i < nodeList.getLength() ; i ++){
				Node n = nodeList.item(i);
				if (n.getNodeType() != Node.ELEMENT_NODE){
					continue;
				}
				
				Element ePartition = (Element)n;
				String _case = ePartition.getAttribute("case");
				if (_case == null || _case.length() <= 0){
					continue;
				}
				
				try {
					Partition instance = factory.newInstance(
						ePartition, 
						p,
						"module",
						DefaultPartition.class.getName()
						);
				
					partitions.put(_case, instance);
				}catch (Exception ex){
					logger.warn("Can not create Partition instance",ex);
					continue;
				}
			}
		}
		
		onConfigure(_e,p);
	}

	
	public void close() throws Exception {
		partitions.clear();
	}

	
	public void report(Element xml) {
		if (xml == null) return;
		Document doc = xml.getOwnerDocument();
		if (defaultPartition != null){
			Element e = doc.createElement("partition");
			defaultPartition.report(e);
			xml.appendChild(e);
		}
		
		if (partitions.size() > 0){
			Element es = doc.createElement("partitions");
			
			Iterator<Entry<String,Partition>> iterator = partitions.entrySet().iterator();
			
			while (iterator.hasNext()){
				Element e = doc.createElement("partition");
				
				Entry<String,Partition> entry = iterator.next();
				e.setAttribute("case", entry.getKey());
				entry.getValue().report(e);
				es.appendChild(e);
			}
			xml.appendChild(es);
		}
		
	}

	
	public void report(Map<String, Object> json) {
		if (json == null) return ;
		
	}

	
	public Partition getPartition(String key) {
		String caseId = getPartitionCase(splitKey(key));
		Partition found = partitions.get(caseId);
		if (found == null){
			found = defaultPartition;
		}
		return found;
	}

	protected String splitKey(final String key){
		if (domainInKey){
			int found = key.indexOf(':');
			if (found > 0){
				return key.substring(0,found);
			}
		}
		return key;
	}
	
	public static class TheFactory extends Factory<Partition>{
		
	}
}
