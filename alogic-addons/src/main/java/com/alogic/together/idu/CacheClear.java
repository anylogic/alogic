package com.alogic.together.idu;


import org.apache.commons.lang3.StringUtils;
import com.alogic.cache.core.CacheStore;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * CacheClear
 * @author yyduan
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
 * 
 * @deprecated
 */
public class CacheClear extends CacheOperation{
	protected String id = "id";
	
	public CacheClear(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		id = PropertiesConstants.getRaw(p, "id", "");
	}

	@Override
	protected void onExecute(CacheStore cache, XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		String idValue = ctx.transform(id);
		if (StringUtils.isNotEmpty(idValue)){
			cache.expire(idValue);
		}
	}

}
