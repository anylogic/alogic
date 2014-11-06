package com.logicbus.redis.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import com.logicbus.redis.util.BuilderFactory;
import com.logicbus.redis.util.RedisConnectException;
import com.logicbus.redis.util.RedisDataException;
import com.logicbus.redis.util.SafeEncoder;


/**
 * 到Redis服务端的连接
 * 
 * @author duanyy
 * @version 1.0.0.1 [20141106 duanyy] <br>
 * - 修正设置index或password之后死循环的bug. <br>
 * 
 */
public class Connection implements AutoCloseable {
	/**
	 * host
	 */
	private String host;

	public String getHost() {
		return host;
	}

	public void setHost(final String host) {
		this.host = host;
	}

	/**
	 * port
	 */
	private int port = Protocol.DEFAULT_PORT;

	public int getPort() {
		return port;
	}

	public void setPort(final int port) {
		this.port = port;
	}

	/**
	 * Socket
	 */
	private Socket socket;

	/**
	 * OutputStream
	 */
	private RedisOutputStream outputStream;

	/**
	 * InputStream
	 */
	private RedisInputStream inputStream;

	private boolean broken = false;

	public boolean isBroken() {
		return broken;
	}

	private int timeout = Protocol.DEFAULT_TIMEOUT;

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(final int timeout) {
		this.timeout = timeout;
	}

	public Connection() {
	}

	public Connection(final String host) {
		super();
		this.host = host;
	}

	public Connection(final String host, final int port) {
		super();
		this.host = host;
		this.port = port;
	}

	public void setTimeoutInfinite() {
		try {
			if (!isConnected(false)) {
				connect();
			}
			socket.setKeepAlive(true);
			socket.setSoTimeout(0);
		} catch (SocketException ex) {
			broken = true;
			throw new RedisConnectException("socket",
					"Socket exception when connecting.", ex);
		}
	}

	public void rollbackTimeout() {
		try {
			socket.setSoTimeout(timeout);
			socket.setKeepAlive(false);
		} catch (SocketException ex) {
			broken = true;
			throw new RedisConnectException("socket",
					"Socket exception when connecting.", ex);
		}
	}

	protected void flush() {
		try {
			outputStream.flush();
		} catch (IOException e) {
			broken = true;
			throw new RedisConnectException("ioexception",
					"IO Exception when connecting.", e);
		}
	}

	protected Connection sendCommand(final byte[] cmd, final String... args) {
		final byte[][] bargs = new byte[args.length][];
		for (int i = 0; i < args.length; i++) {
			bargs[i] = SafeEncoder.encode(args[i]);
		}
		return sendCommand(cmd, bargs);
	}

	protected Connection sendCommand(final byte[] cmd, final byte[]... args) {
		try {
			connect();
			Protocol.sendCommand(outputStream, cmd, args);
			return this;
		} catch (RedisConnectException ex) {
			broken = true;
			throw ex;
		}
	}

	protected Connection sendCommand(final byte[] cmd) {
		try {
			connect();
			Protocol.sendCommand(outputStream, cmd, new byte[0][]);
			return this;
		} catch (RedisConnectException ex) {
			broken = true;
			throw ex;
		}
	}

	public void connect() {
		if (!isConnected(true)){
			try {
				socket = new Socket();
				socket.setReuseAddress(true);
				socket.setKeepAlive(true);
				socket.setTcpNoDelay(true);
				socket.setSoLinger(true, 0);
				socket.connect(new InetSocketAddress(host, port), timeout);
				socket.setSoTimeout(timeout);
				outputStream = new RedisOutputStream(socket.getOutputStream());
				inputStream = new RedisInputStream(socket.getInputStream());
			} catch (IOException ex) {
				broken = true;
				throw new RedisConnectException("ioexception",
						"IO Exception when connecting.", ex);
			}
		}
	}

	public void disconnect() {
		if (isConnected(false)) {
			try {
				inputStream.close();
				outputStream.close();
				if (!socket.isClosed()) {
					socket.close();
				}
			} catch (IOException ex) {
				broken = true;
				throw new RedisConnectException("ioexception",
						"IO Exception when connecting.", ex);
			}
		}
	}

	public boolean isConnected(boolean ping) {
		boolean connected = socket != null && socket.isBound() && !socket.isClosed()
				&& socket.isConnected() && !socket.isInputShutdown()
				&& !socket.isOutputShutdown();
		
		if (connected && ping){
			try{
				Protocol.sendCommand(outputStream, SafeEncoder.encode("ECHO"), SafeEncoder.encode("PING"));
				getBulkReply();
				connected = true;
			}catch(Exception ex){
			      connected = false;
			}
		}
		
		return connected;
	}
	
	
	public void close() throws Exception {
		disconnect();
	}
	
	private static final byte [] CMD_AUTH = SafeEncoder.encode("AUTH");
	
	/**
	 * to authenticate to the server
	 * @param pwd password
	 */
	protected void auth(final String pwd){
		Protocol.sendCommand(outputStream, CMD_AUTH, SafeEncoder.encode(pwd));
		getStatusCodeReply();
	}
	
	private static final byte [] CMD_QUIT = SafeEncoder.encode("QUIT");
	
	/**
	 * to ask the server to close the connection
	 */
	protected void quit(){
		try{
			Protocol.sendCommand(outputStream, CMD_QUIT);
			getStatusCodeReply();
		}catch (Throwable t){
			
		}
	}
	
	private static final byte [] CMD_SELECT = SafeEncoder.encode("SELECT");
	
	/**
	 * to change the selected database for the current connection
	 * @param dbIndex
	 */
	public void select(int dbIndex){
		Protocol.sendCommand(outputStream, CMD_SELECT, SafeEncoder.encode(dbIndex));
		getStatusCodeReply();
	}
	
	protected String getStatusCodeReply() {
		flush();
		final byte[] resp = (byte[]) readProtocolWithCheckingBroken();
		if (null == resp) {
			return null;
		} else {
			return SafeEncoder.encode(resp);
		}
	}

	public String getBulkReply() {
		final byte[] result = getBinaryBulkReply();
		if (null != result) {
			return SafeEncoder.encode(result);
		} else {
			return null;
		}
	}

	public byte[] getBinaryBulkReply() {
		flush();
		return (byte[]) readProtocolWithCheckingBroken();
	}

	public Long getIntegerReply() {
		flush();
		return (Long) readProtocolWithCheckingBroken();
	}

	public List<String> getMultiBulkReply() {
		return BuilderFactory.STRING_LIST.build(getBinaryMultiBulkReply());
	}

	@SuppressWarnings("unchecked")
	public List<byte[]> getBinaryMultiBulkReply() {
		flush();
		return (List<byte[]>) readProtocolWithCheckingBroken();
	}

	@SuppressWarnings("unchecked")
	public List<Object> getRawObjectMultiBulkReply() {
		return (List<Object>) readProtocolWithCheckingBroken();
	}

	public List<Object> getObjectMultiBulkReply() {
		flush();
		return getRawObjectMultiBulkReply();
	}

	@SuppressWarnings("unchecked")
	public List<Long> getIntegerMultiBulkReply() {
		flush();
		return (List<Long>) Protocol.read(inputStream);
	}

	public Object getOne() {
		flush();
		return readProtocolWithCheckingBroken();
	}

	protected Object readProtocolWithCheckingBroken() {
		try {
			return Protocol.read(inputStream);
		} catch (RedisConnectException exc) {
			broken = true;
			throw exc;
		}
	}

	public List<Object> getMany(int count) {
		flush();
		List<Object> responses = new ArrayList<Object>();
		for (int i = 0; i < count; i++) {
			try {
				responses.add(readProtocolWithCheckingBroken());
			} catch (RedisDataException e) {
				responses.add(e);
			}
		}
		return responses;
	}


}
