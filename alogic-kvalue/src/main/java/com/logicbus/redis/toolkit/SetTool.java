package com.logicbus.redis.toolkit;

import java.util.ArrayList;
import java.util.List;

import com.logicbus.redis.client.Connection;
import com.logicbus.redis.params.ScanParams;
import com.logicbus.redis.result.ScanResult;
import com.logicbus.redis.util.SafeEncoder;

public class SetTool extends KeyTool {

	public static enum Command {
		SUNION,
		SUNIONSTORE,
		SINTERSTORE,
		SINTER,
		SDIFFSTORE,
		SDIFF,
		SMOVE,
		SSCAN,
		SRANDMEMBER,
		SREM,
		SPOP,
		SMEMBERS,
		SISMEMBER,
		SCARD,
		SADD;
		
		public final byte [] raw;		
		
		Command(){
			raw = SafeEncoder.encode(name());
		}
	}
	
	public SetTool(Connection _conn) {
		super(_conn);
	}
	
	public long sunionstore(final String destKey,final String key,final String...keys){
		_sunionstore(destKey,key,keys);
		
		return getIntegerReply();
	}

	public void _sunionstore(final String destKey,final String key,final String...keys){
		final byte[][] bargs = new byte[keys.length + 2][];
		bargs[0] = SafeEncoder.encode(destKey);
		bargs[1] = SafeEncoder.encode(key);
		for (int i = 2; i < keys.length + 2; i++) {
			bargs[i] = SafeEncoder.encode(keys[i-2]);
		}
		sendCommand(Command.SUNIONSTORE.raw,bargs);
	}
	
	public List<String> sunion(final String key,final String...keys){
		_sunion(key,keys);
		return getMultiBulkReply(null);
	}
	
	public void _sunion(final String key,final String...keys){
		final byte[][] bargs = new byte[keys.length + 1][];
		bargs[0] = SafeEncoder.encode(key);
		for (int i = 1; i < keys.length + 1; i++) {
			bargs[i] = SafeEncoder.encode(keys[i-1]);
		}
		sendCommand(Command.SUNION.raw,bargs);
	}
	
	public long sinterstore(final String destKey,final String key,final String...keys){
		_sinterstore(destKey,key,keys);
		
		return getIntegerReply();
	}
	
	public void _sinterstore(final String destKey,final String key,final String...keys){
		final byte[][] bargs = new byte[keys.length + 2][];
		bargs[0] = SafeEncoder.encode(destKey);
		bargs[1] = SafeEncoder.encode(key);
		for (int i = 2; i < keys.length + 2; i++) {
			bargs[i] = SafeEncoder.encode(keys[i-2]);
		}
		sendCommand(Command.SINTERSTORE.raw,bargs);
	}
	
	public List<String> sinter(final String key,final String...keys){
		_sinter(key,keys);
		
		return getMultiBulkReply(null);
	}
	
	public void _sinter(final String key,final String...keys){
		final byte[][] bargs = new byte[keys.length + 1][];
		bargs[0] = SafeEncoder.encode(key);
		for (int i = 1; i < keys.length + 1; i++) {
			bargs[i] = SafeEncoder.encode(keys[i-1]);
		}
		sendCommand(Command.SINTER.raw,bargs);
	}
	
	public long sdiffstore(final String destKey,final String key,final String...keys){
		_sdiffstore(destKey,key,keys);
		return getIntegerReply();
	}
	
	public void _sdiffstore(final String destKey,final String key,final String...keys){
		final byte[][] bargs = new byte[keys.length + 2][];
		bargs[0] = SafeEncoder.encode(destKey);
		bargs[1] = SafeEncoder.encode(key);
		for (int i = 2; i < keys.length + 2; i++) {
			bargs[i] = SafeEncoder.encode(keys[i-2]);
		}
		sendCommand(Command.SDIFFSTORE.raw,bargs);
	}
	
	public List<String> sdiff(final String key,final String...keys){
		_sdiff(key,keys);
		
		return getMultiBulkReply(null);
	}
	
	public void _sdiff(final String key,final String...keys){
		final byte[][] bargs = new byte[keys.length + 1][];
		bargs[0] = SafeEncoder.encode(key);
		for (int i = 1; i < keys.length + 1; i++) {
			bargs[i] = SafeEncoder.encode(keys[i-1]);
		}
		sendCommand(Command.SDIFF.raw,bargs);
	}
	
	public void _smove(final String srcKey,final String destKey,final String member){
		sendCommand(Command.SMOVE.raw,srcKey,destKey,member);
	}
	
	public boolean smove(final String srcKey,final String destKey,final String member){
		_smove(srcKey,destKey,member);
		
		return getIntegerReply() > 0;
	}
	
	/**
	 * to scan the keys
	 * @param cursor
	 * @param params
	 */
	public void _sscan(final String key,final String cursor,ScanParams params){
		final List<byte[]> args = new ArrayList<byte[]>();
		args.add(SafeEncoder.encode(cursor));
		args.addAll(params.getParams());
		sendCommand(Command.SSCAN.raw, args.toArray(new byte[args.size()][]));
	}
	
	/**
	 * to scan the keys
	 * @param cursor
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ScanResult<String> sscan(final String key,final String cursor,ScanParams params){
		_sscan(key,cursor,params);
		List<Object> result = getObjectMultiBulkReply();
		
		String newcursor = new String((byte[]) result.get(0));
		List<String> results = new ArrayList<String>();
		List<byte[]> rawResults = (List<byte[]>) result.get(1);
		for (byte[] bs : rawResults) {
		    results.add(SafeEncoder.encode(bs));
		}
		return new ScanResult<String>(newcursor, results);
	}	
	
	public void _srandmember(final String key){
		sendCommand(Command.SRANDMEMBER.raw,key);
	}
	
	public String srandmember(final String key){
		_srandmember(key);
		
		return getBulkReply();
	}
	
	public long srem(final String key,final String...memebers){
		_srem(key,memebers);
		
		return getIntegerReply();
	}
	
	public void _srem(final String key,final String...members){
		final byte[][] bargs = new byte[members.length + 1][];
		bargs[0] = SafeEncoder.encode(key);
		for (int i = 1; i < members.length + 1; i++) {
			bargs[i] = SafeEncoder.encode(members[i-1]);
		}
		sendCommand(Command.SREM.raw,bargs);
	}
	
	public void _spop(final String key){
		sendCommand(Command.SPOP.raw,key);
	}
	
	public String spop(final String key){
		_spop(key);
		
		return getBulkReply();
	}
	
	public void _smembers(final String key){
		sendCommand(Command.SMEMBERS.raw,key);
	}
	
	public List<String> smembers(final String key){
		_smembers(key);
		
		return getMultiBulkReply(null);
	}
	
	public void _sismember(final String key,final String member){
		sendCommand(Command.SISMEMBER.raw,key,member);
	}
	
	public boolean sismember(final String key,final String member){
		_sismember(key,member);
		return getIntegerReply() > 0;
	}
	
	public void _size(final String key){
		sendCommand(Command.SCARD.raw,key);
	}
	
	public long size(final String key){
		_size(key);
		return getIntegerReply();
	}
	
	public void _sadd(final String key,final String...values){
		final byte[][] bargs = new byte[values.length + 1][];
		bargs[0] = SafeEncoder.encode(key);
		for (int i = 1; i < values.length + 1; i++) {
			bargs[i] = SafeEncoder.encode(values[i-1]);
		}
		sendCommand(Command.SADD.raw,bargs);
	}
	
	public long sadd(final String key,final String...values){
		_sadd(key,values);
		return getIntegerReply();
	}
}
