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
 * 删除指定的路径
 * 
 * @author duanyy
 *
 */
public class ZKDelete extends ZKOperation {

	protected String path;
	protected boolean ignoreException;

	public ZKDelete(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);

		path = PropertiesConstants.getRaw(p, "path", "");
		ignoreException = PropertiesConstants.getBoolean(p, "ignoreException", true);
	}

	@Override
	protected void onExecute(ZooKeeperConnector row, XsObject root,XsObject current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		String pathValue = ctx.transform(path).trim();
		
		if (StringUtils.isNotEmpty(pathValue)) {
			row.deletePath(new UPath(pathValue), ignoreException);
		}
	}

}
