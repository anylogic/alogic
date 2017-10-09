package com.alogic.remote.xscript;

import com.alogic.remote.Client;
import com.alogic.remote.Request;
import com.alogic.remote.naming.ClientFactory;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 构造并定义一个Client请求
 * 
 * @author yyduan
 * @since 1.6.10.3
 */
public class CreateRequest extends NS {
	
	/**
	 * 输出对象的上下文id
	 */
	protected String cid = "remote-req";
	
	/**
	 * 客户端id，从Context中获取
	 */
	protected String clientId = "default";
	
	protected String method = "POST";
	
	public CreateRequest(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		cid = PropertiesConstants.getString(p, "cid", cid,true);
		clientId = PropertiesConstants.getString(p, "clientId", clientId,true);
		method = PropertiesConstants.getString(p, "method", method,true);
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {		
		Client client = ClientFactory.getCall(clientId);
		if (client == null){
			throw new BaseException("core.client_not_found","Can not find client :" + clientId);
		}
		Request req = null;
		try {
			req = client.build(method);
			ctx.setObject(cid, req);
			super.onExecute(root, current, ctx, watcher);
		}finally{
			ctx.removeObject(cid);
			IOTools.close(req);
		}
	}
}
