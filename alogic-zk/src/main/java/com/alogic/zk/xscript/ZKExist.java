package com.alogic.zk.xscript;


import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.zk.ZooKeeperConnector;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.UPath;

/**
 * 指定的路径是否存在
 * 
 * @author duanyy
 *
 */
public class ZKExist extends ZKOperation {

	protected String path;

	public ZKExist(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		path = PropertiesConstants.getRaw(p, "path", "");
	}

	@Override
	protected void onExecute(ZooKeeperConnector row, XsObject root,XsObject current,
			LogicletContext ctx, ExecuteWatcher watcher) {		
		String pathValue = ctx.transform(path).trim();
		
		if (StringUtils.isNotEmpty(pathValue)) {
			ctx.SetValue(id, Boolean.toString(row.existPath(new UPath(pathValue), this, false)));
		}
	}

}
