package com.alogic.ac.verifier;

import org.apache.commons.lang3.StringUtils;

import com.alogic.ac.AccessAppKey;
import com.alogic.ac.AccessVerifier;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.code.util.RSAUtil;
import com.logicbus.backend.Context;

/**
 * 基于RSA的签名验证
 * @author yyduan
 * @since 1.6.10.6
 */
public class RSA extends AccessVerifier.Abstract{
	protected String timestampId = "x-alogic-now";
	protected String payloadId = "x-alogic-payload";
	protected String signatureId = "x-alogic-signature";
	
	protected long ttl = 10*1000L;
	
	@Override
	public void configure(Properties p) {
		super.configure(p);
		timestampId = PropertiesConstants.getString(p,"timestampId", timestampId);
		payloadId = PropertiesConstants.getString(p,"payloadId", payloadId);
		signatureId = PropertiesConstants.getString(p,"signatureId", signatureId);
		ttl = PropertiesConstants.getLong(p, "ttl", ttl);
	}
	
	@Override
	public boolean verify(AccessAppKey key, Context ctx) {
		checkTimestamp(key,ctx);

		checkSignature(key,ctx);
		
		return true;
	}
	
	protected void checkSignature(AccessAppKey key, Context ctx) {
		String now = ctx.getRequestHeader(timestampId);
		String payload = ctx.getRequestHeader(payloadId);

		StringBuffer toSign = new StringBuffer();
		
		toSign.append(key.getId()).append("\n");
		toSign.append(now).append("\n");
		toSign.append(ctx.getRequestURI());
		
		if (StringUtils.isNotEmpty(payload)){
			toSign.append("\n").append(payload);
		}
		
		String signature = ctx.getRequestHeader(signatureId);
		
		if (!RSAUtil.verify(toSign.toString(), key.getKeyContent(), signature)){
			throw new BaseException("client.permission_denied",String.format("The signature is not correct.",signature));
		}
	}
	
	protected void checkTimestamp(AccessAppKey key, Context ctx){
		String now = ctx.getRequestHeader(timestampId);
		long timestamp = getLong(now,0);
		if (timestamp <= 0){
			throw new BaseException("client.permission_denied",String.format("Can not find argument %s.",timestampId));
		}
		
		if (Math.abs(System.currentTimeMillis() - timestamp) > ttl){
			throw new BaseException("client.permission_denied",String.format("Timestamp %d is expired.",timestamp));
		}
	}

	private long getLong(String now, long i) {
		try {
			return Long.parseLong(now);
		}catch (NumberFormatException ex){
			return i;
		}
	}
}