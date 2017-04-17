package com.alogic.rpc.serializer.kryo;

import java.io.InputStream;
import java.io.OutputStream;

import com.alogic.rpc.CallException;
import com.alogic.rpc.serializer.Serializer;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * 基于kryo的序列化器
 * 
 * @author duanyy
 * @since 1.6.7.15
 * 
 * @version 1.6.8.7 [20170417 duanyy]<br>
 * - 序列化接口增加上下文 <br>
 */
public class KryoSerializer extends Serializer.Abstract {
	protected KryoPool pool = null;
	protected int priority = 10;
	protected int timeout = 3000;
	@Override
	public void configure(Properties p) {
		timeout = PropertiesConstants.getInt(p,"rpc.kryo.pool.timeout", timeout);
		
		pool = new KryoPool();
		pool.create(p);
	}

	@Override
	public <D> D readObject(InputStream in,Class<D> clazz,Properties ctx){
		KryoWrapper kyro = pool.borrowObject(priority, timeout);
		if (kyro != null){
			try {
				return kyro.getKryo().readObject(new Input(in), clazz);
			}finally{
				pool.returnObject(kyro);
			}
		}else{
			throw new CallException("core.can_not_get_kyro","Can not get a kyro serializer.");
		}
	}

	@Override
	public void writeObject(OutputStream out, Object object,Properties ctx) {
		KryoWrapper kyro = pool.borrowObject(priority, timeout);
		if (kyro != null){
			try {
				Output output = new Output(out);
				kyro.getKryo().writeObject(output, object);
				output.flush();
			}finally{
				pool.returnObject(kyro);
			}
		}else{
			throw new CallException("core.can_not_get_kyro","Can not get a kyro serializer.");
		}
	}

}
