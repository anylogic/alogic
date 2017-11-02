package com.alogic.zk;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.UPath;

/**
 * 基于zk实现的全局锁
 * 
 * @author yyduan
 *
 */
public class ZKLock implements Lock, Watcher {
	
	/**
	 * a logger of log4j
	 */
	private static Logger LOG = LoggerFactory.getLogger(ZKLock.class);	

	/**
	 * a connector to zoo keeper
	 */
	protected ZooKeeperConnector conn = null;

	/**
	 * 锁在ZooKeeper下的路径
	 */
	protected UPath lockPath = null;
	
	/**
	 * Latch
	 */
	protected CountDownLatch latch = null;
	
	/**
	 * 在ZooKeeper中临时节点名称
	 */
	protected String myNode = null;
	
	/**
	 * 要等待的节点
	 */
	protected String waitNode = null;
	
	protected int sessionTimeout = 30000;
	
	public ZKLock(String _lockname,Properties props){
		String lockName  = _lockname;
		String rootPath = PropertiesConstants.getString(props, "root", "${zookeeper.lock.root}");
		if (rootPath.length() <= 0){
			rootPath = "/alogic/global/lock";
		}
		
		conn = new ZooKeeperConnector(props);
		
		sessionTimeout = PropertiesConstants.getInt(props, "${zookeeper.lock.timeout}", sessionTimeout);
		
		lockPath = new UPath(rootPath + "/" + lockName);
		conn.makePath(lockPath,ZooKeeperConnector.DEFAULT_ACL, CreateMode.PERSISTENT);
		
		markOnZooKeeper();
	}	
	
	
	public void process(WatchedEvent event) {
		if (event.getType().equals(EventType.NodeDeleted) && event.getPath().equals(lockPath + "/" + waitNode)){
			if (latch != null){
				latch.countDown();
			}
		}
	}
	
	public void lock() {
		try {
			lockInterruptibly();
		} catch (InterruptedException e) {
			LOG.info("Lock waiting thread has been interrupted,exit",e);
		}
	}

	
	public void lockInterruptibly() throws InterruptedException {
		LOG.info("Using " + getClass().getName() + " to lock.");
		while (!tryLock(sessionTimeout,TimeUnit.MILLISECONDS)){
			try {
				LOG.info("Can not get the lock . Waiting ....");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				throw e;
			}
		}
		LOG.info("Got the lock..");
	}

	
	public Condition newCondition() {
		return null;
	}

	protected void markOnZooKeeper(){
		UPath path = lockPath.append("lock");
		myNode = conn.create(path, "", ZooKeeperConnector.DEFAULT_ACL, CreateMode.EPHEMERAL_SEQUENTIAL, false);
		myNode = myNode.substring(myNode.lastIndexOf("/") + 1);		
	}
	
	public boolean tryLock() {
		List<String> allNodes = conn.getChildren(lockPath, this, false);
		Collections.sort(allNodes);
		
		if (myNode.equals(allNodes.get(0))){
			//我就是最小的节点，取得锁
			return true;
		}
		
		//找到比我小的节点
		waitNode = allNodes.get(Collections.binarySearch(allNodes, myNode) - 1);
		return false;
	}

	
	public boolean tryLock(long timeout, TimeUnit timeUnit){
		if (tryLock()){
			return true;
		}
		try {
			return waitForLock(waitNode,timeout,timeUnit);
		} catch (InterruptedException e) {
			throw new BaseException("zk.keeperexception",e.getMessage(),e);
		}
	}

	/**
	 * 等待指定节点释放资源
	 * @param wn 等待节点
	 * @param timeout 超时时间
	 * @param timeUnit 时间但闻
	 * @return true|false 如果指定节点释放了资源，返回为true，否则为false
	 * @throws InterruptedException 
	 */
	private boolean waitForLock(String wn, long timeout,
			TimeUnit timeUnit) throws InterruptedException {
		boolean exist = conn.existPath(lockPath.append(wn), this, false);
		if (exist){
			latch = new CountDownLatch(1);
			latch.await(timeout, timeUnit);
			latch = null;
			return !conn.existPath(lockPath.append(wn), this, false);
		}else{
			return true;
		}
	}

	
	public void unlock() {
		conn.delete(lockPath.append(myNode), true);
		conn.disconnect();
	}

}