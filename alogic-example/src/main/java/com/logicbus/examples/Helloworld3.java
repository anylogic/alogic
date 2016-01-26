package com.logicbus.examples;

import com.logicbus.backend.Context;
import com.logicbus.backend.Servant;
import com.logicbus.backend.message.RawMessage;

public class Helloworld3 extends Servant {

	
	public int actionProcess(Context ctx) throws Exception {
		//从客户端传入参数列表中读取welcome参数，缺省值为welcome to anylogicbus
		String welcome = getArgument("welcome", "welcome to anylogicbus", ctx);
		
		//获取服务调用消息
		RawMessage msg = (RawMessage)ctx.asMessage(RawMessage.class);
		
		//获取输入输出缓冲区
		StringBuffer buf = msg.getBuffer();
		
		//输出Helloworld到缓冲区
		buf.setLength(0);
		buf.append(welcome);
		return 0;		
	}

}
