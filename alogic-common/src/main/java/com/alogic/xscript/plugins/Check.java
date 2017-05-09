package com.alogic.xscript.plugins;


import org.apache.commons.lang3.StringUtils;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 检查某个参数是否存在
 * 
 * @author duanyy
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
 */
public class Check extends AbstractLogiclet{
	protected String arguId;	
	protected String code = "client.args_not_found";
	protected String reason = "Can not find parameter:%s";
	public Check(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		
		arguId = PropertiesConstants.getString(p,"id", arguId);
		code = PropertiesConstants.getString(p,"code", code);
		reason = PropertiesConstants.getString(p,"reason", reason);
	}		
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		if (StringUtils.isNotEmpty(arguId)){
			String value = ctx.GetValue(arguId, "");
			if (StringUtils.isEmpty(value)){
				throw new BaseException(code,String.format(reason,arguId));
			}
		}
	}

}
