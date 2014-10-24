package com.logicbus.redis.kvalue;

import java.util.List;

import com.logicbus.kvalue.common.Partition;
import com.logicbus.kvalue.core.ListRow;
import com.logicbus.kvalue.core.Table.DataType;
import com.logicbus.redis.client.Client;
import com.logicbus.redis.context.RedisContext;
import com.logicbus.redis.toolkit.ListTool;

public class RedisListRow extends RedisBaseRow implements ListRow {

	public RedisListRow(DataType _dataType, String _key,
			boolean _enableRWSplit, RedisContext _source, Partition _partition) {
		super(_dataType, _key, _enableRWSplit, _source, _partition);
	}

	
	public long insert(String pivot, String value, boolean insertAfter) {
		Client client = getClient(false);
		try {
			ListTool tool = (ListTool)client.getToolKit(ListTool.class);
			return tool.linsert(key(), pivot, value, !insertAfter);
		}finally{
			client.poolClose();
		}
	}

	
	public long length() {
		Client client = getClient(true);
		try {
			ListTool tool = (ListTool)client.getToolKit(ListTool.class);
			return tool.llen(key());
		}finally{
			client.poolClose();
		}
	}

	
	public String get(long index, String dftValue) {
		Client client = getClient(true);
		try {
			ListTool tool = (ListTool)client.getToolKit(ListTool.class);
			return tool.lget(key(), index);
		}catch (Exception ex){
			return dftValue;
		}finally{
			client.poolClose();
		}
	}

	
	public boolean set(long index, String value) {
		Client client = getClient(false);
		try {
			ListTool tool = (ListTool)client.getToolKit(ListTool.class);
			return tool.lset(key(), index, value);
		}finally{
			client.poolClose();
		}
	}

	
	public String leftPop(boolean block) {
		Client client = getClient(false);
		try {
			ListTool tool = (ListTool)client.getToolKit(ListTool.class);
			return tool.lpop(key());
		}finally{
			client.poolClose();
		}
	}

	
	public String rightPop(boolean block) {
		Client client = getClient(false);
		try {
			ListTool tool = (ListTool)client.getToolKit(ListTool.class);
			return tool.rpop(key());
		}finally{
			client.poolClose();
		}
	}

	
	public long leftPush(String... values) {
		Client client = getClient(false);
		try {
			ListTool tool = (ListTool)client.getToolKit(ListTool.class);
			return tool.lpush(key(),values);
		}finally{
			client.poolClose();
		}
	}

	
	public long rightPush(String... values) {
		Client client = getClient(false);
		try {
			ListTool tool = (ListTool)client.getToolKit(ListTool.class);
			return tool.rpush(key(),values);
		}finally{
			client.poolClose();
		}
	}

	
	public List<String> range(long start, long stop) {
		Client client = getClient(true);
		try {
			ListTool tool = (ListTool)client.getToolKit(ListTool.class);
			return tool.lrange(key(), start, stop);
		}finally{
			client.poolClose();
		}
	}

	
	public long remove(String value, long count) {
		Client client = getClient(false);
		try {
			ListTool tool = (ListTool)client.getToolKit(ListTool.class);
			return tool.lrem(key(), value, count);
		}finally{
			client.poolClose();
		}
	}

	
	public long trim(long start, long stop) {
		Client client = getClient(false);
		try {
			ListTool tool = (ListTool)client.getToolKit(ListTool.class);
			tool.ltrim(key(), start, stop);
			return stop - start;
		}finally{
			client.poolClose();
		}
	}

}
