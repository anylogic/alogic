package com.alogic.rpc.serializer.kryo;

import com.anysoft.pool.QueuedPool2;
import com.anysoft.util.BaseException;
import com.anysoft.util.Settings;

/**
 * kryo连接池
 * @author yyduan
 * @since 1.6.7.15
 */
public class KryoPool extends QueuedPool2<KryoWrapper>{
	private static KryoPool instance = null;
	
	public KryoPool(){
		
	}
	
	@Override
	protected String getIdOfMaxQueueLength() {
		return "rpc.kryo.maxQueueLength";
	}

	@Override
	protected String getIdOfIdleQueueLength() {
		return "rpc.kryo.idleQueueLength";
	}

	@Override
	protected KryoWrapper createObject() throws BaseException {
		return new KryoWrapper();
	}

	public static KryoPool get(){
		if (instance == null){
			synchronized (KryoPool.class){
				if (instance == null){
					instance = new KryoPool();
					instance.create(Settings.get());
				}
			}
		}
		
		return instance;
	}
}
