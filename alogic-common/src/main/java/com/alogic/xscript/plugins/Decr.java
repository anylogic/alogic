package com.alogic.xscript.plugins;

import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 对整形变量执行累减操作
 * 
 * @author yyduan
 *
 * @since 1.6.10.1
 */
public class Decr extends AbstractLogiclet {
	protected String id;
	protected String value = "0";
	public Decr(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		id = PropertiesConstants.getString(p,"id","",true);
		value = PropertiesConstants.getRaw(p,"value",value);
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current,final LogicletContext ctx,final ExecuteWatcher watcher){
		long oldValue = getLong(ctx.GetValue(id, "0"),0);
		long incrValue = getLong(ctx.transform(value),0);
		ctx.SetValue(id, String.valueOf(oldValue - incrValue));
	}
	
	protected long getLong(String v,long dft){
		try{
			return Long.parseLong(v);
		}catch (NumberFormatException ex){
			return dft;
		}
	}
}