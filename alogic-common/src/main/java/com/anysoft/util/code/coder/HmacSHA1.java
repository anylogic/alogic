package com.anysoft.util.code.coder;

import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anysoft.util.BaseException;
import com.anysoft.util.code.Coder;

/**
 * HmacSHA1编码
 * 
 * @author yyduan
 * @since 1.6.10.6
 * 
 */
public class HmacSHA1 implements Coder {
	protected static final Logger LOG = LoggerFactory.getLogger(MD5.class);
	public String getAlgorithm() {
		return "HmacSHA1";
	}
	
	@Override
	public String encode(String data, String key) {
		try {
        	SecretKey secretKey = new SecretKeySpec(key.getBytes(), getAlgorithm());  
            Mac mac = Mac.getInstance(secretKey.getAlgorithm());  
            mac.init(secretKey);  
            byte[] bytes = mac.doFinal(data.getBytes());  
            return Base64.encodeBase64String(bytes);
		}catch (Exception ex){
			LOG.error(ExceptionUtils.getStackTrace(ex));
			return data;
		}
	}

	@Override
	public String decode(String data, String key) {
		return data;
	}

	@Override
	public String createKey() {		
		try {
			KeyGenerator generator = KeyGenerator.getInstance(getAlgorithm());
	        SecretKey key = generator.generateKey();  
	        return Base64.encodeBase64URLSafeString(key.getEncoded());
		} catch (NoSuchAlgorithmException e) {
			throw new BaseException("core.e1000",ExceptionUtils.getStackTrace(e));
		} 
	}
	
	@Override
	public String createKey(String key){
		return createKey();
	}
}
