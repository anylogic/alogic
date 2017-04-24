package com.alogic.remote.httpclient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;

import com.alogic.remote.Response;

/**
 * Response
 * 
 * @author yyduan
 * @since 1.6.8.12
 */
public class HttpClientResponse implements Response{
	protected CloseableHttpResponse httpResponse = null;
	protected String encoding = "utf-8";
	
	public HttpClientResponse(CloseableHttpResponse response,String encoding){
		this.httpResponse = response;
		this.encoding = encoding;
	}
	
	@Override
	public String getHeader(String name, String dft) {
		String value = dft;
		
		if (this.httpResponse != null){
			Header header = this.httpResponse.getFirstHeader(name);
			if (header != null){
				value = header.getValue();
			}
		}
		
		return value;
	}

	@Override
	public int getStatusCode() {
		return this.httpResponse == null ? -1 : this.httpResponse.getStatusLine().getStatusCode();
	}

	@Override
	public String getReasonPhrase() {
		return this.httpResponse == null ? "" : this.httpResponse.getStatusLine().getReasonPhrase();
	}

	@Override
	public String asString() throws IOException {
		byte [] bytes = asBytes();		
		return new String(bytes,getEncoding());
	}

	@Override
	public byte[] asBytes() throws IOException {
		InputStream in = asStream();
		if (in != null){
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			byte[] buffer = new byte[10240];
			
			int read = 0;
			
			while ((read = in.read(buffer,0,buffer.length)) != -1){
				out.write(buffer, 0, read);
			}
			
			return out.toByteArray();
		}
		
		return new byte[0];
	}

	@Override
	public InputStream asStream() throws IOException {
		if (this.httpResponse != null){
			HttpEntity entity = this.httpResponse.getEntity();
			if (entity != null){
				return entity.getContent();
			}
		}
		return null;
	}
	
	protected String getEncoding(){
		if (this.httpResponse != null){
			HttpEntity entity = this.httpResponse.getEntity();
			if (entity != null){
				Header contentEncoding =  entity.getContentEncoding();
				if (contentEncoding != null){
					return contentEncoding.getValue();
				}
			}
		}
		return this.encoding;		
	}

}
