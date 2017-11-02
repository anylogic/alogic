package com.logicbus.kvalue.xscript.zset;

import java.util.Map;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.logicbus.kvalue.xscript.KVRowOperation;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.kvalue.core.KeyValueRow;
import com.logicbus.kvalue.core.SortedSetRow;




public class KVZRangeByLex  extends KVRowOperation {
	
	
	protected String min = "-";
	protected String max = "+";
	protected String tag = "data";
	
	/**
	 * 预留2个参数用来支持分页
	 */
	protected String offset = "0";
	protected String count = "100";
	
	public KVZRangeByLex(String tag, Logiclet p) {
		super(tag, p);

	}



	@Override  
	public void configure(Properties p) {
		super.configure(p);
		min = PropertiesConstants.getRaw(p, "min", min);
		max = PropertiesConstants.getRaw(p, "max", max);
		tag = PropertiesConstants.getRaw(p, "tag", tag);
		offset = PropertiesConstants.getRaw(p, "offset", offset);
		count = PropertiesConstants.getRaw(p,"count",count);
	}

	


	@Override
	protected void onExecute(KeyValueRow row, Map<String, Object> root, Map<String, Object> current,
			LogicletContext ctx, ExecuteWatcher watcher) {

		long _offset = getLong(ctx.transform(offset), 0l);
		long _cnt = getLong(ctx.transform(count), 100l);
		
		if (row instanceof SortedSetRow) {
			SortedSetRow r = (SortedSetRow) row;
			current.put(ctx.transform(tag), r.rangeByLex(min, max, _offset, _cnt));
		}

	}







}
