package com.logicbus.dbcp.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.metrics.stream.MetricsCollector;
import com.alogic.pool.impl.Queued;
import com.anysoft.loadbalance.LoadBalance;
import com.anysoft.loadbalance.LoadBalanceFactory;
import com.anysoft.util.Counter;
import com.anysoft.util.KeyGen;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.dbcp.context.DbcpSource;
import com.logicbus.dbcp.core.ConnectionPool;
import com.logicbus.dbcp.util.ConnectionPoolStat;


/**
 * ConnectionPool 虚基类
 * @author duanyy
 * 
 * @since 1.2.9
 * 
 * @version 1.2.9.1 [20141017 duanyy] <br>
 * - ConnectionPool有更新 <br>
 * - 实现Reportable接口 <br>
 * - ConnectionPoolStat模型更新 <br>
 * 
 * @version 1.2.9.3 [20141022 duanyy] <br>
 * - 增加对读写分离的支持 <br>
 * 
 * @version 1.3.0.2 [20141106 duanyy] <br>
 * - 读写分离功能存在bug，暂时取消 <br>
 * 
 * @version 1.6.3.11 [20150402 duanyy] <br>
 * - 增加{@link #recycle(Connection, boolean)},获取客户的使用反馈,以便连接池的处理 <br>
 * - 将所管理的Connection改变为ManagedConnection，以便支持读写分离<br>
 * 
 * @version 1.6.3.13 [20150408 duanyy] <br>
 * - 增加对Connection的有效性判断 <br>
 * 
 * @version 1.6.3.15 [20150409 duanyy] <br>
 * - 修正Connection.isValid出异常的问题 <br>
 * 
 * @version 1.6.3.17 [20150413 duanyy] <br>
 * - 增加控制属性timeout <br>
 * 
 * @version 1.6.6.9 [20161209 duanyy] <br>
 * - 从新的框架下继承 <br>
 * 
 * @version 1.6.9.7 [20170802 duanyy] <br>
 * - 增加强制读写分离功能，应对数据中间层之类的场景 <br>
 * - 可自动为Connection设置autocommit属性 <br>
 * 
 */
abstract public class AbstractConnectionPool extends Queued implements ConnectionPool{
	protected Counter stat = null;
	protected LoadBalance<ReadOnlySource> loadBalance = null;
	protected boolean testConn = true;
	protected boolean autoCommit = true;
	protected boolean enableRWSForce = false;
	
	@Override
	public void configure(Properties props){
		boolean enableStat = true;
		testConn = PropertiesConstants.getBoolean(props, "dbcp.test", testConn);
		autoCommit = PropertiesConstants.getBoolean(props, "dbcp.autoCommit", autoCommit);
		enableRWSForce = PropertiesConstants.getBoolean(props, "dbcp.enableRWS", enableRWSForce);
		enableStat = PropertiesConstants.getBoolean(props, "dbcp.stat.enable", enableStat);
		
		if (enableStat){
			stat = createCounter(props);
		}else{
			stat = null;
		}
		
		//loadbalance
		{
			String lbModule = props.GetValue("loadbalance.module", "Rand");
			
			LoadBalanceFactory<ReadOnlySource> f = new LoadBalanceFactory<ReadOnlySource>();
			
			loadBalance = f.newInstance(lbModule, props);
		}
		
		super.configure(props);
	}
	
	protected Counter createCounter(Properties p){
		String module = PropertiesConstants.getString(p,"dbcp.stat.module", ConnectionPoolStat.class.getName());
		try {
			return Counter.TheFactory.getCounter(module, p);
		}catch (Exception ex){
			logger.warn("Can not create dbcp counter:" + module + ",default counter is instead.");
			return new ConnectionPoolStat(p);
		}
	}
	
	
	public void report(Element xml) {
		if (xml != null){
			Document doc = xml.getOwnerDocument();
			
			//pool
			{
				Element _pool = doc.createElement("pool");
				super.report(_pool);
				xml.appendChild(_pool);
			}
			
			// stat
			if (stat != null){
				Element _stat = doc.createElement("stat");
				stat.report(_stat);
				xml.appendChild(_stat);
			}
		}
	}

	
	public void report(Map<String, Object> json) {
		if (json != null){
			//pool
			{
				Map<String,Object> _pool = new HashMap<String,Object>();
				super.report(_pool);
				json.put("pool", _pool);
			}
			
			if (stat != null){
				Map<String,Object> _stat = new HashMap<String,Object>();
				stat.report(_stat);
				json.put("stat", _stat);
			}
		}
	}

	
	public void report(MetricsCollector collector) {
		// to be define
	}

	
	public Connection getConnection(int timeout, boolean enableRWS) {
		Connection conn = null;
		if (enableRWSForce || enableRWS){
			conn = selectReadSource(timeout);
		}
		
		if (conn == null){
			long start = System.nanoTime();
			try {
				int _timeout = timeout > getMaxWait() ? getMaxWait() : timeout;
				conn = borrowObject(0,_timeout);		
				
				if (testConn){
					try {
						if (!conn.isValid(1)){
							//如果该Connection无效，关闭，并直接创建一个
							logger.info("Connection is not valid , to create one");
							conn.close();
							conn = createObject();
						}
					}catch (Exception ex){
						logger.error("Failed to test the connection,to create one",ex);
						conn = createObject();
					}
				}
			}finally{
				if (stat != null){
					stat.count(System.nanoTime() - start, conn == null);
				}
			}
		}
		
		if (conn != null && autoCommit){
			try {
				conn.setAutoCommit(true);
			}catch (SQLException ex){
				// nothing to do
			}
		}
		return conn;
	}
	
	/**
	 * 尝试选择只读数据源
	 * 
	 * @return
	 */
	protected Connection selectReadSource(int timeout){
		Connection found = null;
		
		List<ReadOnlySource> ross = getReadOnlySources();
		if (ross != null && ross.size() > 0 && loadBalance != null){
			long start = System.nanoTime();
			boolean error = false;
			ReadOnlySource dest = null;
			try {	
				dest = loadBalance.select(KeyGen.getKey(), null, ross);
				
				ConnectionPool pool = DbcpSource.getPool(dest.getId());
				if (pool != null){
					found = pool.getConnection(timeout);
				}
			} catch (Exception e) {
				error = true;
			}finally{
				long _duration = System.nanoTime() - start;
				if (dest != null){
					dest.count(_duration, error);
				}
			}
		}
		return found;
	}
	
	abstract protected List<ReadOnlySource> getReadOnlySources();

	public Connection getConnection(int timeout) {
		return getConnection(timeout,false);
	}

	
	public Connection getConnection(boolean enableRWS) {
		return getConnection(getMaxWait(),enableRWS);
	}

	
	public Connection getConnection() {
		return getConnection(getMaxWait(),false);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected <pooled> pooled createObject(){
		//用ManagedConnection去包装实际的Connection
		Connection wrapper = null;
		Connection real = newConnection();
		if (real != null){
			wrapper = new ManagedConnection(this,real,getTimeout());
		}
		return (pooled)wrapper;
	}
	
	@Override
	public void recycle(Connection conn) {
		//缺省状况下，没有发生错误
		recycle(conn,false);
	}
	
	public void recycle(Connection conn,boolean hasError){
		if (conn != null){
			if (conn instanceof ManagedConnection){
				//ManagedConnection不能close，否则会死循环
				ManagedConnection _conn = (ManagedConnection)conn;
				ConnectionPool _pool = _conn.getPool();
				if (_pool == this){
					//是由本pool创建的
					if (!hasError){
						//如果没有发生错误，归还到连接池
						returnObject(conn);
					}else{
						//发生了错误，直接关闭
						try {
							conn.close();
						} catch (SQLException e) {
		
						}
					}
				}else{
					//不是我创建的，交给相应的pool去回收
					_pool.recycle(conn, hasError);
				}
			}else{
				if (!hasError){ 
					//如果没有发生错误，归还到连接池
					returnObject(conn);
				}else{
					//发生了错误，直接关闭
					try {
						conn.close();
					} catch (SQLException e) {
	
					}
				}
			}
		}
	}
	
	
	protected String getIdOfMaxQueueLength() {
		return "maxActive";
	}

	
	protected String getIdOfIdleQueueLength() {
		return "maxIdle";
	}
	
	/**
	 * 获取争抢连接时的最大等待时间
	 * @return
	 */
	abstract protected int getMaxWait();
	
	abstract protected long getTimeout();
	
	/**
	 * 创建新的链接
	 * 
	 * @return 数据库链接
	 * 
	 * @since 1.6.3.11
	 */
	protected abstract Connection newConnection();
}
