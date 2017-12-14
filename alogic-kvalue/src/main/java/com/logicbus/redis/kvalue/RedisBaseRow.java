package com.logicbus.redis.kvalue;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.logicbus.kvalue.common.Partition;
import com.logicbus.kvalue.core.KeyValueRow;
import com.logicbus.kvalue.core.Table;
import com.logicbus.redis.client.Client;
import com.logicbus.redis.context.RedisContext;
import com.logicbus.redis.context.RedisPool;
import com.logicbus.redis.toolkit.KeyTool;
import com.logicbus.redis.util.RedisException;

public class RedisBaseRow implements KeyValueRow {
	protected String key;
	protected boolean enableRWSplit = false;
	protected RedisContext source = null;
	protected Partition partition = null;
	protected Table.DataType dataType;
	
	public RedisBaseRow(Table.DataType _dataType,String _key,boolean _enableRWSplit,RedisContext _source,Partition _partition){
		dataType = _dataType;
		key = _key;
		enableRWSplit = _enableRWSplit;
		source = _source;
		partition = _partition;		
	}

	public String key(){
		return key;
	}
	/**
	 * 选取合适的Client
	 * 
	 * @param readOnly 是否只读
	 * @return Client实例
	 */
	protected Client getClient(boolean readOnly){
		//主数据源
		String src = partition.getSource();
		if (readOnly && enableRWSplit){
			//如果是只读操作，且允许读写分离
			//只读数据源
			String [] replicates = partition.getReplicates();
			//在所有replicates中随机找一个
			if (replicates != null && replicates.length > 0)
			{
				Random r = new Random();
				int idx = r.nextInt() & Integer.MAX_VALUE % replicates.length;	
				src = replicates[idx];
			}
		}

		Client client = getClient(src);
		if (client == null){
			throw new RedisException("core.e1003",
					"Can not get a client by source name:" + src);
		}
		
		return client;
	}

	private Client getClient(String src){
		RedisPool pool = source.getPool(src);
		
		return pool == null ? null : pool.getClient();
	}
	
	
	public boolean delete() {
		Client client = getClient(false);
		try{
			KeyTool tool = (KeyTool)client.getToolKit(KeyTool.class);
			return tool.del(key()) > 0;
		}finally{
			client.poolClose();
		}
	}	
	
	
	public boolean exists() {
		Client client = getClient(true);
		try {
			KeyTool tool = (KeyTool)client.getToolKit(KeyTool.class);
			return tool.exist(key());
		}finally{
			client.poolClose();
		}
	}

	
	public String type() {
		return dataType.name();
	}

	
	public boolean ttl(long time, TimeUnit timeUnit) {
		Client client = getClient(true);
		try {
			KeyTool tool = (KeyTool)client.getToolKit(KeyTool.class);
			return tool.expire(key(), time, timeUnit);
		}finally{
			client.poolClose();
		}
	}

	
	public boolean ttlAt(long time, TimeUnit timeUnit) {
		Client client = getClient(true);
		
		try {
			KeyTool tool = (KeyTool)client.getToolKit(KeyTool.class);
			return tool.expireat(key(),timeUnit.toMillis(time));
		}finally{
			client.poolClose();
		}
	}

	
	public long ttl() {
		Client client = getClient(true);
		try {
			KeyTool tool = (KeyTool)client.getToolKit(KeyTool.class);
			return tool.ttl(key());
		}finally{
			client.poolClose();
		}
	}

	
	public boolean persist() {
		Client client = getClient(true);
		try {
			KeyTool tool = (KeyTool)client.getToolKit(KeyTool.class);
			return tool.persist(key());
		}finally{
			client.poolClose();
		}
	}

}
