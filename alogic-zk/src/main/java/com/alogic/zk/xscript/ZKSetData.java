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
 * 设置指定路径的数据
 * 
 * @author duanyy
 *
 */
public class ZKSetData extends ZKOperation{
	
	protected String path;
	protected String mode = CreateMode.PERSISTENT.name();
	protected String data;
	protected boolean ignoreException;

	public ZKSetData(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p) {
		super.configure(p);
		path = PropertiesConstants.getRaw(p, "path", "");
		mode = PropertiesConstants.getString(p, "mode", mode, true);
		data = PropertiesConstants.getRaw(p, "data", "");
		ignoreException = PropertiesConstants.getBoolean(p, "ignoreException", true);
	}

	@Override
	protected void onExecute(ZooKeeperConnector row, XsObject root,XsObject current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		String pathValue = ctx.transform(path).trim();
		String dataValue = ctx.transform(data);
		
		if (StringUtils.isNotEmpty(pathValue)){
			row.createOrUpdate(new UPath(pathValue), dataValue, ZooKeeperConnector.DEFAULT_ACL, getCreateMode(mode), null, ignoreException);
		}
	}

}
