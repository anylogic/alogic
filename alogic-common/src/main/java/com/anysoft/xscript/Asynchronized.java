package com.anysoft.xscript;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.w3c.dom.Element;

import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;


/**
 * async语句
 * 
 * @author duanyy
 * @since 1.6.3.22
 * @version 1.6.3.23 [20150513 duanyy] <br>
 * - 优化编译模式 <br>
 * 
 * @version 1.6.4.33 [20160304 duanyy] <br>
 * - 根据sonar建议优化代码 <br>
 */
public class Asynchronized extends Block {
	protected long timeout = 30*60*60*1000L;
	public Asynchronized(String xmlTag,Statement parent) {
		super(xmlTag,parent);
	}

	@Override
	protected int onCompiling(Element e, Properties p,CompileWatcher watcher){
		timeout = PropertiesConstants.getLong(p, "timeout", timeout);
		return 0;
	}

	@Override
	public int onExecute(Properties p,ExecuteWatcher watcher) {
		List<Statement> list = children;
		
		CountDownLatch latch = new CountDownLatch(list.size());
		
		Properties variables = getLocalVariables(p);
		
		for (int i = 0 ; i < list.size(); i ++){
			Statement statement = list.get(i);
			
			WorkThread thread = new WorkThread(statement,variables,latch,watcher);
			thread.start();
		}
		
		try {
			if (!latch.await(timeout, TimeUnit.MILLISECONDS)){
				logger.warn("The async executing is timtout.");
			}
		}catch (Exception ex){
			logger.error(ex);
		}
		return 0;
	}

	/**
	 * 异步处理模式下的工作线程
	 * 
	 * @author duanyy
	 *
	 */
	public static class WorkThread extends Thread{
		protected Statement statement = null;
		protected CountDownLatch latch = null;
		protected Properties properties = null;
		protected ExecuteWatcher executeWatcher = null;
		protected WorkThread(Statement stmt,Properties p,CountDownLatch l,ExecuteWatcher watcher){
			statement = stmt;
			executeWatcher = watcher;
			properties = p;
			latch = l;
		}
		@Override
		public void run(){
			try {
				if (statement != null){
					statement.execute(properties, executeWatcher);
				}
			}finally{
				if (latch != null){
					latch.countDown();
				}
			}
		}
	}
}
