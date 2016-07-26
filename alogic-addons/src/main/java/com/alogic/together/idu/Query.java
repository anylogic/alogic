package com.alogic.together.idu;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.dbcp.processor.Preprocessor;
import com.logicbus.dbcp.sql.DBTools;
import com.logicbus.dbcp.sql.RowRenderer;

/**
 * 查询单条记录
 * 
 * @author duanyy
 *
 */
public class Query extends DBOperation{
	protected String tag = "data";
	protected String sqlQuery = "";
	protected boolean extend = false;
	protected Preprocessor processor = null;
	
	public Query(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		tag = PropertiesConstants.getRaw(p, "tag", tag);
		sqlQuery = PropertiesConstants.getString(p, "sql.Query", sqlQuery);
		processor = new Preprocessor(sqlQuery);
		extend = PropertiesConstants.getBoolean(p, "extend", extend);
	}

	@Override
	protected void onExecute(Connection conn, Map<String, Object> root,
			final Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		if (extend){
			List<Object> data = new ArrayList<Object>();
			String sql = processor.process(ctx, data);
			DBTools.selectAsObjects(conn, sql, new RowRenderer.Default<Object>(){
				@Override
				public Map<String, Object> newRow(int columnCount) {
					return current;
				}
			}, data.toArray());
		}else{
			String tagValue = ctx.transform(tag);
			if (StringUtils.isNotEmpty(tagValue)){
				List<Object> data = new ArrayList<Object>();
				String sql = processor.process(ctx, data);
				Map<String,Object> result = DBTools.selectAsObjects(conn, sql,data.toArray());
				current.put(tagValue, result);
			}
		}
	}
}
