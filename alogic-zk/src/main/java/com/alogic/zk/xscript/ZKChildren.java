package com.alogic.zk.xscript;

import java.util.List;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.plugins.Segment;
import com.alogic.zk.ZooKeeperConnector;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.UPath;

public class ZKChildren extends Segment implements Watcher {
	private String pid = "$zk-conn";
	protected String id = "$value";
	protected String total = "$total";
	protected String all = "$all";
	protected String path;
	protected boolean ignoreException;
	protected String offset = "0";
	protected String limit = "15";

	public ZKChildren(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void process(WatchedEvent event) {
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		pid = PropertiesConstants.getString(p,"pid", pid,true);
		id = PropertiesConstants.getString(p,"id",id,true);
		total = PropertiesConstants.getString(p,"total",total,true);
		all = PropertiesConstants.getString(p,"all",all,true);
		
		path = PropertiesConstants.getRaw(p, "path", "");
		ignoreException = PropertiesConstants.getBoolean(p, "ignoreException", true);

		offset = PropertiesConstants.getRaw(p, "offset", offset);
		limit = PropertiesConstants.getRaw(p, "limit", limit);
	}

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		ZooKeeperConnector conn = ctx.getObject(pid);
		if (conn == null){
			throw new BaseException("core.no_zkconn","It must be in a zk-conn context,check your script.");
		}				
		
		int offsetValue = getLong(ctx.transform(offset),0);
		int limitValue = getLong(ctx.transform(limit),15);
		String pathValue = ctx.transform(path);
		
		List<String> childrenList = conn.getChildren(new UPath(pathValue), this, ignoreException);

		int cnt = 0;
		for (int i = (offsetValue < 0?0:offsetValue) ; i < (offsetValue + limitValue) && i < childrenList.size() ; i ++){
			ctx.SetValue(id, childrenList.get(i));
			super.onExecute(root, current, ctx, watcher);
			cnt ++;
		}
		
		ctx.SetValue(all, String.valueOf(childrenList.size()));
		ctx.SetValue(total, String.valueOf(cnt));
	}
	
	protected int getLong(String value,int dftValue){
		try{
			return Integer.parseInt(value);
		}catch (NumberFormatException ex){
			return dftValue;
		}
	}

}
