package com.alogic.remote.httpclient.filter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.alogic.remote.httpclient.HttpCientFilter;
import com.alogic.remote.httpclient.HttpClientRequest;
import com.alogic.remote.httpclient.HttpClientResponse;
import com.alogic.sda.SDAFactory;
import com.alogic.sda.SecretDataArea;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.code.Coder;
import com.anysoft.util.code.CoderFactory;

/**
 * 签名验证
 * 
 * @author yyduan
 * @since 1.6.10.6
 * 
 * @version 1.6.10.8 [20171122 duanyy] <br>
 * - 支持实时从SDA获取信息 <br>
 * 
 * @version 1.6.10.9 [20171124 duanyy] <br>
 * - Signature和RSA验证的签名文本中的URL更改为URI; <br>
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
	protected String sdaId = "";
	
	protected String timestampId = "x-alogic-now";
	protected String payloadId = "x-alogic-payload";
	protected String signatureId = "x-alogic-signature";
	protected String keyId = "x-alogic-app";
	
	protected Coder coder = null;

	@Override
	public void onRequest(HttpClientRequest request) {
		SecretDataArea sda = null;
		if (StringUtils.isNotEmpty(sdaId)){
			//从sda中装入信息
			try {
				sda = SDAFactory.getDefault().load(sdaId, true);
			}catch (Exception ex){
				LOG.error("Can not find sda : " + sdaId);
				LOG.error(ExceptionUtils.getStackTrace(ex));
			}
		}
		
		if (sda != null){
			String theKey = sda.getField("http.key.id", key);
			String secretKey = sda.getField("http.key.content", keyContent);
			onRequest(request,theKey,secretKey);
		}else{
			onRequest(request,key,keyContent);
		}
	}

	protected void onRequest(HttpClientRequest request,String theKeyId,String secretKey){
		String now = String.valueOf(System.currentTimeMillis());
		
		String payload = request.getHeader(payloadId, "");
		String uriPath = request.getPathInfo();
		String queryInfo = request.getQueryInfo();
		if (StringUtils.isNotEmpty(queryInfo)){
			uriPath += "?" + queryInfo;
		}
		
		StringBuffer toSign = new StringBuffer();
		
		toSign.append(theKeyId).append("\n");
		toSign.append(now).append("\n");
		toSign.append(uriPath);
		if (StringUtils.isNotEmpty(payload)){
			toSign.append("\n").append(payload);
		}
		String signature = coder.encode(toSign.toString(), secretKey);
		
		request.setHeader(signatureId, signature);
		request.setHeader(timestampId, now);
		request.setHeader(keyId, theKeyId);
	}
	
	@Override
	public void onResponse(HttpClientResponse response) {
		
	} 
	
	@Override
	public void configure(Properties p) {
		super.configure(p);
		
		key = PropertiesConstants.getString(p,"key","");
		keyContent = PropertiesConstants.getString(p,"keyContent","");
		
		timestampId = PropertiesConstants.getString(p,"timestampId", timestampId);		
		keyId = PropertiesConstants.getString(p,"keyId",keyId);
		payloadId = PropertiesConstants.getString(p,"payloadId", payloadId);
		signatureId = PropertiesConstants.getString(p,"signatureId", signatureId);
		sdaId = PropertiesConstants.getString(p,"sda",sdaId);
		coder = CoderFactory.newCoder(PropertiesConstants.getString(p,"coder", "HmacSHA256"));
	}
}
