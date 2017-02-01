package com.logicbus.redis.kvalue;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.context.Holder.TheFactory;
import com.anysoft.util.BaseException;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.logicbus.kvalue.core.Schema;
import com.logicbus.kvalue.core.Table;
import com.logicbus.redis.context.RedisContext;
import com.logicbus.redis.context.RedisSource;

/**
 * 基于Redis的KVDB Schema
 * 
 * @author duanyy
 * 
 * @version 1.0.0.1 [20141106 duanyy] <br>
 * - 将Context实现改为通用的配置环境实现. <br>
 * 
 * @version 1.0.0.2 [20141108 duanyy] <br>
 * - 补充Reportable实现. <br>
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 * 
 */
public class RedisSchema implements Schema {
	/**
	 * logger of log4j
	 */
	protected final static Logger logger = LoggerFactory.getLogger(RedisSchema.class);
	
	/**
	 * the id of the schema
	 */
	protected String id;
			
	/**
	 * 内置的RedisContext
	 */
	protected RedisContext source = null;
	
	/**
	 * 全局的RedisContext
	 */
	protected RedisContext globalSource = null;
	
	/**
	 * Schema所包含的table列表
	 */
	protected Hashtable<String,RedisTable> tables = new Hashtable<String,RedisTable>();
	
	/**
	 * 关闭Schema
	 * 
	 * <br>
	 * 主要关闭自身内置的RedisContext
	 */
	public void close() throws Exception {
		IOTools.close(source);
	}
	
	/**
	 * 获取所拥有的RedisContext
	 * 
	 * @return 如果没有内置的Context,则返回全局的Redis Source
	 */
	public RedisContext getRedisSource(){return source != null ? source : globalSource;}

	/**
	 * 装入配置
	 * 
	 * <br>
	 * Schema的配置信息全部定义在XML节点中。
	 * 
	 * @param _e XML节点
	 * @param _properties 环境变量集
	 * 
	 */
	public void configure(Element _e, Properties _properties)
			throws BaseException {
		XmlElementProperties p = new XmlElementProperties(_e,_properties);
		
		Element sourceElement = XmlTools.getFirstElementByTagName(_e, "redis.source");
		if (sourceElement != null){
			try {
				TheFactory<RedisContext> factory = new TheFactory<RedisContext>();
				source = factory.newInstance(sourceElement, p);
			}catch (Exception ex){
				logger.error("Can not create RedisSource instance,check your xml");
			}
		}
		if (source == null){
			//如果无法使用内置的RedisSource,使用全局定义的RedisSource
			globalSource = RedisSource.get();
		}
		
		// tables
		NodeList tablesNodeList = XmlTools.getNodeListByPath(_e, "redis.tables/table");
		for (int i = 0 ; i < tablesNodeList.getLength() ; i ++){
			Node n = tablesNodeList.item(i);
			if (n.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			Element tableElem = (Element)n;
			try {
				RedisTable table = new RedisTable(getRedisSource());
				table.configure(tableElem, p);
				
				String tableName = table.getName();
				if (tableName == null || tableName.length() <= 0){
					continue;
				}
				
				tables.put(tableName, table);
			}catch (Exception ex){
				logger.warn("Can not create redis table.check your xml.",ex);
			}
		}
		create(p);
	}

	/**
	 * 报告到XML
	 * 
	 * @param xml XML节点
	 */
	public void report(Element xml) {
		if (xml != null){
			xml.setAttribute("id", id);
			xml.setAttribute("module",getClass().getName());
			
			Document doc = xml.getOwnerDocument();
			if (source != null){
				Element _redisSource = doc.createElement("redis.source");				
				source.report(_redisSource);
				xml.appendChild(_redisSource);
			}
			
			if (tables != null && tables.size() > 0){
				Element _redisTables = doc.createElement("redis.tables");
				
				Enumeration<RedisTable> iterator = tables.elements();
				
				while (iterator.hasMoreElements()){
					RedisTable table = iterator.nextElement();
					Element _table = doc.createElement("table");
					table.report(_table);
					_redisTables.appendChild(_table);
				}
				
				xml.appendChild(_redisTables);
			}
		}
	}

	/**
	 * 报告到JSON
	 * 
	 * @param json JSON节点
	 */
	public void report(Map<String, Object> json) {
		if (json != null){
			json.put("id", id);
			json.put("module",getClass().getName());
			
			if (source != null){
				Map<String,Object> _redisSource = new HashMap<String,Object>();
				source.report(_redisSource);
				json.put("redis.source", _redisSource);
			}
			
			if (tables != null && tables.size() > 0){
				List<Object> _redisTables = new ArrayList<Object>();
				
				Enumeration<RedisTable> iterator = tables.elements();
				while (iterator.hasMoreElements()){
					RedisTable table = iterator.nextElement();
					
					Map<String,Object> _table = new HashMap<String,Object>();
					table.report(_table);
					_redisTables.add(_table);
				}
				json.put("redis.tables", _redisTables);
			}
		}
	}

	/**
	 * to get id
	 */
	public String getId() {
		return id;
	}

	/**
	 * 通过变量集创建实例
	 */
	public void create(Properties props) throws BaseException {
		id = PropertiesConstants.getString(props, "id", "", true);
	}

	/**
	 * 获取指定的Table
	 * 
	 * @param name table名
	 * @return Table实例 ，如果没有table没有定义，返回为空
	 */
	public Table getTable(String name) {
		return tables.get(name);
	}

}
