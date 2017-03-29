package com.alogic.xscript.plugins;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.jayway.jsonpath.JsonPath;

/**
 * 循环
 * 
 * @author duanyy
 * @version 1.6.7.22 [20170306 duanyy] <br>
 * - 当jsonPath语法错误或者节点不存在时，不再抛出异常 <br>
 * 
 * @version 1.6.8.4 [20170329 duanyy] <br>
 * - 对象的属性可以循环处理 <br>
 */
public class Repeat extends Segment{
	protected String jsonPath;
	protected String value = "$value";
	protected String key = "$key";
	public Repeat(String tag, Logiclet p) {
		super(tag, p);
	}
	
	public void configure(Properties p){
		super.configure(p);
		jsonPath = PropertiesConstants.getString(p, "path", jsonPath);
		value = PropertiesConstants.getString(p,"value",value,true);
		key = PropertiesConstants.getString(p,"key",key,true);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher) {
		if (StringUtils.isNotEmpty(jsonPath)){
			Object result = null;
			try {
				result = JsonPath.read(current, jsonPath);
			}catch (Exception ex){
				
			}
			if (result != null){
				if (result instanceof List<?>){
					List<Object> list = (List<Object>)result;
					for (Object o:list){
						if (o instanceof Map<?,?>){
							Map<String,Object> newCurrent = (Map<String,Object>)o;
							super.onExecute(root, newCurrent, ctx, watcher);
						}else{
							ctx.SetValue(value, o.toString());
							super.onExecute(root, current, ctx, watcher);
						}
					}
				}else{
					if (result instanceof Map<?,?>){
						Map<String,Object> newCurrent = (Map<String,Object>)result;
						Iterator<Entry<String,Object>> iter = newCurrent.entrySet().iterator();
						while (iter.hasNext()){
							Entry<String,Object> entry = iter.next();
							Object val = entry.getValue();
							if (val instanceof String || val instanceof Number){
								ctx.SetValue(key, entry.getKey());
								ctx.SetValue(value, val.toString());
								super.onExecute(root, current, ctx, watcher);
							}
						}
					}else{
						logger.error("Can not locate the path:" + jsonPath);
					}
				}
			}
		}
	}
	
}
