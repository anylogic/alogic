package com.alogic.auth.service;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.auth.Principal;
import com.alogic.auth.PrincipalManager;
import com.alogic.auth.Session;
import com.alogic.auth.SessionManagerFactory;
import com.anysoft.util.JsonTools;
import com.anysoft.util.XmlTools;
import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 查询当前登录信息
 * 
 * @author yyduan
 *
 * @since 1.6.10.10
 */
public class CurrentLogin extends AbstractServant{

	@Override
	protected void onDestroy() {

	}

	@Override
	protected void onCreate(ServiceDescription sd) {

	}

	@Override
	protected int onJson(Context ctx){
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		
		Map<String,Object> data = new HashMap<String,Object>();
		PrincipalManager sm = (PrincipalManager)SessionManagerFactory.getDefault();
		Session sess = sm.getSession(ctx, true);
		if (sess == null){
			JsonTools.setString(data,"isLoggedIn","false");
		}else{			
			Principal principal = sm.getCurrent(ctx);
			if (principal != null){
				principal.report(data);
			}
			JsonTools.setBoolean(data,"isLoggedIn",sess.isLoggedIn());
		}
		
		msg.getRoot().put("data", data);
		return 0;
	}

	@Override
	protected int onXml(Context ctx){
		XMLMessage msg = (XMLMessage)ctx.asMessage(XMLMessage.class);
		
		Document doc = msg.getDocument();
		Element data = doc.createElement("data");
		
		PrincipalManager sm = (PrincipalManager)SessionManagerFactory.getDefault();
		Session sess = sm.getSession(ctx, true);
		if (sess == null){
			XmlTools.setString(data,"isLoggedIn","false");
		}else{			
			Principal principal = sm.getCurrent(ctx);
			if (principal != null){
				principal.report(data);				
			}
			XmlTools.setBoolean(data,"isLoggedIn",sess.isLoggedIn());
		}
		
		msg.getRoot().appendChild(data);
		
		return 0;
	}
}
