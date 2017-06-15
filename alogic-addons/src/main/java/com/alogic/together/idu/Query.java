package com.alogic.together.idu;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.json.JsonObject;
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
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
 * 
 * @version 1.6.9.3 [20170615 duanyy] <br>
 * - 修正在查询记录为空时的异常问题
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
	protected void onExecute(Connection conn, final XsObject root,final XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		if (current instanceof JsonObject){
			if (extend){
				List<Object> data = new ArrayList<Object>();
				String sql = processor.process(ctx, data);
				DBTools.selectAsObjects(conn, sql, new RowRenderer.Default<Object>(){
					@SuppressWarnings("unchecked")
					@Override
					public Map<String, Object> newRow(int columnCount) {
						return (Map<String, Object>)current.getContent();
					}
				}, data.toArray());
			}else{
				String tagValue = ctx.transform(tag);
				if (StringUtils.isNotEmpty(tagValue)){
					List<Object> data = new ArrayList<Object>();
					String sql = processor.process(ctx, data);
					Map<String,Object> result = DBTools.selectAsObjects(conn, sql,data.toArray());
					if (result != null){
						@SuppressWarnings("unchecked")
						Map<String,Object> content = (Map<String,Object>)current.getContent();
						content.put(tagValue, result);
					}
				}
			}
		}else{
			if (extend){
				List<Object> data = new ArrayList<Object>();
				String sql = processor.process(ctx, data);
				Map<String,Object> result = DBTools.selectAsObjects(conn, sql,data.toArray());
				if (result != null){
					Iterator<Entry<String,Object>> iter = result.entrySet().iterator();
					while (iter.hasNext()){
						Entry<String,Object> entry = iter.next();
						Object value = entry.getValue();
						if (value != null){
							current.addProperty(entry.getKey(), value.toString());
						}else{
							current.addProperty(entry.getKey(), "");
						}
					}
				}
			}else{
				String tagValue = ctx.transform(tag);
				if (StringUtils.isNotEmpty(tagValue)){
					XsObject newChild = current.getObjectChild(tagValue, true);
					List<Object> data = new ArrayList<Object>();
					String sql = processor.process(ctx, data);
					Map<String,Object> result = DBTools.selectAsObjects(conn, sql,data.toArray());
					if (result != null){
						Iterator<Entry<String,Object>> iter = result.entrySet().iterator();
						
						while (iter.hasNext()){
							Entry<String,Object> entry = iter.next();
							Object value = entry.getValue();
							if (value != null){
								newChild.addProperty(entry.getKey(), value.toString());
							}else{
								newChild.addProperty(entry.getKey(), "");
							}
						}
					}
				}				
			}
		}
	}
}
