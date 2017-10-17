package com.logicbus.redis.context;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import com.alogic.pool.impl.Queued;
import com.anysoft.util.BaseException;
import com.anysoft.util.Confirmer;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.code.Coder;
import com.anysoft.util.code.CoderFactory;
import com.logicbus.redis.client.Client;
import com.logicbus.redis.client.Protocol;
import com.logicbus.redis.util.RedisContextException;
import com.logicbus.redis.util.RedisException;

/**
 * 连接池
 * 
 * @author duanyy
 * @version 1.6.6.9 [20161209 duanyy] <br>
 * - 从新的框架下继承 <br>
 * 
 * @version 1.6.10.4 [20171017 duanyy] <br>
 * - 优化密码取值功能 <br>
 */
public class RedisPool extends Queued{

	/**
	 * host
	 */
	protected String host;

	/**
	 * port
	 */
	protected int port = Protocol.DEFAULT_PORT;
	
	/**
	 * id
	 */
	protected String id;
	
	/**
	 * password
	 */
	protected String password = "";
	
	/**
	 * 缺省用户名(redis不需要用户名验证，在这里只是作为密码加密的key)
	 */
	protected String username = "alogic";
	
	/**
	 * 缺省的db
	 */
	protected int db = 0;
	
	/**
	 * time out to get client
	 */
	protected int timeout = 30000;
	
	/**
	 * 数据确认类的类名
	 */
	protected String callback = "";
	
	/**
	 * 数据确认ID
	 */
	protected String callbackId = "";
	
	/**
	 * 数据确认者
	 */
	protected Confirmer confirmer = null;
	
	/**
	 * 加密的coder
	 */
	protected String coder = "Default";	
	
	public String getHost(){return host;}
	
	public int getPort(){return port;}
	
	protected int getDB(){return db;}
	
	public String getId(){return id;}
	
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
	
	public void recycle(Client client,boolean error){
		returnObject(client,error);
	}
	
	public void recycle(Client client){
		returnObject(client,false);
	}
	
	@SuppressWarnings("unchecked")
	protected <pooled> pooled createObject(){
		Client instance = null;
		try {
			ClassLoader cl = Settings.getClassLoader();
			if (confirmer == null){
				if (StringUtils.isNotEmpty(callbackId) 
						&& StringUtils.isNotEmpty(callback)){
					try {
						confirmer = (Confirmer)cl.loadClass(callback).newInstance();
						confirmer.prepare(callbackId);
					}catch (Exception ex){
						
					}
				}
			}	
			
			if (confirmer == null){
				String pwd = password;
				if (StringUtils.isNotEmpty(coder)){
					//通过coder进行密码解密
					try {
						Coder _coder = CoderFactory.newCoder(coder);
						pwd = _coder.decode(password, username);
					}catch (Exception ex){
						logger.error("Can not find coder:" + coder);
					}
				}
				instance =  new Client(host,port,pwd,db);
				instance.register(this);
			}else{
				String pwd = confirmer.confirm("password", password);
				instance =  new Client(host,port,pwd,db);
				instance.register(this);
			}
		}catch (Exception ex){
			logger.error(String.format("Can not create a connection to redis %d:%d",host,port),ex);
		}
		return (pooled)instance;
	}

	public void configure(Element _e, Properties _properties)
			throws BaseException {
		XmlElementProperties p = new XmlElementProperties(_e,_properties);
		
		id = PropertiesConstants.getString(p, "id", "",true);		
		host = PropertiesConstants.getString(p,"host", "",true);		
		port = PropertiesConstants.getInt(p, "port", port,true);
		password = PropertiesConstants.getString(p,"password","",true);
		username = PropertiesConstants.getString(p,"username",username,true);
		coder = PropertiesConstants.getString(p,"coder",coder,true);
		callback = PropertiesConstants.getString(p,"callback",callback);
		callbackId = PropertiesConstants.getString(p,"callbackId",callbackId);
		db = PropertiesConstants.getInt(p, "defaultDB", db,true);
		
		timeout = PropertiesConstants.getInt(p,"timeout", timeout);
		configure(p);
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
