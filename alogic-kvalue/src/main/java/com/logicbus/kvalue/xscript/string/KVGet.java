package com.logicbus.kvalue.xscript.string;

import java.util.Map;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.logicbus.kvalue.xscript.KVRowOperation;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.kvalue.core.KeyValueRow;
import com.logicbus.kvalue.core.StringRow;

/**
 * 获取string值，对应redis指令:get
 * 
 * @author duanyy
 *
 */
public class KVGet extends KVRowOperation {
	protected String dftValue = "";
	
	public KVGet(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		dftValue = PropertiesConstants.getRaw(p,"dft",dftValue);
	}

	@Override
	protected void onExecute(KeyValueRow row, Map<String, Object> root, Map<String, Object> current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		if (row instanceof StringRow){
			StringRow r = (StringRow)row;			
			String dft = ctx.transform(dftValue);			
			ctx.SetValue(id, r.get(dft));
		}
	}

}
