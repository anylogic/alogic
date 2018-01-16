package com.alogic.ac.verifier;

import org.apache.commons.lang3.StringUtils;

import com.alogic.ac.AccessAppKey;
import com.alogic.ac.AccessVerifier;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.code.Coder;
import com.anysoft.util.code.CoderFactory;
import com.logicbus.backend.Context;

/**
 * 可授权签名模式
 * <p>
 * 在Signature基础上按照可授权签名的模式扩展
 * 
 * @author yyduan
 * @since 1.6.11.10
 */
public class AuthorizedSignature extends AccessVerifier.Abstract{
	protected String timestampId = "x-alogic-now";
	protected String payloadId = "x-alogic-payload";
	protected String signatureId = "x-alogic-signature";
	
	protected long ttl = 10*1000L;
	protected Coder coder = null;
	protected long cycle = 24 * 60 * 60 * 1000L;
	
	@Override
	public void configure(Properties p) {
		super.configure(p);
		
		timestampId = PropertiesConstants.getString(p,"timestampId", timestampId);
		payloadId = PropertiesConstants.getString(p,"payloadId", payloadId);
		signatureId = PropertiesConstants.getString(p,"signatureId", signatureId);
		ttl = PropertiesConstants.getLong(p, "ttl", ttl);
		cycle = PropertiesConstants.getLong(p, "cycle", cycle);
		
		coder = CoderFactory.newCoder(PropertiesConstants.getString(p,"coder", "HmacSHA256"));
	}
	
	@Override
	public boolean verify(AccessAppKey key, Context ctx) {
		checkTimestamp(key,ctx);

		checkSignature(key,ctx);
		
		return true;
	}
	
	protected void checkSignature(AccessAppKey key, Context ctx){
		if (coder != null){
			String now = ctx.getRequestHeader(timestampId);
			String payload = ctx.getRequestHeader(payloadId);
			
			String uri = ctx.getRequestURI();
			String queryString = ctx.getQueryString();
			if (StringUtils.isNotEmpty(queryString)){
				uri += "?" + queryString;
			}
			
			StringBuffer toSign = new StringBuffer();
			toSign.append(key.getId()).append("\n");
			toSign.append(now).append("\n");
			toSign.append(uri);
			
			if (StringUtils.isNotEmpty(payload)){
				toSign.append("\n").append(payload);
			}
		
			String tmpKey = getAuthorizedKey(key,now);
			String signed = coder.encode(toSign.toString(), tmpKey);			
			String signature = ctx.getRequestHeader(signatureId);
			if (!signed.equals(signature)){
				throw new BaseException("clnt.e2005",String.format("The signature is not correct.",signature));
			}
		}
	}
	
	protected String getAuthorizedKey(AccessAppKey key,String now){
		try {
			long t = Long.parseLong(now) / cycle;
			return coder.encode(String.format("%s:%d", key.getId(),t), key.getKeyContent());
		}catch (NumberFormatException ex){
			throw new BaseException("clnt.e2009",
					String.format("Parameter %s is not a long value,current:%s", timestampId,now));
		}
	}
	
	protected void checkTimestamp(AccessAppKey key, Context ctx){
		String now = ctx.getRequestHeader(timestampId);
		long timestamp = getLong(now,0);
		if (timestamp <= 0){
			throw new BaseException("clnt.e2000",String.format("Can not find argument %s.",timestampId));
		}
		
		if (Math.abs(System.currentTimeMillis() - timestamp) > ttl){
			throw new BaseException("clnt.e2006",String.format("Timestamp %d is expired.",timestamp));
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
