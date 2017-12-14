package com.logicbus.redis.context;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.w3c.dom.Element;

import com.alogic.pool.impl.Queued;
import com.alogic.sda.SDAFactory;
import com.alogic.sda.SecretDataArea;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
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
 * 
 * @version 1.6.10.8 [20171122 duanyy] <br>
 * - 支持用户名密码等信息实时从SDA获取 <br>
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
	
	protected String sdaId = "";
	
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

	public Client getClient() {
		return getClient(timeout);
	}
	
	public Client getClient(int timeout){
		Client found = borrowObject(0, timeout);
		
		if (found == null){
			throw new RedisContextException("core.e1013","The pool is busy , can not get a client.");
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
			SecretDataArea sda = null;
			if (StringUtils.isNotEmpty(sdaId)){
				//从sda中装入信息
				try {
					sda = SDAFactory.getDefault().load(sdaId, true);
				}catch (Exception ex){
					logger.error("Can not find sda : " + sdaId);
					logger.error(ExceptionUtils.getStackTrace(ex));
				}
			}
			
			if (sda != null){
				String pwd = sda.getField("password", password);
				String ip = sda.getField("ip", host);
				int p = sda.getField("port", port);
				int index = sda.getField("db", db);
				instance =  new Client(ip,p,pwd,index);
				instance.register(this);
			}else{
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
			}
		}catch (Exception ex){
			logger.error(String.format("Can not create a connection to redis %d:%d",host,port),ex);
		}
		return (pooled)instance;
	}

	public void configure(Element _e, Properties _properties) {
		XmlElementProperties p = new XmlElementProperties(_e,_properties);
		
		id = PropertiesConstants.getString(p, "id", "",true);		
		host = PropertiesConstants.getString(p,"host", "",true);		
		port = PropertiesConstants.getInt(p, "port", port,true);
		password = PropertiesConstants.getString(p,"password","",true);
		username = PropertiesConstants.getString(p,"username",username,true);
		coder = PropertiesConstants.getString(p,"coder",coder,true);
		sdaId = PropertiesConstants.getString(p,"sda", sdaId);
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
