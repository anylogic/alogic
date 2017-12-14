package com.logicbus.redis.client;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.logicbus.redis.util.RedisConnectException;

/**
 * 实现从Redis服务器输入的输入流
 * 
 * <br>
 * 参考了Jedis的实现。
 * 
 * @author duanyy
 *
 */
public class RedisInputStream extends FilterInputStream {

	protected final byte buf[];

	protected int count, limit;

	public RedisInputStream(InputStream in, int size) {
		super(in);
		buf = new byte[size <= 0 ? 8192 : size];
	}

	public RedisInputStream(InputStream in) {
		this(in, 8192);
	}
	
	public byte readByte() throws IOException {
		if (count == limit) {
			fill();
		}

		return buf[count++];
	}

	public String readLine() {
		int b;
		byte c;
		StringBuilder sb = new StringBuilder();

		try {
			while (true) {
				if (count == limit) {
					fill();
				}
				if (limit == -1)
					break;

				b = buf[count++];
				if (b == '\r') {
					if (count == limit) {
						fill();
					}

					if (limit == -1) {
						sb.append((char) b);
						break;
					}

					c = buf[count++];
					if (c == '\n') {
						break;
					}
					sb.append((char) b);
					sb.append((char) c);
				} else {
					sb.append((char) b);
				}
			}
		} catch (IOException e) {
			throw new RedisConnectException("core.e1004",
					"IO Exception when connecting.", e);
		}
		String reply = sb.toString();
		if (reply.length() == 0) {
			throw new RedisConnectException("core.e1701",
					"It seems like server has closed the connection.");
		}
		return reply;
	}

	
	public int read(byte[] b, int off, int len) throws IOException {
		if (count == limit) {
			fill();
			if (limit == -1)
				return -1;
		}
		final int length = Math.min(limit - count, len);
		System.arraycopy(buf, count, b, off, length);
		count += length;
		return length;
	}
	
	private void fill() throws IOException {
		limit = in.read(buf);
		count = 0;
	}
}
