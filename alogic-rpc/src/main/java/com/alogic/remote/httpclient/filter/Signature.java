package com.alogic.remote.httpclient.filter;

import org.apache.commons.lang3.StringUtils;

import com.alogic.remote.httpclient.HttpCientFilter;
import com.alogic.remote.httpclient.HttpClientRequest;
import com.alogic.remote.httpclient.HttpClientResponse;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.code.Coder;
import com.anysoft.util.code.CoderFactory;

/**
 * 签名验证
 * 
 * @author yyduan
 * @since 1.6.10.6
 */
public class Signature extends HttpCientFilter.Abstract{
	/**
	 * 应用id
	 */
	protected String key;
	
	/**
	 * 密钥
	 */
	protected String keyContent;
	
	protected String timestampId = "x-alogic-now";
	protected String payloadId = "x-alogic-payload";
	protected String signatureId = "x-alogic-signature";
	protected String keyId = "x-alogic-app";
	
	protected Coder coder = null;

	@Override
	public void onRequest(HttpClientRequest request) {
		String now = String.valueOf(System.currentTimeMillis());
		
		String payload = request.getHeader(payloadId, "");
		String uri = request.getURI();
		
		StringBuffer toSign = new StringBuffer();
		
		toSign.append(key).append("\n");
		toSign.append(now).append("\n");
		toSign.append(uri);
		if (StringUtils.isNotEmpty(payload)){
			toSign.append("\n").append(payload);
		}
		String signature = coder.encode(toSign.toString(), keyContent);
		
		request.setHeader(signatureId, signature);
		request.setHeader(timestampId, now);
		request.setHeader(keyId, key);
	}

	@Override
	public void onResponse(HttpClientResponse response) {
		
	} 
	
	@Override
	public void configure(Properties p) {
		super.configure(p);
		
		key = PropertiesConstants.getString(p,"key","");
		keyContent = PropertiesConstants.getString(p,"content","");
		
		timestampId = PropertiesConstants.getString(p,"timestampId", timestampId);		
		keyId = PropertiesConstants.getString(p,"keyId",keyId);
		payloadId = PropertiesConstants.getString(p,"payloadId", payloadId);
		signatureId = PropertiesConstants.getString(p,"signatureId", signatureId);
		
		coder = CoderFactory.newCoder(PropertiesConstants.getString(p,"coder", "HmacSHA256"));
	}
}
