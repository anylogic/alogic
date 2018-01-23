package com.logicbus.backend.message;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anysoft.util.IOTools;
import com.anysoft.util.Settings;
import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.JsonProviderFactory;
import com.logicbus.backend.Context;
import com.logicbus.backend.server.http.HttpContext;

/**
 * JsonRawMessage
 * @author yyduan
 * 
 * @since 1.6.11.12
 */
public class JsonRawMessage implements Message {
	protected static final Logger logger = LoggerFactory.getLogger(JsonMessage.class);
	protected static JsonProvider provider = null;	
	static {
		provider = JsonProviderFactory.createProvider();
	}
	
	protected static String formContentType = "application/x-www-form-urlencoded";
	static {
		formContentType = Settings.get().GetValue("http.formContentType",
				"application/x-www-form-urlencoded");
	}
	
	private String contentType = "application/json;charset=utf-8";
	
	/**
	 * Json结构的根节点
	 */
	protected Map<String,Object> root = null;	
	
	private long contentLength = 0;
	
	@SuppressWarnings("unchecked")
	public void init(Context ctx) {
		String data = null;
		
		{
			byte [] inputData = ctx.getRequestRaw();
			if (inputData != null){
				try {
					data = new String(inputData,ctx.getEncoding());
				}catch (Exception ex){
					
				}
			}
		}
		
		if (data == null){
			//当客户端通过form来post的时候，Message不去读取输入流。
			String _contentType = ctx.getRequestContentType();
			if (_contentType == null || !_contentType.startsWith(formContentType)){
				InputStream in = null;
				try {
					in = ctx.getInputStream();
					data = Context.readFromInputStream(in, ctx.getEncoding());					
				}catch (Exception ex){
					logger.error("Error when reading data from inputstream",ex);
				}finally{
					IOTools.close(in);
				}
			}
		}
		
		if (data != null && data.length() > 0){
			contentLength += data.getBytes().length;
			JsonProvider provider = JsonProviderFactory.createProvider();
			Object rootObj = provider.parse(data);
			if (rootObj instanceof Map){
				root = (Map<String,Object>)rootObj;
			}
		}
		if (root == null){
			root = new HashMap<String,Object>();
		}
		
		contentType = "application/json;charset=" + ctx.getEncoding();
	}

	public void finish(Context ctx,boolean closeStream) {
		OutputStream out = null;
		try {
			out = ctx.getOutputStream();
			String code = ctx.getReturnCode();
			if (!code.equals("core.ok")){
				if (ctx instanceof HttpContext){
					HttpContext httpContext = (HttpContext)ctx;
					httpContext.getResponse().sendError(404, ctx.getReturnCode() + ":" + ctx.getReason());
				}
			}
		}catch (Exception ex){
			logger.error("Error when writing data to outputstream",ex);
		}finally{
			if (closeStream)
				IOTools.close(out);
		}
	}	

	/**
	 * 获取JSON结构的根节点
	 * 
	 * @return JSON结构的根节点
	 */
	public Map<String,Object> getRoot(){
		return root;
	}
	
	public String toString(){
		return provider.toJson(root);
	}
		
	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public long getContentLength() {
		return contentLength;
	}

}
