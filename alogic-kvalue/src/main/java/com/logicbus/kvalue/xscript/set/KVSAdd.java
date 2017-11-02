package com.logicbus.kvalue.xscript.set;

import java.util.Map;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.logicbus.kvalue.xscript.KVRowOperation;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.kvalue.core.KeyValueRow;
import com.logicbus.kvalue.core.SetRow;

/**将一个或多个 item 元素加入到集合 key 当中，已经存在于集合的 item 元素将被忽略。
 * 
 * @author zhongyi
 *
 */
public class KVSAdd extends KVRowOperation {

	protected String item = "";
	protected String delimeter = ";";

	public KVSAdd(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		item = PropertiesConstants.getRaw(p, "item", item);
		delimeter = PropertiesConstants.getRaw(p, "delimeter", delimeter);

	}

	@Override
	protected void onExecute(KeyValueRow row, Map<String, Object> root, Map<String, Object> current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		String[] items = ctx.transform(item).split(delimeter);
		if (row instanceof SetRow) {		
			SetRow r = (SetRow) row;
			ctx.SetValue(id, String.valueOf(r.add(items)));
		}

	}

}
