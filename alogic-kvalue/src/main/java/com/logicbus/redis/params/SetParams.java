package com.logicbus.redis.params;

import java.util.concurrent.TimeUnit;

import com.logicbus.redis.client.Params;
import com.logicbus.redis.util.SafeEncoder;

public class SetParams extends Params {
	
	protected final static byte [] KW_PX = SafeEncoder.encode("PX");
	
	public SetParams ttl(long time,TimeUnit unit){
		add(KW_PX);
		add(SafeEncoder.encode(unit.toMillis(time)));
		return this;
	}
	
	public SetParams onlySet(boolean nxOrxx){
		add(SafeEncoder.encode(nxOrxx ? "NX" : "XX"));
		return this;
	}
}
