package com.alogic.remote.httpclient.customizer;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.impl.client.HttpClientBuilder;

import com.alogic.remote.httpclient.HttpClientCustomizer.Abstract;
import com.anysoft.util.Properties;

/**
 * 忽略客户端的ssl验证
 * 
 * @author yyduan
 * @since 1.6.10.6
 */
public class IgnoreSSL extends Abstract {

	@Override
	public HttpClientBuilder customizeHttpClient(HttpClientBuilder builder,
			Properties p) {
		try {
			SSLContext sc = SSLContext.getInstance("SSLv3");
			X509TrustManager trustManager = new X509TrustManager() {  
		        @Override  
		        public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate,String paramString) 
		        		throws CertificateException {  
		        }  
		  
		        @Override  
		        public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate,String paramString) 
		        		throws CertificateException {  
		        }  
		  
		        @Override  
		        public java.security.cert.X509Certificate[] getAcceptedIssuers() {  
		            return null;  
		        }  
		    }; 
		    
		    sc.init(null, new TrustManager[] { trustManager }, null);
		    
		    if (builder != null){
		    	builder.setSSLHostnameVerifier(NoneHostNameVerifier.INSTANCE);
		    	builder.setSSLContext(sc);
		    }
		} catch (NoSuchAlgorithmException e) {
			LOG.error("Can not create SSLContext instance.");
			LOG.error(ExceptionUtils.getStackTrace(e));
		} catch (KeyManagementException e) {
			LOG.error("Can not create init SSLContext.");
			LOG.error(ExceptionUtils.getStackTrace(e));
		} 
		return builder;
	}

	@Override
	public Builder customizeRequestConfig(Builder builder, Properties p) {
		return builder;
	}

	/**
	 * 无验证模式
	 * @author yyduan
	 *
	 */
	public static class NoneHostNameVerifier implements HostnameVerifier{
		public static final HostnameVerifier INSTANCE = new NoneHostNameVerifier();
		
		@Override
		public boolean verify(final String s, final SSLSession sslSession) {
			return true;
		}
		
	}
}
