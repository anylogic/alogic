package com.alogic.xscript.plugins;

import org.apache.commons.lang3.StringUtils;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.StringMatcher;

/**
 * 字符串匹配插件
 * @author yyduan
 * @since 1.6.8.4
 * 
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
 * 
 */
public class Match extends AbstractLogiclet {
	protected String id = "$match";
	protected String value = "";
	protected StringMatcher matcher = null;
	public Match(String tag, Logiclet p) {
		super(tag, p);
	}

	public void configure(Properties p){
		super.configure(p);
		
		id = PropertiesConstants.getString(p,"id","$" + getXmlTag(),true);
		value = PropertiesConstants.getRaw(p,"value",value);
		matcher = new StringMatcher(PropertiesConstants.getString(p,"pattern", "*",true));
	}

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		if (StringUtils.isNotEmpty(id)){
			String v = ctx.transform(value);
			ctx.SetValue(id, Boolean.toString(matcher.match(v)));
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