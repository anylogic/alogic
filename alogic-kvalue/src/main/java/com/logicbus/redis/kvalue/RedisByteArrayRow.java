package com.logicbus.redis.kvalue;

import java.util.concurrent.TimeUnit;

import com.logicbus.kvalue.common.Partition;
import com.logicbus.kvalue.core.ByteArrayRow;
import com.logicbus.kvalue.core.Table.DataType;
import com.logicbus.redis.client.Client;
import com.logicbus.redis.context.RedisContext;
import com.logicbus.redis.params.SetParams;
import com.logicbus.redis.toolkit.ByteArrayTool;

public class RedisByteArrayRow extends RedisBaseRow implements ByteArrayRow {


	public RedisByteArrayRow(DataType _dataType, String _key,
			boolean _enableRWSplit, RedisContext _source, Partition _partition) {
		super(_dataType, _key, _enableRWSplit, _source, _partition);
	}

	
	public boolean set(byte[] value) {
		Client client = getClient(false);
		try {
			ByteArrayTool tool = (ByteArrayTool)client.getToolKit(ByteArrayTool.class);
			return tool.set(key(), value);
		}finally{
			client.poolClose();
		}
	}

	
	public boolean set(byte[] value, long ttl, boolean writeIfExist,
			boolean writeIfNotExist) {
		Client client = getClient(false);
		
		try {
			ByteArrayTool tool = (ByteArrayTool)client.getToolKit(ByteArrayTool.class);
			
			SetParams params = new SetParams();
			params.ttl(ttl, TimeUnit.MILLISECONDS);
			if (writeIfExist){
				params.onlySet(false);
			}else{
				if (writeIfNotExist){
					params.onlySet(true);
				}
			}
			
			return tool.set(key(), value,params);
		}finally{
			client.poolClose();
		}
	}

	
	public byte[] get(byte[] dftValue) {
		Client client = getClient(true);
		try {
			ByteArrayTool tool = (ByteArrayTool)client.getToolKit(ByteArrayTool.class);
			return tool.get(key(), dftValue);
		}finally{
			client.poolClose();
		}
	}

}
