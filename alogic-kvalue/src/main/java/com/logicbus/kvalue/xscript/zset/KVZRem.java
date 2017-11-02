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

/**
 * 移除有序集 key 中的一个或多个成员，不存在的成员将被忽略
 * @author zhongyi
 *
 */
public class KVZRem extends KVRowOperation {

	protected String item = "";
	protected String delimeter = ";";

	public KVZRem(String tag, Logiclet p) {
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

		String items = ctx.transform(item);
		if (row instanceof SortedSetRow) {	
			SortedSetRow r = (SortedSetRow) row;
			ctx.SetValue(id, String.valueOf(r.remove(items.split(delimeter))));
		}
		
	}
	
	public static void main(String[] args) {
		String test="abcd";
		String[] r=test.split(" ");
		System.out.println(r.length+"-----"+r[0]);
	}

}
