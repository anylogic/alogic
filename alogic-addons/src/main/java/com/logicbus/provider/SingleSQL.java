package com.logicbus.provider;


import java.sql.Connection;
import java.util.Map;
import org.w3c.dom.Element;
import com.alogic.cache.core.MultiFieldObject;
import com.alogic.cache.core.MultiFieldObjectProvider;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.ServantException;
import com.logicbus.dbcp.context.DbcpSource;
import com.logicbus.dbcp.core.ConnectionPool;
import com.logicbus.dbcp.sql.Select;

/**
 * 支持单SQL语句的Provider
 * 
 * @author duanyy
 * @since 1.6.3.3
 * @version 1.6.4.5 [20150910 duanyy] <br>
 * - 优化Report输出 <br>
 */
public class SingleSQL extends MultiFieldObjectProvider.Abstract {

	@Override
	protected MultiFieldObject loadObject(String id, boolean cacheAllowed) {
		ConnectionPool pool = DbcpSource.getPool(dbcp);
		if (pool == null) {
			throw new ServantException("core.no_db_pool",
					"Can not get a connection pool named " + dbcp);
		}
		Connection conn = pool.getConnection();
		Select select = new Select(conn);
		
		try {
			Map<String,Object> result = select.execute(sql, id).singleRow();
			if (result != null){
				MultiFieldObject found = new MultiFieldObject.Default();
				found.fromJson(result);
				
				return found;
			}
			return null;
		} finally {
			Select.close(select);
			pool.recycle(conn);
		}
	}

	@Override
	protected void onConfigure(Element _e, Properties p) {
		sql = PropertiesConstants.getString(p,"sql","");
		dbcp = PropertiesConstants.getString(p,"dbcp","default");
	}
	protected String sql;
	protected String dbcp;
	
	public void report(Element xml) {
		if (xml != null){
			super.report(xml);
			xml.setAttribute("sql", sql);
			xml.setAttribute("dbcp", dbcp);
		}
	}

	public void report(Map<String, Object> json) {
		if (json != null){
			super.report(json);
			json.put("sql", sql);
			json.put("dbcp", dbcp);
		}
	}
}
