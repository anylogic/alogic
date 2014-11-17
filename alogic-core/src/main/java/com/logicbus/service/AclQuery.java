package com.logicbus.service;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.util.Settings;
import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.AccessController;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.MessageDoc;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.models.servant.ServiceDescription;


/**
 * 访问控制信息查询
 * 
 * <br>
 * 查询当前访问控制器的信息，具体信息视访问控制器的实现而定，见{@link com.logicbus.backend.AccessController#toXML(Element) AccessController.toXML(Element)}.<br>
 * 
 * 实现了一个内部核心服务，定义在/com/logicbus/service/servant.xml中，具体配置如下:<br>
 * 
 * {@code 
 * <service id="AclQuery" name="AclQuery" note="查询当前的访问控制列表" 
 * visible="protected" module="com.logicbus.service.AclQuery"/>
 * }
 * 
 * <br>
 * 如果配置在服务器中，访问地址为：<br>
 * {@code
 * http://[host]:[port]/[webcontext]/services/core/AclQuery 
 * }
 * 
 * @author duanyy
 *
 *
 * @version 1.2.8.2 [20141015 duanyy]
 * - 支持双协议:JSON和XML
 */
public class AclQuery extends AbstractServant {
	
	protected void onDestroy() {

	}

	
	protected void onCreate(ServiceDescription sd) throws ServantException {

	}

	
	protected int onXml(MessageDoc msgDoc, Context ctx) throws Exception {
		XMLMessage msg = (XMLMessage)msgDoc.asMessage(XMLMessage.class);
		
		Document doc = msg.getDocument();
		Element root = msg.getRoot();
		
		Settings settings = Settings.get();
		AccessController ac = (AccessController) settings.get("accessController");
		if (ac != null){
			Element acls = doc.createElement("acls");
			
			ac.report(acls);
			
			root.appendChild(acls);
		}
		
		return 0;
	}

	
	protected int onJson(MessageDoc msgDoc, Context ctx) throws Exception {
		JsonMessage msg = (JsonMessage)msgDoc.asMessage(JsonMessage.class);
		
		Map<String,Object> root = msg.getRoot();
		
		Settings settings = Settings.get();
		AccessController ac = (AccessController) settings.get("accessController");
		if (ac != null){
			Map<String,Object> acls = new HashMap<String,Object>();
			ac.report(acls);
			
			root.put("acls", acls);
		}
		
		return 0;
	}

}
