package com.logicbus.redis.context;

import java.util.Map;

import org.w3c.dom.Element;

import com.anysoft.pool.QueuedPool;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.logicbus.redis.client.Client;
import com.logicbus.redis.client.Protocol;
import com.logicbus.redis.util.RedisConnectException;
import com.logicbus.redis.util.RedisContextException;
import com.logicbus.redis.util.RedisException;

/**
 * 连接池
 * 
 * @author duanyy
 * 
 */
public class RedisPool extends QueuedPool<Client> 
	implements XMLConfigurable,Reportable{

	/**
	 * host
	 */
	protected String host;
	
	public String getHost(){return host;};
	
	/**
	 * port
	 */
	protected int port = Protocol.DEFAULT_PORT;
	public int getPort(){return port;}
	
	protected String id;
	public String getId(){return id;}
	
	/**
	 * password
	 */
	protected String password = "";
	
	protected int db = 0;
	protected int getDB(){return db;}
	
	/**
	 * time out to get client
	 */
	protected int timeout = 30000;
	
	
	protected String getIdOfMaxQueueLength() {
		return "maxActive";
	}

	
	protected String getIdOfIdleQueueLength() {
		return "maxIdle";
	}

	public Client getClient()throws RedisException {
		return getClient(timeout);
	}
	
	public Client getClient(int timeout)throws RedisException{
		Client found = borrowObject(0, timeout);
		
		if (found == null){
			throw new RedisContextException("busy","The pool is busy , can not get a client.");
		}
		return found;
	}
	
	public void recycle(Client client){
		returnObject(client);
	}
	
	
	protected Client createObject() throws BaseException {
		Client instance =  new Client(host,port,password,db);
		instance.register(this);
		return instance;
	}

	
	public void configure(Element _e, Properties _properties)
			throws BaseException {
		XmlElementProperties p = new XmlElementProperties(_e,_properties);
		
		id = PropertiesConstants.getString(p, "id", "",true);
		
		host = PropertiesConstants.getString(p,"host", "",true);
		if (host == null || host.length() <= 0){
			throw new RedisConnectException("nohost","Can not find host value");
		}
		
		port = PropertiesConstants.getInt(p, "port", port,true);
		password = PropertiesConstants.getString(p,"password","",true);
		db = PropertiesConstants.getInt(p, "defaultDB", db,true);
		
		timeout = PropertiesConstants.getInt(p,"timeout", timeout);
		create(p);
	}

	
	public void report(Element xml) {
		if (xml != null){
			xml.setAttribute("host", host);
			xml.setAttribute("port", String.valueOf(port));
			xml.setAttribute("defaultDB", String.valueOf(db));
			
			super.report(xml);
		}
	}

	
	public void report(Map<String, Object> json) {
		if (json != null){
			json.put("host", host);
			json.put("port", port);
			json.put("defaultDB", db);
			
			super.report(json);
		}
	}

}
