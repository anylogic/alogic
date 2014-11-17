package com.logicbus.backend.message;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.anysoft.util.IOTools;
import com.anysoft.util.JsonTools;
import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.JsonProviderFactory;
import com.logicbus.backend.Context;
import com.logicbus.backend.message.Message;
import com.logicbus.backend.message.MessageDoc;

/**
 * 基于JSON的消息协议
 * @author duanyy
 * 
 * @since 1.2.1
 */
public class JsonMessage extends Message {

	/**
	 * Json结构的根节点
	 */
	protected Map<String,Object> root = null;
	
	@SuppressWarnings("unchecked")
	public JsonMessage(MessageDoc _doc,StringBuffer _buf) {
		super(_doc);
		
		String data = _buf.toString();
		if (data != null && data.length() > 0){
			JsonProvider provider = JsonProviderFactory.createProvider();
			Object rootObj = provider.parse(_buf.toString());
			if (rootObj instanceof Map){
				root = (Map<String,Object>)rootObj;
			}
		}
		if (root == null){
			root = new HashMap<String,Object>();
		}
		setContentType("application/json;charset=" + msgDoc.getEncoding());
	}

	public Map<String,Object> getRoot(){return root;}
	
	
	public void output(OutputStream out, Context ctx) {
		JsonTools.setString(root, "code", msgDoc.getReturnCode());
		JsonTools.setString(root, "reason", msgDoc.getReason());
		JsonTools.setString(root, "duration", String.valueOf(msgDoc.getDuration()));
		JsonTools.setString(root, "host", ctx.getHost());
		JsonTools.setString(root, "serial", ctx.getGlobalSerial());
		
		String data = provider.toJson(root);
		
		String jsonp = ctx.GetValue("jsonp", "");
		if (jsonp != null && jsonp.length() > 0){
			data = jsonp + "(" + data + ")";
		}
		
		try {
			out.write(data.getBytes(msgDoc.getEncoding()));
		}catch (Exception ex){
			ex.printStackTrace();
		}finally {
			IOTools.closeStream(out);
		}
	}

	
	public boolean hasFatalError(){
		return false;
	}	
	
	public String toString(){
		return provider.toJson(root);
	}
	
	protected static JsonProvider provider = null;
	
	static {
		provider = JsonProviderFactory.createProvider();
	}
}
