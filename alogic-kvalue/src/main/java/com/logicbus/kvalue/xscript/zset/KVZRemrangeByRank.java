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

/**移除有序集 key 中，指定排名(rank)区间内的所有成员。
 * @author zhongyi
 *
 */
public class KVZRemrangeByRank extends KVRowOperation {

	protected String start = "0";
	protected String end = "0";

	public KVZRemrangeByRank(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		start = PropertiesConstants.getRaw(p, "start", start);
		end = PropertiesConstants.getRaw(p, "end", end);

	}

	@Override
	protected void onExecute(KeyValueRow row, Map<String, Object> root, Map<String, Object> current,
			LogicletContext ctx, ExecuteWatcher watcher) {

		if (row instanceof SortedSetRow) {
			SortedSetRow r = (SortedSetRow) row;

			ctx.SetValue(id,
					String.valueOf(r.remove(getLong(ctx.transform(start), 0l), getLong(ctx.transform(end), 0l))));

		}

	}
}
