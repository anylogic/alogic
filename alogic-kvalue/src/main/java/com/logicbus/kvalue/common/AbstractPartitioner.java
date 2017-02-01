package com.logicbus.kvalue.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * @version 1.0.0.2 [20141108 duanyy] <br>
 * - 补充Reportable实现. <br>
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 */
abstract public class AbstractPartitioner implements Partitioner {
	/**
	 * a logger of log4j
	 */
	protected final static Logger logger = LoggerFactory.getLogger(Partitioner.class);
	
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
	
	/**
	 * 通过Key计算PartitionCase.
	 * 
	 * @param key 数据的Key
	 * @return PartitionCase,用于对Key进行分组,实际上是一个分区的id
	 */
	abstract protected String getPartitionCase(String key);
	
	/**
	 * 处理Configure事件，在{@link #configure(Element, Properties)}中触发。提供给子类，让子类能够进行配置。
	 * @param _e XML配置节点
	 * @param _p 变量集
	 */
	abstract protected void onConfigure(Element _e,Properties _p);
	
	/**
	 * 装入配置
	 * 
	 * <br>
	 * Partitioner的配置信息全部定义在XML节点中。
	 * 
	 * @param _e XML节点
	 * @param _properties 环境变量集
	 * 
	 */
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

	/**
	 * report到XML
	 * 
	 * @param xml XML节点
	 */
	public void report(Element xml) {
		if (xml == null) return;
		Document doc = xml.getOwnerDocument();
		if (defaultPartition != null){
			Element e = doc.createElement("partition");
			defaultPartition.report(e);
			xml.appendChild(e);
		}
		
		if (partitions != null && partitions.size() > 0){
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

	/**
	 * report到JSON
	 * 
	 * @param json JSON节点
	 */
	public void report(Map<String, Object> json) {
		if (json == null) return ;
		if (defaultPartition != null){
			Map<String,Object> _partition = new HashMap<String,Object>();
			defaultPartition.report(_partition);
			json.put("partition", _partition);
		}
		if (partitions != null && partitions.size() > 0){
			List<Object> es = new ArrayList<Object>(partitions.size());
			
			Iterator<Entry<String,Partition>> iterator = partitions.entrySet().iterator();
			
			while (iterator.hasNext()){
				Map<String,Object> e = new HashMap<String,Object>();
				Entry<String,Partition> entry = iterator.next();
				e.put("case", entry.getKey());
				entry.getValue().report(e);
				es.add(e);
			}
			json.put("partitions", es);
		}
	}

	/**
	 * 根据指定的Key获取对应的分区
	 */
	public Partition getPartition(String key) {
		String caseId = getPartitionCase(splitKey(key));
		Partition found = partitions.get(caseId);
		if (found == null){
			found = defaultPartition;
		}
		return found;
	}

	/**
	 * 拆分Key。在某些情况下，如果希望某些key分配到同一个分区，可以将Key拆分到相同的Key上。
	 * 
	 * @param key key
	 * @return 拆分之后的key
	 */
	protected String splitKey(final String key){
		if (domainInKey){
			int found = key.indexOf(':');
			if (found > 0){
				return key.substring(0,found);
			}
		}
		return key;
	}
	
	/**
	 * Partition工程类
	 * 
	 * <br>
	 * 用于创建Partition实例。
	 * 
	 * @author duanyy
	 *
	 */
	public static class TheFactory extends Factory<Partition>{
		
	}
}
