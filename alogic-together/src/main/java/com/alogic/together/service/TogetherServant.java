package com.alogic.together.service;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.Script;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.json.JsonObject;
import com.alogic.xscript.doc.xml.XmlObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.models.servant.ServiceDescription;

/**
 * TogetherServant
 * 
 * @author duanyy
 * @version 1.6.9.8 [20170821 duanyy] <br>
 * - 服务上下文增加keyword关键字，和tlog对接; <br>
 */
public class TogetherServant extends AbstractServant {
	protected Script script = null;
	protected String service;
	
	@Override
	protected void onDestroy() {
		
	}

	@Override
	protected void onCreate(ServiceDescription sd) {
		Properties p = sd.getProperties();
		service = sd.getPath();
		String bootstrap = PropertiesConstants.getString(p,"bootstrap","",true);
		if (StringUtils.isEmpty(bootstrap)){
			String config = PropertiesConstants.getString(p,"script","");
			if (StringUtils.isNotEmpty(config)){
				script = Script.create(config, p);
			}
		}else{
			String config = PropertiesConstants.getString(p,"script","");
			if (StringUtils.isNotEmpty(config)){
				script = Script.create(bootstrap, config, p);
			}
		}
	}

	@Override
	protected int onJson(Context ctx)  {
		if (script != null){
			JsonMessage msg = (JsonMessage) ctx.asMessage(JsonMessage.class);
			
			LogicletContext logicletContext = new SevantLogicletContext(ctx);
			logicletContext.setObject("$context", ctx);
			logicletContext.SetValue("$service", service);
			try {
				XsObject doc = new JsonObject("root",msg.getRoot());
				script.execute(doc,doc, logicletContext, null);
			}finally{
				logicletContext.removeObject("$context");
				
				String keyword = logicletContext.GetValue("$keyword", "");
				if (StringUtils.isNotEmpty(keyword)){
					ctx.setKeyword(keyword);
				}				
			}
		}else{
			ctx.asMessage(JsonMessage.class);
		}
		return 0;
	}

	protected int onXml(Context ctx) { 
		if (script != null){
			XMLMessage msg = (XMLMessage) ctx.asMessage(XMLMessage.class);
			
			LogicletContext logicletContext = new SevantLogicletContext(ctx);
			logicletContext.setObject("$context", ctx);
			logicletContext.SetValue("$service", service);
			try {
				XsObject doc = new XmlObject("root",msg.getRoot());
				script.execute(doc,doc, logicletContext, null);
			}finally{
				logicletContext.removeObject("$context");
				
				String keyword = logicletContext.GetValue("$keyword", "");
				if (StringUtils.isNotEmpty(keyword)){
					ctx.setKeyword(keyword);
				}
			}
		}else{
			ctx.asMessage(XMLMessage.class);
		}
		return 0;	
	}
}
