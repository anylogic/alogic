package com.alogic.xscript;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.anysoft.batch.Process;
import com.anysoft.util.CommandLine;
import com.anysoft.util.Copyright;
import com.anysoft.util.DefaultProperties;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.SystemProperties;
import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.JsonProviderFactory;

/**
 * 命令行脚本入口
 * 
 * @author duanyy
 *
 */
public class Main implements Process {
	/**
	 * 参数集
	 */
	protected Properties props = null;
	
	/**
	 * 脚本
	 */
	protected Script script = null;
	
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
		if (StringUtils.isEmpty(xscript)){
			logger.error("I do not know which script to run!");
			return -1;
		}
		
		script = Script.create(xscript, props);
		if (script == null){
			logger.error("Fail to compile the script:" + xscript);
			return -1;
		}
		
		return 0;
	}

	public int run() {
		if (script != null){
			Map<String,Object> root = new HashMap<String,Object>();
			LogicletContext ctx = new LogicletContext(props);
			script.execute(root, root, ctx, new ExecuteWatcher.Quiet());
			
			JsonProvider provider = JsonProviderFactory.createProvider();
			logger.info(provider.toJson(root));
		}
		return 0;
	}

}
