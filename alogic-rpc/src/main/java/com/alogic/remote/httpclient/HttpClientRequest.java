package com.alogic.remote.httpclient;

import java.io.InputStream;
import java.net.URI;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

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
 */
public class HttpClientRequest implements Request{
	protected HttpRequestBase httpRequest = null;
	protected CloseableHttpClient httpClient = null;
	protected HttpClient client = null;
	protected String encoding = "utf-8";
	
	public HttpClientRequest(CloseableHttpClient httpClient,HttpRequestBase request,HttpClient client,String encoding){
		this.httpClient = httpClient;
		this.httpRequest = request;
		this.client = client;
		this.encoding = encoding;
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
	public Response execute(String path,String key,Properties ctx) {
		int retryCount = 0;
		Response result = null;
		while (true){
			Backend backend = null;
			long start = System.nanoTime();
			boolean error = false;
			try {
				backend = client.getBackend(key,ctx, retryCount ++ );
				
				if (backend != null){
					result = execute(path,backend);
				}
				break;
			}catch (CallException ex){
				System.out.println(ex);
				error = true;
				String code = ex.getCode();
				if (!code.startsWith("core")){
					throw ex;
				}
			}finally{
				if (backend != null){
					backend.count(System.nanoTime() - start, error);
				}
			}
		}
		
		return result;
	}

	protected Response execute(String path,Backend backend) {
		String url = client.getInvokeURL(backend, path);
		try {			
			httpRequest.setURI(URI.create(url));
			return new HttpClientResponse(httpClient.execute(httpRequest),encoding);			
		}catch (Exception ex){
			throw new CallException("core.io_error","Can not connect to remote host : " + url, ex);
		}
	}

	@Override
	public void release() {
		if (httpRequest != null){
			httpRequest.releaseConnection();
		}
	}

}
