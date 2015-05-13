package com.anysoft.xscript;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * 编译监视器
 * 
 * @author duanyy
 * @since 1.6.3.23
 */
public interface CompileWatcher {
	/**
	 * 消息处理
	 * 
	 * <p>在编译过程中，由Statement发出此消息。
	 * 
	 * @param stmt Statement
	 * @param msgType 消息类型(info,error,warn)
	 * @param msg 消息
	 */
	public void message(Statement stmt,String msgType,String msg);
	
	/**
	 * 开始编译
	 * @param stmt 正在编译的Statement
	 * @param timestamp 时间戳
	 */
	public void begin(Statement stmt,long timestamp);
	
	/**
	 * 结束编译
	 * @param stmt 正在编译的Statement
	 * @param timestamp 时间戳
	 * @param duration 编译时长
	 */
	public void end(Statement stmt,long timestamp,long duration);
	
	/**
	 * 缺省实现
	 * 
	 * @author duanyy
	 * 
	 */
	public static class Default implements CompileWatcher{
		
		/**
		 * a logger of log4j
		 */
		protected static final Logger logger = LogManager.getLogger(CompileWatcher.class);
	
		public void message(Statement stmt, String msgType, String msg) {
			if (msgType.compareToIgnoreCase("error") == 0){
				logger.error(msg);
			}else{
				if (msgType.compareToIgnoreCase("warn") == 0){
					logger.warn(msg);
				}else{
					logger.info(msg);
				}
			}
		}

		@Override
		public void begin(Statement stmt, long timestamp) {
			logger.info("开始编译:" + stmt.getXmlTag());
		}

		@Override
		public void end(Statement stmt, long timestamp, long duration) {
			logger.info("完成编译:" + stmt.getXmlTag() + ",耗时:" + duration + "ms");
		}

	}
}
