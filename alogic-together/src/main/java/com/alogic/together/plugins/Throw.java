package com.alogic.together.plugins;

import java.util.Map;

import com.alogic.together.AbstractLogiclet;
import com.alogic.together.ExecuteWatcher;
import com.alogic.together.Logiclet;
import com.alogic.together.LogicletContext;
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
