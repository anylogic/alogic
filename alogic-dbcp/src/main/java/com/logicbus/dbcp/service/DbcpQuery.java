package com.logicbus.dbcp.service;

import java.util.HashMap;
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
 * 查询指定的连接池信息
 * 
 * @author duanyy
 * @since 1.6.4.4
 */
public class DbcpQuery extends AbstractServant {

	protected int onXml(Context ctx){
		XMLMessage msg = (XMLMessage) ctx.asMessage(XMLMessage.class);

		String id = getArgument("id",ctx);
		
		Document doc = msg.getDocument();
		Element root = msg.getRoot();
		
		DbcpSource src = DbcpSource.get();
		ConnectionPool found = src.get(id);
		if (found == null){
			throw new ServantException("clnt.e2007","Can not find the db connection pool :" + id);
		}
		
		Element elem = doc.createElement("dbcp");
		found.report(elem);
		root.appendChild(elem);

		return 0;
	}
	
	protected int onJson(Context ctx){
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		String id = getArgument("id",ctx);
		
		DbcpSource src = DbcpSource.get();
		ConnectionPool found = src.get(id);
		if (found == null){
			throw new ServantException("clnt.e2007","Can not find the db connection pool :" + id);
		}
		
		Map<String,Object> map = new HashMap<String,Object>();
		found.report(map);
		msg.getRoot().put("dbcp", map);
		
		return 0;
	}
	
	@Override
	protected void onDestroy() {
		
	}
	@Override
	protected void onCreate(ServiceDescription sd) throws ServantException {

	}

}