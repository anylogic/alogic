package com.alogic.pool.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.alogic.pool.Pool;
import com.alogic.pool.CloseAware;

/**
 * 基于队列的缓冲池实现
 * 
 * 
 * @author duanyy
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 * 
 * @version 1.6.9.9 [20170829 duanyy] <br>
 * - Pool的returnObject接口增加是否出错的参数 <br>
 */
public abstract class Queued implements Pool,CloseAware {
	/**
	 * a logger of log4j
	 */
	protected Logger logger = LoggerFactory.getLogger(Queued.class);
	
	/**
	 * 正在工作的对象个数
	 */
	private volatile int workingCnt = 0;
	
	/**
	 * 空闲的对象个数
	 */
	private volatile int idleCnt = 0;
	
	/**
	 * 等待的进程个数
	 */
	private volatile int waitCnt = 0;
	
	/**
	 * 正在创建对象的进程个数
	 */
	private volatile int creatingCnt = 0;
	
	/**
	 * 空闲队列
	 */
	private ConcurrentLinkedQueue<Object> idleQueue = null;
	
	/**
	 * 普通优先级(priority=0)下的队列最大长度
	 *
	 */
	private int maxQueueLength = 10;

	/**
	 * 空闲队列长度
	 */
	private int idleQueueLength = 5;
	
	/**
	 * 针对idleQueue的锁
	 */
	protected ReentrantLock lock = new ReentrantLock();
	
	/**
	 * 条件，空闲队列非空
	 */
	protected Condition notEmpty = lock.newCondition();	
	
	public int getWorkingCnt(){return workingCnt;}
	public int getIdleCnt(){return idleCnt;}
	public int getWaitCnt(){return waitCnt;}
	public int getCreatingCnt(){return creatingCnt;}
	
	public int getMaxActive(){return maxQueueLength;}
	public int getMaxIdle(){return idleQueueLength;}
	
	/**
	 * 获取maxQueueLength的参数ID
	 * @return 参数ID
	 */	
	protected String getIdOfMaxQueueLength() {
		return "maxQueueLength";
	}
	
	/**
	 * 获取idleQueueLength的参数ID
	 * @return 参数ID
	 */
	protected String getIdOfIdleQueueLength() {
		return "idleQueueLength";
	}	
	
	@Override
	public void configure(Element e, Properties p) {
		XmlElementProperties props = new XmlElementProperties(e,p);
		configure(props);
	}	
	
	@Override
	public void configure(Properties props) {
		String id = getIdOfMaxQueueLength();
		
		maxQueueLength = PropertiesConstants.getInt(props, id, maxQueueLength,false);
		maxQueueLength = maxQueueLength <= 0 ? 10 : maxQueueLength;
		
		id = getIdOfIdleQueueLength();
		idleQueueLength = PropertiesConstants.getInt(props, id, idleQueueLength,false);
		idleQueueLength = idleQueueLength <= 0? maxQueueLength : idleQueueLength;
				
		idleQueue = new ConcurrentLinkedQueue<Object>();		
	}

	
	public void close() {
		Object found = null;
		while (( found = idleQueue.poll())!= null){
			close(found);
		}
		idleCnt = 0;
		waitCnt = 0;
		workingCnt = 0;
		creatingCnt = 0;
	}

	private synchronized int workingIncr(int count){
		workingCnt += count;
		return workingCnt;
	}

	private synchronized int idleIncr(int count){
		idleCnt += count;
		return idleCnt;
	}
	
	private synchronized int creatingIncr(int count){
		creatingCnt += count;
		return creatingCnt;
	}	
	
	private void close(Object toClose){
		if (toClose != null && toClose instanceof AutoCloseable){
			IOTools.close((AutoCloseable)toClose);
		}
	}
	
	@Override
	public void closeObject(Object _pooled){
		returnObject(_pooled,false);
	}
	
	protected <pooled> pooled borrowObject(){
		//在idle中没有抢到，直接创建一个实例
		try {
			creatingIncr(1);
			pooled found = createObject();
			if (found != null){
				workingIncr(1);
				return found;
			}else{
				return null;
			}
		}finally{
			creatingIncr(-1);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <pooled> pooled borrowObject(int priority,int timeout) {
		//当前优先级所允许的最大长度
		int maxLength = maxQueueLength * (1+priority);
		
		//是否承诺服务
		boolean promised = (workingCnt + idleCnt + creatingCnt) < maxLength;
		
		if (promised){
			//已经承诺，从idle队列中抢
			if (!idleQueue.isEmpty()){
				//先从idle队列中拿
				Object found = idleQueue.poll();
				if (found != null){
					//拿到了
					workingIncr(1);
					idleIncr(-1);
					return (pooled)found;
				}
			}
			
			//在idle中没有抢到，直接创建一个实例
			return borrowObject();
		}
	
		//是否愿意等待其他线程释放实例到idle队列
		if (timeout > 0){
			lock.lock();
			try {
				long nanos = TimeUnit.MILLISECONDS.toNanos(timeout);
				while (idleQueue.isEmpty()){
					if (nanos <= 0L)
						return null;
					nanos = notEmpty.awaitNanos(nanos);
				}
				
				Object found = idleQueue.poll();
				if (found != null){
					workingIncr(1);
					idleIncr(-1);
					waitCnt = lock.getQueueLength() + lock.getWaitQueueLength(notEmpty);
				}
				
				//在这里也有可能没有抢到，算它倒霉
				return (pooled)found;
			}catch (Exception ex){
				logger.error("Error when borrowing object from pool",ex);
				return null;
			}finally{
				lock.unlock();
			}
		}
		//拿不到实例
		return null;
	}

	@Override
	public <pooled> void returnObject(pooled obj,boolean hasError) {
		workingIncr(-1);		
		if (hasError || idleCnt >= idleQueueLength){
			//实际idle数大于许可idle数
			//不用归还到空闲队列，直接释放
			close(obj);
			obj = null;
		}else{
			//归还到队列
			idleIncr(1);
			idleQueue.offer(obj);
		}				
	}

	/**
	 * 创建缓冲池对象
	 * @return pooled
	 */
	protected abstract <pooled> pooled createObject();
	
	public void report(Element xml){
		if (xml != null){
			xml.setAttribute("idle", String.valueOf(idleCnt));
			xml.setAttribute("wait", String.valueOf(waitCnt));
			xml.setAttribute("creating", String.valueOf(creatingCnt));
			xml.setAttribute("working", String.valueOf(workingCnt));
			
			xml.setAttribute("maxIdle", String.valueOf(idleQueueLength));
			xml.setAttribute("maxActive", String.valueOf(maxQueueLength));
			xml.setAttribute("module", getClass().getName());
		}
	}
	
	
	public void report(Map<String,Object> json){
		if (json != null){
			json.put("idle", idleCnt);
			json.put("wait", waitCnt);
			json.put("creating",creatingCnt);
			json.put("working", workingCnt);
			
			json.put("maxIdle", idleQueueLength);
			json.put("maxActive", maxQueueLength);
			json.put("module", getClass().getName());
		}
	}
}