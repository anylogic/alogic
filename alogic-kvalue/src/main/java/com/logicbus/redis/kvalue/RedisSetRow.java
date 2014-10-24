package com.logicbus.redis.kvalue;

import java.util.List;

import com.logicbus.kvalue.common.Partition;
import com.logicbus.kvalue.core.SetRow;
import com.logicbus.kvalue.core.Table.DataType;
import com.logicbus.redis.client.Client;
import com.logicbus.redis.context.RedisContext;
import com.logicbus.redis.toolkit.SetTool;

public class RedisSetRow extends RedisBaseRow implements SetRow {

	public RedisSetRow(DataType _dataType, String _key, boolean _enableRWSplit,
			RedisContext _source, Partition _partition) {
		super(_dataType, _key, _enableRWSplit, _source, _partition);
	}

	
	
	public long add(String... elements) {
		Client client = getClient(false);
		try {
			SetTool tool = (SetTool)client.getToolKit(SetTool.class);
			return tool.sadd(key(), elements);
		}finally{
			client.poolClose();
		}
	}

	
	public List<String> getAll() {
		Client client = getClient(true);
		try {
			SetTool tool = (SetTool)client.getToolKit(SetTool.class);
			return tool.smembers(key());
		}finally{
			client.poolClose();
		}
	}

	
	public boolean contain(String element) {
		Client client = getClient(true);
		try {
			SetTool tool = (SetTool)client.getToolKit(SetTool.class);
			return tool.sismember(key(), element);
		}finally{
			client.poolClose();
		}
	}

	
	public long size() {
		Client client = getClient(true);
		try {
			SetTool tool = (SetTool)client.getToolKit(SetTool.class);
			return tool.size(key());
		}finally{
			client.poolClose();
		}
	}

	
	public String pop() {
		Client client = getClient(false);
		try {
			SetTool tool = (SetTool)client.getToolKit(SetTool.class);
			return tool.spop(key());
		}finally{
			client.poolClose();
		}
	}

	
	public long remove(String... elements) {
		Client client = getClient(false);
		try {
			SetTool tool = (SetTool)client.getToolKit(SetTool.class);
			return tool.srem(key(), elements);
		}finally{
			client.poolClose();
		}
	}

	
	public String random() {
		Client client = getClient(true);
		try {
			SetTool tool = (SetTool)client.getToolKit(SetTool.class);
			return tool.srandmember(key());
		}finally{
			client.poolClose();
		}
	}

	
	public List<String> diff(String... others) {
		Client client = getClient(false);
		try {
			SetTool tool = (SetTool)client.getToolKit(SetTool.class);
			
			return tool.sdiff(key(), others);
		}finally{
			client.poolClose();
		}
	}

	
	public long diffStore(String dstKey, String... others) {
		Client client = getClient(false);
		try {
			SetTool tool = (SetTool)client.getToolKit(SetTool.class);
			
			return tool.sdiffstore(dstKey,key(), others);
		}finally{
			client.poolClose();
		}
	}

	
	public List<String> inter(String... others) {
		Client client = getClient(false);
		try {
			SetTool tool = (SetTool)client.getToolKit(SetTool.class);
			
			return tool.sinter(key(), others);
		}finally{
			client.poolClose();
		}
	}

	
	public long interStore(final String dstKey, String... others) {
		Client client = getClient(false);
		try {
			SetTool tool = (SetTool)client.getToolKit(SetTool.class);
			
			return tool.sinterstore(dstKey,key(), others);
		}finally{
			client.poolClose();
		}
	}

	
	public List<String> union(String... others) {
		Client client = getClient(false);
		try {
			SetTool tool = (SetTool)client.getToolKit(SetTool.class);
			
			return tool.sunion(key(), others);
		}finally{
			client.poolClose();
		}
	}

	
	public long unionStore(final String dstKey,String subkey, String... others) {
		Client client = getClient(false);
		try {
			SetTool tool = (SetTool)client.getToolKit(SetTool.class);
			
			return tool.sunionstore(dstKey,key(), others);
		}finally{
			client.poolClose();
		}
	}

}
