package com.logicbus.service;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.logicbus.backend.Context;
import com.logicbus.backend.Servant;
import com.logicbus.backend.message.MessageDoc;
import com.logicbus.backend.message.XMLMessage;

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
 *
 */
public class AreYouAlive extends Servant {
	
	
	public int actionProcess(MessageDoc msgDoc, Context ctx) throws Exception{
		XMLMessage msg = (XMLMessage)msgDoc.asMessage(XMLMessage.class);
		Element root = msg.getRoot();
		Document doc = root.getOwnerDocument();
		root.appendChild(doc.createTextNode("Ok,i am alive."));
		return 0;
	}
}