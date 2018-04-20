package com.alogic.xscript.plugins;

import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 从模板中一次设置多个变量
 * 
 * @author yyduan
 * @since 1.6.11.28
 */
public class SetMulti extends AbstractLogiclet {
	/**
	 * 模板
	 */
	protected String pattern = "";
	
	protected String delimeter1 = ";";
	
	protected String delimeter2 = "=";
	
	public SetMulti(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		pattern = PropertiesConstants.getRaw(p, "pattern", "");
		
		delimeter1 = PropertiesConstants.getString(p,"delimeter1",delimeter1);
		delimeter2 = PropertiesConstants.getString(p,"delimeter2",delimeter2);
	}

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		String p = PropertiesConstants.transform(ctx, pattern, "");
		
		if (StringUtils.isNotEmpty(p)){
			StringTokenizer t = new StringTokenizer(p,delimeter1);
		     while (t.hasMoreTokens()) {
		         String pair = t.nextToken();
		         int index = pair.indexOf(delimeter2);
		         if (index < 0){
		        	 continue;
		         }
		         String name = pair.substring(0,index);
		         String value = pair.substring(index + 1,pair.length());
		         if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(value)){
		        	 ctx.SetValue(name, value);
		         }
		     }		
		}
	}

}

