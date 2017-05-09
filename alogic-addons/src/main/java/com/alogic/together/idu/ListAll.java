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
import com.alogic.xscript.doc.XsArray;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.json.JsonObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.dbcp.processor.Preprocessor;
import com.logicbus.dbcp.sql.DBTools;

/**
 * 执行查询语句
 * 
 * @author duanyy
 * 
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
 * 
 */
public class ListAll extends DBOperation{
	protected String tag = "data";
	protected String sqlQuery = "";	
	protected Preprocessor processor = null;
	
	public ListAll(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		tag = PropertiesConstants.getRaw(p, "tag", tag);
		sqlQuery = PropertiesConstants.getString(p, "sql.Query", sqlQuery);
		processor = new Preprocessor(sqlQuery);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onExecute(Connection conn, XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		String tagValue = ctx.transform(tag);
		if (StringUtils.isNotEmpty(tagValue)){
			List<Object> data = new ArrayList<Object>();
			String sql = processor.process(ctx, data);
			List<Map<String,Object>> result = DBTools.listAsObject(conn, sql,data.toArray());
			
			if (current instanceof JsonObject){
				Map<String,Object> content = (Map<String,Object>)current.getContent();
				content.put(tagValue, result);
			}else{
				XsArray array = current.getArrayChild(tagValue, true);
				
				for (Map<String,Object> item:result){
					XsObject arrayItem = array.newObject();
					
					Iterator<Entry<String,Object>> iter = item.entrySet().iterator();
					
					while (iter.hasNext()){
						Entry<String,Object> entry = iter.next();
						Object value = entry.getValue();
						if (value != null){
							arrayItem.addProperty(entry.getKey(), value.toString());
						}else{
							arrayItem.addProperty(entry.getKey(), "");
						}
					}
					
					array.add(arrayItem);
				}
			}			
		}
	}
}
