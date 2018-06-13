package com.logicbus.dbcp.xscript;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.dbcp.processor.Preprocessor;
import com.logicbus.dbcp.sql.DBTools;

/**
 * Delete
 * @author yyduan
 *
 * @since 1.6.10.5
 * @version 1.6.11.27 [20180417 duanyy] <br>
 * - 增加debug参数 <br>
 * 
 * @version 1.6.11.36 [20180613 duanyy] <br>
 * - 支持对sql语句进行transform<br>
 */
public class Delete extends DBOperation{
	protected String sqlDelete = "";	
	protected Preprocessor processor = null;
	protected boolean transform = false;
	
	public Delete(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		sqlDelete = PropertiesConstants.getRaw(p, "sql", sqlDelete);
		transform = PropertiesConstants.getBoolean(p,"transform",transform,true);
		processor = new Preprocessor(transform,sqlDelete);
	}

	@Override
	protected void onExecute(Connection conn,XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		List<Object> data = new ArrayList<Object>();
		String sql = processor.process(ctx, data);
		
		if (debug){
			log("sql=" + sql,"debug");
			log("binded=" + data.toString(),"debug");
		}
		
		DBTools.delete(conn, sql, data.toArray());
	}

}
