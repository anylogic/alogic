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

/**对一个列表进行修剪(trim)，<br/>就是说，让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除。
下标 0 表示列表的第一个元素，以 1 表示列表的第二个元素，以此类推。 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
 * @author zhongyi
 *
 */
public class KVLTrim extends KVRowOperation {
	
	protected String start = "0";
	protected String end = "-1";
	
	public KVLTrim(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		start = PropertiesConstants.getRaw(p,"start",start);
		end = PropertiesConstants.getRaw(p,"end",end);	
	}
	
	@Override
	protected void onExecute(KeyValueRow row, Map<String, Object> root, Map<String, Object> current,
			LogicletContext ctx, ExecuteWatcher watcher) {

		if (row instanceof ListRow) {
			ListRow r = (ListRow) row;
			ctx.SetValue(id, String.valueOf(r.trim(getLong(ctx.transform(start),0), getLong(ctx.transform(end),-1))));
			
		}
	}

}
