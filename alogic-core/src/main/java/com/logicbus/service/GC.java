package com.logicbus.service;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.util.SystemStatus;
import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.models.servant.ServiceDescription;


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
 * 
 * @version 1.6.3.27 [20150623 duanyy] <br>
 * - 增加XML和JSON双协议支持
 */
public class GC extends AbstractServant {
	@Override
	protected void onDestroy() {

	}

	@Override
	protected void onCreate(ServiceDescription sd) throws ServantException {

	}
	
	protected int onXml(Context ctx)  {
		XMLMessage msg = (XMLMessage)ctx.asMessage(XMLMessage.class);	
		
		SystemStatus before = new SystemStatus();
		System.gc();
		SystemStatus after = new SystemStatus();
		
		Element root = msg.getRoot();
		Document doc = root.getOwnerDocument();
		Element result = doc.createElement("gcResult");
		result.setAttribute("before", String.valueOf(before.getFreeMem()));
		result.setAttribute("after", String.valueOf(after.getFreeMem()));
		result.setAttribute("msg", "内存回收成功,共回收"
						+String.valueOf((after.getFreeMem() - before.getFreeMem())/1000)+"kb内存.");
		root.appendChild(result);
		return 0;
	}

	
	protected int onJson(Context ctx)  {
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		
		SystemStatus before = new SystemStatus();
		System.gc();
		SystemStatus after = new SystemStatus();
		
		Map<String,Object> root = msg.getRoot();
		Map<String,Object> result = new HashMap<String,Object>();
		
		result.put("before", before.getFreeMem());
		result.put("after", after.getFreeMem());
		result.put("msg", "内存回收成功,共回收"
						+String.valueOf((after.getFreeMem() - before.getFreeMem())/1000)+"kb内存.");
		
		root.put("gcResult", result);
		
		return 0;
	}	
}