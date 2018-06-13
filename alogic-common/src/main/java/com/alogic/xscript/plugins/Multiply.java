package com.alogic.xscript.plugins;

import java.text.DecimalFormat;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 简单相乘
 * @author yyduan
 * @since 1.6.11.36 
 */
public class Multiply extends AbstractLogiclet {
	protected String id;
	protected String $a = "1";
	protected String $b = "1";
	protected boolean asDouble = false;
	protected DecimalFormat formatter = null;
	public Multiply(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		id = PropertiesConstants.getString(p,"id","",true);
		$a = PropertiesConstants.getRaw(p,"a",$a);
		$b = PropertiesConstants.getRaw(p,"b",$b);
		asDouble = PropertiesConstants.getBoolean(p,"asDouble",false);
		formatter = new DecimalFormat(PropertiesConstants.getString(p,"format","#.##",true));
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current,final LogicletContext ctx,final ExecuteWatcher watcher){
		if (StringUtils.isNotEmpty(id)){
			if (asDouble){
				double result = PropertiesConstants.transform(ctx, $a, 0.0) * PropertiesConstants.transform(ctx, $b, 0.0);
				ctx.SetValue(id, formatter.format(result));
			}else{
				ctx.SetValue(id, String.valueOf(PropertiesConstants.transform(ctx, $a, 0) * PropertiesConstants.transform(ctx, $b, 0)));
			}
		}
	}
	
}
