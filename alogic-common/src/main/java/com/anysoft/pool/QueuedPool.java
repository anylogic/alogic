package com.anysoft.pool;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 基于队列的缓冲池
 * 
 * @author duanyy
 * @since 1.1.0
 * 
 * @param <pooled> 缓冲池对象、
 * 
 * @version 1.2.2 [20140722 duanyy]
 * - 可缓冲的对象改为AutoCloseable
 * - 优化计数器的同步机制
 * 
 * @version 1.3.3 [20140815 duanyy]
 * - 实现Reportable接口
 * 
 * @version 1.3.7 [20140826 duanyy]
 * - 增加Pooled对象自关闭的支持
 * 
 */
abstract public class QueuedPool<pooled extends AutoCloseable> implements Pool<pooled>,CloseAware<pooled> {
	/**
	 * a logger of log4j
	 */
	protected Logger logger = LogManager.getLogger(QueuedPool.class);
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
	private ConcurrentLinkedQueue<pooled> idleQueue = null;
	
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
	abstract protected String getIdOfMaxQueueLength();
	
	/**
	 * 获取idleQueueLength的参数ID
	 * @return 参数ID
	 */
	abstract protected String getIdOfIdleQueueLength();	
	
	
	public void create(Properties props) {
		String id = getIdOfMaxQueueLength();
		
		maxQueueLength = PropertiesConstants.getInt(props, id, maxQueueLength,false);
		maxQueueLength = maxQueueLength <= 0 ? 10 : maxQueueLength;
		
		id = getIdOfIdleQueueLength();
		idleQueueLength = PropertiesConstants.getInt(props, id, idleQueueLength,false);
		idleQueueLength = idleQueueLength <= 0? maxQueueLength : idleQueueLength;
				
		idleQueue = new ConcurrentLinkedQueue<pooled>();		
	}

	
	public void close() {
		pooled found = null;
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
	
	
	private void close(pooled toClose){
		try {
			toClose.close();
		} catch (Exception e) {
		}
	}
	
	
	public void closeObject(pooled _pooled){
		returnObject(_pooled);
	}
	
	
	public pooled borrowObject(int priority,int timeout) throws BaseException {
		//当前优先级所允许的最大长度
		int maxLength = maxQueueLength * (1+priority);
		if (workingCnt + idleCnt + creatingCnt < maxLength){
			//当前对象个数小于所允许的最大长度
			if (idleQueue.isEmpty()){
			//空闲队列为空，直接创建一个新的对象
				try {
					creatingIncr(1);
					pooled found = createObject();
					if (found != null){
						workingIncr(1);
					}
					return found;//sorry , perhaps be null.
				}finally{
					creatingIncr(-1);
				}
			}
		}
		if (timeout <= 0){
			pooled found = idleQueue.poll();
			if (found != null){
				workingIncr(1);
				idleIncr(-1);
			}
			return found;//sorry, perhaps be null.
		}else{
			lock.lock();
			try {
				long nanos = TimeUnit.MILLISECONDS.toNanos(timeout);
				while (idleQueue.isEmpty()){
					if (nanos <= 0L)
						return null;
					nanos = notEmpty.awaitNanos(nanos);
				}
				workingIncr(1);
				idleIncr(-1);
				waitCnt = lock.getQueueLength() + lock.getWaitQueueLength(notEmpty);
				return idleQueue.poll();
			}catch (Exception ex){
				logger.error("Error when borrowing object from pool",ex);
				return null;
			}finally{
				lock.unlock();
			}
		}
	}

	
	public void returnObject(pooled obj) {
		if (idleCnt > idleQueueLength){
			//实际idle数大于许可idle数
			//不用归还到空闲队列，直接释放
			close(obj);
			obj = null;
			workingIncr(-1);
		}else{
			//归还到队列
			idleQueue.offer(obj);
			workingIncr(-1);
			idleIncr(1);
		}				
	}

	/**
	 * 创建缓冲池对象
	 * @return pooled
	 * @throws BaseException
	 */
	abstract protected pooled createObject()throws BaseException;
	
	
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
