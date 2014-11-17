package com.logicbus.service;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.util.Settings;
import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.ServantFactory;
import com.logicbus.backend.ServantPool;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.models.catalog.Path;
import com.logicbus.models.servant.ServantManager;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 查询服务的详细信息
 * 
 * <br>
 * 查询服务的详细信息，包括服务的基本描述信息，以及服务的运行统计信息.<br>
 * 
 * 实现了一个内部核心服务，定义在/com/logicbus/service/servant.xml中，具体配置如下:<br>
 * 
 * {@code 
 * <service 
 * id = "ServiceDetailQuery" 
 * name="ServiceDetailQuery" 
 * note="查询指定的服务信息" 
 * visible="protected" 
 * module="com.logicbus.service.ServiceDetailQuery"/>
 * }
 * 
 * <br>
 * 本服务需要客户端传送参数，包括：<br>
 * - service 要查询的服务ID,本参数属于必选项<br>
 * 
 * 本服务属于系统核心管理服务，内置了快捷访问，在其他服务的URL中加上wsdl参数即可直接访问，例如：要查询/demo/logicbus/Helloworld服务的信息，可输入URL为：<br>
 * {@code
 * http://[host]:[port]/[webcontext]/services/demo/logicbus/Helloworld?wsdl
 * }
 * <br>
 * 如果配置在服务器中，访问地址为：<br>
 * {@code
 * http://[host]:[port]/[webcontext]/services/core/ServiceDetailQuery?service=[服务ID] 
 * }
 * 
 * @author duanyy
 *
 * @version 1.0.3 [20140410 duanyy]
 * - 改用取参数机制来提取参数
 * 
 * @version 1.2.6 [20140807 duanyy] <br>
 * - ServantPool和ServantFactory插件化 
 * 
 * @version 1.2.8.2 [20141014 duanyy]<br>
 * - 支持双协议:XML,JSON <br>
 * 
 * @version 1.4.0 [20141117 duanyy] <br>
 * - 将MessageDoc和Context进行合并整合 <br>
 */
public class ServiceDetailQuery extends AbstractServant {
	
	protected void onDestroy() {

	}

	
	protected void onCreate(ServiceDescription sd) throws ServantException {

	}

	
	protected int onXml(Context ctx) throws Exception {
		XMLMessage msg = (XMLMessage)ctx.asMessage(XMLMessage.class);	
		
		String id = getArgument("service", ctx);		
		Path path = new Path(id);
		ServantManager sm = ServantManager.get();
		ServiceDescription sd = sm.get(path);
		if (sd == null){
			throw new ServantException("user.data_not_found","Service does not exist:" + id);
		}	
		
		Element root = msg.getRoot();
		Document doc = root.getOwnerDocument();
		
		Element service = doc.createElement("service");
		
		sd.report(service);
		
		//关联服务的实时统计信息
		Settings settings = Settings.get();
		ServantFactory sf = (ServantFactory)settings.get("servantFactory");
		ServantPool pool = sf.getPool(path);
		if (pool != null) {
			pool.report(service);
		}
		root.appendChild(service);
		return 0;
	}

	
	protected int onJson(Context ctx) throws Exception {
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);	
		
		String id = getArgument("service", ctx);		
		Path path = new Path(id);
		ServantManager sm = ServantManager.get();
		ServiceDescription sd = sm.get(path);
		if (sd == null){
			throw new ServantException("user.data_not_found","Service does not exist:" + id);
		}	
		
		Map<String,Object> root = msg.getRoot();

		Map<String,Object> service = new HashMap<String,Object>();
		
		sd.report(service);
		
		//关联服务的实时统计信息
		Settings settings = Settings.get();
		ServantFactory sf = (ServantFactory)settings.get("servantFactory");
		ServantPool pool = sf.getPool(path);
		if (pool != null) {
			pool.report(service);
		}
		
		root.put("service", service);
		return 0;
	}

}
