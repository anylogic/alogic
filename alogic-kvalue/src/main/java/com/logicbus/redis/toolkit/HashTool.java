package com.logicbus.redis.toolkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.logicbus.redis.client.Connection;
import com.logicbus.redis.params.ScanParams;
import com.logicbus.redis.result.ScanResult;
import com.logicbus.redis.util.BuilderFactory;
import com.logicbus.redis.util.SafeEncoder;

/**
 * Hash类型数据的工具集
 * @author duanyy
 *
 */
public class HashTool extends KeyTool {
	public static enum Command {
		HDEL,
		HEXISTS,
		HGET,
		HGETALL,
		HINCRBY,
		HINCRBYFLOAT,
		HMAX,
		HMAXBYFLOAT,
		HMIN,
		HMINBYFLOAT,
		HAVG,
		HAVGBYFLOAT,
		HKEYS,
		HLEN,
		HMGET,
		HMSET,
		HSET,
		HSETNX,
		HVALS,
		HSCAN;
		
		public final byte [] raw;		
		Command(){
			raw = SafeEncoder.encode(name());
		}
	}
	public HashTool(Connection _conn) {
		super(_conn);
	}
	
	/**
	 * to scan the keys
	 * @param cursor
	 * @param params
	 */
	public void _hscan(final String key,final String cursor,ScanParams params){
		final List<byte[]> args = new ArrayList<byte[]>();
		args.add(SafeEncoder.encode(cursor));
		args.addAll(params.getParams());
		sendCommand(Command.HSCAN.raw, args.toArray(new byte[args.size()][]));
	}
	
	/**
	 * to scan the keys
	 * @param cursor
	 * @param params
	 */
	@SuppressWarnings("unchecked")
	public ScanResult<String> hscan(final String key,final String cursor,ScanParams params){
		_hscan(key,cursor,params);
		List<Object> result = getObjectMultiBulkReply();
		
		String newcursor = new String((byte[]) result.get(0));
		List<String> results = new ArrayList<String>();
		List<byte[]> rawResults = (List<byte[]>) result.get(1);
		for (byte[] bs : rawResults) {
		    results.add(SafeEncoder.encode(bs));
		}
		return new ScanResult<String>(newcursor, results);
	}	
	
	public List<String> hvals(final String key){
		_hvals(key);
		
		return getMultiBulkReply(null);
	}
	
	public void _hvals(final String key){
		sendCommand(Command.HVALS.raw,key);
	}
	
	public boolean hsetnx(final String key,final String field,final String value){
		_hsetnx(key,field,value);
		return getIntegerReply() > 0;
	}
	
	public void _hsetnx(final String key,final String field,final String value){
		sendCommand(Command.HSETNX.raw,key,field,value);
	}
	
	public boolean hset(final String key,final String field,final String value){
		_hset(key,field,value);
		return getIntegerReply() > 0;
	}
	
	public void _hset(final String key,final String field,final String value){
		sendCommand(Command.HSET.raw,key,field,value);
	}
	
	public boolean hmset(final String key,final String...fieldvalues){
		_hmset(key,fieldvalues);
		getStatusCodeReply();
		return true;
	}
	
	public void _hmset(final String key,final String...fieldvalues){
		final List<byte[]> args = new ArrayList<byte[]>();
		args.add(SafeEncoder.encode(key));
	
		for (String fv:fieldvalues){
			args.add(SafeEncoder.encode(fv));
		}
		
		sendCommand(Command.HMSET.raw,args.toArray(new byte[args.size()][]));
	}
	
	public List<String> hmget(final String key,final String...fields){
		_hmget(key,fields);
		return getMultiBulkReply(null);
	}
	
	public void _hmget(final String key,final String... fields){
		final List<byte[]> args = new ArrayList<byte[]>();
		args.add(SafeEncoder.encode(key));
	
		for (String field:fields){
			args.add(SafeEncoder.encode(field));
		}
		
		sendCommand(Command.HMGET.raw,args.toArray(new byte[args.size()][]));
	}
	
	public long hlen(final String key){
		_hlen(key);
		return getIntegerReply();
	}

	public void _hlen(final String key){
		sendCommand(Command.HLEN.raw,key);
	}
	
	public List<String> hkeys(final String key){
		_hkeys(key);
		
		return getMultiBulkReply(null);
	}
	
	public void _hkeys(final String key){
		sendCommand(Command.HKEYS.raw,key);
	}
	
	public double hincrbyfloat(final String key,final String field,final double increment){
		_hincrbyfloat(key,field,increment);
		return Double.parseDouble(getBulkReply());
	}
	
	public void _hincrbyfloat(final String key,final String field,final double increment){
		sendCommand(
				Command.HINCRBYFLOAT.raw,
				SafeEncoder.encode(key),
				SafeEncoder.encode(field),
				SafeEncoder.encode(increment)
				);
	}
	

	public void _hincrby(final String key,final String field,final long increment){
		sendCommand(
				Command.HINCRBY.raw,
				SafeEncoder.encode(key),
				SafeEncoder.encode(field),
				SafeEncoder.encode(increment)
				);
	}
	
	public long hincrby(final String key,final String field,final long increment){
		_hincrby(key,field,increment);
		return getIntegerReply();
	}
	
	public void _hgetall(final String key){
		sendCommand(Command.HGETALL.raw,key);
	}
	
	public Map<String,String> hgetall(final String key){
		_hgetall(key);
		return BuilderFactory.STRING_MAP.build(getBinaryMultiBulkReply(),null);
	}
	
	public Map<String,Object> hgetall(final String key,final Map<String,Object> json){
		_hgetall(key);
		return BuilderFactory.JSON_MAP.build(getBinaryMultiBulkReply(),json);
	}	
	
	public void _hget(final String key,final String field){
		sendCommand(Command.HGET.raw,key,field);
	}
	
	public String hget(final String key,final String field){
		_hget(key,field);
		return getBulkReply();
	}
	
	public void _hexists(final String key,final String field){
		sendCommand(Command.HEXISTS.raw,key,field);
	}
	
	public boolean hexists(final String key,final String field){
		_hexists(key,field);
		return getIntegerReply() > 0;
	}
	
	public void _hdel(final String key,final String... fields){
		final List<byte[]> args = new ArrayList<byte[]>();
		args.add(SafeEncoder.encode(key));
	
		for (String field:fields){
			args.add(SafeEncoder.encode(field));
		}
		
		sendCommand(Command.HDEL.raw,args.toArray(new byte[args.size()][]));
	}
	
	public long hdel(final String key,final String... fields){
		_hdel(key,fields);
		return getIntegerReply();
	}

	public long hmax(final String key, final String field, final long latest) {
		_hmax(key,field,latest);
		return getIntegerReply();
	}
	
	public void _hmax(final String key,final String field,final long latest){
		sendCommand(
				Command.HMAX.raw,
				SafeEncoder.encode(key),
				SafeEncoder.encode(field),
				SafeEncoder.encode(latest)
				);		
	}
	
	public long hmin(final String key, final String field, final long latest) {
		_hmin(key,field,latest);
		return getIntegerReply();
	}
	
	public void _hmin(final String key,final String field,final long latest){
		sendCommand(
				Command.HMIN.raw,
				SafeEncoder.encode(key),
				SafeEncoder.encode(field),
				SafeEncoder.encode(latest)
				);		
	}	
	
	public long havg(final String key, final String field, final long latest,final double rate) {
		_havg(key,field,latest,rate);
		return getIntegerReply();
	}
	
	public void _havg(final String key,final String field,final long latest,final double rate){
		sendCommand(
				Command.HAVG.raw,
				SafeEncoder.encode(key),
				SafeEncoder.encode(field),
				SafeEncoder.encode(latest),
				SafeEncoder.encode(rate)
				);		
	}	
	
	public double hmaxbyfloat(final String key, final String field, final double latest) {
		_hmaxbyfloat(key,field,latest);
		return Double.parseDouble(getBulkReply());
	}
	
	public void _hmaxbyfloat(final String key,final String field,final double latest){
		sendCommand(
				Command.HMAXBYFLOAT.raw,
				SafeEncoder.encode(key),
				SafeEncoder.encode(field),
				SafeEncoder.encode(latest)
				);		
	}
	
	public double hminbyfloat(final String key, final String field, final double latest) {
		_hminbyfloat(key,field,latest);
		return Double.parseDouble(getBulkReply());
	}
	
	public void _hminbyfloat(final String key,final String field,final double latest){
		sendCommand(
				Command.HMINBYFLOAT.raw,
				SafeEncoder.encode(key),
				SafeEncoder.encode(field),
				SafeEncoder.encode(latest)
				);		
	}	
	
	public double havgbyfloat(final String key, final String field, final double latest,final double rate) {
		_havgbyfloat(key,field,latest,rate);
		return Double.parseDouble(getBulkReply());
	}
	
	public void _havgbyfloat(final String key,final String field,final double latest,final double rate){
		sendCommand(
				Command.HAVGBYFLOAT.raw,
				SafeEncoder.encode(key),
				SafeEncoder.encode(field),
				SafeEncoder.encode(latest),
				SafeEncoder.encode(rate)
				);		
	}		
}
