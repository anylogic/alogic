package com.anysoft.util.code.coder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anysoft.util.KeyGen;
import com.anysoft.util.code.Coder;


/**
 * SHA1 加密
 * 
 * @author duanyy
 *
 * @since 1.0.13
 */
public class SHA1 implements Coder {
	protected static final Logger LOG = LoggerFactory.getLogger(SHA1.class);
	public String getAlgorithm() {
		return "sha-1";
	}
	
	public String encode(String data, String key) {
		try {
			MessageDigest m = MessageDigest.getInstance(getAlgorithm());
			String content = data + key;
			m.update(content.getBytes());
			byte result[] = m.digest();
			return new String(Base64.encodeBase64(result));
		} catch (NoSuchAlgorithmException e) {
			LOG.error(ExceptionUtils.getStackTrace(e));
			return data;
		}
	}

	
	public String decode(String data, String key) {
		return data;
	}

	
	public String createKey() {
		return KeyGen.getKey(8);
	}

	@Override
	public String createKey(String init){
		return init;
	}
}