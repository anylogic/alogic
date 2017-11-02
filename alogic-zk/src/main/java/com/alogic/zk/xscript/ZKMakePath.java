package com.alogic.zk.xscript;

import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;

import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.zk.ZooKeeperConnector;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.UPath;


/**
 * 创建指定路径
 * 
 * @author duanyy
 *
 */
public class ZKMakePath extends ZKOperation{

	protected String path;
	protected String mode = CreateMode.PERSISTENT.name();
	
	public ZKMakePath(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p) {
		super.configure(p);
		
		path = PropertiesConstants.getRaw(p, "path", "");
		mode = PropertiesConstants.getString(p, "mode", mode, true);
	}

	@Override
	protected void onExecute(ZooKeeperConnector row, XsObject root,XsObject current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		String pathValue = ctx.transform(path).trim();

		if (StringUtils.isNotEmpty(pathValue)) {
			row.makePath(new UPath(pathValue), ZooKeeperConnector.DEFAULT_ACL, getCreateMode(mode));
		}
	}
}
