package com.alogic.xscript.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.alogic.xscript.Logiclet;
import com.anysoft.stream.AbstractHandler;
import com.anysoft.util.Properties;


/**
 * 缺省处理器
 * 
 * @author duanyy
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 */
public class Default extends AbstractHandler<LogInfo>{
	/**
	 * a logger of log4j
	 */
	protected static final Logger logger = LoggerFactory.getLogger(Logiclet.class);

	@Override
	protected void onHandle(LogInfo _data, long timestamp) {
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

	@Override
	protected void onFlush(long timestamp) {
		// i have no buffer
	}

	@Override
	protected void onConfigure(Element e, Properties p) {
		// nothing to do
	}

}
