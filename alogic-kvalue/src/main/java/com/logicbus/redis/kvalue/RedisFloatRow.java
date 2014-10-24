package com.logicbus.redis.kvalue;

import java.util.concurrent.TimeUnit;

import com.logicbus.kvalue.common.Partition;
import com.logicbus.kvalue.core.FloatRow;
import com.logicbus.kvalue.core.Table.DataType;
import com.logicbus.redis.client.Client;
import com.logicbus.redis.context.RedisContext;
import com.logicbus.redis.params.SetParams;
import com.logicbus.redis.toolkit.StringTool;

public class RedisFloatRow extends RedisBaseRow implements FloatRow{

	public RedisFloatRow(DataType _dataType, String _key,
			boolean _enableRWSplit, RedisContext _source, Partition _partition) {
		super(_dataType, _key, _enableRWSplit, _source, _partition);
	}

	
	public boolean set(double value) {
		Client client = getClient(false);
		try {
			StringTool tool = (StringTool)client.getToolKit(StringTool.class);
			return tool.set(key(), String.valueOf(value));
		}finally{
			client.poolClose();
		}
	}

	
	public boolean set(double value, long ttl, boolean writeIfExist,
			boolean writeIfNotExist) {
		Client client = getClient(false);
		
		try {
			StringTool tool = (StringTool)client.getToolKit(StringTool.class);
			
			SetParams params = new SetParams();
			params.ttl(ttl, TimeUnit.MILLISECONDS);
			if (writeIfExist){
				params.onlySet(false);
			}else{
				if (writeIfNotExist){
					params.onlySet(true);
				}
			}
			
			return tool.set(key(), String.valueOf(value),params);
		}finally{
			client.poolClose();
		}
	}

	
	public double get(double dftValue) {
		Client client = getClient(true);
		try {
			StringTool tool = (StringTool)client.getToolKit(StringTool.class);
			String value = tool.get(key(), "");
			try {
				return Double.parseDouble(value);
			}catch (Exception ex){
				return dftValue;
			}
		}finally{
			client.poolClose();
		}
	}

	
	public double incr(double increment) {
		Client client = getClient(false);
		try {
			StringTool tool = (StringTool)client.getToolKit(StringTool.class);
			return tool.incrbyfloat(key(), increment);
		}finally{
			client.poolClose();
		}
	}
}
