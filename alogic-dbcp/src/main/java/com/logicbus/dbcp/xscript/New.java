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
 * 通过ID来新增记录
 * 
 * @author duanyy
 * 
 * @since 1.6.10.5
 * 
 * @version 1.6.11.27 [20180417 duanyy] <br>
 * - 增加debug参数 <br>
 */
public class New extends DBOperation{
	protected String sqlInsert = "";	
	protected Preprocessor processor = null;
	
	public New(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		sqlInsert = PropertiesConstants.getString(p, "sql", sqlInsert);
		processor = new Preprocessor(sqlInsert);
	}

	@Override
	protected void onExecute(Connection conn, XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		List<Object> data = new ArrayList<Object>();
		String sql = processor.process(ctx, data);
		
		if (debug){
			log("sql=" + sql,"debug");
			log("binded=" + data.toString(),"debug");
		}
		
		
		DBTools.insert(conn, sql, data.toArray());
	}

}
