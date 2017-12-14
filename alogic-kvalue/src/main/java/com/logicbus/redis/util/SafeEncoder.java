package com.logicbus.redis.util;

import java.io.UnsupportedEncodingException;


/**
 * 编码/解码工具类
 * 
 * @author duanyy
 *
 */
public class SafeEncoder {

	public static byte[] encode(final String str) {
		try {
			if (str == null) {
				throw new RedisException("null",
						"value sent to redis cannot be null");
			}
			return str.getBytes(ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new RedisException("core.e1005", "unsupported encoding", e);
		}
	}
	
	public static byte[] encode(final boolean value) {
		return encode(value ? 1 : 0);
	}

	public static byte[] encode(final int value) {
		return SafeEncoder.encode(String.valueOf(value));
	}

	public static byte[] encode(final long value) {
		return SafeEncoder.encode(String.valueOf(value));
	}

	public static byte[] encode(final double value) {
		return SafeEncoder.encode(String.valueOf(value));
	}
	
	public static String encode(final byte[] data) {
		try {
			return new String(data, ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new RedisException("core.e1005", "unsupported encoding", e);
		}
	}
	
	public static final String ENCODING = "utf-8";
	
	public static byte[][] encode(final String... strs) {
		byte[][] many = new byte[strs.length][];
		for (int i = 0; i < strs.length; i++) {
			many[i] = encode(strs[i]);
		}
		return many;
	}

}
