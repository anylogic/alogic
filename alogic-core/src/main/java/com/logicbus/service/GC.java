package com.logicbus.service;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.util.SystemStatus;
import com.logicbus.backend.Context;
import com.logicbus.backend.Servant;
import com.logicbus.backend.message.XMLMessage;


/**
 * GC
 * 
 * <br>
 * 一个小小的测试服务，调用了系统的GC.定义在/com/logicbus/service/servant.xml中，具体配置如下:<br>
 * 
 * {@code 
 * <service id="GC" 
 * name="内存回收服务" 
 * note="主动触发虚拟机回收内存" 
 * visible="protected" 
 * module="com.logicbus.service.GC"
 * />
 * }
 * 
 * <br>
 * 如果配置在服务器中，访问地址为：<br>
 * {@code
 * http://[host]:[port]/[webcontext]/services/core/GC 
 * }
 * 
 * @author duanyy
 * @version 1.4.0 [20141117 duanyy] <br>
 * - 将MessageDoc和Context进行合并整合 <br>
 */
public class GC extends Servant {
	
	
	public int actionProcess(Context ctx) throws Exception{
		SystemStatus before = new SystemStatus();
		System.gc();
		SystemStatus after = new SystemStatus();
		
		XMLMessage msg = (XMLMessage)ctx.asMessage(XMLMessage.class);	
		Element root = msg.getRoot();
		Document doc = root.getOwnerDocument();
		Element result = doc.createElement("gcResult");
		result.setAttribute("before", String.valueOf(before.getFreeMem()));
		result.setAttribute("after", String.valueOf(after.getFreeMem()));
		result.appendChild(
				doc.createTextNode("内存回收成功,共回收"
						+String.valueOf((after.getFreeMem() - before.getFreeMem())/1000)+"kb内存."));
		root.appendChild(result);
		return 0;
	}
}