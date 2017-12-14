package com.logicbus.dbcp.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.dbcp.context.DbcpSource;
import com.logicbus.dbcp.core.ConnectionPool;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 查看当前正在使用的数据源列表
 * 
 * @author duanyy
 * @since 1.6.4.4
 */
public class DbcpList extends AbstractServant {

	protected int onXml(Context ctx){
		XMLMessage msg = (XMLMessage) ctx.asMessage(XMLMessage.class);

		Document doc = msg.getDocument();
		Element root = msg.getRoot();
		
		DbcpSource src = DbcpSource.get();
		
		Collection<ConnectionPool> pools = src.current();
		for (ConnectionPool pool:pools){
			Element elem = doc.createElement("pool");
			pool.report(elem);
			root.appendChild(elem);
		}
		
		return 0;
	}
	protected int onJson(Context ctx) {
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		
		List<Object> list = new ArrayList<Object>();
		
		DbcpSource src = DbcpSource.get();
		
		Collection<ConnectionPool> pools = src.current();
		for (ConnectionPool pool:pools){
			Map<String,Object> map = new HashMap<String,Object>();
			pool.report(map);
			list.add(map);
		}
		
		msg.getRoot().put("pool", list);
		
		return 0;
	}
	
	@Override
	protected void onDestroy() {
		
	}
	@Override
	protected void onCreate(ServiceDescription sd) throws ServantException {

	}

}
