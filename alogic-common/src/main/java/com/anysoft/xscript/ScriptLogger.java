package com.anysoft.xscript;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.anysoft.stream.AbstractHandler;
import com.anysoft.stream.Handler;
import com.anysoft.stream.HubHandler;
import com.anysoft.util.BaseException;
import com.anysoft.util.Factory;
import com.anysoft.util.Properties;

/**
 * 脚本日志处理器
 * 
 * @author duanyy
 * @since 1.6.3.25
 * 
 */
public interface ScriptLogger extends Handler<ScriptLogInfo>{
	/**
	 * 记录日志
	 * 
	 * @param activity 当前活动
	 * @param message 日志信息
	 * @param level 级别
	 * @param progress 进度
	 */
	public void log(String activity,String message,String level,int progress);
	
	/**
	 * 集线器
	 * @author duanyy
	 * @since 1.6.3.25
	 */
	public static class Hub extends HubHandler<ScriptLogInfo> implements ScriptLogger{
		public void log(String activity,String message,String level,int progress){
			handle(new ScriptLogInfo(activity,message,level,progress),
					System.currentTimeMillis());
		}
		public String getHandlerType(){
			return "logger";
		}
	}
	
	/**
	 * 缺省的处理器(通过log4j输出日志)
	 * 
	 * @author duanyy
	 * @since 1.6.3.25
	 */
	public static class Default extends AbstractHandler<ScriptLogInfo> implements ScriptLogger{
		/**
		 * a logger of log4j
		 */
		protected static final Logger logger = LogManager.getLogger(ScriptLogger.class);
		
		protected void onHandle(ScriptLogInfo _data,long t) {
			String level = _data.level();
			if (level.equals("error")){
				logger.error(_data.message());
			}else{
				if (level.equals("warn")){
					logger.warn(_data.message);
				}else{
					logger.info(_data.message());
				}
			}
		}
		
		protected void onFlush(long t) {
			// i have no buffer
		}

		protected void onConfigure(Element e, Properties p) {

		}
		
		public void log(String activity,String message,String level,int progress){
			handle(new ScriptLogInfo(activity,message,level,progress),
					System.currentTimeMillis());
		}
	}
	
	/**
	 * xscript插件，用于创建logger
	 * 
	 * @author duanyy
	 *
	 */
	public static class Plugin extends AbstractStatement{

		public Plugin(String _tag, Statement _parent) {
			super(_tag, _parent);
		}

		@Override
		protected int compiling(Element _e, Properties _properties, CompileWatcher watcher) {
			Factory<ScriptLogger> factory = new Factory<ScriptLogger>();
			try {
				ScriptLogger logger = factory.newInstance(_e, _properties, "module", Default.class.getName());
				if (logger != null){
					Statement parent = parent();
					if (parent != null){
						parent.registerLogger(logger);
					}else{
						if (watcher != null){
							watcher.message(this, "warn", "Parent statement is null,ignored");
						}
					}
				}else{
					if (watcher != null){
						watcher.message(this, "error", "Can not create script logger.");
					}
				}
			}catch (Exception ex){
				if (watcher != null){
					watcher.message(this, "error", "Can not create script logger.");
				}
			}
			return 0;
		}

		public boolean isExecutable(){
			return false;
		}

		public int onExecute(Properties p,ExecuteWatcher watcher) throws BaseException{
			return 0;
		}
		
	}
}
