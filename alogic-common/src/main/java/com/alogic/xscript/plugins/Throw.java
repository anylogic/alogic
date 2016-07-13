package com.alogic.xscript.plugins;

import java.util.Map;

import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * Throw
 * 
 * <p>
 * Throw is used to throw an exception.
 * 
 * @author duanyy
 *
 */
public class Throw extends AbstractLogiclet{
	protected String id = STMT_EXCEPTION;
	protected String msg;
	
	public Throw(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		id = PropertiesConstants.getString(p,"id",id,true);
		msg = PropertiesConstants.getString(p,"msg","",true);
	}

	@Override
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher) {
		throw new BaseException(id,msg);
	}

}
