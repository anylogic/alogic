package com.anysoft.xscript;

/**
 * 脚本
 * @author duanyy
 * @since 1.6.3.22
 * @version 1.6.3.25 <br>
 * - 统一脚本的日志处理机制 <br>
 * 
 * @version 1.6.4.17 [20151216 duanyy] <br>
 * - 根据sonar建议优化代码 <br>
 */
public class Script extends Segment {

	public Script(String xmlTag, Statement parent) {
		super(xmlTag, parent);
	}

	@Override
	public void log(ScriptLogInfo logInfo){
		if (scriptLogger == null){
			/**
			 * 当脚本没有配置logger的时候，创建一个缺省的Logger
			 */
			synchronized (this){
				if (scriptLogger == null){ // NOSONAR
					scriptLogger = new ScriptLogger.Default();
				}
			}
		}
		scriptLogger.handle(logInfo, System.currentTimeMillis());
	}
}
