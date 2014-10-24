package com.logicbus.backend.acm;

import com.anysoft.util.Settings;
import com.logicbus.backend.AccessController;
import com.logicbus.backend.Context;
import com.logicbus.backend.Servant;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.MessageDoc;
import com.logicbus.backend.message.XMLMessage;

public class TokenDestroy extends Servant {

	public int actionProcess(MessageDoc msg, Context ctx) throws Exception {
		String json = getArgument("json","true",msg,ctx);
		if (json != null && json.equals("true")){
			return actionProcessOnJson(msg,ctx);
		}else{
			return actionProcessOnXml(msg,ctx);
		}
	}
	
	protected int actionProcessOnJson(MessageDoc msgDoc,Context ctx)throws Exception{
		JsonMessage msg = (JsonMessage)msgDoc.asMessage(JsonMessage.class);
		
		String id = getArgument("id",msg, ctx);
		
		Settings settings = Settings.get();
		AccessController ac = (AccessController) settings.get("accessController");
		if (ac != null && ac instanceof ACMAccessController){
			TokenHolder holder = ((ACMAccessController)ac).getTokenHolder();
			if (holder != null){
				holder.remove(id);
			}
		}
		
		return 0;
	}
	
	protected int actionProcessOnXml(MessageDoc msgDoc,Context ctx)throws Exception{
		XMLMessage msg = (XMLMessage)msgDoc.asMessage(XMLMessage.class);		
		String id = getArgument("id",msg, ctx);		
		Settings settings = Settings.get();
		AccessController ac = (AccessController) settings.get("accessController");
		if (ac != null){
			TokenHolder holder = ((ACMAccessController)ac).getTokenHolder();
			if (holder != null){
				holder.remove(id);
			}
		}
		
		return 0;
	}

}