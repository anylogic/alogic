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

/**Redis Lrem 根据参数 COUNT 的值，移除列表中与参数 VALUE 相等的元素。<br/>
COUNT 的值可以是以下几种：<br/>
count > 0 : 从表头开始向表尾搜索，移除与 VALUE 相等的元素，数量为 COUNT 。<br/>
count < 0 : 从表尾开始向表头搜索，移除与 VALUE 相等的元素，数量为 COUNT 的绝对值。<br/>
count = 0 : 移除表中所有与 VALUE 相等的值<br/>
 * @author zhongyi
 *
 */
public class KVLRem extends KVRowOperation{

	protected String value = "";
	/**
	 * 
	 */
	protected String count = "1";

	public KVLRem(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		value = PropertiesConstants.getRaw(p,"value",value);
		count = PropertiesConstants.getRaw(p,"count",count);		
	}
	
	@Override
	protected void onExecute(KeyValueRow row, Map<String, Object> root, Map<String, Object> current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		if (row instanceof ListRow) {
			ListRow r = (ListRow) row;
			ctx.SetValue(id, String.valueOf(r.remove(ctx.transform(value),  getLong(ctx.transform(count),1))));
		}
		
	}

}
