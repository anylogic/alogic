package com.logicbus.examples;

import com.logicbus.backend.Context;
import com.logicbus.backend.Servant;
import com.logicbus.backend.message.RawMessage;

public class Helloworld extends Servant {

	
	public int actionProcess(Context ctx) throws Exception {
		
		//获取服务调用消息
		RawMessage msg = (RawMessage)ctx.asMessage(RawMessage.class);
		
		//获取输入输出缓冲区
		StringBuffer buf = msg.getBuffer();
		
		//输出Helloworld到缓冲区
		buf.setLength(0);
		buf.append("Hello world");
		return 0;
	}

}
