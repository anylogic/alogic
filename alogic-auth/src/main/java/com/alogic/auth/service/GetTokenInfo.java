package com.alogic.auth.service;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.auth.Principal;
import com.alogic.auth.PrincipalManager;
import com.alogic.auth.SessionManagerFactory;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlTools;
import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 获取指定Token的信息
 * 
 * @author yyduan
 * @since 1.6.10.10
 * 
 * @version 1.6.11.7 [20180107 duanyy] <br>
 * - 优化Session管理 <br>
 * 
 * @version 1.6.11.20 [20180223 duanyy] <br>
 * - app参数id改为$provider <br>
 */
public class GetTokenInfo extends AbstractServant{
	protected String dftApp = "${server.app}";
	protected String arguToken = "token";
	protected String arguFromIp = "fromIp";
	protected String arguCallback = "callback";
	protected String arguApp = "$provider";
	
	@Override
	protected void onDestroy() {

	}

	@Override
	protected void onCreate(ServiceDescription sd) {
		Properties p = sd.getProperties();
		dftApp = PropertiesConstants.getString(p, "dftApp", dftApp);
		arguToken = PropertiesConstants.getString(p, "auth.para.token", arguToken);
		arguFromIp = PropertiesConstants.getString(p, "auth.para.fromIp", arguFromIp);
		arguCallback = PropertiesConstants.getString(p, "auth.para.callback", arguCallback);
		arguApp = PropertiesConstants.getString(p, "auth.para.app", arguApp);
	}

	@Override
	protected int onJson(Context ctx){
		JsonMessage msg = (JsonMessage)ctx.asMessage(JsonMessage.class);
		
		String token = getArgument(arguToken, ctx);
		String fromIp = getArgument(arguFromIp,ctx);
		String callback = getArgument(arguCallback,"",ctx);
		
		String app = getArgument(arguApp,dftApp,ctx);
		
		Map<String,Object> data = new HashMap<String,Object>();
		PrincipalManager sm = (PrincipalManager)SessionManagerFactory.getDefault();
		
		Principal principal = sm.getPrincipal(app, token,callback);
		if (principal == null){
			JsonTools.setString(data,"isLoggedIn","false");
		}else{
			if (fromIp.equals(principal.getLoginIp())){
				//token必须绑定ip
				JsonTools.setString(data,"isLoggedIn","true");
				principal.report(data);
			}else{				
				JsonTools.setString(data,"isLoggedIn","false");
			}
		}
		
		msg.getRoot().put("data", data);
		return 0;
	}

	@Override
	protected int onXml(Context ctx){
		XMLMessage msg = (XMLMessage)ctx.asMessage(XMLMessage.class);
		
		String token = getArgument(arguToken, ctx);
		String fromIp = getArgument(arguFromIp,ctx);
		String app = getArgument("$app",dftApp,ctx);
		String callback = getArgument(arguCallback,"",ctx);
		
		Document doc = msg.getDocument();
		Element data = doc.createElement("data");
		
		PrincipalManager sm = (PrincipalManager)SessionManagerFactory.getDefault();
		Principal principal = sm.getPrincipal(app, token,callback);
		if (principal == null){
			XmlTools.setString(data,"isLoggedIn","false");
		}else{
			if (fromIp.equals(principal.getLoginIp())){
				//token必须绑定ip
				XmlTools.setString(data,"isLoggedIn","true");
				principal.report(data);
			}else{
				XmlTools.setString(data,"isLoggedIn","false");
			}
		}
		
		msg.getRoot().appendChild(data);
		
		return 0;
	}
}