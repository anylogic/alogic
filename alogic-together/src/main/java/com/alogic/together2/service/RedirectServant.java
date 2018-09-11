package com.alogic.together2.service;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import com.alogic.together2.TogetherServiceDescription;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.Script;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.json.JsonObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.JsonRawMessage;
import com.logicbus.backend.server.http.HttpContext;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 用于做重定向的Servant
 * 
 * @author yyduan
 * @version 1.6.11.59 [20180911 duanyy] <br>
 * - 使用新的ServantLogicletContext类;
 */
public class RedirectServant extends AbstractServant {
	protected Script script = null;
	protected String service;
	
	@Override
	protected void onDestroy() {
		
	}

	@Override
	protected void onCreate(ServiceDescription sd) {
		service = sd.getPath();
		if (sd instanceof TogetherServiceDescription){
			script = ((TogetherServiceDescription)sd).getScript();
		}else{
			Properties p = sd.getProperties();
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
	}

	@Override
	protected int onJson(Context ctx)  {
		if (script != null){
			JsonRawMessage msg = (JsonRawMessage) ctx.asMessage(JsonRawMessage.class);
			
			LogicletContext logicletContext = new Context.ServantLogicletContext(ctx);

			try {
				logicletContext.setObject("$context", ctx);
				logicletContext.SetValue("$service", service);
				XsObject doc = new JsonObject("root",msg.getRoot());
				script.execute(doc,doc,logicletContext, null);
			}finally{
				logicletContext.removeObject("$context");
				logicletContext.removeObject("$message");
				String keyword = logicletContext.GetValue("$keyword", "");
				if (StringUtils.isNotEmpty(keyword)){
					ctx.setKeyword(keyword);
				}				
				String redirectUrl = logicletContext.GetValue("$redirect","");
				if (StringUtils.isNotEmpty(redirectUrl) && ctx instanceof HttpContext){
					HttpContext httpContext = (HttpContext)ctx;					
					try {
						httpContext.getResponse().sendRedirect(redirectUrl);
					} catch (IOException e) {
						logger.error(ExceptionUtils.getStackTrace(e));
					}
				}
			}
		}else{
			ctx.asMessage(JsonMessage.class);
		}
		return 0;
	}
}
