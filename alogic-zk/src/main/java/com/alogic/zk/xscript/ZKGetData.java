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
 * 获取指定路径的数据
 * 
 * @author duanyy
 *
 */
public class ZKGetData extends ZKOperation {

	protected String path = "";
	protected boolean ignoreException;

	public ZKGetData(String tag, Logiclet p) {
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
			ctx.SetValue(id, row.loadData(new UPath(pathValue), this, ignoreException));
		}
	}
}
