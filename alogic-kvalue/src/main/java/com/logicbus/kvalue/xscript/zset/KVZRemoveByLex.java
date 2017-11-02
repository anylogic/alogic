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

/**按区间移除元素，默认区间是 - -。
 * @author zhongyi
 *
 */
public class KVZRemoveByLex  extends KVRowOperation {

	protected String min = "-";
	protected String max = "-";
	
	public KVZRemoveByLex(String tag, Logiclet p) {
		super(tag, p);


	}

	
	@Override  
	public void configure(Properties p) {
		super.configure(p);
		min = PropertiesConstants.getRaw(p, "min", min);
		max = PropertiesConstants.getRaw(p, "max", max);
	}
	
	
	
	
	@Override
	protected void onExecute(KeyValueRow row, Map<String, Object> root, Map<String, Object> current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		if (row instanceof SortedSetRow) {
			SortedSetRow r = (SortedSetRow) row;
			ctx.SetValue(id,
					String.valueOf(r.removeByLex(min, max)));
		}
		
	}
}
