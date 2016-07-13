package com.alogic.xscript.log;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.alogic.xscript.Logiclet;
import com.anysoft.stream.AbstractHandler;
import com.anysoft.util.Properties;


/**
 * 缺省处理器
 * 
 * @author duanyy
 *
 */
public class Default extends AbstractHandler<LogInfo>{
	/**
	 * a logger of log4j
	 */
	protected static final Logger logger = LogManager.getLogger(Logiclet.class);

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
