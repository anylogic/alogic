package com.anysoft.xscript;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.anysoft.batch.Process;
import com.anysoft.util.CommandLine;
import com.anysoft.util.Copyright;
import com.anysoft.util.DefaultProperties;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.SystemProperties;

/**
 * 命令行脚本运行入口
 * 
 * @author duanyy
 * 
 * @since 1.6.3.36
 * 
 */
public class Main implements Process{
	/**
	 * a logger of log4j
	 */
	protected static Logger logger = LogManager.getLogger(Main.class);
	
	/**
	 * 入口
	 * @param args
	 */
	public static void main(String[] args) {
		Copyright.bless(logger, "\t\t");
		int result = 0;
		try {
			CommandLine cmdLine = new CommandLine(args,new SystemProperties());		
			Main main = new Main();
			
			result = main.init(cmdLine);
			if (result == 0){
				result = main.run();
			}
		}catch (Exception ex){
			logger.error(ex.getMessage(), ex);
			result = -1;
		}
		System.exit(result);	
	}

	public int init(DefaultProperties p) {
		props = p;
		
		/**
		 * 读取script参数
		 */
		String xscript = PropertiesConstants.getString(props, "script", "");
		if (isNull(xscript)){
			logger.error("I do not know which script to run!");
			return -1;
		}
		
		script = XScriptTool.compile(xscript, props, new CompileWatcher.Quiet());
		if (script == null){
			logger.error("Fail to compile the script:" + xscript);
			return -1;
		}
		
		return 0;
	}

	public int run() {
		if (script != null){
			script.execute(props, new ExecuteWatcher.Quiet());
		}
		return 0;
	}

	protected static boolean isNull(String value){
		return value == null || value.length() <= 0;
	}
	
	/**
	 * 参数集
	 */
	protected Properties props = null;
	
	/**
	 * 脚本
	 */
	protected Statement script = null;
}
