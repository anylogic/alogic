package com.logicbus.redis.toolkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.logicbus.redis.client.Connection;
import com.logicbus.redis.client.Toolkit;
import com.logicbus.redis.params.MigrateParams;
import com.logicbus.redis.params.ScanParams;
import com.logicbus.redis.result.ScanResult;
import com.logicbus.redis.util.BuilderFactory;
import com.logicbus.redis.util.RedisDataException;
import com.logicbus.redis.util.SafeEncoder;

/**
 * Key相关的工具集
 * 
 * @author duanyy
 *
 */
public class KeyTool extends Toolkit {

	public KeyTool(Connection _conn) {
		super(_conn);
	}

	public static enum Command {
		//常用
		DEL,
		EXISTS,
		TYPE,
		KEYS,
		RENAME,
		RENAMENX,
		SCAN,
		
		//生命期
		EXPIRE,
		EXPIREAT,
		TTL,
		PERSIST,
		PEXPIRE,
		PEXPIREAT,
		PTTL,
		
		//很少用		
		DUMP,
		RESTORE,
		MIGRATE,
		MOVE,
		OBJECT,
		RANDOMKEY;
		
		public final byte [] raw;		
		Command(){
			raw = SafeEncoder.encode(name());
		}
	}
	
	/**
	 * to delete keys
	 * @param keys
	 */
	public void _del(final String... keys){
		sendCommand(Command.DEL.raw, keys);
	}
	
	/**
	 * to delete keys
	 * @param keys
	 * @return the number of keys that were removed.
	 */
	public long del(final String... keys){
		_del(keys);
		return getIntegerReply();
	}
		
	/**
	 * to determine if a key exists
	 * @param key
	 */
	public void _exist(final String key){
		sendCommand(Command.EXISTS.raw,key);
	}
	
	/**
	 * to determine if a key exists
	 * @param key
	 * @return true if exists otherwise false
	 */
	public boolean exist(final String key){
		_exist(key);
		return getIntegerReply() > 0;
	}
	
	/**
	 * to determine the type stored at key
	 * @param key
	 */
	public void _type(final String key){
		sendCommand(Command.TYPE.raw,key);
	}
	
	/**
	 * to determine the type stored at key
	 * @param key
	 * @return type of key, or none when key does not exist.
	 */
	public String type(final String key){
		_type(key);
		return getStatusCodeReply();
	}
	
	/**
	 * to get all the keys matching pattern
	 * 
	 * <br>
	 * The time complexity is O(N) with N being the number of keys in the database.
	 * It should be used carefully.
	 *  
	 * @param pattern
	 */
	public void _keys(final String pattern){
		sendCommand(Command.KEYS.raw,pattern);
	}
	
	/**
	 * to get all the keys matching pattern
	 * 
	 * <br>
	 * The time complexity is O(N) with N being the number of keys in the database.
	 * It should be used carefully.
	 *  
	 * @param pattern
	 * @return list of keys matching pattern.
	 */	
	public Set<String> keys(final String pattern){
		_keys(pattern);
		return BuilderFactory.STRING_SET.build(
				getBinaryMultiBulkReply()
				);
	}
	
	/**
	 * to rename the key to a new key
	 * @param key
	 * @param newKey
	 */
	public void _rename(final String key,final String newKey){
		sendCommand(Command.RENAME.raw,key,newKey);
	}
	
	/**
	 * to rename the key to a new key
	 * @param key
	 * @param newKey
	 */
	public boolean rename(final String key,final String newKey){
		_rename(key,newKey);
		try {
			getStatusCodeReply();
			return true;
		}catch (RedisDataException ex){
			return false;
		}
	}
	
	/**
	 * to rename the key to a new key if the new key does not yet exist.
	 * @param key
	 * @param newKey
	 */
	public void _renamenx(final String key,final String newKey){
		sendCommand(Command.RENAMENX.raw,key,newKey);
	}
	
	/**
	 * to rename the key to a new key if the new key does not yet exist.
	 * @param key
	 * @param newKey
	 * @return true if the key was renamed
	 */
	public boolean renamenx(final String key,final String newKey){
		_renamenx(key,newKey);
		return getIntegerReply() > 0;
	}
	
	/**
	 * to scan the keys
	 * @param cursor
	 * @param params
	 */
	public void _scan(final String cursor,ScanParams params){
		final List<byte[]> args = new ArrayList<byte[]>();
		args.add(SafeEncoder.encode(cursor));
		args.addAll(params.getParams());
		sendCommand(Command.SCAN.raw, args.toArray(new byte[args.size()][]));
	}
	
	/**
	 * to scan the keys
	 * @param cursor
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ScanResult<String> scan(final String cursor,ScanParams params){
		_scan(cursor,params);
		List<Object> result = getObjectMultiBulkReply();
		
		String newcursor = new String((byte[]) result.get(0));
		List<String> results = new ArrayList<String>();
		List<byte[]> rawResults = (List<byte[]>) result.get(1);
		for (byte[] bs : rawResults) {
		    results.add(SafeEncoder.encode(bs));
		}
		return new ScanResult<String>(newcursor, results);
	}
	
	/**
	 * to set a key's time to live
	 * @param key
	 * @param time
	 * @param tu
	 */
	public void _expire(final String key,final long time,final TimeUnit tu){
		long ttl = tu.toMillis(time);
		sendCommand(Command.PEXPIRE.raw,SafeEncoder.encode(key),SafeEncoder.encode(ttl));
	}
	
	/**
	 * to set a key's time to live
	 * @param key
	 * @param time
	 * @param tu
	 * @return true if the timeout is set,false if key does not exist or the timeout can not be set.
	 */
	public boolean expire(final String key,final long time,final TimeUnit tu){
		_expire(key,time,tu);
		return getIntegerReply() > 0;
	}
	
	/**
	 * to set the expiration for a key in milliseconds
	 * @param key
	 * @param deadtime
	 */
	public void _expireat(final String key,final long deadtime){
		sendCommand(Command.PEXPIREAT.raw,SafeEncoder.encode(key),SafeEncoder.encode(deadtime));
	}
	
	/**
	 * to set the expiration for a key in milliseconds
	 * @param key
	 * @param deadtime
	 * @return true if the timeout is set,false if key does not exist or the timeout can not be set.
	 */
	public boolean expireat(final String key,final long deadtime){
		_expireat(key,deadtime);
		return getIntegerReply() > 0;
	}
	
	/**
	 * to get the time to live for a key
	 * @param key
	 */
	public void _ttl(final String key){
		sendCommand(Command.PTTL.raw,key);
	}
	
	/**
	 * to get the time to live for a key in milliseconds
	 * @param key
	 * @return the time to live ,or a negative value in order to signal an error.
	 */
	public long ttl(final String key){
		_ttl(key);
		return getIntegerReply();
	}
	
	/**
	 * to remove the existing timeout on a key
	 * @param key
	 */
	public void _persist(final String key){
		sendCommand(Command.PERSIST.raw,key);
	}
	
	/**
	 * to remove the existing timeout on a key
	 * @param key
	 * @return true if the timeout was removed,false if key does not exist or does not have an associated timeout.
	 */
	public boolean persist(final String key){
		_persist(key);
		return getIntegerReply() > 0;
	}
	
	/**
	 * to transfer a key from a Redis instance to another one.
	 * @param host
	 * @param port
	 * @param key
	 * @param db
	 * @param timeout
	 * @param params
	 */
	public void _migrate(final String host,final int port,final String key,final int db,final int timeout,MigrateParams params){
		final List<byte[]> args = new ArrayList<byte[]>();
		args.add(SafeEncoder.encode(host));
		args.add(SafeEncoder.encode(port));
		args.add(SafeEncoder.encode(key));
		args.add(SafeEncoder.encode(db));
		args.add(SafeEncoder.encode(timeout));
		args.addAll(params.getParams());
		
		sendCommand(Command.MIGRATE.raw,args.toArray(new byte[args.size()][]));
	}
	
	/**
	 * to transfer a key from a Redis instance to another one.
	 * @param host
	 * @param port
	 * @param key
	 * @param db
	 * @param timeout
	 * @param params
	 */
	public void migrate(final String host,final int port,final String key,final int db,final int timeout,MigrateParams params){
		_migrate(host,port,key,db,timeout,params);
		getStatusCodeReply();
	}
	
	/**
	 * move key from the current selected database to the specified destination database
	 * @param key
	 * @param db
	 */
	public void _move(final String key,final int db){
		sendCommand(Command.MOVE.raw,SafeEncoder.encode(key),SafeEncoder.encode(db));
	}
	
	/**
	 * move key from the current selected database to the specified destination database
	 * @param key
	 * @param db
	 * @return true if key was moved,false if key was not moved.
	 */
	public boolean move(final String key,final int db){
		_move(key,db);
		return getIntegerReply() > 0;
	}
}
