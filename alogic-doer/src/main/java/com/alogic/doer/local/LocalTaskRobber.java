package com.alogic.doer.local;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.alogic.doer.core.TaskCenter;
import com.alogic.doer.core.TaskDispatcher;
import com.alogic.timer.core.Doer;
import com.alogic.timer.core.Task;
import com.anysoft.util.Factory;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;


/**
 * 任务争抢者
 * 
 * @author duanyy
 * @since 1.6.3.4
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 * 
 * @version 1.6.9.2 [20170601 duanyy] <br>
 * - 改造TaskCenter模型，以便提供分布式任务处理支持; <br>
 */
public interface LocalTaskRobber extends TaskDispatcher,Runnable{
	/**
	 * 开始任务处理线程
	 */
	public void start(TaskCenter tc);
	
	/**
	 * 中断任务处理线程（需等待当前处理完成）
	 */
	public void stop();
	
	/**
	 * 等待进程结束
	 * @param timeout
	 */
	public void join(long timeout);
		
	/**
	 * 是否已经中断
	 * @return true | false
	 */
	public boolean isStopped();
	
	/**
	 * 虚基类
	 * @author duanyy
	 * @since 1.6.3.4
	 */
	public static class Default implements LocalTaskRobber{
		/**
		 * a logger of log4j
		 */
		protected static final Logger logger = LoggerFactory.getLogger(LocalTaskRobber.class);
		
		/**
		 * 是否停止进程
		 */
		protected boolean stopped = false;
		
		/**
		 * 超时
		 */
		protected long timeout = 10000;
		
		/**
		 * 处理一个任务之后的时间间隔
		 */
		protected long interval = 2000;
		
		/**
		 * 处理的事件
		 */
		protected String event = "default";
		
		/**
		 * 处理线程数
		 */
		protected int threadCnt = 1;
		
		/**
		 * 执行线程列表
		 */
		protected List<Thread> threads = new ArrayList<Thread>();
		
		/**
		 * 当前的TaskCenter
		 */
		protected TaskCenter tc = null;
		
		/**
		 * 实际执行的doer
		 */
		protected Doer doer = null;
		
		/**
		 * latch，用于检测子线程退出
		 */
		protected CountDownLatch latch = null;
		
		public void dispatch(String queue,Task task){
			if (doer != null){
				doer.run(task);
			}
		}
		
		@Override
		public void configure(Element root, Properties props){
			Properties p = new XmlElementProperties(root,props);
			configure(p);
			
			Factory<Doer> factory = new Factory<Doer>();
			
			try {
				doer = factory.newInstance(root, props, "module");
			}catch (Exception ex){
				logger.error(String.format("Can not create doer with xml %s",XmlTools.node2String(root)));
			}
		}
		
		@Override
		public void configure(Properties p) {
			event = PropertiesConstants.getString(p,"event",event);
			timeout = PropertiesConstants.getLong(p, "timeout", timeout);
			interval = PropertiesConstants.getLong(p, "interval", interval);
			threadCnt = PropertiesConstants.getInt(p, "threadCnt", threadCnt);
		}
		
		@Override
		public void start(TaskCenter center) {
			this.tc = center;
			if (doer != null){
				doer.setTaskStateListener(this.tc);
				doer.setContextHolder(this.tc);
			}
			
			if (this.tc != null){
				logger.info("Task doer is starting,cnt = " + threadCnt);
				latch = new CountDownLatch(threadCnt);
				
				for (int i = 0 ;i < threadCnt ; i ++){
					Thread thread = new Thread(this);				
					thread.setDaemon(true);
					thread.start();
					threads.add(thread);
				}
			}
		}

		@Override
		public void stop() {
			stopped = true;
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				
			}
			
			for (Thread t:threads){
				if (t.isAlive()){
					t.interrupt();
				}
			}
		}
		
		@Override
		public boolean isStopped(){
			return stopped;
		}
		
		@Override
		public void report(Element xml) {
			if (xml != null){
				xml.setAttribute("module", getClass().getName());
				xml.setAttribute("timeout", String.valueOf(timeout));
				xml.setAttribute("interval", String.valueOf(interval));
				xml.setAttribute("stopped", Boolean.toString(stopped));
			}
		}

		@Override
		public void report(Map<String, Object> json) {
			if (json != null){
				JsonTools.setString(json, "module", getClass().getName());
				JsonTools.setLong(json,"timeout",timeout);
				JsonTools.setLong(json,"interval",interval);
				JsonTools.setBoolean(json, "stopped", stopped);
			}
		}
		
		@Override
		public void run(){
			if (this.tc != null){
				try {
					logger.info(String.format("Thread %d is ready..",Thread.currentThread().getId()));
					stopped = false;
					while (!stopped){
						if (this.tc.askForTask(this.event,this,timeout) > 0){
							try{
								Thread.sleep(interval);
							}catch (Exception ex){
								
							}
						}
					}
					logger.info(String.format("Thread %d finished.",Thread.currentThread().getId()));
				}finally{
					latch.countDown();
				}
			}
		}

		@Override
		public void join(long timeout) {
			try {
				latch.await(timeout, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				
			}
		}
	}
}