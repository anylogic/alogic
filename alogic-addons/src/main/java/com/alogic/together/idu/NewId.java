package com.alogic.together.idu;

import java.sql.Connection;
import com.alogic.sequence.client.SeqTool;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 新申请一个全局id
 * 
 * @author duanyy
 * 
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
 * @deprecated
 */
public class NewId extends DBOperation{
	protected String seqId = "default";
	protected String id = "id";
	
	public NewId(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		seqId = PropertiesConstants.getString(p,"seqId",seqId);
		id = PropertiesConstants.getString(p, "id", id);
	}
	
	@Override
	protected void onExecute(Connection conn, XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		long newId = SeqTool.nextLong(seqId);
		ctx.SetValue(id, String.valueOf(newId));
	}

}
