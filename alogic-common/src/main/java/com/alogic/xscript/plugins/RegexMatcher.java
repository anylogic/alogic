package com.alogic.xscript.plugins;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 正则表达式匹配
 * @author yyduan
 * 
 * @since 1.6.11.57
 * 
 */
public class RegexMatcher extends Segment {
	protected String id = "";
	protected String cid = "$regex";
	protected Pattern pattern = null;
	protected String $value = "";
	protected String var = "v%d";
	
	public RegexMatcher(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		id = PropertiesConstants.getString(p,"id","$" + this.getXmlTag(),true);
		cid = PropertiesConstants.getString(p,"cid",cid,true);
		var = PropertiesConstants.getString(p,"var",var,true);
		$value = PropertiesConstants.getRaw(p,"value",$value);
		
		String regex = PropertiesConstants.getString(p,"pattern","",true);
		if (StringUtils.isNotEmpty(regex)){
			try {
				pattern = Pattern.compile(regex);
			}catch (Exception ex){
				logger.error(ExceptionUtils.getStackTrace(ex));
			}
		}
	}
	
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		if (pattern != null){
			String value = PropertiesConstants.transform(ctx, $value, "");
			if (StringUtils.isNotEmpty(value)){
				Matcher matcher = pattern.matcher(value);
				if (matcher.find()){
					ctx.SetValue(id, "true");
					for (int i = 1; i < matcher.groupCount() + 1 ; i ++){
						ctx.SetValue(String.format(var, i), matcher.group(i));
					}					
					super.onExecute(root, current, ctx, watcher);
				}else{
					ctx.SetValue(id, "false");
				}
			}
		}
	}	
}
