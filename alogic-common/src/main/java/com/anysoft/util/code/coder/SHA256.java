package com.anysoft.util.code.coder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

import com.anysoft.util.KeyGen;
import com.anysoft.util.code.Coder;

/**
 * SHA256
 * 
 * @author yyduan
 *
 * @since 1.6.10.5
 * 
 */
public class SHA256 implements Coder {
	public String getAlgorithm() {
		return "sha-256";
	}
	
	@Override
	public String encode(String data, String key) {
		try {
			MessageDigest m = MessageDigest.getInstance(getAlgorithm());
			String content = data + key;
			m.update(content.getBytes());
			byte result[] = m.digest();
			return Base64.encodeBase64URLSafeString(result);
		} catch (NoSuchAlgorithmException e) {
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
	public String createKey(String init){
		return init;
	}
}
