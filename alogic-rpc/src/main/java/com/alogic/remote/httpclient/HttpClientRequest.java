package com.alogic.remote.httpclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.AbstractHttpEntity;
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
		Response result = null;
		String lastErrorCode = "core.io_error";
		String lastErrorMsg = "";
		
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
				error = true;
				lastErrorCode = ex.getCode();
				lastErrorMsg = ex.getMessage();
				if (!lastErrorCode.startsWith("core")){
					throw ex;
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

	protected Response execute(String path,Backend backend) {
		String url = client.getInvokeURL(backend, path);
		try {			
			httpRequest.setURI(URI.create(url));
			return new HttpClientResponse(httpClient.execute(httpRequest),encoding);			
		}catch (Exception ex){
			throw new CallException("core.io_error",url, ex);
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
