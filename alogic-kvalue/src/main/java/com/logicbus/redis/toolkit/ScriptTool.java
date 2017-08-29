package com.logicbus.redis.toolkit;

import java.util.List;

import com.logicbus.redis.client.Connection;
import com.logicbus.redis.client.Toolkit;
import com.logicbus.redis.util.SafeEncoder;

/**
 * 脚本工具
 * @author yyduan
 * 
 * @since 1.6.9.9
 * @version 1.6.9.9 [20170829 duanyy] <br>
 * - 增加redis的script指令; <br>
 * 
 */
public class ScriptTool extends Toolkit {

	public ScriptTool(Connection conn) {
		super(conn);
	}

	public static enum Command {
		//常用
		EVAL,
		EVALSHA,
		SCRIPT;
		
		public final byte [] raw;		
		Command(){
			raw = SafeEncoder.encode(name());
		}
	}
	
	public void eval(final String script,final int keys,String...params){
		final byte[][] bargs = new byte[params.length + 2][];
		bargs[0] = SafeEncoder.encode(script);
		bargs[1] = SafeEncoder.encode(keys);
		for (int i = 2; i < params.length + 2; i++) {
			bargs[i] = SafeEncoder.encode(params[i-2]);
		}
		sendCommand(Command.EVAL.raw,bargs);
	}
	
	public void evalsha(final String sha1,final int keys,String...params){
		final byte[][] bargs = new byte[params.length + 2][];
		bargs[0] = SafeEncoder.encode(sha1);
		bargs[1] = SafeEncoder.encode(keys);
		for (int i = 1; i < params.length + 1; i++) {
			bargs[i] = SafeEncoder.encode(params[i-1]);
		}
		sendCommand(Command.EVALSHA.raw,bargs);
	}
	
	public void _scriptLoad(final String script){
		sendCommand(Command.SCRIPT.raw,SafeEncoder.encode("LOAD"),SafeEncoder.encode(script));
	}
	
	public void _scriptExist(final String sha1){
		sendCommand(Command.SCRIPT.raw,SafeEncoder.encode("EXIST"),SafeEncoder.encode(sha1));
	}
	
	public void _scriptFlush(){
		sendCommand(Command.SCRIPT.raw,SafeEncoder.encode("FLUSH"));
	}
	
	public void scriptFlush(){
		_scriptFlush();
		this.getStatusCodeReply();
	}
	
	public String scriptLoad(final String script){
		_scriptLoad(script);
		return this.getBulkReply();
	}
	
	public boolean scriptExist(final String sha1){
		_scriptExist(sha1);
		return this.getIntegerReply() > 0;
	}
	
	/**
	 * 获取应答，应答内容为状态码
	 * @return
	 */
	public String getStatusCodeReply(){
		return super.getStatusCodeReply();
	}
	
	/**
	 * 获取应答，应答内容为大块字符串
	 * @return
	 */
	public String getBulkReply(){
		return super.getBulkReply();
	}

	/**
	 * 获取应答，应答内容为大块二进制块
	 * @return
	 */	
	public byte[] getBinaryBulkReply(){
		return super.getBinaryBulkReply();
	}
	
	/**
	 * 获取应答，应答内容为数值
	 * @return
	 */
	public Long getIntegerReply(){
		return super.getIntegerReply();
	}
	
	/**
	 * 获取应答，应答内容为多个大块字符串
	 * @return
	 */
	public List<String> getMultiBulkReply(List<String> t){
		return super.getMultiBulkReply(t);
	}
	
	/**
	 * 获取应答，应答内容为多个大块二进制块
	 * @return
	 */
	public List<byte[]> getBinaryMultiBulkReply(){
		return super.getBinaryMultiBulkReply();
	}
	
	/**
	 * 获取应答，应答内容为多个对象
	 * @return
	 */
	public List<Object> getObjectMultiBulkReply(){
		return super.getObjectMultiBulkReply();
	}
}
