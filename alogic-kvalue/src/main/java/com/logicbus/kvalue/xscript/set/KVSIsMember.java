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

/**判断 member 元素是否集合 key 的成员。
 * 
 * @author zhongyi
 *
 */
public class KVSIsMember extends KVRowOperation {

	protected String item = "";


	public KVSIsMember(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		item = PropertiesConstants.getRaw(p, "item", item);
	}

	@Override
	protected void onExecute(KeyValueRow row, Map<String, Object> root, Map<String, Object> current,
			LogicletContext ctx, ExecuteWatcher watcher) {

		if (row instanceof SetRow) {		
			SetRow r = (SetRow) row;			
			ctx.SetValue(id, String.valueOf(r.contain(ctx.transform(item))));
		}

	}

}
