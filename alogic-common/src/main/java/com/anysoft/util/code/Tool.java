package com.anysoft.util.code;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import com.anysoft.batch.Process;
import com.anysoft.util.CommandLine;
import com.anysoft.util.Copyright;
import com.anysoft.util.DefaultProperties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.SystemProperties;

/**
 * 工具
 * 
 * 用于对小文本编码
 * 
 * @author duanyy
 * @since 1.6.3.35
 */
public class Tool implements Process {
	/**
	 * a logger of log4j
	 */
	protected static Logger logger = LogManager.getLogger(Tool.class);

	protected String text;
	
	protected String key;
	
	protected String coder = "DES3";
	
	public int init(DefaultProperties p) {
		text = PropertiesConstants.getString(p, "txt", "");
		if (isNull(text)){
			logger.info("Can not find the text to encode");
			return -1;
		}
		
		key = PropertiesConstants.getString(p, "key", "");
		if (isNull(key)){
			logger.info("Can not find the key to encode");
			return -1;
		}
		
		coder = PropertiesConstants.getString(p,"coder","DES3");
		return 0;
	}

	public int run() {
		try{
			Coder _coder = CoderFactory.newCoder(coder);
			logger.info("The encrypt text is " + _coder.encode(text, key));
			return 0;
		}catch (Exception ex){
			logger.info("Can not find coder named " + coder);
			return -1;
		}
	}	
	
	public static void main(String[] args) {
		Copyright.bless(logger, "\t\t");
		int result = 0;
		try {
			CommandLine cmdLine = new CommandLine(args,new SystemProperties());		
			Tool main = new Tool();
			
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
	
	public static boolean isNull(String value){
		return value == null || value.length() <= 0;
	}
}
