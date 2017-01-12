package com.alogic.xscript.plugins;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.KeyGen;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 生成uuid到指定的上下文变量
 * 
 * @author duanyy
 *
 * @version 1.6.6.13 [20170112 duanyy] <br>
 * - 支持按字符区间取值 <br>
 */
public class UUid extends AbstractLogiclet {
	protected String id;
	protected int length = -1;
	protected int redix = -1;
	protected int start = 0;
	protected int end = 61;
	
	public UUid(String tag, Logiclet p) {
		super(tag, p);
	}

	public void configure(Properties p){
		super.configure(p);
		
		id = PropertiesConstants.getString(p,"id","",true);
		length = PropertiesConstants.getInt(p,"length",length,true);
		redix = PropertiesConstants.getInt(p,"redix",redix,true);
		start = PropertiesConstants.getInt(p,"start",start,true);
		end = PropertiesConstants.getInt(p,"end",end,true);
	}

	@Override
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher) {
		if (StringUtils.isNotEmpty(id)){
			if (redix >= 0){
				ctx.SetValue(id, KeyGen.uuid(length,redix));
			}else{
				ctx.SetValue(id,KeyGen.uuid(length,start,end));
			}
		}
	}

}
