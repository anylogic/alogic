package com.alogic.xscript.plugins;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 将当前时间(毫米数设置到上下文变量)
 * 
 * @author duanyy
 * 
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
 * 
 * @version 1.6.9.7 <br>
 * - 增加输出的pattern支持 <br>
 */
public class Now extends AbstractLogiclet {
	protected String id;
	protected String pattern;
	public Now(String tag, Logiclet p) {
		super(tag, p);
	}

	public void configure(Properties p){
		super.configure(p);
		
		id = PropertiesConstants.getString(p,"id","",true);
		pattern = PropertiesConstants.getString(p,"pattern","",true);
	}

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		if (StringUtils.isNotEmpty(id)){
			long now = System.currentTimeMillis();
			if (StringUtils.isEmpty(pattern)){
				ctx.SetValue(id, String.valueOf(now));
			}else{
				String value = DateFormatUtils.format(now, pattern);
				ctx.SetValue(id, value);
			}
		}
	}

}
