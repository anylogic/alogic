package com.logicbus.kvalue.context;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.util.BaseException;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.logicbus.kvalue.core.Schema;

public class SchemaHolder implements XMLConfigurable,AutoCloseable,Reportable{
	
	/**
	 * a logger of log4j
	 */
	protected final Logger logger = LogManager.getLogger(SchemaHolder.class);
	
	/**
	 * contexts
	 */
	protected Hashtable<String,Schema> schemas = new Hashtable<String,Schema>();
	
	
	public void close() throws Exception {
		Collection<Schema> _schemas = schemas.values();
		
		for (Schema c:_schemas){
			IOTools.close(c);
		}
		
		schemas.clear();
	}

	
	public void configure(Element _e, Properties _properties)
			throws BaseException {
		XmlElementProperties p = new XmlElementProperties(_e,_properties);
		
		NodeList rcps = XmlTools.getNodeListByPath(_e, "schema");
		
		for (int i = 0 ; i < rcps.getLength() ; i ++){
			Node n = rcps.item(i);
			
			if (n.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			
			Element e = (Element)n;
			try {
				Schema instance = factory.newInstance(e, p);
				String id = instance.getId();
				if (instance != null && id != null && id.length() > 0){
					schemas.put(id, instance);
				}else{
					logger.warn("Not a valid schema:" + id + ",ignored.");
				}
			}catch (Exception ex){
				logger.warn("Can not create Schema instance,check your xml configurations.");
			}
			
		}
	}
	
	public Schema getSchema(String id){
		return schemas.get(id);
	}

	public static final class TheFactory extends Factory<Schema>{
		
	}
	
	protected static final TheFactory factory = new TheFactory();

	
	public void report(Element xml) {

	}

	
	public void report(Map<String, Object> json) {

	}
}
