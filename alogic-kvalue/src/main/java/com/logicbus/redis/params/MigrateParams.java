package com.logicbus.redis.params;

import com.logicbus.redis.client.Params;
import com.logicbus.redis.util.SafeEncoder;

public class MigrateParams extends Params {
	
	protected final byte [] KW_COPY = SafeEncoder.encode("COPY");
	
	protected final byte [] KW_REPLACE = SafeEncoder.encode("REPLACE");
	
	public MigrateParams copy(){
		add(KW_COPY);
		return this;
	}
	
	public MigrateParams replace(){
		add(KW_REPLACE);
		return this;
	}
}
