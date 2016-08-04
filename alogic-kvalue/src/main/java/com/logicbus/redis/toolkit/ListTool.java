package com.logicbus.redis.toolkit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.logicbus.redis.client.Connection;
import com.logicbus.redis.util.RedisDataException;
import com.logicbus.redis.util.SafeEncoder;

public class ListTool extends KeyTool {

	public static enum Command {
		BLPOP,
		BRPOP,
		BRPOPLPUSH,
		RPOPLPUSH,
		RPUSH,
		RPUSHX,
		RPOP,
		LTRIM,
		LSET,
		LREM,
		LRANGE,
		LPUSHX,
		LPUSH,
		LPOP,
		LINSERT,
		LINDEX,
		LLEN;
		public final byte [] raw;		
		Command(){
			raw = SafeEncoder.encode(name());
		}
	}
	
	public ListTool(Connection _conn) {
		super(_conn);
	}

	public void _linsert(final String key,final String pivot,final String value,boolean insertBefore){
		sendCommand(Command.LINSERT.raw,
				SafeEncoder.encode(key),
				SafeEncoder.encode(insertBefore ? "BEFORE":"AFTER"),
				SafeEncoder.encode(pivot),
				SafeEncoder.encode(value)
				);
	}
	
	public long linsert(final String key,final String pivot,final String value,boolean insertBefore){
		_linsert(key,pivot,value,insertBefore);
		
		return getIntegerReply();
	}
	
	public void _llen(final String key){
		sendCommand(Command.LLEN.raw,key);
	}
	
	public long llen(final String key){
		_llen(key);
		return getIntegerReply();
	}
	
	public void _lget(final String key,final long index){
		sendCommand(Command.LINDEX.raw,SafeEncoder.encode(key),SafeEncoder.encode(index));
	}
	
	public String lget(final String key,final long index){
		_lget(key,index);
		
		return getBulkReply();
	}
	
	public void _lpop(final String key){
		sendCommand(Command.LPOP.raw,key);
	}
	
	public String lpop(final String key){
		_lpop(key);
		
		return getBulkReply();
	}
	
	public void _lpush(final String key,final String...values){
		final byte[][] bargs = new byte[values.length + 1][];
		bargs[0] = SafeEncoder.encode(key);
		for (int i = 1; i < values.length + 1; i++) {
			bargs[i] = SafeEncoder.encode(values[i-1]);
		}
		sendCommand(Command.LPUSH.raw,bargs);
	}
	
	public long lpush(final String key,final String...values){
		_lpush(key,values);
		return getIntegerReply();
	}
	
	public void _lpushx(final String key,final String value){
		sendCommand(Command.LPUSHX.raw,key,value);
	}
	
	public long lpushx(final String key,final String value){
		_lpushx(key,value);
		return getIntegerReply();
	}
	
	public void _lrange(final String key,final long start,final long stop){
		sendCommand(Command.LRANGE.raw,
				SafeEncoder.encode(key),
				SafeEncoder.encode(start),
				SafeEncoder.encode(stop)
				);
	}
	
	public List<String> lrange(final String key,final long start,final long stop){
		_lrange(key,start,stop);
		
		return getMultiBulkReply(null);
	}
	
	public void _lrem(final String key,final String value,final long count){
		sendCommand(Command.LREM.raw,
				SafeEncoder.encode(key),
				SafeEncoder.encode(count),
				SafeEncoder.encode(value));
	}
	
	public long lrem(final String key,final String value,final long count){
		_lrem(key,value,count);
		
		return getIntegerReply();
	}
	
	public void _lset(final String key,final long index,final String value){
		sendCommand(Command.LSET.raw,
				SafeEncoder.encode(key),
				SafeEncoder.encode(index),
				SafeEncoder.encode(value));
	}
	
	public boolean lset(final String key,final long index,final String value){
		_lset(key,index,value);
		try {
			getStatusCodeReply();
			return true;
		}catch (RedisDataException ex){
			return false;
		}
	}
	
	public void _ltrim(final String key,final long start,final long stop){
		sendCommand(Command.LTRIM.raw,
				SafeEncoder.encode(key),
				SafeEncoder.encode(start),
				SafeEncoder.encode(stop)
				);
	}
	
	public void ltrim(final String key,final long start,final long stop){
		_ltrim(key,start,stop);
		getStatusCodeReply();
	}
	
	public void _rpop(final String key){
		sendCommand(Command.RPOP.raw,key);
	}
	
	public String rpop(final String key){
		_rpop(key);
		return getBulkReply();
	}
	
	public void _rpush(final String key,final String...values){
		final byte[][] bargs = new byte[values.length + 1][];
		bargs[0] = SafeEncoder.encode(key);
		for (int i = 1; i < values.length + 1; i++) {
			bargs[i] = SafeEncoder.encode(values[i-1]);
		}
		sendCommand(Command.RPUSH.raw,bargs);
	}
	
	public long rpush(final String key,final String...values){
		_rpush(key,values);
		return getIntegerReply();
	}
	
	public void _rpushx(final String key,final String value){
		sendCommand(Command.RPUSHX.raw,key,value);
	}
	
	public long rpushx(final String key,final String value){
		_rpushx(key,value);
		return getIntegerReply();
	}
	
	public void _rpoplpush(final String srcKey,final String destKey){
		sendCommand(Command.RPOPLPUSH.raw,srcKey,destKey);
	}
	
	public String rpoplpush(final String srcKey,final String destKey){
		_rpoplpush(srcKey,destKey);
		return getBulkReply();
	}
	
	public void _blpop(long time,TimeUnit tu,final String...keys){
		final List<byte[]> args = new ArrayList<byte[]>();
		
		for (String key:keys){
			args.add(SafeEncoder.encode(key));
		}
			
		args.add(SafeEncoder.encode(tu.toSeconds(time)));
		
		sendCommand(Command.BLPOP.raw, args.toArray(new byte[args.size()][]));
	}
	
	public List<String> blpop(long time,TimeUnit tu,final String...keys){
		_blpop(time,tu,keys);
		return getMultiBulkReply(null);
	}
	
	public void _brpop(long time,TimeUnit tu,final String...keys){
		final List<byte[]> args = new ArrayList<byte[]>();
		
		for (String key:keys){
			args.add(SafeEncoder.encode(key));
		}
			
		args.add(SafeEncoder.encode(tu.toSeconds(time)));
		
		sendCommand(Command.BRPOP.raw, args.toArray(new byte[args.size()][]));
	}
	
	public List<String> brpop(long time,TimeUnit tu,final String...keys){
		_brpop(time,tu,keys);
		return getMultiBulkReply(null);
	}	
	
	public void _brpoplpush(final String srcKey,final String destKey,long time,TimeUnit tu){
		sendCommand(Command.BRPOPLPUSH.raw,
				SafeEncoder.encode(srcKey),
				SafeEncoder.encode(destKey),
				SafeEncoder.encode(tu.toSeconds(time)));
	}
	
	public List<String> brpoplpush(final String srcKey,final String destKey,long time,TimeUnit tu){
		_brpoplpush(srcKey,destKey,time,tu);
		return getMultiBulkReply(null);
	}	
}
