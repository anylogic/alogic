package com.alogic.remote.httpclient.customizer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultClientConnectionReuseStrategy;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import com.alogic.remote.httpclient.HttpClientCustomizer.Abstract;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 缺省实现
 * 
 * @author yyduan
 * @since 1.6.10.6
 */
public class Default extends Abstract{

	@Override
	public HttpClientBuilder customizeHttpClient(HttpClientBuilder builder,Properties p) {
		if (builder != null){
			final long keepAliveTime = PropertiesConstants.getInt(p,"rpc.http.keepAlive.ttl",60000);
			ConnectionKeepAliveStrategy kaStrategy = new DefaultConnectionKeepAliveStrategy() {
			    @Override
			    public long getKeepAliveDuration(final HttpResponse response, final HttpContext context) {
			    	long keepAlive = super.getKeepAliveDuration(response, context);
					if (keepAlive == -1) {
						keepAlive = keepAliveTime;
					}
					return keepAlive;
			    }
			};
			
			List<Header> headers = new ArrayList<Header>();
			boolean keepAliveEnable = PropertiesConstants.getBoolean(p,"rpc.http.keepAlive.enable", true);
			if (keepAliveEnable){
				headers.add(new BasicHeader("Connection",HTTP.CONN_KEEP_ALIVE));
			}else{
				headers.add(new BasicHeader("Connection",HTTP.CONN_CLOSE));
			}
			
			int maxConnPerRoute = PropertiesConstants.getInt(p, "rpc.http.maxConnPerHost", 200);
			int maxConn = PropertiesConstants.getInt(p,"rpc.http.maxConn",2000);
			int ttlOfConn = PropertiesConstants.getInt(p,"rpc.http.keepalive.ttl",60000);
			
			builder.useSystemProperties().
					setMaxConnPerRoute(maxConnPerRoute).
					setConnectionReuseStrategy(DefaultClientConnectionReuseStrategy.INSTANCE).
					setMaxConnTotal(maxConn).
					setDefaultHeaders(headers).
					setConnectionTimeToLive(ttlOfConn,TimeUnit.MILLISECONDS).
					setKeepAliveStrategy(kaStrategy);
		}
		
		return builder;
	}

	@Override
	public RequestConfig.Builder customizeRequestConfig(Builder builder,Properties p) {
		if (builder != null){
			int timeOut = PropertiesConstants.getInt(p, "rpc.http.timeout", 10000);
			builder.setConnectionRequestTimeout(PropertiesConstants.getInt(p, "rpc.http.timeout.request", timeOut))
	                .setConnectTimeout(PropertiesConstants.getInt(p, "rpc.http.timeout.conn", timeOut))
	                .setSocketTimeout(PropertiesConstants.getInt(p, "rpc.http.timeout.socket", timeOut));
		}
		
		return builder;
	}
}