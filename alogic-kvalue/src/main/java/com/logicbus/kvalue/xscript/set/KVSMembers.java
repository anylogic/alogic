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

/**返回集合 中的所有成员。
 * 
 * @author zhongyi
 *
 */
public class KVSMembers extends KVRowOperation {


	protected String tag = "data";

	public KVSMembers(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);

		tag = PropertiesConstants.getRaw(p, "tag", tag);
	}

	@Override
	protected void onExecute(KeyValueRow row, Map<String, Object> root, Map<String, Object> current,
			LogicletContext ctx, ExecuteWatcher watcher) {

		if (row instanceof SetRow) {		
			SetRow r = (SetRow) row;
			current.put(ctx.transform(tag), r.getAll());
		}

	}

}
