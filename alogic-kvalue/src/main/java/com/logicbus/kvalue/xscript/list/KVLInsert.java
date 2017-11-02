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

/**
 * Redis Linsert 命令用于在列表的元素前或者后插入元素。 当指定元素不存在于列表中时，不执行任何操作。
 * 当列表不存在时，被视为空列表，不执行任何操作。 如果 key 不是列表类型，返回一个错误。</br>
 * 如果命令执行成功，返回插入操作完成之后，列表的长度。 如果没有找到指定元素 ，返回 -1 。 如果 key 不存在或为空列表，返回 0
 * 
 * @author zhongyi
 *
 */
public class KVLInsert extends KVRowOperation {

	protected String value = "";
	/**
	 * 参照值；会在参照值前或后插入新的值
	 */
	protected String pivot = "";
	protected String insertAfter = "false";
	// protected String delimeter = "";

	public KVLInsert(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		value = PropertiesConstants.getRaw(p, "value", value);
		insertAfter = PropertiesConstants.getRaw(p, "insertAfter", insertAfter);
		pivot = PropertiesConstants.getRaw(p, "pivot", pivot);
	}

	@Override
	protected void onExecute(KeyValueRow row, Map<String, Object> root, Map<String, Object> current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		if (row instanceof ListRow) {
			ListRow r = (ListRow) row;
			String result=String
					.valueOf(r.insert(ctx.transform(pivot), ctx.transform(value), getBoolean(ctx.transform(insertAfter), false)));
			ctx.SetValue(id, result);
		}
	}

}
