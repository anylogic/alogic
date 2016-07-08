package com.alogic.together.plugins;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alogic.together.AbstractLogiclet;
import com.alogic.together.ExecuteWatcher;
import com.alogic.together.Logiclet;
import com.alogic.together.LogicletContext;
import com.anysoft.util.KeyGen;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 生成uuid到指定的上下文变量
 * 
 * @author duanyy
 *
 */
public class UUid extends AbstractLogiclet {
	protected String id;
	protected int length = -1;
	protected int redix = 0;
	
	public UUid(String tag, Logiclet p) {
		super(tag, p);
	}

	public void configure(Properties p){
		super.configure(p);
		
		id = PropertiesConstants.getString(p,"id","",true);
		length = PropertiesConstants.getInt(p,"length",length,true);
		redix = PropertiesConstants.getInt(p,"redix",redix,true);
	}

	@Override
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher) {
		if (StringUtils.isNotEmpty(id)){
			ctx.SetValue(id, KeyGen.uuid(length,redix));
		}
	}

}
