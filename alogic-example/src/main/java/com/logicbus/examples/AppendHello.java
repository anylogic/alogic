package com.logicbus.examples;

import com.anysoft.util.Properties;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.MessageDoc;
import com.logicbus.models.servant.Argument;
import com.logicbus.models.servant.getter.Default;

public class AppendHello extends Default {
	public AppendHello(Properties props) {
		super(props);
	}

	
	public String getValue(Argument argu, MessageDoc msg, Context ctx) throws ServantException {
		return super.getValue(argu, msg, ctx) + "hello";
	}
}
