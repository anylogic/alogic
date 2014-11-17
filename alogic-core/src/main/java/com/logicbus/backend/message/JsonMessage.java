package com.logicbus.backend.message;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.anysoft.util.JsonTools;
import com.anysoft.util.Settings;
import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.JsonProviderFactory;
import com.logicbus.backend.Context;
import com.logicbus.backend.message.Message;

/**
 * 基于JSON的消息协议
 * @author duanyy
 * 
 * @since 1.2.1
 * 
 * @version 1.4.0 [20141117 duanyy] <br>
 * - Message被改造为接口 <br>
 */
public class JsonMessage implements Message {
	public void write(OutputStream out, Context doc) {
		JsonTools.setString(root, "code", doc.getReturnCode());
		JsonTools.setString(root, "reason", doc.getReason());
		JsonTools.setString(root, "duration", String.valueOf(doc.getDuration()));
		JsonTools.setString(root, "host", doc.getHost());
		JsonTools.setString(root, "serial", doc.getGlobalSerial());
		
		String data = provider.toJson(root);
		
		String jsonp = doc.GetValue("jsonp", "");
		if (jsonp != null && jsonp.length() > 0){
			data = jsonp + "(" + data + ")";
		}

		Context.writeToOutpuStream(out, data, doc.getEncoding());
	}

	@SuppressWarnings("unchecked")
	public void read(InputStream in, Context doc) {
		String data = Context.readFromInputStream(in, doc.getEncoding());
		if (data != null && data.length() > 0){
			JsonProvider provider = JsonProviderFactory.createProvider();
			Object rootObj = provider.parse(data);
			if (rootObj instanceof Map){
				root = (Map<String,Object>)rootObj;
			}
		}
		if (root == null){
			root = new HashMap<String,Object>();
		}
	}
	
	/**
	 * Json结构的根节点
	 */
	protected Map<String,Object> root = null;
	
	/**
	 * 获取JSON结构的根节点
	 * 
	 * @return JSON结构的根节点
	 */
	public Map<String,Object> getRoot(){return root;}
	
	public String toString(){
		return provider.toJson(root);
	}
	
	protected static JsonProvider provider = null;
	
	static {
		provider = JsonProviderFactory.createProvider();
	}

	public boolean doRead(Context doc) {
		//当客户端通过form来post的时候，Message不去读取输入流。
		String contentType = doc.getReqestContentType();
		return !(contentType!=null&&contentType.startsWith(formContentType));
	}

	public boolean doWrite(Context doc) {
		return true;
	}

	public String getContentType(Context doc) {
		return "application/json;charset=" + doc.getEncoding();
	}
	
	protected static String formContentType = "application/x-www-form-urlencoded";
	static {
		formContentType = Settings.get().GetValue("http.alloworigin",
				"application/x-www-form-urlencoded");
	}
}
