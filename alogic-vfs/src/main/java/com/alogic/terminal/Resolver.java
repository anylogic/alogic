package com.alogic.terminal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 指令输出解析器
 * 
 * @author duanyy
 * @since 1.6.7.8 
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 */
public interface Resolver {
	
	/**
	 * 开始指令解析
	 * 
	 * @return cookies
	 */
	public Object resolveBegin(String cmd);
	
	/**
	 * 解析文本行
	 * @param cookies cookies
	 * @param content 本行的文本内容
	 */
	public void resolveLine(Object cookies,String content);
	
	/**
	 * 结束本次解析
	 * @param cookies cookies
	 */
	public void resolveEnd(Object cookies);
	
	/**
	 * 缺省实现
	 * 
	 * @author duanyy
	 *
	 */
	public static class Default implements Resolver{
		
		/**
		 * a logger of log4j
		 */
		protected static final Logger logger = LoggerFactory.getLogger(Resolver.class.getName());
		
		/**
		 * 指令编号
		 */
		protected volatile Integer no = 0;
		
		@Override
		public Object resolveBegin(String cmd) {
			logger.info(String.format("[%d]%s",no++,cmd));
			return no;
		}

		@Override
		public void resolveLine(Object cookies, String content) {
			logger.info(String.format("[%d]%s",no++,content));
		}

		@Override
		public void resolveEnd(Object cookies) {
			// nothing to do
		}
		
	}
}
