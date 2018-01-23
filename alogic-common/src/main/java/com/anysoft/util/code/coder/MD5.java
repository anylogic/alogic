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
 * MD5 加密
 * 
 * @author duanyy
 *
 * @since 1.0.13
 */
public class MD5 implements Coder {
	protected static final Logger LOG = LoggerFactory.getLogger(MD5.class);
	public String getAlgorithm() {
		return "md5";
	}
	@Override
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

	@Override
	public String decode(String data, String key) {
		return data;
	}

	@Override
	public String createKey() {
		return KeyGen.getKey(8);
	}
	
	@Override
	public String createKey(String key){
		return key;
	}
}
