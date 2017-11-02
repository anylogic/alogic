package com.logicbus.kvalue.xscript;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.kvalue.core.KeyValueRow;

/**
 * 设置ttl
 * 
 * @author duanyy
 *
 */
public class KVTTL extends KVRowOperation {
	protected String ttl = "0";
	protected boolean at = false;
	
	public KVTTL(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		ttl = PropertiesConstants.getRaw(p,"ttl",ttl);
		at = PropertiesConstants.getBoolean(p,"at", at,true);
	}
	
	@Override
	protected void onExecute(KeyValueRow row, Map<String, Object> root, Map<String, Object> current,
			LogicletContext ctx, ExecuteWatcher watcher) {

		long ttlValue = getLong(ctx.transform(ttl),0);
	
		if (ttlValue == 0){
			//获取当前的ttl值
			ctx.SetValue(id, String.valueOf(row.ttl()));
		}else{
			if (ttlValue > 0){
				//如果ttl>0,是设置ttl
				if (at){
					//按绝对时间设置
					ctx.SetValue(id, String.valueOf(row.ttlAt(ttlValue, TimeUnit.MILLISECONDS)));
				}else{
					//按相对时间设置
					ctx.SetValue(id, String.valueOf(row.ttl(ttlValue,TimeUnit.MILLISECONDS)));
				}
			}else{
				//如果ttl<0，是移除ttl
				ctx.SetValue(id, String.valueOf(row.persist()));
			}
		}
	}

}
