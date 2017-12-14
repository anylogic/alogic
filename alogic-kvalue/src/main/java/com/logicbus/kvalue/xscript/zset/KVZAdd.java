package com.logicbus.kvalue.xscript.zset;

import java.util.HashMap;
import java.util.Map;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.logicbus.kvalue.xscript.KVRowOperation;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.ServantException;
import com.logicbus.kvalue.core.KeyValueRow;
import com.logicbus.kvalue.core.SortedSetRow;

/**ZADD key score member [[score member] [score member] ...] </br>
*将一个或多个 member 元素及其 score 值加入到有序集 key 当中。  </br>
*如果某个 member 已经是有序集的成员，那么更新这个 member 的 score 值，并通过重新插入这个 member 元素，来保证该 member 在正确的位置上</br>
 * 
 * @author zhongyi
 *
 */
public class KVZAdd extends KVRowOperation {

	protected String item = "";
	protected String score = "";
	protected String delimeter = ";";
	protected double defualScore = 1d;

	public KVZAdd(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		item = PropertiesConstants.getRaw(p, "item", item);
		score = PropertiesConstants.getRaw(p, "score", score);
		delimeter = PropertiesConstants.getRaw(p, "delimeter", delimeter);

	}

	@Override
	protected void onExecute(KeyValueRow row, Map<String, Object> root, Map<String, Object> current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		String scores = ctx.transform(score);
		String items = ctx.transform(item);
		if (row instanceof SortedSetRow) {
			String[] _items = items.split(delimeter);
			String[] _scores = scores.split(delimeter);
			if (_items.length != _scores.length) {
				throw new ServantException("core.e1003", String.format(
						"items length not equels scores length,please to check,items :%s，scores:%s", items, scores));
			}

			Map<String,Double> elements = new HashMap<String,Double>();
			for (int i = 0; i < _items.length; i++) {
				elements.put(_items[i], getDouble(_scores[i], defualScore));
			}

			SortedSetRow r = (SortedSetRow) row;
			ctx.SetValue(id, String.valueOf(r.add(elements)));
		}

	}

}
