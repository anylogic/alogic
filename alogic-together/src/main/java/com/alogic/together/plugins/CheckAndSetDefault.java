package com.alogic.together.plugins;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alogic.together.AbstractLogiclet;
import com.alogic.together.ExecuteWatcher;
import com.alogic.together.Logiclet;
import com.alogic.together.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 检查参数，如果为空，就设置缺省值
 * 
 * @author duanyy
 *
 */
public class CheckAndSetDefault extends AbstractLogiclet{
	protected String arguId;	
	protected String dftValue;
	
	public CheckAndSetDefault(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		
		arguId = PropertiesConstants.getString(p,"id", arguId);
		dftValue = PropertiesConstants.getString(p,"dft", dftValue);
	}		
	
	@Override
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		if (StringUtils.isNotEmpty(arguId)){
			String value = ctx.GetValue(arguId, "");
			if (StringUtils.isEmpty(value)){
				ctx.SetValue(arguId, ctx.transform(dftValue));
			}
		}
	}

}