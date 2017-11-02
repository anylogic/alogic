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

/**这个命令类似于 SINTER 命令，但它将结果保存到 dstKey 集合，而不是简单地返回结果集。</br>
 *如果 dstKey 集合已经存在，则将其覆盖。</br>
 *dstKey 可以是 key 本身。</br>
 * 
 * @author zhongyi
 *
 */
public class KVSInterStore extends KVRowOperation {

	protected String others = "";
	protected String dstKey = "";
	protected String delimeter = ";";


	public KVSInterStore(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		others = PropertiesConstants.getRaw(p, "others", others);
		dstKey = PropertiesConstants.getRaw(p, "dstKey", dstKey);
		delimeter = PropertiesConstants.getRaw(p, "delimeter", delimeter);

	}

	@Override
	protected void onExecute(KeyValueRow row, Map<String, Object> root, Map<String, Object> current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		if (row instanceof SetRow) {
			SetRow r = (SetRow) row;
			ctx.SetValue(id, String.valueOf(r.interStore(dstKey, ctx.transform(others).split(delimeter))));
		}
	}

}
