package com.alogic.remote.httpclient.customizer;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContexts;

import com.alogic.remote.httpclient.HttpClientCustomizer.Abstract;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.resource.ResourceFactory;

/**
 * 定制ssl选项
 * @author yyduan
 * @since 1.6.10.6
 */
public class SSL extends Abstract {

	@Override
	public HttpClientBuilder customizeHttpClient(HttpClientBuilder builder,
			Properties p) {
		String ksType = PropertiesConstants.getString(p,"rpc.http.ssl.ks.type",KeyStore.getDefaultType());
		String ksPath = PropertiesConstants.getString(p,"rpc.http.ssl.ks.path","${ketty.home}/keystore/alogic.keystore");
		String ksPasswd = PropertiesConstants.getString(p,"rpc.http.ssl.ks.passwd","nopassword");
		boolean verifyHostName = PropertiesConstants.getBoolean(p, "rpc.http.ssl.ks.hostname.verify", false);
		ResourceFactory rf = Settings.getResourceFactory();
		InputStream in = null;
		try {
			in = rf.load(ksPath, null);
			KeyStore ks = KeyStore.getInstance(ksType);
			ks.load(in, ksPasswd.toCharArray());
			SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(ks, new TrustSelfSignedStrategy()).build();
			if (builder != null){
				if (!verifyHostName){
					builder.setSSLHostnameVerifier(NoneHostNameVerifier.INSTANCE);
				}
				builder.setSSLContext(sslContext);
			}
		}  catch (KeyStoreException e) {
			LOG.error("Can not create key store,type=" + ksType);
			LOG.error(ExceptionUtils.getStackTrace(e));
		}  catch (Exception e){
			LOG.error("Can not load key store,path=" + ksPath);
			LOG.error(ExceptionUtils.getStackTrace(e));
		}finally{
			IOTools.close(in);
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
