package com.logicbus.service;


import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;

import com.alogic.tracer.Tool;
import com.alogic.tracer.TraceContext;
import com.anysoft.util.IOTools;
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
 * 
 * @version 1.6.5.8 [20160601 duanyy] <br>
 * - Proxy支持web应用的Context路径 <br>
 * 
 * @version 1.6.5.11 [20160603 duanyy] <br>
 * - 修正采用HttpURLConnection导致的一些bug <br>
 * 
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
		
		String contextPath = ctx.GetValue("contexPath", "");

		String endPoint;
		if (StringUtils.isEmpty(contextPath)){
			endPoint = scheme + "://" + host + proxyPath + service;
		}else{
			endPoint = scheme + "://" + host + "/" + contextPath + proxyPath + service;
		}
		//组装URL
		{
			String query = ctx.GetValue("query", "");
			if (query != null && query.length() > 0){
				endPoint = endPoint + "?" + query;
			}
		}		
		
		boolean error = false;
		String errorMsg = "OK";
		TraceContext trace = traceEnable?Tool.start(ctx.getGlobalSerial(), ctx.getGlobalSerialOrder()):null;
		try {	
			URL url = new URL(endPoint);
			String method = ctx.getMethod();
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(method);		
			conn.setDoInput(true);			
			
			//设置request header
			{
				if (forwarded){
					conn.addRequestProperty(forwardedHeader, ctx.getClientIp());
				}
				String contentType = ctx.getRequestContentType();
				if (contentType != null && contentType.length() > 0){
					conn.addRequestProperty("Content-Type", contentType);
				}
				if (traceEnable && trace != null){
					TraceContext childTrace = trace.newChild();
					conn.addRequestProperty("GlobalSerial", childTrace.sn());
					conn.addRequestProperty("GlobalSerialOrder", String.valueOf(childTrace.order()));					
				}else{
					String globalSerial = ctx.getGlobalSerial();
					long order = ctx.getGlobalSerialOrder();
					if (globalSerial != null && globalSerial.length() > 0){
						conn.addRequestProperty("GlobalSerial", globalSerial);
						conn.addRequestProperty("GlobalSerialOrder", String.valueOf(order));
					}
				}
			}
			output(conn,msg);			
			int ret = conn.getResponseCode();
			if (ret!= HttpURLConnection.HTTP_OK){
				IOTools.close(conn.getInputStream());
				throw new ServantException("client.invoke_error", 
						"Error occurs when invoking service :"
						+ conn.getResponseMessage());
			}
			
			input(conn,msg);
		}catch (Exception ex){
			error = true;
			errorMsg = ex.getMessage();
			throw ex;
		}
		finally{
			if (traceEnable){
				Tool.end(trace, "ALOGIC", "Proxy", error?"FAILED":"OK", error?errorMsg:endPoint);
			}
		}
		return 0;
	}
	
	public void output(HttpURLConnection conn,ByteMessage msg){
		OutputStream out = null;
		
		try {
			byte[] toWrite = msg.getInput();
			if (toWrite.length > 0){
				conn.setDoOutput(true);
				out = conn.getOutputStream();
				ByteMessage.writeBytes(conn.getOutputStream(), toWrite);
			}else{
				conn.setDoOutput(false);
			}
		}catch (Exception ex){
			throw new ServantException("core.io_error","Can not write data to network.");
		}finally{
			IOTools.close(out);
		}
	}
	
	protected void input(HttpURLConnection conn,ByteMessage msg){
		InputStream in = null;
		
		try {
			in = conn.getInputStream();
			byte[] toRead = ByteMessage.readBytes(conn.getInputStream());
			
			String resonseContentType = conn.getContentType();
			if (resonseContentType != null && resonseContentType.length() > 0){
				msg.setContentType(resonseContentType);
			}
			msg.setOutput(toRead);		
		}catch (Exception ex){
			throw new ServantException("core.io_error","Can not read data from network.");
		}finally{
			IOTools.close(in);
		}
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
		
		traceEnable = PropertiesConstants.getBoolean(p, "servant.tracer", traceEnable);
	}
	
	protected String forwardedHeader = "X-Forwarded-For";
	protected boolean forwarded = false;
	protected String proxyPath = "/services/";
	protected String scheme = "http";
	protected boolean enable = true;
	protected boolean traceEnable = false;
}
