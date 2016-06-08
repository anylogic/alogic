package com.alogic.together.idu;

import java.sql.Connection;
import java.util.Map;

import com.alogic.sequence.client.SeqTool;
import com.alogic.together.ExecuteWatcher;
import com.alogic.together.Logiclet;
import com.alogic.together.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 新申请一个全局id
 * 
 * @author duanyy
 *
 */
public class NewId extends DBOperation{
	protected String seqId = "default";
	protected String tag = "id";
	
	public NewId(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		seqId = PropertiesConstants.getString(p,"seqId",seqId);
		tag = PropertiesConstants.getString(p, "tag", tag);
	}
	
	@Override
	protected void onExecute(Connection conn, Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		long newId = SeqTool.nextLong(seqId);
		current.put(tag, newId);
	}

}
