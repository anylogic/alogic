package com.alogic.rpc.call.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
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
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 基于apache http client的Call
 * 
 * @author yyduan
 * @since 1.6.7.15
 */
public class HttpClientCall extends Call.Abstract {
	
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
	
	/**
	 * HttpClient
	 */
	protected CloseableHttpClient httpClient;
	
	protected RequestConfig requestConfig = null;
	
	public HttpClientCall(){
		
	}
	
	protected Result invoke(String sn, String order, String url,Parameters params) {
		HttpPost httppost = new HttpPost(url);
		try {
			httppost.setConfig(requestConfig);
			if (params != null) {
				params.sn(sn);
				params.order(order);
				httppost.setHeader("GlobalSerial", sn);
				httppost.setHeader("GlobalSerialOrder", order);
			}

			if (StringUtils.isNotEmpty(cookies)) {
				httppost.setHeader("Cookie", cookies);
			}

			SerializerEntity entity = new SerializerEntity(serializer, params);
			entity.setContentEncoding(defaultEncoding);
			entity.setContentType(defaultContentType);

			httppost.setEntity(entity);

			CloseableHttpResponse httpResponse = httpClient.execute(httppost);

			StatusLine ret = httpResponse.getStatusLine();
			if (ret.getStatusCode() != HttpURLConnection.HTTP_OK) {
				throw new CallException("core.invoke_error",
						"Error occurs when invoking service :"
								+ ret.getReasonPhrase());
			}

			Header newCookies = httpResponse.getFirstHeader("Set-Cookie");
			if (newCookies != null) {
				cookies = newCookies.getValue();
			}

			HttpEntity result = httpResponse.getEntity();
			InputStream in = result.getContent();
			if (in != null){
				return serializer.readObject(in, Result.Default.class);
			}else{
				throw new CallException("core.invoke_error","the inputstream from server is null");				
			}
		} catch (IOException ex) {
			throw new CallException("core.io_error",
					"Can not connect to remote host : " + url, ex);
		} finally {
			httppost.releaseConnection();
		}
	}
	
	@Override
	public Result invoke(String id,String method, Parameters params) {
		// 处理context
		if (!filters.isEmpty()) {
			InvokeContext ctx = new InvokeContext.Default();
			for (InvokeFilter f : filters) {
				if (f != null) {
					f.doFilter(ctx);
				}
			}

			if (!ctx.isEmpty()) {
				params.context(ctx);
			}
		}		
		Result result = null;
		//开始调用			
		TraceContext ctx = Tool.start();		
		try {	
			TraceContext child = ctx == null ? null:ctx.newChild();
			//按照路由生成路径
			String url = getServiceUrl(id,method);

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
	protected String getServiceUrl(String id,String method){
		StringBuffer url = new StringBuffer();
		url.append(address).append("/").append(id).append("?method=").append(method);	
		return url.toString();
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
		
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setDefaultMaxPerRoute(PropertiesConstants.getInt(p, "rpc.http.maxConnPerHost", 200));
		cm.setMaxTotal(PropertiesConstants.getInt(p,"rpc.http.maxConn",2000));
				
		httpClient = HttpClients.custom().setConnectionManager(cm).build();
		
		int timeOut = PropertiesConstants.getInt(p, "rpc.http.timeout", 10000);
		requestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeOut)
                .setConnectTimeout(timeOut).setSocketTimeout(timeOut).build();
	}
	
	public static class SerializerEntity extends AbstractHttpEntity{
		protected Serializer serializer = null;
		protected Object data = null; 
		public SerializerEntity(Serializer s,Object d){
			serializer = s;
			data = d;
		}
		
		@Override
		public boolean isRepeatable() {
			return true;
		}

		@Override
		public long getContentLength() {
			// i don't know.
			return -1;
		}

		@Override
		public InputStream getContent() throws IOException,
				UnsupportedOperationException {
			ByteArrayOutputStream baos = new   ByteArrayOutputStream();
			serializer.writeObject(baos, data);
			baos.flush();			
			return new ByteArrayInputStream(baos.toByteArray());
		}

		@Override
		public void writeTo(OutputStream outstream) throws IOException {
			serializer.writeObject(outstream, data);
			outstream.flush();
		}

		@Override
		public boolean isStreaming() {
			return false;
		}
		
	}
}
