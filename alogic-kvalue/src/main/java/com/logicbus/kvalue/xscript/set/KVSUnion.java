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

/**
 *返回一个集合的全部成员，该集合是所有给定集合的并集。<br/>
 *不存在的 key 被视为空集。
 * 
 * @author zhongyi
 *
 */
public class KVSUnion extends KVRowOperation {

	protected String others = "";
	protected String delimeter = ";";
	protected String tag = "data";

	public KVSUnion(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		others = PropertiesConstants.getRaw(p, "others", others);
		delimeter = PropertiesConstants.getRaw(p, "delimeter", delimeter);
		tag = PropertiesConstants.getRaw(p, "tag", tag);
	}

	@Override
	protected void onExecute(KeyValueRow row, Map<String, Object> root, Map<String, Object> current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		if (row instanceof SetRow) {
			SetRow r = (SetRow) row;
			current.put(ctx.transform(tag), r.union(ctx.transform(others).split(delimeter)));
		}
	}

}
