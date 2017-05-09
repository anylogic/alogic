package com.alogic.xscript.plugins;

import org.apache.commons.lang3.StringUtils;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 检查参数，如果为空，就设置缺省值
 * 
 * @author duanyy
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
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
		dftValue = PropertiesConstants.getRaw(p, "dft", "");
	}		
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		if (StringUtils.isNotEmpty(arguId)){
			String value = ctx.GetValue(arguId, "");
			if (StringUtils.isEmpty(value)){
				ctx.SetValue(arguId, ctx.transform(dftValue));
			}
		}
	}

}