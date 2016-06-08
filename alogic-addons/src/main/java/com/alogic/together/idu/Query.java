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

/**
 * 查询单条记录
 * 
 * @author duanyy
 *
 */
public class Query extends DBOperation{
	protected String tag = "data";
	protected String sqlQuery = "";	
	protected Preprocessor processor = null;
	
	public Query(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		tag = PropertiesConstants.getString(p, "tag", tag);
		sqlQuery = PropertiesConstants.getString(p, "sql.Query", sqlQuery);
		processor = new Preprocessor(sqlQuery);
	}

	@Override
	protected void onExecute(Connection conn, Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		List<Object> data = new ArrayList<Object>();
		String sql = processor.process(ctx, data);
		Map<String,Object> result = DBTools.selectAsObjects(conn, sql,data.toArray());
		current.put(tag, result);
	}
}
