package com.alogic.zk.xscript;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.plugins.Segment;
import com.alogic.zk.ZooKeeperConnector;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 创建一个zk连接
 * 
 * @author duanyy
 *
 */
public class ZKConn extends Segment{
	protected String cid = "$zk-conn";
	protected String connectString = "${zookeeper.connectString}";
	
	public ZKConn(String tag, Logiclet p) {
		super(tag, p);	
		registerModule("zk-children",ZKChildren.class);
		registerModule("zk-delete",ZKDelete.class);
		registerModule("zk-exist",ZKExist.class);
		registerModule("zk-get",ZKGetData.class);
		registerModule("zk-mkpath",ZKMakePath.class);
		registerModule("zk-set",ZKSetData.class);
		registerModule("zk-getAsJson",ZKGetAsJson.class);
		registerModule("zk-setAsJson",ZKSetAsJson.class);
		registerModule("zk-escapePath",ZKEscapePath.class);
		registerModule("zk-unescapePath",ZKUnescapePath.class);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		cid = PropertiesConstants.getString(p,"cid",cid,true);
		connectString = PropertiesConstants.getString(p,"connectString",connectString,true);
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		ZooKeeperConnector conn = new ZooKeeperConnector(ctx,connectString);
		try {
			ctx.setObject(cid, conn);
			super.onExecute(root, current, ctx, watcher);
		}finally{
			conn.disconnect();
			ctx.removeObject(cid);
		}
	}

}
