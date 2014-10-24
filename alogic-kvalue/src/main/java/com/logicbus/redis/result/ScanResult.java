package com.logicbus.redis.result;

import java.util.List;

import com.logicbus.redis.client.Result;
import com.logicbus.redis.util.SafeEncoder;

/**
 * Key扫描结果
 * 
 * @author duanyy
 *
 * @param <T>
 */
public class ScanResult<T> extends Result<T> {
	private byte[] cursor;
	private List<T> results;

	public ScanResult(String cursor, List<T> results) {
		this(SafeEncoder.encode(cursor), results);
	}

	public ScanResult(byte[] cursor, List<T> results) {
		this.cursor = cursor;
		this.results = results;
	}

	public String getCursor() {
		return SafeEncoder.encode(cursor);
	}

	public byte[] getCursorAsBytes() {
		return cursor;
	}

	public List<T> getResult() {
		return results;
	}
}
