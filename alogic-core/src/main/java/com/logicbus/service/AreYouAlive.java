package com.logicbus.service;

import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.models.servant.ServiceDescription;

/**
 * Are you alive?
 * 
 * <br>
 * 一个小小的测试服务，定义在/com/logicbus/service/servant.xml中，具体配置如下:<br>
 * 
 * {@code 
 * <service id="AreYouAlive" name="AreYouAlive"	note="查询服务器是否可用" visible="public" module="com.logicbus.service.AreYouAlive"/>
 * }
 * 
 * <br>
 * 如果配置在服务器中，访问地址为：<br>
 * {@code
 * http://[host]:[port]/[webcontext]/services/demo/logicbus/AreYouAlive 
 * }
 * 
 * @author duanyy
 * @version 1.4.0 [20141117 duanyy] <br>
 * - 将MessageDoc和Context进行合并整合 <br>
 * 
 * @version 1.6.3.27 [20150623 duanyy] <br>
 * - 增加XML和JSON双协议支持
 * 
 */
public class AreYouAlive extends AbstractServant {
	@Override
	protected void onDestroy() {

	}

	@Override
	protected void onCreate(ServiceDescription sd) throws ServantException {

	}
	
	protected int onXml(Context ctx) throws Exception {
		XMLMessage msg = (XMLMessage)ctx.asMessage(XMLMessage.class);
		Element root = msg.getRoot();
		Document doc = root.getOwnerDocument();
		root.appendChild(doc.createTextNode("Ok,i am alive."));
		return 0;
	}

	
	protected int onJson(Context ctx) throws Exception {
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		
		Map<String,Object> root = msg.getRoot();
		
		root.put("msg", "Ok,i am alive.");
		return 0;
	}	
}