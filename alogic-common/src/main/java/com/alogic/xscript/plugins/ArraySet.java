package com.alogic.xscript.plugins;

import java.util.HashSet;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.json.JsonObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

import java.util.Set;

/**
 * 创建Set集合(用于对数据去重)
 * 
 * @author yyduan
 * @since 1.6.11.34
 * 
 * @version 1.6.11.43 [20180708 duanyy]  <br>
 * - 增加array-set-list插件 <br>
 * 
 * @version 1.6.11.60 [20180912 duanyy] <br>
 * - 增加array-set-save指令,用来将数组保存为字符串，并存储到指定变量 <br>
 */
public class ArraySet extends Segment {
	protected String tag = "data";
	protected String cid = "$set";
	protected boolean output = true;
	
	public ArraySet(String tag, Logiclet p) {
		super(tag, p);
		
		registerModule("array-set-add",ArraySetAdd.class);
		registerModule("array-set-del",ArraySetDel.class);
		registerModule("array-set-exist",ArraySetExist.class);
		registerModule("array-set-list",ArraySetList.class);
		registerModule("array-set-save",ArraySetSave.class);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		cid = PropertiesConstants.getString(p,"cid",cid,true);
		tag = PropertiesConstants.getRaw(p,"tag", tag);
		output = PropertiesConstants.getBoolean(p,"output",true);
	}
	
	@SuppressWarnings("unchecked")
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		String tagValue = ctx.transform(tag);
		if (StringUtils.isNotEmpty(tagValue)){
			Set<String> set = new HashSet<String>();
			try {
				ctx.setObject(cid, set);
				super.onExecute(root, current, ctx, watcher);
				if (output && current instanceof JsonObject){
					Map<String,Object> content = (Map<String,Object>)current.getContent();
					if (content != null){
						content.put(tagValue, set.toArray(new String[0]));
					}
				}
			}finally{
				ctx.removeObject(cid);
			}
		}
	}		
}