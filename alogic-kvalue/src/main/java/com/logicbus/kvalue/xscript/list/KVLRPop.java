package com.logicbus.kvalue.xscript.list;

import java.util.Map;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.logicbus.kvalue.xscript.KVRowOperation;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.kvalue.core.KeyValueRow;
import com.logicbus.kvalue.core.ListRow;

/**移除并返回列表 key 的尾元素
 * @author zhongyi
 *
 */
public class KVLRPop extends KVRowOperation {

	protected String block = "false";

	
	public KVLRPop(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		block=PropertiesConstants.getRaw(p, "block", block);
	}
	
	@Override
	protected void onExecute(KeyValueRow row, Map<String, Object> root, Map<String, Object> current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		
		if (row instanceof ListRow){
			ListRow r = (ListRow)row;
			ctx.SetValue(id,  r.leftPop(getBoolean(ctx.transform(block), false)));			
		}
	}

}
