package com.alogic.zk;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooDefs.Perms;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.UPath;

/**
 * ZooKeeper连接器
 * 
 * @author duanyy
 *
 */
public final class ZooKeeperConnector implements Watcher{
	/**
	 * a logger of log4j
	 */
	protected final static Logger logger = LoggerFactory.getLogger(ZooKeeperConnector.class);
	
	/**
	 * 缺省的ACL
	 */
    public final static ArrayList<ACL> DEFAULT_ACL = new ArrayList<ACL>(
            Collections.singletonList(new ACL(Perms.ALL, Ids.ANYONE_ID_UNSAFE)));	
	
	/**
	 * ZooKeeper的连接串
	 */
	protected String connectString = "${zookeeper.connectString}";
	
	/**
	 * ZooKeeper的超时
	 */
	protected int sessionTimeout = 3000;	
	
	/**
	 * ZooKeeper
	 */
	protected ZooKeeper zookeeper = null;
	
	/**
	 * ZooKeeper中的数据编码
	 */
	protected String encoding = "utf-8";
	
	private CountDownLatch connectedSignal = new CountDownLatch(1);  
	
	public ZooKeeperConnector(Properties props){
		connectString = PropertiesConstants.getString(props, "connectString",
				connectString);
		sessionTimeout = PropertiesConstants.getInt(props, "sessionTimeout",
				sessionTimeout);
		encoding = PropertiesConstants.getString(props,"encoding", encoding);
		
		connect();
	}
	
	public ZooKeeperConnector(Properties props,String connectStrings){
		connectString = connectStrings;
		sessionTimeout = PropertiesConstants.getInt(props, "sessionTimeout",
				sessionTimeout);
		encoding = PropertiesConstants.getString(props,"encoding", encoding);

		connect();
	}
	
	/**
	 * 连接ZooKeeper
	 */
	public void connect(){
		try {
			logger.info("connecting to " + connectString);
			zookeeper = new ZooKeeper(connectString, sessionTimeout, this);
			connectedSignal.await();
		} catch (IOException e) {
			logger.error("Can not connect to zookeeper:" + connectString);
		} catch (InterruptedException e) {
			logger.error("Can not connect to zookeeper:" + connectString);
		}
	}
	
	/**
	 * 从ZooKeeper断开
	 */
	public void disconnect(){
		if (isConnected()){
			try {
				zookeeper.close();
				zookeeper = null;
			} catch (InterruptedException e) {
			}
		}
	}
	
	/**
	 * 是否已经连接
	 * @return true|false
	 */
	public boolean isConnected(){
		return zookeeper != null && zookeeper.getState().isAlive();
	}
	
	/**
	 * 重新连接
	 */
	public void reconnect(){
		disconnect();
		connect();
	}
	
	/**
	 * 装入ZooKeeper节点数据
	 * @param path 节点路径
	 * @param watcher 监听器
	 * @param ignoreException 是否忽略异常
	 * @return 节点数据
	 */
	public String loadData(UPath path,Watcher watcher,boolean ignoreException){
		if (!isConnected()){
			if (!ignoreException)
				throw new BaseException("core.e1500","the zk is not connected.");
			logger.error("the zk is not connected.");
			return null;
		}
		Stat stat = new Stat();
		try {
			byte [] data = null;
			if (watcher == null){
				data = zookeeper.getData(path.getPath(), false, stat);
			}else{
				data = zookeeper.getData(path.getPath(), watcher, stat);
			}
			if (data != null){
				return new String(data,encoding);	
			}	
		} catch (KeeperException e) {
			if (!ignoreException)
				throw new BaseException("zk.keeperexception",e.getMessage(),e);
		} catch (InterruptedException e) {
			if (!ignoreException)
				throw new BaseException("zk.interrupted",e.getMessage(),e);
		} catch (UnsupportedEncodingException e) {
			if (!ignoreException)
				throw new BaseException("zk.unsupportedencoding",e.getMessage(),e);
		}
		return null;
	}
	
	/**
	 * 装入指定节点的所有子节点
	 * @param path
	 * @param watcher
	 * @param ignoreException 是否忽略异常
	 * @return 子节点列表
	 */
	public String [] loadChildren(UPath path,Watcher watcher,boolean ignoreException){
		if (!isConnected()){
			if (!ignoreException)
				throw new BaseException("core.e1500","the zk is not connected.");
			logger.error("the zk is not connected.");
			return new String[0];
		}		
		List<String> children = getChildren(path,watcher,ignoreException);
		return children != null ? children.toArray(new String[0]) : new String[0];
	}
	/**
	 * 装入指定节点的所有子节点
	 * @param path
	 * @param watcher
	 * @param ignoreException 是否忽略异常
	 * @return 子节点列表
	 */	
	public List<String> getChildren(UPath path,Watcher watcher,boolean ignoreException){
		if (!isConnected()){
			if (!ignoreException)
				throw new BaseException("core.e1500","the zk is not connected.");
			logger.error("the zk is not connected.");
			return null;
		}		
		List<String> children = null;
		try {
			if (watcher != null){
				children = zookeeper.getChildren(path.getPath(), watcher);
			}else{
				children = zookeeper.getChildren(path.getPath(), false);
			}
		}catch (KeeperException e) {
			if (!ignoreException)
				throw new BaseException("core.e1501",e.getMessage(),e);
		} catch (InterruptedException e) {
			if (!ignoreException)
				throw new BaseException("core.e1006",e.getMessage(),e);
		}
		return children;
	}
	
	/**
	 * 是否存在指定的路径
	 * @param path 路径
	 * @param watcher 监听器
	 * @param ignoreException 是否忽略异常
	 * @return true|false
	 */
	public boolean existPath(UPath path,Watcher watcher,boolean ignoreException){
		if (!isConnected()){
			if (!ignoreException)
				throw new BaseException("core.e1500","the zk is not connected.");
			logger.error("the zk is not connected.");
			return false;
		}		
		Stat stat = null;
		try {
			if (watcher != null){
				stat = zookeeper.exists(path.getPath(), watcher);
			}else{
				stat = zookeeper.exists(path.getPath(), false);
			}
		}catch (KeeperException e) {
			if (!ignoreException)
				throw new BaseException("core.e1501",e.getMessage(),e);
		} catch (InterruptedException e) {
			if (!ignoreException)
				throw new BaseException("core.e1006",e.getMessage(),e);
		}
		return stat != null;
	}

	/**
	 * 创建或更新节点数据
	 * @param path　路径
	 * @param data　数据
	 * @param acls　访问控制列表
	 * @param mode　创建模式
	 * @param watcher　监听器
	 * @param ignoreException　是否忽略异常
	 */
	public void createOrUpdate(UPath path,String data,List<ACL> acls,CreateMode mode,
			Watcher watcher,boolean ignoreException){
		if (!isConnected()){
			if (!ignoreException)
				throw new BaseException("core.e1500","the zk is not connected.");
			logger.error("the zk is not connected.");
			return ;
		}		
		String _thePath = path.getPath();
		try {
			Stat stat = null;
			if (watcher != null){
				stat = zookeeper.exists(_thePath, watcher);
			}else{
				stat = zookeeper.exists(_thePath, false);
			}
			if (stat == null) {
				zookeeper.create(_thePath,data.getBytes(encoding),acls,mode);
			}else{
				zookeeper.setData(_thePath,data.getBytes(encoding), -1);
			}
		}catch (KeeperException e) {
			if (!ignoreException)
				throw new BaseException("core.e1501",e.getMessage(),e);
		} catch (InterruptedException e) {
			if (!ignoreException)
				throw new BaseException("core.e1006",e.getMessage(),e);
		}catch (UnsupportedEncodingException e) {
			if (!ignoreException)
				throw new BaseException("core.e1005",e.getMessage(),e);
		}		
	}
	
	/**
	 * 创建新的节点
	 * @param path 路径
	 * @param data 数据
	 * @param acls 权限
	 * @param mode 创建模式
	 * @param ignoreException 是否忽略异常
	 */
	public String create(UPath path,String data,List<ACL> acls,CreateMode mode,boolean ignoreException){
		if (!isConnected()){
			if (!ignoreException)
				throw new BaseException("core.e1500","the zk is not connected.");
			logger.error("the zk is not connected.");
			return null;
		}		
		String _thePath = path.getPath();
		try {
			return zookeeper.create(_thePath,data.getBytes(encoding),acls,mode);
		}catch (KeeperException e) {
			if (!ignoreException)
				throw new BaseException("core.e1501",e.getMessage(),e);
		} catch (InterruptedException e) {
			if (!ignoreException)
				throw new BaseException("core.e1006",e.getMessage(),e);
		}catch (UnsupportedEncodingException e) {
			if (!ignoreException)
				throw new BaseException("core.e1005",e.getMessage(),e);
		}	
		return null;
	}
	
	/**
	 * 创建ZooKeeper路径
	 * @param path
	 */
	public void makePath(UPath path,List<ACL> acls,CreateMode mode){
		String _path = path.getPath();
		String [] _paths = _path.split("/");
		UPath _thePath = new UPath("");
		for (String _segment:_paths){
			if (_segment.length() <= 0)
				continue;
			_thePath = _thePath.append(_segment);
			if (! existPath(_thePath, null, true)){
				create(_thePath,"",acls,mode,true);
			}
		}
	}
	
	/**
	 * 删除指定路径的节点
	 * @param path 路径
	 */
	public void delete(UPath path,boolean ignoreException){
		if (!isConnected()){
			if (!ignoreException)
				throw new BaseException("core.e1500","the zk is not connected.");
			logger.error("the zk is not connected.");
			return ;
		}		
		try {
			zookeeper.delete(path.getPath(), -1);
		} catch (InterruptedException e) {
			if (!ignoreException)
				throw new BaseException("core.e1006",e.getMessage(),e);
		} catch (KeeperException e) {
			if (!ignoreException)
				throw new BaseException("core.e1501",e.getMessage(),e);
		}
	}
	
	/**
	 * 删除指定的路径
	 * @param path 路径
	 * @param ignoreException 是否忽略异常
	 */
	public void deletePath(UPath path,boolean ignoreException){
		boolean exist = existPath(path, null, ignoreException);
		if (!exist){
			return ;
		}
		
		List<String> children = getChildren(path, null, ignoreException);
		for (String child:children){
			deletePath(path.append(child),ignoreException);
		}
		
		delete(path,ignoreException);
	}
	
	/**
	 * 将带有/的路径进行转义
	 * 
	 * @param path 路径
	 * @return 转义之后的路径
	 */
	public static String escapePath(String path){
		return path.replace("/", "$");
	}
	
	/**
	 * 将带有/的路径进行反转义
	 * @param escaped 
	 * @return 转义前的路径
	 */
	public static String unescapePath(String escaped){
		return escaped.replace("$", "/");
	}	

	@Override
	public void process(WatchedEvent event) {
		if (event.getState() == KeeperState.SyncConnected) {  
			connectedSignal.countDown();  
		} 
	}
}