package com.logicbus.backend.bizlog.handler;

import org.w3c.dom.Element;
import com.anysoft.stream.AbstractHandler;
import com.anysoft.util.Properties;
import com.logicbus.backend.bizlog.BizLogItem;
import com.logicbus.backend.bizlog.BizLogger;

/**
 * 安静模式
 * 
 * @author yyduan
 * @since 1.6.7.12
 */
public class Quiet extends AbstractHandler<BizLogItem>  implements BizLogger {

	@Override
	protected void onHandle(BizLogItem _data, long timestamp) {

	}

	@Override
	protected void onFlush(long timestamp) {

	}

	@Override
	protected void onConfigure(Element e, Properties p) {

	}

}
