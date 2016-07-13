package com.alogic.xscript.plugins;

import java.util.Map;

import org.w3c.dom.Element;

import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.log.Default;
import com.alogic.xscript.log.LogInfo;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.anysoft.stream.Handler;
import com.anysoft.util.Factory;


/**
 * 定义日志处理器
 * 
 * @author duanyy
 *
 */
public class Logger extends AbstractLogiclet{

	public Logger(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	protected void onExecute(Map<String, Object> root, Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		// 不会被执行
	}

	@Override
	public boolean isExecutable() {
		return false;
	}
	
	@Override
	public void configure(Element e, Properties p) {
		Properties props = new XmlElementProperties(e,p);
		configure(props);
		
		Factory<Handler<LogInfo>> factory = new Factory<Handler<LogInfo>>();
		try {
			Handler<LogInfo> logHandler = factory.newInstance(e, props, "module", Default.class.getName());
			if (logHandler != null){
				Logiclet parent = this.parent();
				if (parent != null){
					parent.registerLogger(logHandler);
				}
			}
		}catch (Exception ex){
			logger.error("Can not create log handler,xml=" + XmlTools.node2String(e));
		}
	}	
}
