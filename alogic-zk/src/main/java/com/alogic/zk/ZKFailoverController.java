package com.alogic.zk;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;

import com.alogic.ha.FailoverController;
import com.alogic.ha.FailoverListener;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.UPath;

/**
 * 基于ZK的Failverover控制器
 * @author yyduan
 *
 * @version 1.6.11.29 [20180510 duanyy] <br>
 * - 优化处理 <br>
 */
public class ZKFailoverController extends FailoverController.Abstract implements Runnable,Watcher{
	/**
	 * the connector to ZooKeeper
	 */
	protected ZooKeeperConnector conn = null;	
	
	/**
	 * 执行线程池
	 */
	protected static ScheduledThreadPoolExecutor exec = new  ScheduledThreadPoolExecutor(1);
	
	/**
	 * 定时线程的句柄
	 */
	protected ScheduledFuture<?> future = null;
	
	/**
	 * 当前是否active状态
	 */
	protected boolean active = false;
	
	/**
	 * 根节点地址
	 */
	protected UPath rootPath = new UPath("/alogic/global/ha");
	
	/**
	 * 监听器
	 */
	protected FailoverListener listener = null;
	
	/**
	 * ha锁的所在的路径
	 */
	protected UPath haPath = null;
	
	/**
	 * 在ZooKeeper中临时节点名称
	 */
	protected String myNode = null;
	
	/**
	 * 要等待的节点
	 */
	protected String waitNode = null;	
	
	/**
	 * 进程信息
	 */
	protected String process = "${server.ip}:${server.port}";
	
	protected long interval = 5000L;
	
	private void detect(){
		List<String> allNodes = conn.getChildren(haPath, this, false);
		Collections.sort(allNodes);
		
		if (myNode.equals(allNodes.get(0))){
			//我就是最小的节点，取得锁
			active(true);
		}else{
			active(false);
			//找到比我小的节点
			waitNode = allNodes.get(Collections.binarySearch(allNodes, myNode) - 1);
			//订阅等待节点的消息
			conn.existPath(haPath.append(waitNode), this, false);
		}
	}
	
	private void active(boolean b) {
		if (active){
			if (!b){
				//active->standby
				active = b;
				if (listener != null){
					listener.becomeStandby();
				}
			}
		}else{
			if (b){
				//standy->active
				active = b;
				if (listener != null){
					listener.becomeActive();
				}
			}
		}
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public void start(FailoverListener theListener) {
		listener = theListener;
		
		//保证路径确实存在
		conn.makePath(haPath,ZooKeeperConnector.DEFAULT_ACL, CreateMode.PERSISTENT);
		
		myNode = conn.create(haPath.append("ha"), process, ZooKeeperConnector.DEFAULT_ACL, 
				CreateMode.EPHEMERAL_SEQUENTIAL, false);
		myNode = myNode.substring(myNode.lastIndexOf("/") + 1);	
		
		detect();
		
		future = exec.scheduleWithFixedDelay(this, 1000, interval, TimeUnit.MILLISECONDS);
	}

	@Override
	public void stop() {
		if (future != null){
			future.cancel(false);
		}
		conn.delete(haPath.append(myNode), true);
		conn.disconnect();
		active(false);
	}

	@Override
	public void configure(Properties p) {
		haPath = new UPath(rootPath + "/" + PropertiesConstants.getString(p,"failover.name","default"));
		conn = new ZooKeeperConnector(p);
		process = PropertiesConstants.getString(p, "failover.process", process);
		interval = PropertiesConstants.getLong(p,"failover.interval",interval);
	}

	@Override
	public void run() {
		if (!conn.isConnected()){
			conn.connect();
		}
		
		boolean exist = conn.existPath(haPath.append(myNode), this, false);
		if (!exist){
			//我的节点都被删除了，可能是前期zk掉过线
			myNode = conn.create(haPath.append("ha"), process, ZooKeeperConnector.DEFAULT_ACL, 
					CreateMode.EPHEMERAL_SEQUENTIAL, false);
			myNode = myNode.substring(myNode.lastIndexOf("/") + 1);				
		}
		detect();
	}

	@Override
	public void process(WatchedEvent event) {
		if (event.getType().equals(EventType.NodeDeleted) && event.getPath().equals(haPath + "/" + waitNode)){
			//我等待的节点已经被删除，触发检测
			detect();
		}
	}

}
