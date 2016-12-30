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
 * 从上下文变量中获取变量值，并设置到当前节点上
 * 
 * @author duanyy
 * 
 * @version 1.6.6.11 [duanyy 20161227] <br>
 * - 增加类型
 */
public class Get extends AbstractLogiclet {
	protected String id;
	protected String value;
	protected String type;
	public Get(String tag, Logiclet p) {
		super(tag, p);
	}

	public void configure(Properties p){
		super.configure(p);
		
		id = PropertiesConstants.getRaw(p,"id","");
		value = PropertiesConstants.getRaw(p,"value","");
		type = PropertiesConstants.getString(p,"type","string",true);
	}

	@Override
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher) {
		MapProperties p = new MapProperties(current,ctx);
		String idValue = p.transform(id);
		if (StringUtils.isNotEmpty(idValue)){
			String v = p.transform(value);
			if (StringUtils.isNotEmpty(v)){
				if (type.equals("string")){
					current.put(idValue, v);
				}else{
					if (type.equals("long")){
						try{
							current.put(idValue,Long.parseLong(v));
						}catch (NumberFormatException ex){
							current.put(idValue, v);
						}
					}else{
						if (type.equals("double")){
							try{
								current.put(idValue,Double.parseDouble(v));
							}catch (NumberFormatException ex){
								current.put(idValue, v);
							}							
						}else{
							current.put(idValue, v);
						}
					}
				}
			}else{
				current.remove(idValue);
			}
		}
	}

}
