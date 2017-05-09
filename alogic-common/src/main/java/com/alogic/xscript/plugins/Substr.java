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
 * 对取值取子字符串，然后设为变量
 * 
 * @author duanyy
 * @version 1.6.8.4 [20170329 duanyy] <br>
 * - 只取context变量，不取文档属性变量 <br>
 * 
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
 * 
 */
public class Substr extends AbstractLogiclet {
	protected String id;
	protected String value;
	protected String begin;
	protected String length;
	
	public Substr(String tag, Logiclet p) {
		super(tag, p);
	}

	public void configure(Properties p){
		super.configure(p);
		
		id = PropertiesConstants.getString(p,"id","",true);
		value = PropertiesConstants.getRaw(p,"value", "");
		begin = PropertiesConstants.getRaw(p,"start", "0");
		length = PropertiesConstants.getRaw(p, "length", "-1");
	}

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		if (StringUtils.isNotEmpty(id)){
			String v = ctx.transform(value);
			int start = getInt(ctx,begin,0);
			int len = getInt(ctx,length,-1);
			
			start = start < 0 || start > v.length() ? 0 : start;
			len = len < 0 || start + len > v.length() ? v.length() - start : len;
			ctx.SetValue(id, v.substring(start,start + len));
		}
	}

	public int getInt(Properties p,String pattern,int dft){
		String v = p.transform(pattern);
		if (StringUtils.isNotEmpty(v)){
			try {
				return Integer.parseInt(v);
			}catch (NumberFormatException ex){
				return dft;
			}
		}
		return dft;
	}
}
