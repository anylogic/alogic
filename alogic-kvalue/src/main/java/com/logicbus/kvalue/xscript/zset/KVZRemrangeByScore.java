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

/**移除有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。</br>
 * 返回值:被移除成员的数量。
 * @author zhongyi
 *
 */
public class KVZRemrangeByScore extends KVRowOperation {

	protected String min = "0";
	protected String max = "0";

	public KVZRemrangeByScore(String tag, Logiclet p) {
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
					String.valueOf(r.remove(getDouble(ctx.transform(min), 0d), getDouble(ctx.transform(max), 0d))));

		}

	}
}
