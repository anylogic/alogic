package com.logicbus.dbcp.cache;

import java.sql.Connection;
import java.util.Map;

import com.alogic.cache.CacheObject;
import com.alogic.load.Loader;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.ServantException;
import com.logicbus.dbcp.context.DbcpSource;
import com.logicbus.dbcp.core.ConnectionPool;
import com.logicbus.dbcp.sql.Select;

/**
 * 多主键单SQL语句实现的Loader
 * @author yyduan
 * @since 1.6.11.6
 */
public class MultiKeySingleSQL extends Loader.Abstract<CacheObject>{
	protected String sql;
	protected String dbcp;
	protected String delimeter = "%";
	
	@Override
	public void configure(Properties p) {
		sql = PropertiesConstants.getString(p,"sql","");
		dbcp = PropertiesConstants.getString(p,"dbcpId","default");
		delimeter = PropertiesConstants.getString(p,"delimeter", delimeter);
	}

	@Override
	public CacheObject load(String id, boolean cacheAllowed) {
		return loadObject(id.split(delimeter),cacheAllowed);
	}
	
	protected CacheObject loadObject(Object[] id, boolean cacheAllowed) {
		ConnectionPool pool = DbcpSource.getPool(dbcp);
		if (pool == null) {
			throw new ServantException("core.e1003",
					"Can not get a connection pool named " + dbcp);
		}
		Connection conn = pool.getConnection();
		Select select = new Select(conn);
	
		try {
			Map<String,Object> result = select.execute(sql, id).singleRow();
			if (result != null){
				CacheObject found = new CacheObject.Simple();
				found.fromJson(result);
				return found;
			}
			return null;
		} finally {
			Select.close(select);
			pool.recycle(conn);
		}
	}	
}