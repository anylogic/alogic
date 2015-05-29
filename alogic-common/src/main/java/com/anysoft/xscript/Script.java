package com.anysoft.xscript;

/**
 * 脚本
 * @author duanyy
 * @since 1.6.3.22
 * @version 1.6.3.25 <br>
 * - 统一脚本的日志处理机制 <br>
 */
public class Script extends Segment {

	public Script(String xmlTag, Statement _parent) {
		super(xmlTag, _parent);
	}

	public void log(ScriptLogInfo logInfo){
		if (scriptLogger == null){
			/**
			 * 当脚本没有配置logger的时候，创建一个缺省的Logger
			 */
			synchronized (this){
				if (scriptLogger == null){
					scriptLogger = new ScriptLogger.Default();
				}
			}
		}
		scriptLogger.handle(logInfo, System.currentTimeMillis());
	}
}
