package com.logicbus.service;


import java.net.HttpURLConnection;
import java.net.URL;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.Context;
import com.logicbus.backend.Servant;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.ByteMessage;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 代理服务
 * <br>
 * 提供代理服务功能，和ProxyNormalizer配合使用。
 * 
 * @author duanyy
 * 
 * @since 1.2.7.2
 * 
 * @version 1.6.1.1 <br>
 * - 透传前端请求的Method、ContentType、HTTPBody等信息 <br>
 * - 抛弃MessageDoc <br>
 * 
 * @version 1.6.1.2 <br>
 * - 修正Already connected问题 <br>
 */
public class Proxy extends Servant {

	
	public int actionProcess(Context ctx) throws Exception {
		ByteMessage msg = (ByteMessage) ctx.asMessage(ByteMessage.class);
		
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
		
		try {
			String _url = scheme + "://" + host + proxyPath + service;
			//组装URL
			{
				String query = ctx.GetValue("query", "");
				if (query != null && query.length() > 0){
					_url = _url + "?" + query;
				}
			}
			
			URL url = new URL(_url);
			String method = ctx.getMethod();
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(method);
			//设置request header
			{
				if (forwarded){
					conn.addRequestProperty(forwardedHeader, ctx.getClientIp());
				}
				String contentType = ctx.getRequestContentType();
				if (contentType != null && contentType.length() > 0){
					conn.addRequestProperty("Content-Type", contentType);
				}
				String globalSerial = ctx.getGlobalSerial();
				if (globalSerial != null && globalSerial.length() > 0){
					conn.addRequestProperty("GlobalSerial", globalSerial);
				}
			}
			
			conn.setDoInput(true);
			byte[] toWrite = msg.getInput();
			if (toWrite.length > 0){
				conn.setDoOutput(true);
				ByteMessage.writeBytes(conn.getOutputStream(), toWrite);
			}else{
				conn.setDoOutput(false);
			}
			
			
			int ret = conn.getResponseCode();
			if (ret!= HttpURLConnection.HTTP_OK){
				throw new ServantException("client.invoke_error", 
						"Error occurs when invoking service :"
						+ conn.getResponseMessage());
			}
			
			byte[] toRead = ByteMessage.readBytes(conn.getInputStream());
			
			String resonseContentType = conn.getContentType();
			if (resonseContentType != null && resonseContentType.length() > 0){
				msg.setContentType(resonseContentType);
			}
			msg.setOutput(toRead);
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
	}
	
	protected String forwardedHeader = "X-Forwarded-For";
	protected boolean forwarded = false;
	protected String proxyPath = "/services/";
	protected String scheme = "http";
	protected boolean enable = true;
}
