package com.logicbus.dbcp.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.cache.Cacheable;
import com.anysoft.util.Confirmer;
import com.anysoft.util.JsonTools;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * 连接模型
 * @author duanyy
 * @since 1.2.9
 * 
 * @version 1.3.0.2 [20141106 duanyy] <br>
 * - 从XML的id属性中获取name <br>
 * @version 1.6.3.2 [20150213 duanyy] <br>
 * - 接口{@link com.anysoft.cache.Cacheable Cacheable}增加了{@link com.anysoft.cache.Cacheable#expire() Cacheable.expire}方法 <br>
 */
public class ConnectionModel implements Cacheable{
	/**
	 * a logger of log4j
	 */
	protected static final Logger logger 
		= LogManager.getLogger(ConnectionModel.class);	
	/**
	 * 名称
	 */
	protected String name;
	
	/**
	 * 获取名称
	 * @return
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * jdbc驱动
	 */
	protected String driver;
	
	/**
	 * 获取JDBC驱动类
	 * @return
	 */
	public String getDriver(){
		return driver;
	}
	
	/**
	 * URL
	 */
	protected String url;
	
	/**
	 * 获取数据库连接所用的URI
	 * @return
	 */
	public String getURI(){
		return url;
	}
	
	/**
	 * username
	 */
	protected String username;
	
	/**
	 * 获取用户名
	 * @return
	 */
	public String getUserName(){
		return username;
	}
	
	/**
	 * password
	 */
	protected String password;
	
	/**
	 * 获取密码
	 * @return
	 */
	public String getPassword(){
		return password;
	}
	
	/**
	 * 最大连接数
	 */
	protected int maxActive = 3;
	
	/**
	 * 获取maxActive
	 * @return
	 */
	public int getMaxActive(){return maxActive;}
	
	/**
	 * 空闲连接数
	 */
	protected int maxIdle = 1;
	
	/**
	 * 获取空闲连接数
	 * @return
	 */
	public int getMaxIdle(){return maxIdle;}
	
	/**
	 * 最大等待时间
	 */
	protected int maxWait = 5000;
	
	/**
	 * 获取最大等待时间
	 * @return
	 */
	public int getMaxWait(){return maxWait;}
	
	/**
	 * 数据确认ID
	 */
	protected String callbackId = "";
	
	/**
	 * 数据确认类的类名
	 */
	protected String callback = "";
	
	/**
	 * 数据确认者
	 */
	protected Confirmer confirmer = null;

	protected List<ReadOnlySource> readonlys = null; 
	
	public List<ReadOnlySource> getReadOnlySources(){return readonlys;}
	
	public void report(Element e){
		e.setAttribute("id", name);
		e.setAttribute("driver", driver);
		e.setAttribute("url", url);
		e.setAttribute("username", username);
		e.setAttribute("maxActive", String.valueOf(maxActive));
		e.setAttribute("maxIdle", String.valueOf(maxIdle));
		e.setAttribute("maxWait", String.valueOf(maxWait));
		e.setAttribute("callbackId", callbackId);
		e.setAttribute("callback", callback);
		
		//readonlys
		if (readonlys != null && readonlys.size() > 0){
			Document doc = e.getOwnerDocument();
			
			Element _readonlys = doc.createElement("ross");
			
			for (ReadOnlySource s:readonlys){
				Element _s = doc.createElement("ros");
				s.report(_s);
				_readonlys.appendChild(_s);
			}
			
			e.appendChild(_readonlys);
		}
	}
	
	public void report(Map<String,Object> json){
		JsonTools.setString(json, "id",name);
		JsonTools.setString(json, "driver",driver);
		JsonTools.setString(json, "url",url);
		JsonTools.setString(json, "username", username);
		JsonTools.setInt(json, "maxActive", maxActive);
		JsonTools.setInt(json, "maxIdle", maxIdle);
		JsonTools.setInt(json, "maxWait", maxWait);
		JsonTools.setString(json, "callbackId", callbackId);
		JsonTools.setString(json, "callback", callback);
		
		//readonlys
		if (readonlys != null && readonlys.size() > 0){
			List<Object> _readonlys = new ArrayList<Object>(readonlys.size());
			
			for (ReadOnlySource s:readonlys){
				Map<String,Object> _s = new HashMap<String,Object>();
				s.report(_s);
				_readonlys.add(_s);
			}
			
			json.put("ross", _readonlys);
		}
	}	
	
	
	public void toXML(Element e) {
		report(e);
		e.setAttribute("password", password);
	}

	
	public void fromXML(Element e) {
		XmlElementProperties props = new XmlElementProperties(e,null);
		
		name = PropertiesConstants.getString(props,"id", "");
		driver = PropertiesConstants.getString(props, "driver", "");
		url = PropertiesConstants.getString(props, "url", "");
		username = PropertiesConstants.getString(props, "username","");
		password = PropertiesConstants.getString(props, "password","");
		maxActive = PropertiesConstants.getInt(props, "maxActive",3);
		maxIdle = PropertiesConstants.getInt(props, "maxIdle",1);
		maxWait = PropertiesConstants.getInt(props, "maxWait",5000);
		callbackId = PropertiesConstants.getString(props, "callbackId", "");
		callback = PropertiesConstants.getString(props, "callback", "");
		
		NodeList _readonlys = XmlTools.getNodeListByPath(e, "ross/ros");
		if (_readonlys != null && _readonlys.getLength() > 0){
			readonlys = new ArrayList<ReadOnlySource>(_readonlys.getLength());
			
			for (int i = 0; i < _readonlys.getLength() ; i ++){
				Node n = _readonlys.item(i);
				if (n.getNodeType() != Node.ELEMENT_NODE){
					continue;
				}
				Element _e = (Element)n;
				ReadOnlySource ros = new ReadOnlySource(props);
				ros.fromXML(_e);
				readonlys.add(ros);
			}
		}
	}

	
	public void toJson(Map<String,Object> json) {
		report(json);
		JsonTools.setString(json, "password",password);
	}

	
	public void fromJson(Map<String,Object> json) {
		name = JsonTools.getString(json, "id", "");
		driver = JsonTools.getString(json, "driver", "");
		url = JsonTools.getString(json, "url", "");
		username = JsonTools.getString(json, "username", "");
		password = JsonTools.getString(json, "password", "");
		maxActive = JsonTools.getInt(json, "maxActive",3);
		maxIdle = JsonTools.getInt(json, "maxIdle",1);
		maxWait = JsonTools.getInt(json, "maxWait",5000);
		callbackId = JsonTools.getString(json, "callbackId", callbackId);
		callback = JsonTools.getString(json, "callback", callback);
		
		Object _readonlys = json.get("ross");
		if (_readonlys != null && _readonlys instanceof List){
			@SuppressWarnings("unchecked")
			List<Object> _ross = (List<Object>)_readonlys;
			
			for (Object _o:_ross){
				if (_o instanceof Map){
					@SuppressWarnings("unchecked")
					Map<String,Object> _ros = (Map<String,Object>)_o;
					ReadOnlySource ros = new ReadOnlySource(Settings.get());
					ros.fromJson(_ros);
					readonlys.add(ros);
				}
			}
		}
	}

	
	public String getId() {
		return name;
	}

	
	public boolean isExpired() {
		return false;
	}

	public void expire(){
		
	}	
	
	/**
	 * 按照当前的连接属性创建数据库连接
	 * @return
	 */
	public Connection newConnection(){
		Connection conn = null;
		try {
			ClassLoader cl = Settings.getClassLoader();
			if (confirmer == null){
				if (callbackId != null && callbackId.length() > 0 
						&& callback != null && callback.length() > 0){
					try {
						confirmer = (Confirmer)cl.loadClass(callback).newInstance();
						confirmer.prepare(callbackId);
					}catch (Exception ex){
						
					}
				}
			}			
			if (confirmer == null){
				Class.forName(driver, true, cl);
				conn = DriverManager.getConnection(url, username, password);
			}else{
				String _driver = confirmer.confirm("driver", driver);
				String _url = confirmer.confirm("url", url);
				String _username = confirmer.confirm("username", username);
				String _password = confirmer.confirm("password", password);
				
				Class.forName(_driver,true,cl);
				conn = DriverManager.getConnection(_url, _username,_password);
			}
		}catch (Exception ex){
			logger.error("Can not create a connection to " + url,ex);
		}		
		return conn;
	}	
}
