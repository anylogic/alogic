package com.logicbus.redis.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.logicbus.redis.util.RedisConnectException;
import com.logicbus.redis.util.RedisDataException;
import com.logicbus.redis.util.RedisException;
import com.logicbus.redis.util.SafeEncoder;

/**
 * Redis的输入输出协议
 * 
 * @author duanyy
 * 
 */
public final class Protocol {
	public static final int DEFAULT_PORT = 6379;
	public static final int DEFAULT_SENTINEL_PORT = 26379;
	public static final int DEFAULT_TIMEOUT = 2000;
	public static final int DEFAULT_DATABASE = 0;

	public static final byte DOLLAR_BYTE = '$';
	public static final byte ASTERISK_BYTE = '*';
	public static final byte PLUS_BYTE = '+';
	public static final byte MINUS_BYTE = '-';
	public static final byte COLON_BYTE = ':';

	public static void sendCommand(final RedisOutputStream os,
			final byte[] command, final byte[]... args) {
		try {
			os.write(ASTERISK_BYTE);
			os.writeIntCrLf(args.length + 1);
			os.write(DOLLAR_BYTE);
			os.writeIntCrLf(command.length);
			os.write(command);
			os.writeCrLf();

			for (final byte[] arg : args) {
				os.write(DOLLAR_BYTE);
				os.writeIntCrLf(arg.length);
				os.write(arg);
				os.writeCrLf();
			}
		} catch (IOException e) {
			throw new RedisConnectException("core.e1004",
					"IO Exception when connecting.", e);
		}
	}

	private static void processError(final RedisInputStream is) {
		String message = is.readLine();
		throw new RedisDataException("core.e1703",message);
	}

	private static Object process(final RedisInputStream is) {
		try {
			byte b = is.readByte();
			if (b == MINUS_BYTE) {
				processError(is);
			} else if (b == ASTERISK_BYTE) {
				return processMultiBulkReply(is);
			} else if (b == COLON_BYTE) {
				return processInteger(is);
			} else if (b == DOLLAR_BYTE) {
				return processBulkReply(is);
			} else if (b == PLUS_BYTE) {
				return processStatusCodeReply(is);
			} else {
				throw new RedisConnectException("core.e1700", "Unknown reply: "+ (char) b);
			}
		} catch (IOException e) {
			throw new RedisConnectException("core.e1004",
					"IO Exception when connecting.", e);
		}
		return null;
	}

	private static byte[] processStatusCodeReply(final RedisInputStream is) {
		return SafeEncoder.encode(is.readLine());
	}

	private static byte[] processBulkReply(final RedisInputStream is) {
		int len = Integer.parseInt(is.readLine());
		if (len == -1) {
			return null;
		}
		byte[] read = new byte[len];
		int offset = 0;
		try {
			while (offset < len) {
				int size = is.read(read, offset, (len - offset));
				if (size == -1)
					throw new RedisConnectException("core.e1701",
							"It seems like server has closed the connection.");
				offset += size;
			}
			is.readByte();
			is.readByte();
		} catch (IOException e) {
			throw new RedisConnectException("core.e1004",
					"IO Exception when connecting.", e);
		}

		return read;
	}

	private static Long processInteger(final RedisInputStream is) {
		String num = is.readLine();
		return Long.valueOf(num);
	}

	private static List<Object> processMultiBulkReply(final RedisInputStream is) {
		int num = Integer.parseInt(is.readLine());
		if (num == -1) {
			return null;
		}
		List<Object> ret = new ArrayList<Object>(num);
		for (int i = 0; i < num; i++) {
			try {
				ret.add(process(is));
			} catch (RedisException e) {
				ret.add(e);
			}
		}
		return ret;
	}

	public static Object read(final RedisInputStream is) {
		return process(is);
	}
}
