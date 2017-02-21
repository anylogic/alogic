package com.alogic.rpc.call.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.alogic.rpc.Call;
import com.alogic.rpc.CallException;
import com.alogic.rpc.InvokeContext;
import com.alogic.rpc.InvokeFilter;
import com.alogic.rpc.Parameters;
import com.alogic.rpc.Result;
import com.alogic.rpc.serializer.Serializer;
import com.alogic.rpc.serializer.kryo.KryoSerializer;
import com.alogic.tracer.Tool;
import com.alogic.tracer.TraceContext;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 基于Http的调用
 * 
 * @author duanyy
 * @since 1.6.7.15
 */
public class HttpCall extends Call.Abstract {
	
	/**
	 * 服务调用地址
	 */
	protected String address;

	/**
	 * cookies
	 * 
	 * <br>
	 * 保存服务器返回的cookies
	 */
	protected String cookies;
	
	/**
	 * 缺省的encoding
	 * @since 1.0.7
	 */
	protected String defaultEncoding = "utf-8";
	
	/**
	 * 缺省的content-type
	 * @since 1.0.7
	 */
	protected String defaultContentType = "text/plain;charset=utf-8";
	
	protected Serializer serializer = null;
	
	public HttpCall(){
		
	}
	
	protected Result invoke(String sn,String order,URL url, Parameters params) {
		try {			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			if (cookies != null && cookies.length() > 0){
				conn.addRequestProperty("Cookie", cookies);
			}
			if (params != null){
				params.sn(sn);
				params.order(order);
				conn.addRequestProperty("GlobalSerial", sn);
				conn.addRequestProperty("GlobalSerialOrder", order);				
				conn.addRequestProperty("Content-Type", defaultContentType);
				conn.addRequestProperty("Content-Encoding", defaultEncoding);					
				output(conn,params);
			}
			
			int ret = conn.getResponseCode();
			if (ret != HttpURLConnection.HTTP_OK) {
				throw new CallException("core.invoke_error", 
						"Error occurs when invoking service :"
						+ conn.getResponseMessage());
			}
			
			String newCookies = conn.getHeaderField("Set-Cookie");
			if (newCookies != null){
				cookies = newCookies;
			}
 
			return input(conn);
		}catch (IOException ex){
			throw new CallException("core.io_error","Can not connect to remote host : " + url,ex);
		}
	}

	protected Result input(HttpURLConnection conn) {
		InputStream in = null;
		try {
			String contentType = conn.getHeaderField("Content-Type");
			String encoding = conn.getHeaderField("Content-Encoding");
			if (encoding == null){
				if (contentType != null){
					int offset = contentType.indexOf("charset=");
					if (offset >= 0){
						encoding = contentType.substring(offset + 8);
					}
				}
			}
			encoding = encoding == null || encoding.length() <= 0 ? defaultEncoding : encoding;
			contentType = contentType == null ? defaultContentType : contentType;	
			
			return serializer.readObject(conn.getInputStream(), Result.Default.class);	
		}catch (IOException ex){
			throw new CallException("core.io_error","Can not read data from network.",ex);
		}
		finally {
			IOTools.closeStream(in);
		}
	}

	protected void output(HttpURLConnection conn, Parameters params) {
		OutputStream out = null;
		try {		
			out = conn.getOutputStream();
			serializer.writeObject(out, params);			
		}catch (IOException ex){
			throw new CallException("core.io_error","Can not write data to network.",ex);
		}finally {
			IOTools.closeStream(out);
		}
	}


	@Override
	public Result invoke(String id,String method, Parameters params) {
		//处理context
		if (!filters.isEmpty()){
			InvokeContext ctx = new InvokeContext.Default();
			for (InvokeFilter f:filters){
				if (f != null){
					f.doFilter(ctx);
				}
			}
			
			if (!ctx.isEmpty()){
				params.context(ctx);
			}
		}		
		Result result = null;
		//开始调用			
		TraceContext ctx = Tool.start();		
		try {	
			TraceContext child = ctx == null ? null:ctx.newChild();
			//按照路由生成路径
			URL url = getServiceUrl(id,method);

			//服务调用
			result = invoke(child == null ? null:child.sn(),child == null ? "1":child.order(),url,params);
			
			//结束调用
			Tool.end(ctx,"HttpCall",method + "@" + id,"OK","");			
		} catch (Exception e) {
			//结束调用
			Tool.end(ctx,"HttpCall",method + "@" + id,"FAILED",e.getMessage());			
			throw e;
		}			
		return result;
	}

	/**
	 * 拼装URL
	 * @param id 服务ID
	 * @param method 服务方法
	 * @return URL
	 */
	protected URL getServiceUrl(String id,String method){
		StringBuffer url = new StringBuffer();
		url.append(address).append("/").append(id).append("?method=").append(method);	
		try {
			return new URL(url.toString());
		} catch (MalformedURLException e) {
			throw new CallException("client.error_url","URL error :" + url,e);
		}
	}	
	
	@Override
	public void configure(Properties p) {
		address = PropertiesConstants.getString(p,"rpc.http.address","");
		
		if (serializer == null){
			String serializerClass = PropertiesConstants.getString(p,"rpc.serializer",KryoSerializer.class.getName());
			Factory<Serializer> factory = new Factory<Serializer>();
			try {
				serializer = factory.newInstance(serializerClass, p);
			}catch (Exception ex){ // NOSONAR
				serializer = new KryoSerializer();
				serializer.configure(p);
			}		
		}
	}
}
