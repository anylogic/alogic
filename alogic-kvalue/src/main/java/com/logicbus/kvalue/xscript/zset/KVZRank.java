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

/**返回有序集 key 中成员 item 的排名。</br>
 * reverse=false,其中有序集成员按 score 值递减(从小到大)排序；排名以 0 为底，也就是说， score 值最大的成员排名为 0 </br>
 * reverse=true,其中有序集成员按 score 值递减(从大到小)排序；排名以 0 为底，也就是说， score 值最小的成员排名为  0</br>
 * @author zhongyi
 *
 */
public class KVZRank extends KVRowOperation {

	protected String item = "";
	protected String reverse = "";
	public KVZRank(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		item = PropertiesConstants.getRaw(p, "item", item);
		reverse = PropertiesConstants.getRaw(p, "reverse", reverse);
	}

	@Override
	protected void onExecute(KeyValueRow row, Map<String, Object> root, Map<String, Object> current,
			LogicletContext ctx, ExecuteWatcher watcher) {

		if (row instanceof SortedSetRow) {
			SortedSetRow r = (SortedSetRow) row;
	
			ctx.SetValue(id,
					String.valueOf(r.rank(ctx.transform(item), getBoolean(ctx.transform(reverse),false))));
		}

	}
}
