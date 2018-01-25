package com.alogic.together.idu;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.alogic.cache.CacheObject;
import com.alogic.load.Store;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.json.JsonObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.ServantException;

/**
 * 查询当前缓存中指定id的对象，并输出到当前文档
 * 
 * @author duanyy
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
 * 
 * @version 1.6.11.13 [20180125 duanyy] <br>
 * - 切换到新的缓存实现 <br>
 * @deprecated
 */
public class CacheQuery extends CacheOperation {
	protected String tag = "data";
	protected String id = "id";
	protected boolean extend = false;
	
	public CacheQuery(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		tag = PropertiesConstants.getRaw(p, "tag", tag);
		id = PropertiesConstants.getRaw(p, "id", "");		
		extend = PropertiesConstants.getBoolean(p,"extend",extend);
	}	
	
	@Override
	protected void onExecute(Store<CacheObject> cache, XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		String idValue = ctx.transform(id);
		
		if (current instanceof JsonObject){
			if (StringUtils.isNotEmpty(idValue)){
				@SuppressWarnings("unchecked")
				Map<String,Object> content = (Map<String,Object>)current.getContent();
				CacheObject found = cache.load(idValue, true);
				if (found == null){
					throw new ServantException("clnt.e2007","Can not find object,id=" + idValue);
				}
			
				if (extend){
					//扩展当前节点
					found.toJson(content);
				}else{
					Map<String,Object> data = new HashMap<String,Object>();		
					found.toJson(data);		
					String tagValue = ctx.transform(tag);
					content.put(tagValue, data);
				}
			}
		}else{
			if (StringUtils.isNotEmpty(idValue)){
				CacheObject found = cache.load(idValue, true);
				if (found == null){
					throw new ServantException("core.data_not_found","Can not find object,id=" + idValue);
				}

				Map<String,Object> result = new HashMap<String,Object>();		
				found.toJson(result);	
				
				if (extend){
					Iterator<Entry<String,Object>> iter = result.entrySet().iterator();
					while (iter.hasNext()){
						Entry<String,Object> entry = iter.next();
						Object value = entry.getValue();
						if (value != null){
							current.addProperty(entry.getKey(), value.toString());
						}else{
							current.addProperty(entry.getKey(), "");
						}
					}
				}else{
					String tagValue = ctx.transform(tag);
					if (StringUtils.isNotEmpty(tagValue)){
						XsObject newChild = current.getObjectChild(tagValue, true);
						Iterator<Entry<String,Object>> iter = result.entrySet().iterator();
						
						while (iter.hasNext()){
							Entry<String,Object> entry = iter.next();
							Object value = entry.getValue();
							if (value != null){
								newChild.addProperty(entry.getKey(), value.toString());
							}else{
								newChild.addProperty(entry.getKey(), "");
							}
						}
					}				
				}				
			}
		}
	}

}
