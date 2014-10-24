package com.logicbus.service;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.MessageDoc;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.dbcp.context.DbcpSource;
import com.logicbus.dbcp.core.ConnectionPool;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 查询指定数据源的信息
 * 
 * <br>
 * 实现了一个内部核心服务，定义在/com/logicbus/service/servant.xml中，具体配置如下:<br>
 * 
 * {@code
 * <service 
 *     id="DataSourceQuery" 
 *     name="DataSourceQuery" 
 *     note="查询数据源信息"
 *     visible="protected"
 *     module="com.logicbus.service.DataSourceQuery"
 * />	
 * }
 * 
 * 本服务需要客户端传送参数，包括：<br>
 * - name 要查询数据源的名称,本参数属于必选项<br>
 * 
 * 如果配置在服务器中，访问地址为：<br>
 * {@code
 * http://[host]:[port]/[webcontext]/services/core/manager/DataSourceQuery?name=<名称>
 * }
 * 
 * @author duanyy
 * 
 * @since 1.2.9.1
 * 
 */
public class DataSourceQuery extends AbstractServant {

	protected void onDestroy() {
	}

	
	protected void onCreate(ServiceDescription sd) throws ServantException {
	}

	
	protected int onXml(MessageDoc msgDoc, Context ctx) throws Exception {
		XMLMessage msg = (XMLMessage) msgDoc.asMessage(XMLMessage.class);		
		
		Element root = msg.getRoot();		
		Document doc = msg.getDocument();	
		
		String id = getArgument("id","all", msgDoc, ctx);	
		if (id == null || id.length() <= 0 || id.equals("all")){
			Element source = doc.createElement("source");
			
			DbcpSource ds = DbcpSource.get();				
			ds.report(source);
			
			root.appendChild(source);
		}else{			
			Element dbcp = doc.createElement("dbcp");
			
			DbcpSource ds = DbcpSource.get();				
			ConnectionPool pool = ds.get(id);		
			if (pool != null){
				pool.report(dbcp);
			}
			root.appendChild(dbcp);
		}
		return 0;
	}

	
	protected int onJson(MessageDoc msgDoc, Context ctx) throws Exception {
		JsonMessage msg = (JsonMessage) msgDoc.asMessage(JsonMessage.class);
		Map<String,Object> root = msg.getRoot();

		String id = getArgument("id","all", msgDoc, ctx);
		
		if (id == null || id.length() <= 0 || id.equals("all")){
			Map<String,Object> source = new HashMap<String,Object>();
			
			DbcpSource ds = DbcpSource.get();				
			ds.report(source);
			
			root.put("source", source);
		}else{
			Map<String,Object> dbcp = new HashMap<String,Object>();
			
			DbcpSource ds = DbcpSource.get();				
			ConnectionPool pool = ds.get(id);		
			if (pool != null){
				pool.report(dbcp);
			}
			root.put("dbcp", dbcp);
		}
		return 0;
	}

}

