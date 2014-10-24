package com.logicbus.dbcp.jndi;

import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.w3c.dom.Element;

import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.Watcher;
import com.logicbus.dbcp.core.ConnectionPool;

public class JndiContext implements com.anysoft.context.Context<ConnectionPool>{

	
	public void configure(Element _e, Properties _properties)
			throws BaseException {
	}

	
	public void close() throws Exception {
	}

	
	public ConnectionPool get(String id) {
		try {
			Context jndiCntx = (Context) (new InitialContext())
					.lookup("java:comp/env");
			DataSource ds = (DataSource) jndiCntx.lookup(id);
			if (ds != null)
				return new JndiConnectionPool(id, ds);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return null;
	}

	
	public void addWatcher(Watcher<ConnectionPool> watcher) {

	}

	
	public void removeWatcher(Watcher<ConnectionPool> watcher) {

	}

	
	public void report(Element xml){
		if (xml != null){
			xml.setAttribute("module", getClass().getName());
		}
	}
	
	
	public void report(Map<String,Object> json){
		if (json != null){
			json.put("module", getClass().getName());
		}
	}
}
