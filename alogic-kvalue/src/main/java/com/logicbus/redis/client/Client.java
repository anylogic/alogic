package com.logicbus.redis.client;

import java.lang.reflect.Constructor;

import com.alogic.pool.CloseAware;
import com.alogic.pool.PooledCloseable;
import com.logicbus.redis.util.RedisException;


/**
 * Redis客户端
 * @author duanyy
 *
 * @version 1.0.0.1 [20141106 duanyy] <br>
 * - 修正设置index或password之后死循环的bug. <br>
 * 
 * @version 1.6.6.9 [20161209 duanyy] <br>
 * - 从新的框架下继承 <br>
 */
public class Client extends Connection implements PooledCloseable{
	
	/**
	 * 当前数据库 
	 */
	private int db = 0;
	
	protected CloseAware closeAware = null;	

	/**
	 * 验证密码
	 */
	private String password;	
	
	/**
	 * 设置当前DB
	 * @param dbIndex 
	 */
	public void setCurrentDB(final int dbIndex){
		db = dbIndex;
	}
	
	/**
	 * 获取当前DB
	 * @return 当前的DB
	 */
	public long getCurrentDB(){
		return db;
	}
	
	/**
	 * 设置验证密码
	 * @param pwd
	 */
	public void setPassword(final String pwd){
		password = pwd;
	}
	
	public Client(final String host) {
		super(host);
	}

	public Client(final String host, final int port) {
		super(host, port);
	}
	
	public Client(final String host, final int port, final int dbIndex){
		super(host, port);
		db = dbIndex;
	}
	
	public Client(final String host, final int port, final String pwd){
		super(host, port);
		password = pwd;
	}
	
	public Client(final String host, final int port, final String pwd, final int dbIndex){
		super(host, port);
		db = dbIndex;
		password = pwd;
	}
	

	public void connect() {
		super.connect();
		if (password != null && password.length() > 0) {
			auth(password);
		}
		if (db > 0) {
			select(db);
		}
	}
	
	
	public void disconnect() {
		db = 0;
		if (isConnected(false)){
			// to ask the server to close
			try {
				quit();
			}catch (Exception ex){
				
			}
		}
		super.disconnect();
	}
	
	@Override
	public void poolClose() {
		if (closeAware != null){
			closeAware.closeObject(this);
		}
	}

	@Override
	public void register(CloseAware listener) {
		closeAware = listener;
	}

	@Override
	public void unregister(CloseAware listener) {
		closeAware = null;
	}		
	
	public Toolkit getToolKit(Class<? extends Toolkit> clazz){
		try {
			Constructor<? extends Toolkit> c = clazz.getConstructor(Connection.class);
			return c.newInstance(this);
		}catch (Exception e){			
			throw new RedisException("client","can not get toolkit",e);
		}
	}
}
