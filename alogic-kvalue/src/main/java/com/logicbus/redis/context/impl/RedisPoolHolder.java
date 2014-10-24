package com.logicbus.redis.context.impl;

import java.util.Collection;
import java.util.Hashtable;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.logicbus.redis.context.RedisPool;

public class RedisPoolHolder implements XMLConfigurable,AutoCloseable {

	protected Hashtable<String,RedisPool> pools = new Hashtable<String,RedisPool>();
	
	protected final static Logger logger = LogManager.getLogger(Inner.class);
	
	
	public void configure(Element _e, Properties _properties)
			throws BaseException {
		XmlElementProperties p = new XmlElementProperties(_e,_properties);
		
		NodeList rcps = XmlTools.getNodeListByPath(_e, "rcp");
		
		for (int i = 0 ; i < rcps.getLength() ; i ++){
			Node n = rcps.item(i);
			
			if (n.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			
			Element e = (Element)n;
			
			try {
				RedisPool newPool = new RedisPool();
				newPool.configure(e, p);
				
				String id = newPool.getId();
				if (newPool != null && id != null && id.length() > 0){
					pools.put(id, newPool);
				}
			}catch (Exception ex){
				logger.warn("Can not create RedisPool instance,check your xml configurations.");
			}
			
		}
	}

	
	public void close() throws Exception {
		Collection<RedisPool> values = pools.values();
		
		for (RedisPool p:values){
			p.close();
		}
		pools.clear();
	}
	
	public RedisPool getPool(String id) {
		return pools.get(id);
	}	
	
}
