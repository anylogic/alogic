package com.logicbus.redis.kvalue;

import java.util.List;
import java.util.Map;

import com.logicbus.kvalue.common.Partition;
import com.logicbus.kvalue.core.HashRow;
import com.logicbus.kvalue.core.Table.DataType;
import com.logicbus.redis.client.Client;
import com.logicbus.redis.context.RedisContext;
import com.logicbus.redis.toolkit.HashTool;

public class RedisHashRow extends RedisBaseRow implements HashRow {

	public RedisHashRow(DataType _dataType, String _key,
			boolean _enableRWSplit, RedisContext _source, Partition _partition) {
		super(_dataType, _key, _enableRWSplit, _source, _partition);
	}

	
	public List<String> values() {
		Client client = getClient(true);
		
		try {
			HashTool tool = (HashTool)client.getToolKit(HashTool.class);
			return tool.hvals(key());
		}finally{
			client.poolClose();
		}
	}

	
	public List<String> keys() {
		Client client = getClient(true);
		try {
			HashTool tool = (HashTool)client.getToolKit(HashTool.class);
			return tool.hkeys(key());
		}finally{
			client.poolClose();
		}
	}

	
	public long del(String... fields) {
		Client client = getClient(false);
		try {
			HashTool tool = (HashTool)client.getToolKit(HashTool.class);
			return tool.hdel(key(), fields);
		}finally{
			client.poolClose();
		}
	}

	
	public boolean exists(String field) {
		Client client = getClient(true);
		try {
			HashTool tool = (HashTool)client.getToolKit(HashTool.class);
			return tool.hexists(key(), field);
		}finally{
			client.poolClose();
		}
	}

	
	public String get(String field, String dftValue) {
		Client client = getClient(true);
		try {
			HashTool tool = (HashTool)client.getToolKit(HashTool.class);
			return tool.hget(key(), field);
		}catch (Exception ex){
			return dftValue;
		}finally{
			client.poolClose();
		}
	}

	
	public long get(String field, long dftValue) {
		String value = get(field,"");
		try{
			return Long.parseLong(value);
		}catch (Exception ex){
			return dftValue;
		}
	}

	
	public double get(String field, double dftValue) {
		String value = get(field,"");
		try{
			return Double.parseDouble(value);
		}catch (Exception ex){
			return dftValue;
		}
	}

	
	public Map<String, String> getAll() {
		Client client = getClient(true);
		try {
			HashTool tool = (HashTool)client.getToolKit(HashTool.class);
			return tool.hgetall(key());
		}finally{
			client.poolClose();
		}
	}
	
	@Override
	public Map<String, Object> getAll(Map<String, Object> json) {
		Client client = getClient(true);
		try {
			HashTool tool = (HashTool)client.getToolKit(HashTool.class);
			return tool.hgetall(key(),json);
		}finally{
			client.poolClose();
		}
	}	

	
	public boolean set(String field, String value) {
		Client client = getClient(false);
		try {
			HashTool tool = (HashTool)client.getToolKit(HashTool.class);
			return tool.hset(key(), field, value);
		}finally{
			client.poolClose();
		}
	}

	
	public boolean set(String field, long value) {
		return set(field,String.valueOf(value));
	}

	
	public boolean set(String field, double value) {
		return set(field,String.valueOf(value));
	}

	
	public boolean mset(String... keyvalues) {
		Client client = getClient(false);
		try {
			HashTool tool = (HashTool)client.getToolKit(HashTool.class);
			return tool.hmset(key(), keyvalues);
		}finally{
			client.poolClose();
		}
	}

	
	public long incr(String field, long increment) {
		Client client = getClient(false);
		try {
			HashTool tool = (HashTool)client.getToolKit(HashTool.class);
			return tool.hincrby(key(), field, increment);
		}finally{
			client.poolClose();
		}
	}

	
	public double incr(String field, double increment) {
		Client client = getClient(false);
		try {
			HashTool tool = (HashTool)client.getToolKit(HashTool.class);
			return tool.hincrbyfloat(key(), field, increment);
		}finally{
			client.poolClose();
		}
	}

	
	public List<String> mget(String... fields) {
		Client client = getClient(false);
		try {
			HashTool tool = (HashTool)client.getToolKit(HashTool.class);
			return tool.hmget(key(), fields);
		}finally{
			client.poolClose();
		}
	}

	
	public long length() {
		Client client = getClient(true);
		try {
			HashTool tool = (HashTool)client.getToolKit(HashTool.class);
			return tool.hlen(key());
		}finally{
			client.poolClose();
		}
	}

}
