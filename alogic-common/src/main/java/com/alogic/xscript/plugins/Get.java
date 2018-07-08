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
 * 从上下文变量中获取变量值，并设置到当前节点上
 * 
 * @author duanyy
 * 
 * @version 1.6.6.11 [duanyy 20161227] <br>
 * - 增加类型<br>
 * 
 * @version 1.6.7.21 [duanyy 20170303] <br>
 * - 修改xscript的Get插件，可支持为空时忽略 <br>
 * 
 * @version 1.6.7.22 [duanyy 20170306] <br>
 * - 不再将当前文档节点的属性作为变量 <br>
 * 
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
 * 
 * @version 1.6.11.43 [20180708 duanyy]  <br>
 * - 支持raw模式 <br>
 */
public class Get extends AbstractLogiclet {
	protected String id;
	protected String value;
	protected String type;
	protected boolean ignoreIfNull = false;
	protected boolean raw = false;
	public Get(String tag, Logiclet p) {
		super(tag, p);
	}

	public void configure(Properties p){
		super.configure(p);
		
		id = PropertiesConstants.getRaw(p,"id","");
		value = PropertiesConstants.getRaw(p,"value","");
		type = PropertiesConstants.getString(p,"type","string",true);
		ignoreIfNull = PropertiesConstants.getBoolean(p,"ignoreIfNull",ignoreIfNull,true);
		raw = PropertiesConstants.getBoolean(p, "raw", raw);
	}

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		String idValue = ctx.transform(id);
		if (StringUtils.isNotEmpty(idValue)){
			String v = raw?PropertiesConstants.getRaw(ctx,value,""):ctx.transform(value);
			if (StringUtils.isNotEmpty(v)){
				if (type.equals("string")){
					current.addProperty(idValue, v);
				}else{
					if (type.equals("long")){
						try{
							current.addProperty(idValue, Long.parseLong(v));
						}catch (NumberFormatException ex){
							current.addProperty(idValue, v);
						}
					}else{
						if (type.equals("double")){
							try{
								current.addProperty(idValue, Double.parseDouble(v));
							}catch (NumberFormatException ex){
								current.addProperty(idValue, v);
							}							
						}else{
							current.addProperty(idValue, v);
						}
					}
				}
			}else{
				if (!ignoreIfNull){
					current.remove(idValue);
				}
			}
		}
	}

}
