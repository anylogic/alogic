package com.alogic.timer;

import java.util.Map;
import java.util.Random;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.anysoft.util.BaseException;
import com.anysoft.util.Configurable;
import com.anysoft.util.Properties;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;

/**
 * 定时任务
 * 
 * @author duanyy
 * @since 1.6.3.37
 */
public interface Task extends Configurable,XMLConfigurable,Runnable,Reportable{
	
	/**
	 * 任务状态
	 * 
	 * @author duanyy
	 *
	 */
	public enum State{
		/**
		 * 空闲
		 */
		Idle,
		/**
		 * 已调度
		 */
		Scheduled,
		/**
		 * 工作中
		 */
		Working
	}
	
	/**
	 * 获取当前ID
	 * @return ID
	 */
	public String getCurrentId();
	
	/**
	 * 获取任务状态
	 * @return
	 */
	public State getState();
	
	/**
	 * 准备执行
	 * 
	 * @param ctx 任务的上下文
	 */
	public void prepare(Properties ctx);
	
	/**
	 * 执行
	 * @param ctx 任务的上下文
	 */
	public void execute(Properties ctx);
	
	/**
	 * Abstract
	 * @author duanyy
	 *
	 */
	abstract public static class Abstract implements Task {
		protected static final Logger logger = LogManager.getLogger(Task.class);
		/**
		 * 任务状态
		 */
		protected State state = State.Idle;
		protected Properties ctx = null;
		protected String currentId;
		
		public String getCurrentId(){
			return currentId;
		}
		
		public void prepare(Properties _ctx){
			state = State.Scheduled;
			ctx = _ctx;
			currentId = newTaskId();
		}
		
		public void run(){
			try {
				state = State.Working;
				execute(ctx);
			}catch (Throwable t){
				logger.fatal("Exception when executing the task:" + getCurrentId());
			}finally{
				state = State.Idle;
			}
		}
		
		public void report(Element xml) {
			if (xml != null){
				xml.setAttribute("module", getClass().getName());
				xml.setAttribute("current", getCurrentId());
				xml.setAttribute("state", state.name());
			}
		}

		public void report(Map<String, Object> json) {
			if (json != null){
				json.put("module", getClass().getName());
				json.put("current", getCurrentId());
				json.put("state", state.name());
			}
		}

		public State getState() {
			return state;
		}

		public void configure(Element _e, Properties _properties)
				throws BaseException {
			Properties p = new XmlElementProperties(_e,_properties);
			configure(p);			
		}		
		
		public void configure(Properties p) throws BaseException {
			// nothing to do
		}
		
		protected String newTaskId(){
			return System.currentTimeMillis() + randomString(6);
		}
		
		/**
		 * 字符表
		 */
		protected static final char[] Chars = {
		      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
		      'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
		      'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
		      'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
		      'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
		      'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
		      '8', '9'
		 };
		
		/**
		 * 按照指定宽度生成随机字符串
		 * @param _width 字符串的宽度
		 * @return 随机字符串
		 */
		static protected String randomString(int _width){
			int width = _width <= 0 ? 6 : _width;
			char [] ret = new char[width];
			Random ran = new Random();
			for (int i = 0 ; i < width ; i ++){
				int intValue = ran.nextInt(62) % 62;
				ret[i] = Chars[intValue];
			}
			
			return new String(ret);
		}
	}

	/**
	 * Runnable包裹器
	 * 
	 * @author duanyy
	 *
	 */
	public static class Wrapper extends Abstract{
		protected Runnable real = null;
		
		public Wrapper(Runnable runnable){
			real = runnable;
		}
		
		@Override
		public void configure(Properties p) throws BaseException {
			// nothing to do
		}

		@Override
		public void execute(Properties ctx) {
			if (real != null){
				real.run();
			}
		}
	}
}
