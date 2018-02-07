package com.alogic.xscript.plugins;

import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 对整型变量增加累加量
 * @author yyduan
 * 
 * @since 1.6.10.1
 * 
 * @version 1.6.11.16 [20180207 duanyy] <br>
 * - value缺省值由0改为1; <br>
 */
public class Incr extends AbstractLogiclet {
	protected String id;
	protected String value = "1";
	public Incr(String tag, Logiclet p) {
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
		ctx.SetValue(id, String.valueOf(oldValue + incrValue));
	}
	
	protected long getLong(String v,long dft){
		try{
			return Long.parseLong(v);
		}catch (NumberFormatException ex){
			return dft;
		}
	}
}
