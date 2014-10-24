package com.logicbus.redis.toolkit;

import java.util.ArrayList;
import java.util.List;

import com.logicbus.redis.client.Connection;
import com.logicbus.redis.client.Toolkit;
import com.logicbus.redis.params.SetParams;
import com.logicbus.redis.util.SafeEncoder;

public class ByteArrayTool extends Toolkit {
	public static enum Command {
		//to set value
		SET,
		GET;
		
		public final byte [] raw;		
		Command(){
			raw = SafeEncoder.encode(name());
		}
	}
	public ByteArrayTool(Connection _conn) {
		super(_conn);
	}

	/**
	 * to set key to hold the String value
	 * @param key
	 * @param value
	 */
	public void _set(final String key,final byte[] value){
		sendCommand(Command.SET.raw,SafeEncoder.encode(key),value);
	}
	
	/**
	 * to set key to hold String value
	 * @param key
	 * @param value
	 * @param params
	 */
	public void _set(final String key,final byte[] value,SetParams params){
		if (params == null)
		{
			_set(key,value);
		}else{
			final List<byte[]> args = new ArrayList<byte[]>();
			args.add(SafeEncoder.encode(key));
			args.add(value);
			args.addAll(params.getParams());
			
			sendCommand(Command.SET.raw, args.toArray(new byte[args.size()][]));
		}
	}
	
	/**
	 * to set key to hold the String value
	 * @param key
	 * @param value
	 */
	public boolean set(final String key,final byte[] value){
		_set(key,value);
		getStatusCodeReply();
		return true;
	}
	
	/**
	 * to set key to hold String value
	 * @param key
	 * @param value
	 * @param params
	 */
	public boolean set(final String key,final byte[] value,SetParams params){
		_set(key,value,params);
		getStatusCodeReply();
		return true;
	}
	
	/**
	 * to get value of the key
	 * @param key
	 */
	public void _get(final String key){
		sendCommand(Command.GET.raw,key);
	}
	
	/**
	 * to get value of the key
	 * @param key
	 * @return
	 */
	public byte [] get(final String key,final byte [] defaultValue){
		_get(key);
		byte [] value = getBinaryBulkReply();
		return value == null ? defaultValue : value;
	}	
}
