package com.logicbus.redis.toolkit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.anysoft.util.Pair;
import com.logicbus.redis.client.Connection;
import com.logicbus.redis.params.ScanParams;
import com.logicbus.redis.params.ZParams;
import com.logicbus.redis.result.ScanResult;
import com.logicbus.redis.util.SafeEncoder;

public class SortedSetTool extends KeyTool {

	public static enum Command {
		ZUNIONSTORE,
		ZINTERSTORE,
		ZSCAN,
		ZSCORE,
		ZREVRANK,
		ZREVRANGEBYSCORE,
		ZREVRANGE,
		ZREMRANGEBYSCORE,
		ZREMRANGEBYRANK,
		ZREM,
		ZRANK,
		ZRANGEBYSCORE,
		ZRANGE,
		ZINCRBY,
		ZCOUNT,
		ZCARD,
		ZADD;
		
		public final byte [] raw;		
		
		Command(){
			raw = SafeEncoder.encode(name());
		}
	}
	
	public SortedSetTool(Connection _conn) {
		super(_conn);
	}
	
	public void _zinterstore(final String destKey,ZParams params,final String...keys){
		if (params == null){
			_zinterstore(destKey,keys);
		}else{
			final List<byte[]> args = new ArrayList<byte[]>();
			args.add(SafeEncoder.encode(destKey));
			args.add(SafeEncoder.encode(keys.length));
			
			for (int i = 0 ; i < keys.length ; i ++){
				args.add(SafeEncoder.encode(keys[i]));
			}
			
			args.addAll(params.getParams());
			
			sendCommand(Command.ZINTERSTORE.raw, args.toArray(new byte[args.size()][]));
		}
	}
	
	public long zinterstore(final String destKey,ZParams params,final String...keys){
		_zinterstore(destKey,params,keys);
		return getIntegerReply();
	}
	
	public void _zunionstore(final String destKey,ZParams params,final String...keys){
		if (params == null){
			_zunionstore(destKey,keys);
		}else{
			final List<byte[]> args = new ArrayList<byte[]>();
			args.add(SafeEncoder.encode(destKey));
			args.add(SafeEncoder.encode(keys.length));
			
			for (int i = 0 ; i < keys.length ; i ++){
				args.add(SafeEncoder.encode(keys[i]));
			}
			
			args.addAll(params.getParams());
			
			sendCommand(Command.ZUNIONSTORE.raw, args.toArray(new byte[args.size()][]));
		}
	}
	
	public long zunionstore(final String destKey,ZParams params,final String...keys){
		_zunionstore(destKey,params,keys);
		return getIntegerReply();
	}
	
	public void _zunionstore(final String destKey,final String...keys){
		final byte[][] params = new byte[keys.length + 2][];
		params[0] = SafeEncoder.encode(destKey);
		params[1] = SafeEncoder.encode(keys.length);
		
		for (int i = 2 ; i < keys.length + 2 ; i ++){
			params[i] = SafeEncoder.encode(keys[i-2]);
		}
		
		sendCommand(Command.ZUNIONSTORE.raw,params);
	}
	
	public long zunionstore(final String destKey,final String...keys){
		_zunionstore(destKey,keys);
		return getIntegerReply();
	}
	
	public void _zinterstore(final String destKey,final String...keys){
		final byte[][] params = new byte[keys.length + 2][];
		params[0] = SafeEncoder.encode(destKey);
		params[1] = SafeEncoder.encode(keys.length);
		
		for (int i = 2 ; i < keys.length + 2 ; i ++){
			params[i] = SafeEncoder.encode(keys[i-2]);
		}
		
		sendCommand(Command.ZINTERSTORE.raw,params);
	}
	
	public long zinterstore(final String destKey,final String...keys){
		_zinterstore(destKey,keys);
		return getIntegerReply();
	}	
	
	/**
	 * to scan the keys
	 * @param cursor
	 * @param params
	 */
	public void _zscan(final String key,final String cursor,ScanParams params){
		final List<byte[]> args = new ArrayList<byte[]>();
		args.add(SafeEncoder.encode(cursor));
		args.addAll(params.getParams());
		sendCommand(Command.ZSCAN.raw, args.toArray(new byte[args.size()][]));
	}
	
	/**
	 * to scan the keys
	 * @param cursor
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ScanResult<String> zscan(final String key,final String cursor,ScanParams params){
		_zscan(key,cursor,params);
		List<Object> result = getObjectMultiBulkReply();
		
		String newcursor = new String((byte[]) result.get(0));
		List<String> results = new ArrayList<String>();
		List<byte[]> rawResults = (List<byte[]>) result.get(1);
		for (byte[] bs : rawResults) {
		    results.add(SafeEncoder.encode(bs));
		}
		return new ScanResult<String>(newcursor, results);
	}
	
	public double zscore(final String key,final String member){
		_zscore(key,member);
		
		String newscore = getBulkReply();
		try {
			return Double.valueOf(newscore);
		}catch (Exception ex){
			return 0;
		}
	}
	
	public void _zscore(final String key,final String member){
		sendCommand(Command.ZSCORE.raw,key,member);
	}
	
	public List<String> zrevrangebyscore(final String key,final String min,final String max){
		_zrevrangebyscore(key,min,max,false);
		return getMultiBulkReply(null);
	}
	
	public List<Pair<String,Double>> zrevrangebyscoreWithScores(final String key,final String min,final String max){
		_zrevrangebyscore(key,min,max,true);
		return  getResultWithScores();
	}
	
	public List<String> zrevrangebyscore(final String key,final String min,final String max,final long offset,final long count){
		_zrevrangebyscore(key,min,max,false,offset,count);
		return getMultiBulkReply(null);
	}
	
	public List<Pair<String, Double>> zrevrangebyscoreWithScores(final String key,final String min,final String max,final long offset,final long count){
		_zrevrangebyscore(key,min,max,true,offset,count);
		return  getResultWithScores();
	}
	
	public void _zrevrangebyscore(final String key,final String min,final String max,final boolean withScores,
			final long offset,final long count){
		if (withScores){
			sendCommand(Command.ZREVRANGEBYSCORE.raw,
					SafeEncoder.encode(key),
					SafeEncoder.encode(min),
					SafeEncoder.encode(max),
					SafeEncoder.encode("LIMIT"),
					SafeEncoder.encode(offset),
					SafeEncoder.encode(count),
					SafeEncoder.encode("WITHSCORES")
					);
		}else{
			sendCommand(Command.ZREVRANGEBYSCORE.raw,
				SafeEncoder.encode(key),
				SafeEncoder.encode(min),
				SafeEncoder.encode(max),
				SafeEncoder.encode("LIMIT"),
				SafeEncoder.encode(offset),
				SafeEncoder.encode(count)
				);
		}
	}
	
	public void _zrevrangebyscore(final String key,final String min,final String max,final boolean withScores){
		if (withScores){
			sendCommand(Command.ZREVRANGEBYSCORE.raw,
					SafeEncoder.encode(key),
					SafeEncoder.encode(min),
					SafeEncoder.encode(max),
					SafeEncoder.encode("WITHSCORES"));
		}else{
			sendCommand(Command.ZREVRANGEBYSCORE.raw,
				SafeEncoder.encode(key),
				SafeEncoder.encode(min),
				SafeEncoder.encode(max)
				);
		}
	}
	
	public List<Pair<String, Double>> zrevrangeWithScores(final String key,final long start,final long stop){
		_zrevrange(key,start,stop,true);
		return  getResultWithScores();
	}
	
	public List<String> zrevrange(final String key,final long start,final long stop){
		_zrevrange(key,start,stop,false);
		return getMultiBulkReply(null);
	}
	
	public void _zrevrange(final String key,final long start,final long stop,boolean withScores){
		if (withScores){
			sendCommand(Command.ZREVRANGE.raw,
					SafeEncoder.encode(key),
					SafeEncoder.encode(start),
					SafeEncoder.encode(stop),
					SafeEncoder.encode("WITHSCORES"));
		}else{
			sendCommand(Command.ZREVRANGE.raw,
				SafeEncoder.encode(key),
				SafeEncoder.encode(start),
				SafeEncoder.encode(stop)
				);
		}
	}
	
	public long zremrangebyscore(final String key,final String min,final String max){
		_zremrangebyscore(key,min,max);
		return getIntegerReply();
	}
	
	public void _zremrangebyscore(final String key,final String min,final String max){
		sendCommand(Command.ZREMRANGEBYSCORE.raw,key,min,max);
	}
	
	public long zremrangebyrank(final String key,final long start,final long stop){
		_zremrangebyrank(key,start,stop);
		return getIntegerReply();
	}
	
	public void _zremrangebyrank(final String key,final long start,final long stop){
		sendCommand(Command.ZREMRANGEBYRANK.raw,
				SafeEncoder.encode(key),
				SafeEncoder.encode(start),
				SafeEncoder.encode(stop)
				);
	}
	
	public long zrem(final String key,final String...members){
		_zrem(key,members);
		
		return getIntegerReply();
	}
	
	public void _zrem(final String key,final String...members){
		final byte[][] bargs = new byte[members.length + 1][];
		bargs[0] = SafeEncoder.encode(key);
		for (int i = 1; i < members.length + 1; i++) {
			bargs[i] = SafeEncoder.encode(members[i-1]);
		}
		sendCommand(Command.ZREM.raw,bargs);
	}
	
	public long zrevrank(final String key,final String member){
		_zrevrank(key,member);
		return getIntegerReply();
	}
	
	public void _zrevrank(final String key,final String member){
		sendCommand(Command.ZREVRANK.raw,key,member);
	}
	
	public long zrank(final String key,final String member){
		_zrank(key,member);
		return getIntegerReply();
	}
	
	public void _zrank(final String key,final String member){
		sendCommand(Command.ZRANK.raw,key,member);
	}
	
	public List<String> zrangebyscore(final String key,final String min,final String max){
		_zrangebyscore(key,min,max,false);
		return getMultiBulkReply(null);
	}
	
	public List<Pair<String,Double>> zrangebyscoreWithScores(final String key,final String min,final String max){
		_zrangebyscore(key,min,max,true);
		return  getResultWithScores();
	}
	
	public List<String> zrangebyscore(final String key,final String min,final String max,final long offset,final long count){
		_zrangebyscore(key,min,max,false,offset,count);
		return getMultiBulkReply(null);
	}
	
	public List<Pair<String, Double>> zrangebyscoreWithScores(final String key,final String min,final String max,final long offset,final long count){
		_zrangebyscore(key,min,max,true,offset,count);
		return  getResultWithScores();
	}
	
	public void _zrangebyscore(final String key,final String min,final String max,final boolean withScores,
			final long offset,final long count){
		if (withScores){
			sendCommand(Command.ZRANGEBYSCORE.raw,
					SafeEncoder.encode(key),
					SafeEncoder.encode(min),
					SafeEncoder.encode(max),
					SafeEncoder.encode("LIMIT"),
					SafeEncoder.encode(offset),
					SafeEncoder.encode(count)
					);
		}else{
			sendCommand(Command.ZRANGEBYSCORE.raw,
				SafeEncoder.encode(key),
				SafeEncoder.encode(min),
				SafeEncoder.encode(max),
				SafeEncoder.encode("LIMIT"),
				SafeEncoder.encode(offset),
				SafeEncoder.encode(count),
				SafeEncoder.encode("WITHSCORES")
				);
		}
	}
	
	public void _zrangebyscore(final String key,final String min,final String max,final boolean withScores){
		if (withScores){
			sendCommand(Command.ZRANGEBYSCORE.raw,
					SafeEncoder.encode(key),
					SafeEncoder.encode(min),
					SafeEncoder.encode(max),
					SafeEncoder.encode("WITHSCORES"));
		}else{
			sendCommand(Command.ZRANGEBYSCORE.raw,
				SafeEncoder.encode(key),
				SafeEncoder.encode(min),
				SafeEncoder.encode(max)
				);
		}
	}
	
	public List<Pair<String, Double>> zrangeWithScores(final String key,final long start,final long stop){
		_zrange(key,start,stop,true);
		return  getResultWithScores();
	}
	
	public List<String> zrange(final String key,final long start,final long stop){
		_zrange(key,start,stop,false);
		return getMultiBulkReply(null);
	}
	
	public void _zrange(final String key,final long start,final long stop,boolean withScores){
		if (withScores){
			sendCommand(Command.ZRANGE.raw,
					SafeEncoder.encode(key),
					SafeEncoder.encode(start),
					SafeEncoder.encode(stop),
					SafeEncoder.encode("WITHSCORES"));
		}else{
			sendCommand(Command.ZRANGE.raw,
				SafeEncoder.encode(key),
				SafeEncoder.encode(start),
				SafeEncoder.encode(stop)
				);
		}
	}
	
	public double zincrby(final String key,final String member,final double increment){
		_zincrby(key,member,increment);
		
		String newscore = getBulkReply();
		try {
			return Double.valueOf(newscore);
		}catch (Exception ex){
			return 0;
		}
	}
	
	public void _zincrby(final String key,final String member,final double increment){
		sendCommand(Command.ZINCRBY.raw,
				SafeEncoder.encode(key),
				SafeEncoder.encode(increment),
				SafeEncoder.encode(member)
				);
	}

	public long zcount(final String key,final double min,final double max){
		_zcount(key,String.valueOf(min),String.valueOf(max));
		
		return getIntegerReply();
	}
	
	public long zcount(final String key,final long min,final long max){
		_zcount(key,String.valueOf(min),String.valueOf(max));
		
		return getIntegerReply();
	}
	
	public long zcount(final String key,final String min,final String max){
		_zcount(key,min,max);
		return getIntegerReply();
	}
	
	public void _zcount(final String key,final String min,final String max){
		sendCommand(Command.ZCOUNT.raw,
				SafeEncoder.encode(key),
				SafeEncoder.encode(min),
				SafeEncoder.encode(max)
				);
	}
	
	public long size(final String key){
		_size(key);
		
		return getIntegerReply();
	}	
	
	public void _size(final String key){
		sendCommand(Command.ZCARD.raw,SafeEncoder.encode(key));
	}
	
	public void _zadd(final String key,final String member,final double score){
		sendCommand(Command.ZADD.raw,
				SafeEncoder.encode(key),
				SafeEncoder.encode(score),
				SafeEncoder.encode(member)
				);
	}
	
	public boolean zadd(final String key,final String member,final double score){
		_zadd(key,member,score);
		
		return this.getIntegerReply() > 0;
	}
	
	
	public void _zadd(final String key,Map<String,Double> scoreMembers){
		ArrayList<byte[]> args = new ArrayList<byte[]>(
				scoreMembers.size() * 2 + 1);
		args.add(SafeEncoder.encode(key));

		for (Map.Entry<String, Double> entry : scoreMembers.entrySet()) {
			args.add(SafeEncoder.encode(entry.getValue()));
			args.add(SafeEncoder.encode(entry.getKey()));
		}

		byte[][] argsArray = new byte[args.size()][];
		args.toArray(argsArray);

		sendCommand(Command.ZADD.raw, argsArray);
	}
	
	public long zadd(final String key,Map<String,Double> scoreMembers){
		_zadd(key,scoreMembers);
		
		return getIntegerReply();
	}
	
	private List<Pair<String,Double>> getResultWithScores(){
		List<String> membersWithScores = getMultiBulkReply(null);
		
		List<Pair<String,Double>> result = new ArrayList<Pair<String,Double>>();
		
		Iterator<String> iterator = membersWithScores.iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			String value = iterator.next();
			if (key == null || value == null){
				continue;
			}
			
			Double _value = 0.0;
			try {
			 _value = Double.parseDouble(value);
			}catch (Exception ex){
				_value = 0.0;
			}
			result.add(new Pair.Default<String, Double>(key,_value));
		}
		return result;
	}
	
}
