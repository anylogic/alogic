package com.alogic.remote.httpclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URI;

import org.apache.http.Header;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alogic.remote.Request;
import com.alogic.remote.Response;
import com.alogic.remote.backend.Backend;
import com.alogic.rpc.CallException;
import com.anysoft.util.Properties;

/**
 * Request
 * 
 * @author yyduan
 * @since 1.6.8.12
 * 
 * @version 1.6.8.14 <br>
 * - 优化http远程调用的超时机制 <br>
 * 
 * @version 1.6.8.15 [20170511 duanyy] <br>
 * - 增加绝对路径调用功能 <br>
 * 
 * @version 1.6.10.1 [20170910 duanyy] <br>
 * - 修正httpclient连接的“failed to respond”异常;
 * 
 * @version 1.6.10.6 [20171114 duanyy] <br>
 * - 增加Filter的事件触发 <br>
 * 
 * @version 1.6.10.9 [20171124 duanyy] <br>
 * - 增加{@link #getPathInfo()}和{@link #getQueryInfo()}方法 <br>
 * 
 * @version 1.6.11.14 [duanyy 20180129] <br>
 * - 修正QueryInfo和服务器取法不一致的问题 <br>
 */
public class HttpClientRequest implements Request{
	protected static final Logger LOG = LoggerFactory.getLogger(HttpClientRequest.class);
	protected HttpRequestBase httpRequest = null;
	protected CloseableHttpClient httpClient = null;
	protected HttpClient client = null;
	protected String encoding = "utf-8";
	protected int autoRetryCnt = 3;
	
	public HttpClientRequest(CloseableHttpClient httpClient,HttpRequestBase request,HttpClient client,String encoding,int autoRetryCnt){
		this.httpClient = httpClient;
		this.httpRequest = request;
		this.client = client;
		this.encoding = encoding;
		this.autoRetryCnt = autoRetryCnt;
	}
	
	/**
	 * 获取当前的URI
	 * @return URI
	 */
	public String getURI(){
		URI uri = this.httpRequest != null ? this.httpRequest.getURI() : null;
		return uri != null ? uri.toString() : "";
	}
	
	public String getPathInfo(){
		URI uri = this.httpRequest != null ? this.httpRequest.getURI() : null;
		return uri != null ? uri.getPath() : "";
	}
	
	public String getQueryInfo(){
		URI uri = this.httpRequest != null ? this.httpRequest.getURI() : null;
		return uri != null ? uri.getRawQuery() : "";
	}
	
	/**
	 * 获取指定的header值
	 * @param name header名称
	 * @param dft 缺省值
	 * @return header值
	 */
	public String getHeader(String name,String dft){
		Header header = this.httpRequest != null ? this.httpRequest.getFirstHeader(name) : null;
		return header == null ? dft: header.getValue();
	}
	
	@Override
	public Request setHeader(String name, String value) {
		if (this.httpRequest != null){
			this.httpRequest.setHeader(name, value);
		}
		return this;
	}

	@Override
	public Request setBody(String text) {
		if (httpRequest != null && httpRequest instanceof HttpEntityEnclosingRequestBase){
			HttpEntityEnclosingRequestBase entityRequest = (HttpEntityEnclosingRequestBase) httpRequest;			
			entityRequest.setEntity(new StringEntity(text,encoding));
		}
		return this;
	}

	@Override
	public Request setBody(byte[] body) {
		if (httpRequest != null && httpRequest instanceof HttpEntityEnclosingRequestBase){
			HttpEntityEnclosingRequestBase entityRequest = (HttpEntityEnclosingRequestBase) httpRequest;			
			entityRequest.setEntity(new ByteArrayEntity(body));
		}
		
		return this;
	}

	@Override
	public Request setBody(InputStream in) {
		if (httpRequest != null && httpRequest instanceof HttpEntityEnclosingRequestBase){
			HttpEntityEnclosingRequestBase entityRequest = (HttpEntityEnclosingRequestBase) httpRequest;			
			entityRequest.setEntity(new InputStreamEntity(in));
		}
		
		return this;
	}
	
	@Override
	public Request setBody(DirectOutput out){
		if (httpRequest != null && httpRequest instanceof HttpEntityEnclosingRequestBase){
			HttpEntityEnclosingRequestBase entityRequest = (HttpEntityEnclosingRequestBase) httpRequest;			
			entityRequest.setEntity(new DirectOutputEntity(out));
		}
		
		return this;
	}
	
	@Override
	public Response execute(String path,String key,Properties ctx) {
		int retryCount = 0;
		int autoRetry = 0;
		Response result = null;
		String lastErrorCode = "core.io_error";
		String lastErrorMsg = "";
		
		while (true){
			Backend backend = null;
			long start = System.nanoTime();
			boolean error = false;
			try {
				backend = client.getBackend(key,ctx, autoRetry > 0 ? 0 : retryCount ++ );				
				if (backend != null){
					result = execute(path,backend);
				}
				break;
			}catch (CallException ex){
				error = true;
				lastErrorCode = ex.getCode();
				lastErrorMsg = ex.getMessage();
				if (lastErrorCode.equals("core.e1602") 
						|| lastErrorCode.equals("core.e1603")
						|| lastErrorCode.equals("core.e1604")){
					//对于internal错误，属于连接错误，可以重试
					autoRetry = autoRetry >= autoRetryCnt ? 0 : autoRetry + 1;
					if (autoRetry > 0){
						LOG.error("Internal error occurs,Retry " + autoRetry);
					}
				}else{
					if (!lastErrorCode.startsWith("core")){
						throw ex;
					}
				}
			}finally{
				if (backend != null){
					backend.count(System.nanoTime() - start, error);
				}
			}
		}
		
		if (result == null){
			throw new CallException(lastErrorCode,lastErrorMsg);
		}
		return result;
	}
	

	@Override
	public Response execute(String fullPath) {
		String url = fullPath;
		try {			
			httpRequest.setURI(URI.create(url));
			
			//request事件
			client.onRequest(this);
			HttpClientResponse response =  new HttpClientResponse(httpClient.execute(httpRequest),encoding);			
			//response事件
			client.onResponse(response);
			return response;
		}catch (SocketTimeoutException ex){
			throw new CallException("core.e1601",url, ex);
		}catch (ConnectTimeoutException ex){
			throw new CallException("core.e1602",url, ex);
		}catch (ConnectException ex){
			throw new CallException("core.e1603",url, ex);
		}catch (NoHttpResponseException ex){
			throw new CallException("core.e1604",url, ex);
		}catch (Exception ex){
			throw new CallException("core.e1004",url, ex);
		}
	}	

	protected Response execute(String path,Backend backend) {
		String url = client.getInvokeURL(backend, path);
		try {		
			httpRequest.setURI(URI.create(url));
			//request事件
			client.onRequest(this);
			HttpClientResponse response = new HttpClientResponse(httpClient.execute(httpRequest),encoding);	
			//response事件
			client.onResponse(response);			
			return response;
		}catch (SocketTimeoutException ex){
			throw new CallException("core.e1601",url, ex);
		}catch (ConnectTimeoutException ex){
			throw new CallException("core.e1602",url, ex);
		}catch (ConnectException ex){
			throw new CallException("core.e1603",url, ex);
		}catch (NoHttpResponseException ex){
			throw new CallException("core.e1604",url, ex);
		}catch (Exception ex){
			throw new CallException("core.e1004",url, ex);
		}
	}

	@Override
	public void close() {
		if (httpRequest != null){
			httpRequest.releaseConnection();
		}
	}

	public static class DirectOutputEntity extends AbstractHttpEntity{
		protected DirectOutput out = null;
		
		public DirectOutputEntity(DirectOutput out){
			this.out = out;
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
			throw new UnsupportedOperationException();
		}

		@Override
		public void writeTo(OutputStream outstream) throws IOException {
			if (this.out != null){
				this.out.writeTo(outstream);
			}
		}

		@Override
		public boolean isStreaming() {
			return false;
		}
		
	}

}
