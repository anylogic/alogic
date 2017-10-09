package com.alogic.remote.xscript;

import org.apache.commons.lang3.StringUtils;

import com.alogic.remote.Request;
import com.alogic.remote.Response;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.plugins.Segment;
import com.anysoft.util.BaseException;
import com.anysoft.util.KeyGen;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 执行Request请求
 * @author yyduan
 * @since 1.6.10.3
 */
public class ExecuteRequest extends Segment {
	
	/**
	 * Response对象的上下文id
	 */
	protected String cid = "remote-res";
	
	/**
	 * Request对象的上下文id
	 */
	protected String pid = "remote-req";	
	
	/**
	 * 服务路径
	 */
	protected String path = "";
	
	/**
	 * 调用key
	 */
	protected String key = "";
	
	public ExecuteRequest(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		cid = PropertiesConstants.getString(p, "cid", cid,true);
		pid = PropertiesConstants.getString(p, "pid", pid,true);
		path = PropertiesConstants.getRaw(p,"path",path);
		key = PropertiesConstants.getRaw(p,"key",key);
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {		
		Request req = ctx.getObject(pid);
		if (req == null){
			throw new BaseException("core.no_request","It must be in a remote-request context,check your script.");
		}
		
		Response response = null;
		try {
			String p = ctx.transform(path);
			if (StringUtils.isEmpty(p)){
				throw new BaseException("core.no_path","The url path to call is null.");
			}
			
			String k = ctx.transform(key);
			k = StringUtils.isEmpty(k)?KeyGen.uuid(10, 0, 9) : k;			
			response = req.execute(p, k, ctx);			
			ctx.setObject(cid, response);
			
			super.onExecute(root, current, ctx, watcher);
		}finally{
			ctx.removeObject(cid);
		}
	}
}