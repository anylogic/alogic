package com.alogic.metrics.xscript;

import java.util.Map;

import com.alogic.metrics.Fragment;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 指标构造器
 * 
 * @author yyduan
 *
 */
public abstract class MetricsBuilder extends AbstractLogiclet{
	protected String pid = "$metrics";
	
	public MetricsBuilder(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		pid = PropertiesConstants.getString(p,"pid",pid,true);
	}
	
	@Override
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		Fragment f = ctx.getObject(pid);
		if (f == null){
			throw new BaseException("core.no_metrics_context",
					String.format("%s must be in a metrics context", getXmlTag()));
		}
		
		onExecute(f,root,current,ctx,watcher);
	}

	protected abstract void onExecute(Fragment f, Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher);

}
