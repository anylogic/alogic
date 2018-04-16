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
 */
public class Delete extends DBOperation{
	protected String sqlDelete = "";	
	protected Preprocessor processor = null;
	
	public Delete(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		sqlDelete = PropertiesConstants.getString(p, "sql", sqlDelete);
		processor = new Preprocessor(sqlDelete);
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
