package com.alogic.remote.httpclient;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import com.alogic.remote.AbstractClient;
import com.alogic.remote.Request;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 基于apache http client实现的Client
 * 
 * @author yyduan
 * @since 1.6.8.12
 */
public class HttpClient extends AbstractClient{
	/**
	 * http client
	 */
	protected CloseableHttpClient httpClient = null;
	
	/**
	 * request configuration
	 */
	protected RequestConfig requestConfig = null;
	
	protected String encoding = "utf-8";
	
	@Override
	public void configure(Properties p) {
		super.configure(p);
		
		encoding = PropertiesConstants.getString(p,"http.encoding",encoding);
		
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		
		cm.setDefaultMaxPerRoute(PropertiesConstants.getInt(p, "rpc.http.maxConnPerHost", 200));
		cm.setMaxTotal(PropertiesConstants.getInt(p,"rpc.http.maxConn",2000));
				
		httpClient = HttpClients.custom().setConnectionManager(cm).build();
		
		int timeOut = PropertiesConstants.getInt(p, "rpc.http.timeout", 10000);
		requestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeOut)
                .setConnectTimeout(timeOut).setSocketTimeout(timeOut).build();
	}
	
	@Override
	public Request build(String method) {
		return new HttpClientRequest(httpClient,new HttpPost(),this,encoding);
	}
}
