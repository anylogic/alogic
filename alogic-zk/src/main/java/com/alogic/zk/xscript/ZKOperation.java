package com.alogic.zk.xscript;

import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.zk.ZooKeeperConnector;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * ZK操作基类
 * 
 * @author duanyy
 *
 */
public abstract class ZKOperation extends AbstractLogiclet implements Watcher{

	protected String pid = "$zk-conn";
	
	/**
	 * 返回结果的id
	 */
	protected String id;
	
	public ZKOperation(String tag, Logiclet p) {
		super(tag, p);
	}
	
	public void configure(Properties p){
		super.configure(p);
		pid = PropertiesConstants.getString(p,"pid", pid,true);
		id = PropertiesConstants.getString(p,"id", "$" + getXmlTag(),true);
	}

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		ZooKeeperConnector conn = ctx.getObject(pid);
		if (conn == null){
			throw new BaseException("core.e1001","It must be in a zk-conn context,check your script.");
		}
		
		if (StringUtils.isNotEmpty(id)){
			onExecute(conn,root,current,ctx,watcher);
		}
	}

	protected abstract void onExecute(ZooKeeperConnector row, XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher);
		
	@Override
	public void process(WatchedEvent event) {
		
	}		
	
	protected CreateMode getCreateMode(String name){
		if (name.equalsIgnoreCase("PERSISTENT")){
			return CreateMode.PERSISTENT;
		}
		if (name.equalsIgnoreCase("PERSISTENT_SEQUENTIAL")){
			return CreateMode.PERSISTENT_SEQUENTIAL;
		}
		if (name.equalsIgnoreCase("EPHEMERAL")){
			return CreateMode.EPHEMERAL;
		}
		if (name.equalsIgnoreCase("EPHEMERAL_SEQUENTIAL")){
			return CreateMode.EPHEMERAL_SEQUENTIAL;
		}		
		return CreateMode.PERSISTENT;
	}
}