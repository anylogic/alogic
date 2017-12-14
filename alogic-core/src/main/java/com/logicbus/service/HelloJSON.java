package com.logicbus.service;

import com.logicbus.backend.Context;
import com.logicbus.backend.Servant;
import com.logicbus.backend.message.RawMessage;

/**
 * HelloJSON
 * 
 * <br>
 * 一个小小的测试服务，采用JSON格式向客户端输出了hello.定义在/com/logicbus/service/servant.xml中，具体配置如下:<br>
 * 
 * {@code 
 * <service id="HelloJSON"
 * name="HelloJSON"
 * note="HelloJSON ,我的第一个Logicbus服务。"
 * visible="public"
 * module="com.logicbus.service.HelloJSON"
 * />
 * }
 * 
 * <br>
 * 如果配置在服务器中，访问地址为：<br>
 * {@code
 * http://[host]:[port]/[webcontext]/services/demo/logicbus/HelloJSON 
 * }
 * 
 * @author duanyy
 * @version 1.4.0 [20141117 duanyy] <br>
 * - 将MessageDoc和Context进行合并整合 <br>
 */
public class HelloJSON extends Servant {
	
	public int actionProcess(Context ctx)  {
		RawMessage msg = (RawMessage)ctx.asMessage(RawMessage.class);
		StringBuffer buf = msg.getBuffer();
		buf.setLength(0);
		buf.append("{\"say\":\"hello world!\"}");
		return 0;
	}
}
