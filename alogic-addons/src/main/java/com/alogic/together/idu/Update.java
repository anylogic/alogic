package com.alogic.together.idu;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alogic.together.ExecuteWatcher;
import com.alogic.together.Logiclet;
import com.alogic.together.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.dbcp.processor.Preprocessor;
import com.logicbus.dbcp.sql.DBTools;

public class Update extends DBOperation{
	protected String sqlUpdate = "";	
	protected Preprocessor processor = null;
	
	public Update(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		sqlUpdate = PropertiesConstants.getString(p, "sql.Update", sqlUpdate);
		processor = new Preprocessor(sqlUpdate);
	}

	@Override
	protected void onExecute(Connection conn, Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		List<Object> data = new ArrayList<Object>();
		String sql = processor.process(ctx, data);
		DBTools.update(conn, sql, data.toArray());
	}

}
