package com.logicbus.redis.kvalue;

import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
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
 */
public class RedisSchema implements Schema {
	/**
	 * logger of log4j
	 */
	protected final static Logger logger = LogManager.getLogger(RedisSchema.class);
	
	protected String id;
	
	protected RedisContext source = null;
	protected RedisContext globalSource = null;
	
	protected Hashtable<String,RedisTable> tables = new Hashtable<String,RedisTable>();
	
	
	public void close() throws Exception {
		IOTools.close(source);
	}
	
	public RedisContext getRedisSource(){return source != null ? source : globalSource;}

	
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

	
	public void report(Element xml) {

	}

	
	public void report(Map<String, Object> json) {

	}

	
	public String getId() {
		return id;
	}

	
	public void create(Properties props) throws BaseException {
		id = PropertiesConstants.getString(props, "id", "", true);
	}

	
	public Table getTable(String name) {
		return tables.get(name);
	}

}
