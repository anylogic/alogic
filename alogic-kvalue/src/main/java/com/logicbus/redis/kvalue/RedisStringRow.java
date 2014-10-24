package com.logicbus.redis.kvalue;

import java.util.concurrent.TimeUnit;

import com.logicbus.kvalue.common.Partition;
import com.logicbus.kvalue.core.StringRow;
import com.logicbus.kvalue.core.Table.DataType;
import com.logicbus.redis.client.Client;
import com.logicbus.redis.context.RedisContext;
import com.logicbus.redis.params.SetParams;
import com.logicbus.redis.toolkit.StringTool;

/**
 * StringValueTool的Redis实现
 * 
 * @author duanyy
 *
 */
public class RedisStringRow extends RedisBaseRow implements StringRow {

	public RedisStringRow(DataType _dataType, String _key,
			boolean _enableRWSplit, RedisContext _source, Partition _partition) {
		super(_dataType, _key, _enableRWSplit, _source, _partition);
	}

	
	public boolean set(String value) {
		Client client = getClient(false);
		try {
			StringTool tool = (StringTool)client.getToolKit(StringTool.class);
			return tool.set(key(), value);
		}finally{
			client.poolClose();
		}
	}

	
	public boolean set(String value, long ttl, boolean writeIfExist,
			boolean writeifNotExist) {
		Client client = getClient(false);
		
		try {
			StringTool tool = (StringTool)client.getToolKit(StringTool.class);
			
			SetParams params = new SetParams();
			params.ttl(ttl, TimeUnit.MILLISECONDS);
			if (writeIfExist){
				params.onlySet(false);
			}else{
				if (writeifNotExist){
					params.onlySet(true);
				}
			}
			
			return tool.set(key(), value,params);
		}finally{
			client.poolClose();
		}
	}

	
	public String get(String dftValue) {
		Client client = getClient(true);
		try {
			StringTool tool = (StringTool)client.getToolKit(StringTool.class);
			return tool.get(key(), dftValue);
		}finally{
			client.poolClose();
		}
	}

	
	public long setRange(long offset, String value) {
		Client client = getClient(false);
		try {
			StringTool tool = (StringTool)client.getToolKit(StringTool.class);
			return tool.setrange(key(), offset, value);
		}finally{
			client.poolClose();
		}
	}

	
	public long append(String value) {
		Client client = getClient(false);
		try {
			StringTool tool = (StringTool)client.getToolKit(StringTool.class);
			return tool.append(key(), value);
		}finally{
			client.poolClose();
		}
	}

	
	public long strlen() {
		Client client = getClient(true);
		try {
			StringTool tool = (StringTool)client.getToolKit(StringTool.class);
			return tool.strlen(key());
		}finally{
			client.poolClose();
		}
	}

}
