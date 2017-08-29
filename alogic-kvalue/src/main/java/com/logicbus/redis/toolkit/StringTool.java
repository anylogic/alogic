package com.logicbus.redis.toolkit;

import java.util.ArrayList;
import java.util.List;

import com.logicbus.redis.client.Connection;
import com.logicbus.redis.params.SetParams;
import com.logicbus.redis.util.SafeEncoder;

/**
 * String类型数据的操作工具
 * 
 * @author duanyy
 *
 */
public class StringTool extends KeyTool {

	public static enum BitOP {
		AND,
		OR,
		XOR,
		NOT;
		
		public final byte [] raw;		
		BitOP(){
			raw = SafeEncoder.encode(name());
		}
	}
	
	public static enum Command {
		//to set value
		SET,
		SETBIT,
		SETEX,
		SETNX,
		SETRANGE,
		GETSET,
		PSETEX,
		MSET,
		MSETNX,
		// to get value
		GET,
		GETBIT,
		GETRANGE,
		MGET,
		// to change
		APPEND,
		BITOP,
		DECR,
		DECRBY,
		INCR,
		INCRBY,
		INCRBYFLOAT,
		// to get info
		BITCOUNT,
		BITPOS,
		STRLEN;
		
		public final byte [] raw;		
		Command(){
			raw = SafeEncoder.encode(name());
		}
	}
	
	public StringTool(Connection _conn) {
		super(_conn);
	}

	/**
	 * to set key to hold the String value
	 * @param key
	 * @param value
	 */
	public void _set(final String key,final String value){
		sendCommand(Command.SET.raw,key,value);
	}
	
	/**
	 * to set key to hold String value
	 * @param key
	 * @param value
	 * @param params
	 */
	public void _set(final String key,final String value,SetParams params){
		if (params == null)
		{
			_set(key,value);
		}else{
			final List<byte[]> args = new ArrayList<byte[]>();
			args.add(SafeEncoder.encode(key));
			args.add(SafeEncoder.encode(value));
			args.addAll(params.getParams());
			
			sendCommand(Command.SET.raw, args.toArray(new byte[args.size()][]));
		}
	}
	
	/**
	 * to set key to hold the String value
	 * @param key
	 * @param value
	 */
	public boolean set(final String key,final String value){
		_set(key,value);
		return getStatusCodeReply() != null;
	}
	
	/**
	 * to set key to hold String value
	 * @param key
	 * @param value
	 * @param params
	 */
	public boolean set(final String key,final String value,SetParams params){
		_set(key,value,params);
		return getStatusCodeReply() != null;
	}
	
	/**
	 * to set or clear the bit at offset in the string value stored at key.
	 * @param key
	 * @param offset
	 * @param value
	 */
	public void _setbit(final String key,final long offset,final boolean value){
		sendCommand(
				Command.SETBIT.raw,
				SafeEncoder.encode(key),
				SafeEncoder.encode(offset),
				SafeEncoder.encode(value)
				);
	}
	
	/**
	 * to set or clear the bit at offset in the string value stored at key.
	 * @param key
	 * @param offset
	 * @param value
	 * @return the old bit value stored at offset
	 */
	public long setbit(final String key,final long offset,final boolean value){
		_setbit(key,offset,value);
		return getIntegerReply();
	}
	
	/**
	 * to overwrite part of a string at key starting at the specified offset
	 * @param key
	 * @param offset
	 * @param value
	 */
	public void _setrange(final String key,final long offset,final String value){
		sendCommand(
				Command.SETRANGE.raw,
				SafeEncoder.encode(key),
				SafeEncoder.encode(offset),
				SafeEncoder.encode(value)
				);
	}
	
	/**
	 * to overwrite part of a string at key starting at the specified offset
	 * @param key
	 * @param offset
	 * @param value
	 * @return the length of the string after it was modified by the command.
	 */
	public long setrange(final String key,final long offset,final String value){
		_setrange(key,offset,value);
		return getIntegerReply();
	}	
	
	/**
	 * to set the given keys to their respective values
	 * @param keyvalues
	 */
	public void _mset(final String... keyvalues){
		sendCommand(Command.MSET.raw,keyvalues);
	}
	
	/**
	 * to set the given keys to their respective values
	 * @param keyvalues
	 */
	public void mset(final String... keyvalues){
		_mset(keyvalues);
		getStatusCodeReply();
	}
	
	/**
	 * to set the given keys to their respective values, only if none of the keys exist
	 * @param keyvalues
	 */
	public void _msetex(final String... keyvalues){
		sendCommand(Command.MSET.raw,keyvalues);
	}
	
	/**
	 * to set the given keys to their respective values,only if none of the keys exist
	 * @param keyvalues
	 * @return true if all the keys are set,false if no key was set
	 */
	public boolean msetex(final String... keyvalues){
		_msetex(keyvalues);
		return getIntegerReply() > 0;
	}	
	
	/**
	 * to set the string value of a key and return its old value
	 * @param key
	 * @param value
	 */
	public void _getset(final String key,final String value){
		sendCommand(Command.GETSET.raw,key,value);
	}
	
	/**
	 * to set the string value of a key and return its old value
	 * @param key
	 * @param value
	 * @return  the old value stored at key, or nil when key did not exist.
	 */
	public String getset(final String key,final String value){
		_getset(key,value);
		return getBulkReply();
	}
	
	/**
	 * to get value of the key
	 * @param key
	 */
	public void _get(final String key){
		sendCommand(Command.GET.raw,SafeEncoder.encode(key));
	}
	
	/**
	 * to get value of the key
	 * @param key
	 * @return
	 */
	public String get(final String key,final String defaultValue){
		_get(key);
		String value = getBulkReply();
		return value == null ? defaultValue : value;
	}
	
	/**
	 * to get the bit value at offset in the string value stored at key
	 * @param key
	 * @param offset
	 */
	public void _getbit(final String key,final long offset){
		sendCommand(Command.GETBIT.raw,SafeEncoder.encode(key),SafeEncoder.encode(offset));
	}
	
	/**
	 * to get the bit value at offset in the string value stored at key
	 * @param key
	 * @param offset
	 * @return the bit value stored at offset.
	 */
	public long getbit(final String key,final long offset){
		_getbit(key,offset);
		return getIntegerReply();
	}
	
	/**
	 * to get a substring of the string value stored at the key
	 * @param key
	 * @param start
	 * @param end
	 */
	public void _getrange(final String key,final long start,final long end){
		sendCommand(Command.GETRANGE.raw,SafeEncoder.encode(key),SafeEncoder.encode(start),SafeEncoder.encode(end));
	}
	
	/**
	 * to get a substring of the string value stored at the key
	 * @param key
	 * @param start
	 * @param end
	 * @return the substring 
	 */
	public String getrange(final String key,final long start,final long end){
		_getrange(key,start,end);
		return getBulkReply();
	}

	/**
	 * to get the values of the specified keys.
	 * @param keys
	 */
	public void _mget(final String...keys){
		sendCommand(Command.MGET.raw,keys);
	}
	
	/**
	 * to get the values of the specified keys.
	 * @param keys
	 * @return
	 */
	public List<String> mget(final String...keys){
		_mget(keys);
		return getMultiBulkReply(null);
	}

	/**
	 * to append a value to the key
	 * @param key
	 * @param value
	 */
	public void _append(final String key,final String value){
		sendCommand(Command.APPEND.raw,key,value);
	}
	
	/**
	 * to append a value to the key
	 * @param key
	 * @param value
	 * @return the length of the string after the append operation
	 */
	public long append(final String key,final String value){
		_append(key,value);
		return getIntegerReply();
	}
	
	public void _and(final String destKey,final String... srcKeys){
		final List<byte[]> args = new ArrayList<byte[]>();
		
		args.add(BitOP.AND.raw);
		args.add(SafeEncoder.encode(destKey));
		
		for (String s:srcKeys){
			args.add(SafeEncoder.encode(s));
		}
		
		sendCommand(Command.BITOP.raw,args.toArray(new byte[args.size()][]));
	}
	
	public long and(final String destKey,final String... srcKeys){
		_and(destKey,srcKeys);
		return getIntegerReply();
	}
	
	public void _or(final String destKey,final String... srcKeys){
		final List<byte[]> args = new ArrayList<byte[]>();
		
		args.add(BitOP.OR.raw);
		args.add(SafeEncoder.encode(destKey));
		
		for (String s:srcKeys){
			args.add(SafeEncoder.encode(s));
		}
		
		sendCommand(Command.BITOP.raw,args.toArray(new byte[args.size()][]));
	}
	
	public long or(final String destKey,final String... srcKeys){
		_or(destKey,srcKeys);
		return getIntegerReply();
	}

	public void _xor(final String destKey,final String... srcKeys){
		final List<byte[]> args = new ArrayList<byte[]>();
		
		args.add(BitOP.XOR.raw);
		args.add(SafeEncoder.encode(destKey));
		
		for (String s:srcKeys){
			args.add(SafeEncoder.encode(s));
		}
		
		sendCommand(Command.BITOP.raw,args.toArray(new byte[args.size()][]));
	}
	
	public long xor(final String destKey,final String... srcKeys){
		_xor(destKey,srcKeys);
		return getIntegerReply();
	}

	public void _not(final String destKey,final String srcKeys){
		sendCommand(Command.BITOP.raw,
				SafeEncoder.encode(destKey),
				SafeEncoder.encode(srcKeys)
				);
	}
	
	public long not(final String destKey,final String srcKeys){
		_not(destKey,srcKeys);
		return getIntegerReply();
	}
	
	public void _decr(final String key){
		sendCommand(Command.DECR.raw,key);
	}
	
	public long decr(final String key){
		_decr(key);
		return getIntegerReply();
	}
	
	public void _decrby(final String key,final long decrement){
		sendCommand(Command.DECRBY.raw,SafeEncoder.encode(key),SafeEncoder.encode(decrement));
	}
	
	public long decrby(final String key,final long decrement){
		_decrby(key,decrement);
		return getIntegerReply();
	}
	
	public void _incr(final String key){
		sendCommand(Command.INCR.raw,key);
	}
	
	public long incr(final String key){
		_incr(key);
		return getIntegerReply();
	}
	
	public void _incrby(final String key,final long increment){
		sendCommand(Command.INCRBY.raw,SafeEncoder.encode(key),SafeEncoder.encode(increment));
	}
	
	public long incrby(final String key,final long increment){
		_incrby(key,increment);
		return getIntegerReply();
	}
	
	public void _incrbyfloat(final String key,final double increment){
		sendCommand(Command.INCRBYFLOAT.raw,SafeEncoder.encode(key),SafeEncoder.encode(increment));
	}
	
	public double incrbyfloat(final String key,final double increment){
		_incrbyfloat(key,increment);
		return Double.valueOf(getBulkReply());
	}
	
	public void _strlen(final String key){
		sendCommand(Command.STRLEN.raw,key);
	}
	
	public long strlen(final String key){
		_strlen(key);
		return getIntegerReply();
	}
	
	public void _bitcount(final String key){
		sendCommand(Command.BITCOUNT.raw,key);
	}
	
	public long bitcount(final String key){
		_bitcount(key);
		return getIntegerReply();
	}
	
	public void _bitcount(final String key,final long start,final long end){
		sendCommand(Command.BITCOUNT.raw,
				SafeEncoder.encode(key),
				SafeEncoder.encode(start),
				SafeEncoder.encode(end)
				);
	}
	
	public long bitcount(final String key,final long start,final long end){
		_bitcount(key,start,end);
		return getIntegerReply();
	}
	
	public void _bitpos(final String key,final boolean bit){
		sendCommand(Command.BITPOS.raw,
				SafeEncoder.encode(key),
				SafeEncoder.encode(bit)
				);
	}
	
	public long bitpos(final String key,final boolean bit){
		_bitpos(key,bit);
		return getIntegerReply();
	}
	
	public void _bitpos(final String key,final boolean bit,final long start){
		sendCommand(Command.BITPOS.raw,
				SafeEncoder.encode(key),
				SafeEncoder.encode(bit),
				SafeEncoder.encode(start)
				);
	}
	
	public long bitpos(final String key,final boolean bit,final long start){
		_bitpos(key,bit,start);
		return getIntegerReply();
	}	

	public void _bitpos(final String key,final boolean bit,final long start,final long end){
		sendCommand(Command.BITPOS.raw,
				SafeEncoder.encode(key),
				SafeEncoder.encode(bit),
				SafeEncoder.encode(start),
				SafeEncoder.encode(end)
				);
	}
	
	public long bitpos(final String key,final boolean bit,final long start,final long end){
		_bitpos(key,bit,start,end);
		return getIntegerReply();
	}	

}
