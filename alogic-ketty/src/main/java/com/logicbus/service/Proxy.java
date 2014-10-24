package com.logicbus.service;

import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.Context;
import com.logicbus.backend.Servant;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.MessageDoc;
import com.logicbus.backend.message.RawMessage;
import com.logicbus.models.servant.ServiceDescription;
import com.logicbus.remote.client.HttpClient;
import com.logicbus.remote.client.Request;
import com.logicbus.remote.client.Response;

/**
 * 代理服务
 * <br>
 * 提供代理服务功能，和ProxyNormalizer配合使用。
 * 
 * @author duanyy
 * @version 1.2.7.2
 * 
 */
public class Proxy extends Servant {

	
	public int actionProcess(MessageDoc msgDoc, Context ctx) throws Exception {
		RawMessage msg = (RawMessage) msgDoc.asMessage(RawMessage.class);
		
		if (!enable){
			throw new ServantException("core.servicedisable","the proxy service is disable now.");
		}
		
		String host = ctx.GetValue("host", "");
		String service = ctx.GetValue("service", "");
		
		if (host == null || host.length() <= 0){
			throw new ServantException("client.nohost","Can not get host from url.");
		}
		
		if (service == null || service.length() <= 0){
			throw new ServantException("client.noservice","Can not get service from url.");
		}
		
		String url = scheme + "://" + host + proxyPath + service;
		
		String query = ctx.GetValue("query", "");
		if (query != null && query.length() > 0){
			url = url + "?" + query;
		}
		try {
			buf.resetMessage(msg,ctx.getGlobalSerial());
			client.invoke(url,null,buf,buf);
			msg.setContentType(buf.getContentType());
		}catch (Exception ex){
			throw ex;
		}
		return 0;
	}

	public void create(ServiceDescription sd) throws ServantException{
		super.create(sd);
		Properties p = sd.getProperties();
		
		proxyPath = PropertiesConstants.getString(p, "proxy.path", proxyPath);
		
		forwarded = PropertiesConstants.getBoolean(p, "proxy.forwarded", forwarded);
		
		forwardedHeader = PropertiesConstants.getString(p, "proxy.forwarded.header", "${http.forwardedheader}");
		if (forwardedHeader == null || forwardedHeader.length() <= 0){
			forwardedHeader = "X-Forwarded-For";
		}
		
		scheme = PropertiesConstants.getString(p, "proxy.scheme", scheme);
		
		enable = PropertiesConstants.getBoolean(p, "proxy.enable", enable);
		
		client = new HttpClient(p);
		buf = new Data();
	}
	
	protected String forwardedHeader = "X-Forwarded-For";
	protected boolean forwarded = false;
	protected String proxyPath = "/services/";
	protected String scheme = "http";
	protected Data buf = null;
	protected HttpClient client = null;
	protected boolean enable = true;
	
	public static class Data implements Request,Response{
		protected RawMessage rawMessage = null;
	
		protected String contentType;

		protected String globalSerial = null;
		
		public String getContentType(){return contentType;}
		
		public void resetMessage(RawMessage msg,String serial){
			rawMessage = msg;
			globalSerial = serial;
		}
		
		
		public StringBuffer getBuffer() {
			return rawMessage.getBuffer();
		}

		
		public String[] getResponseAttributeNames() {
			return null;
		}

		
		public String[] getRequestAttributeNames() {
			return new String[]{"GlobalSerial"};
		}
		
		
		public void setResponseAttribute(String name, String value) { 
			if (name.equals("Content-Type")){
				contentType = value;
			}
		}
		
		
		public String getRequestAttribute(String name, String defaultValue) {
			if (name.equals("GlobalSerial")){
				return globalSerial;
			}
			return defaultValue;
		}

		
		public void prepareBuffer(boolean flag) {

		}		
	}	
}
