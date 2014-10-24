package com.logicbus.examples;

import com.anysoft.util.Properties;
import com.logicbus.backend.Context;
import com.logicbus.backend.Servant;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.MessageDoc;
import com.logicbus.backend.message.RawMessage;
import com.logicbus.models.servant.ServiceDescription;

public class Helloworld2 extends Servant {
	
	
	public int actionProcess(MessageDoc msgDoc, Context ctx) throws Exception {
		//获取服务调用消息
		RawMessage msg = (RawMessage)msgDoc.asMessage(RawMessage.class);
		
		//获取输入输出缓冲区
		StringBuffer buf = msg.getBuffer();
		
		//输出Helloworld到缓冲区
		buf.setLength(0);
		buf.append(welcome);
		return 0;
	}

	
	public void create(ServiceDescription sd) throws ServantException{
		super.create(sd);
		//获取服务描述的初始化参数表
		Properties props = sd.getProperties();
		//从参数表中获取welcome参数，如果没有配置，缺省值为Hello world
		welcome = props.GetValue("welcome", "Hello world");
	}
	
	/**
	 * 欢迎语
	 */
	protected String welcome;	
}
