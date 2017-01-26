package com.alogic.xscript.plugins;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.util.MapProperties;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 设置变量值到上下文
 * 
 * @author duanyy
 *
 */
public class Set extends AbstractLogiclet {
	protected String id;
	protected String value;
	protected String dftValue = "";
	protected boolean ref = false;
	
	public Set(String tag, Logiclet p) {
		super(tag, p);
	}

	public void configure(Properties p){
		super.configure(p);
		
		id = PropertiesConstants.getString(p,"id","",true);
		value = p.GetValue("value", "", false, true);
		ref = PropertiesConstants.getBoolean(p,"ref",ref,true);
		dftValue = PropertiesConstants.getString(p,"dft",dftValue,true);
	}

	@Override
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher) {
		if (StringUtils.isNotEmpty(id)){
			MapProperties p = new MapProperties(current,ctx);
			String v = p.transform(value);
			String dft = p.transform(dftValue);
			if (StringUtils.isEmpty(v)){
				v = dft;
			}
			if (ref){
				v = PropertiesConstants.getString(p,v,dft,false);
			}
			
			ctx.SetValue(id, v);
		}
	}

}
