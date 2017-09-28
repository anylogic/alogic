package com.alogic.xscript.plugins;

import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 生成指定范围的随机数
 * @author yyduan
 *
 */
public class Rand extends AbstractLogiclet {
	protected String id;
	protected String min = "0";
	protected String max = "100";
	
	public Rand(String tag, Logiclet p) {
		super(tag, p);
	}

	public void configure(Properties p){
		super.configure(p);
		
		id = PropertiesConstants.getRaw(p,"id","");
		min = PropertiesConstants.getRaw(p,"min",min);
		max = PropertiesConstants.getRaw(p,"max",max);
	}

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		String idValue = PropertiesConstants.transform(ctx, id, "");
		if (StringUtils.isNotEmpty(idValue)){
			int minValue = PropertiesConstants.transform(ctx, min, 0);
			int maxValue = PropertiesConstants.transform(ctx, max, 0);
			
			if (maxValue < minValue){
				maxValue = maxValue + minValue;
				minValue = maxValue - minValue;
				maxValue = maxValue - minValue;
			}
			
			Random r = new Random();
			
			long value = (maxValue == minValue)? minValue : (minValue + r.nextInt(maxValue - minValue) % (maxValue - minValue));
			
			ctx.SetValue(idValue,String.valueOf(value));
		}
	}

}
