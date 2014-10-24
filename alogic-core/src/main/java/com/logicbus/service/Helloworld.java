package com.logicbus.service;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.logicbus.backend.Context;
import com.logicbus.backend.Servant;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.MessageDoc;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.models.servant.ServiceDescription;

/**
 * Helloworld
 * 
 * <br>
 * 一个小小的测试服务，向客户端说了声Helloworld
 * 定义在/com/logicbus/service/servant.xml中，具体配置如下:<br>
 * 
 * {@code 
 * <service
 * id="Helloworld"
 * name="Helloworld"
 * note="Hello world,我的第一个Logicbus服务"
 * visible="public"
 * module="com.logicbus.service.Helloworld">
 *     <properties>
 *         <parameter id="welcome" value="北京欢迎你."/>
 *     </properties>
 * </service>
 * }
 * 
 * <br>
 * 如果配置在服务器中，访问地址为：<br>
 * {@code
 * http://[host]:[port]/[webcontext]/services/demo/logicbus/Helloworld 
 * }
 * 
 * <br>
 * 本实现支持在服务描述信息中定义参数,参数如下：<br>
 * - welcome:用于出现不同的欢迎语
 * 
 * @author duanyy
 *
 */
public class Helloworld extends Servant {
	protected String m_welcome;
	
	
	public int actionProcess(MessageDoc msgDoc, Context ctx) throws Exception{
		XMLMessage msg = (XMLMessage)msgDoc.asMessage(XMLMessage.class);		
		Element root = msg.getRoot();
		Document doc = root.getOwnerDocument();
		root.appendChild(doc.createTextNode( m_welcome));
		return 0;
	}
	
	public void create(ServiceDescription sd) throws ServantException{
		super.create(sd);		
		m_welcome = sd.getProperties().GetValue("welcome", "Welcome to Logic Bus Server!");
	}
	
}
