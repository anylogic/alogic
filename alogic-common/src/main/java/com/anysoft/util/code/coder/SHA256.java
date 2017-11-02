package com.anysoft.util.code.coder;

import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.anysoft.util.BaseException;
import com.anysoft.util.code.Coder;

/**
 * 基于HMAC-SHA256算法
 * 
 * @author yyduan
 *
 * @since 1.6.10.5
 * 
 */
public class SHA256 implements Coder {

	public String getAlgorithm() {
		return "HmacSHA256";
	}
	
	@Override
	public String encode(String data, String key) {
		try {
        	byte [] byteKey = Base64.decodeBase64(key);
        	SecretKey secretKey = new SecretKeySpec(byteKey, getAlgorithm());  
            Mac mac = Mac.getInstance(secretKey.getAlgorithm());  
            mac.init(secretKey);  
            byte[] bytes = mac.doFinal(data.getBytes());  
            return Base64.encodeBase64URLSafeString(bytes);
		}catch (Exception ex){
			throw new BaseException("core.hmac_error",ExceptionUtils.getStackTrace(ex));
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
			throw new BaseException("core.no_such_algorithm",ExceptionUtils.getStackTrace(e));
		} 
	}
	
	public String createKey(String key){
		return createKey();
	}
}
