package com.logicbus.kvalue.xscript.hash;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.logicbus.kvalue.xscript.KVRowOperation;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.kvalue.core.HashRow;
import com.logicbus.kvalue.core.KeyValueRow;

public class KVHMGet extends KVRowOperation {
    protected String key = "";
    protected String delimeter = ";";
    protected String tag = "data";
    /**
     * 返回结果的类型。list返回的是List<String>;map返回的是Map<String,Object>.default:list.
     */
    protected String resultType = "list";
    
    /**
     * 当返回为map的时候，可以extend到当前节点
     */
    protected boolean extend = false;

    public KVHMGet(String tag, Logiclet p) {
        super(tag, p);
    }

    @Override
    public void configure(Properties p) {
        super.configure(p);

        tag = PropertiesConstants.getRaw(p, "tag", tag);
        key = PropertiesConstants.getRaw(p, "key", key);
        delimeter = PropertiesConstants.getString(p, "delimiter", delimeter, true);
        resultType = PropertiesConstants.getRaw(p, "resultType", resultType);
        extend = PropertiesConstants.getBoolean(p,"extend",extend,true);
    }

    @Override
    protected void onExecute(KeyValueRow row, Map<String, Object> root, Map<String, Object> current,
            LogicletContext ctx, ExecuteWatcher watcher) {
        String tagValue = ctx.transform(tag);

        if (row instanceof HashRow) {
            if ("map".equalsIgnoreCase(resultType)) {                
                HashRow r = (HashRow) row;
                String keyList = ctx.transform(key);
                List<String> l = r.mget(keyList.split(delimeter));

                String[] keys = keyList.split(delimeter);
                
                if (extend){
	                for (int i = 0; i < keys.length; i++) {
	                    if(null != l.get(i)){
	                        ctx.SetValue(keys[i], l.get(i));
	                    }else{
	                        ctx.SetValue(keys[i], "null");
	                    }                  
	                }
                }else{
	                Map<String, Object> resultMap = new HashMap<String, Object>();
	                for (int i = 0; i < keys.length; i++) {
	                    resultMap.put(keys[i], l.get(i));
	                }
	                current.put(tagValue, resultMap);
                }
            } else {
                HashRow r = (HashRow) row;
                String keyList = ctx.transform(key);
                if (StringUtils.isNotEmpty(tagValue)){
                	current.put(tagValue, r.mget(keyList.split(delimeter)));
                }
            }
        }
    }

}