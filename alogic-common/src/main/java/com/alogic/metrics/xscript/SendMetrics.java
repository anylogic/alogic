package com.alogic.metrics.xscript;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alogic.metrics.Fragment;
import com.alogic.metrics.impl.DefaultFragment;
import com.alogic.metrics.stream.MetricsHandlerFactory;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.plugins.Segment;
import com.anysoft.stream.Handler;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 通过脚本发送指标
 * 
 * @author yyduan
 *
 */
public class SendMetrics extends Segment{
	/**
	 * 指标id
	 */
	protected String id = "";
	protected String type = "metrics";
	
	protected String cid = "$metrics";
	
	protected Handler<Fragment> handler = null;
	
	public SendMetrics(String tag, Logiclet p) {
		super(tag, p);
		
		registerModule("metrics-dim",AddDimension.class);
		registerModule("metrics-mea",AddMeasure.class);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		id = PropertiesConstants.getRaw(p,"id", "");
		cid = PropertiesConstants.getString(p,"cid",cid,true);
		type = PropertiesConstants.getString(p,"type",type,true);
		
		handler = MetricsHandlerFactory.getClientInstance();
	}
	
	@Override
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		String idValue = ctx.transform(id);
		
		if (StringUtils.isNotEmpty(idValue)){
			Fragment f = new DefaultFragment(id,type);
			try{
				ctx.setObject(cid, f);
				super.onExecute(root, current, ctx, watcher);
				if (handler != null){
					handler.handle(f, System.currentTimeMillis());
				}
			}finally{
				ctx.removeObject(cid);
			}
		}
	}

}