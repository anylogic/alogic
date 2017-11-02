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

/**为有序集 key 的成员 item 的 score 值加上增量 increment 。
 * 返回成员的新 score 值
 * @author zhongyi
 *
 */
public class KVZIncrby extends KVRowOperation {

	protected String item = "";
	protected String increment = "0";


	public KVZIncrby(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		item = PropertiesConstants.getRaw(p, "item", item);
		increment = PropertiesConstants.getRaw(p, "increment", increment);


	}

	@Override
	protected void onExecute(KeyValueRow row, Map<String, Object> root, Map<String, Object> current,
			LogicletContext ctx, ExecuteWatcher watcher) {

		if (row instanceof SortedSetRow) {

			SortedSetRow r = (SortedSetRow) row;
			ctx.SetValue(id, String.valueOf(r.incr(ctx.transform(item), getDouble(ctx.transform(increment), 0d))));
		}

	}

}
