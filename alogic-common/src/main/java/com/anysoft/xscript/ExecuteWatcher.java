package com.anysoft.xscript;

import java.util.Date;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.anysoft.util.Properties;

/**
 * 执行监视器
 * 
 * @author duanyy
 * @since 1.6.3.22
 */
public interface ExecuteWatcher {
	/**
	 * Statement执行完成
	 * 
	 * @param statement 语句
	 * @param p 执行参数
	 * @param start 开始时间
	 * @param duration 结束时间
	 */
	public void executed(Statement statement,Properties p,long start,long duration);

	public static class Default implements ExecuteWatcher{
		/**
		 * a logger of log4j
		 */
		protected static final Logger logger = LogManager.getLogger(ExecuteWatcher.class);
		
		public void executed(Statement statement, Properties p, long start,
				long duration) {
			logger.info(statement.getXmlTag() + "---> 开始于" + new Date(start) + ",耗时:" + duration + "毫秒");
		}
	}
}
