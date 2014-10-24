package com.logicbus.redis.kvalue;

import com.logicbus.kvalue.common.Partition;
import com.logicbus.kvalue.core.BitRow;
import com.logicbus.kvalue.core.Table.DataType;
import com.logicbus.redis.client.Client;
import com.logicbus.redis.context.RedisContext;
import com.logicbus.redis.toolkit.StringTool;

public class RedisBitRow extends RedisBaseRow implements BitRow {


	public RedisBitRow(DataType _dataType, String _key, boolean _enableRWSplit,
			RedisContext _source, Partition _partition) {
		super(_dataType, _key, _enableRWSplit, _source, _partition);
	}


	
	public boolean getBit(long offset) {
		Client client = getClient(false);
		try {
			StringTool tool = (StringTool)client.getToolKit(StringTool.class);
			return tool.getbit(key(), offset) > 0;
		}finally{
			client.poolClose();
		}
	}


	
	public boolean setBit(long offset, boolean value) {
		Client client = getClient(false);
		try {
			StringTool tool = (StringTool)client.getToolKit(StringTool.class);
			return tool.setbit(key(), offset, value) > 0;
		}finally{
			client.poolClose();
		}
	}


	
	public long bitCount(long start, long end) {
		Client client = getClient(false);
		try {
			StringTool tool = (StringTool)client.getToolKit(StringTool.class);
			return tool.bitcount(key(), start, end);
		}finally{
			client.poolClose();
		}
	}
}
