package com.logicbus.dbcp.cache;


import java.sql.Connection;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alogic.cache.CacheObject;
import com.alogic.load.Loader;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.dbcp.context.DbcpSource;
import com.logicbus.dbcp.core.ConnectionPool;
import com.logicbus.dbcp.sql.DBTools;

/**
 * 通过SQL语句装入Hash类型的对象数据
 * 
 * @author yyduan
 * @since 1.6.11.6
 * 
 * @version 1.6.11.8 [20180109] duanyy <br>
 * - 优化缓存相关的xscript插件 <br>
 */
public class HashSQLLoader extends Loader.Abstract<CacheObject>{
	protected String sql;
	protected String dbcp;
	protected String delimeter = "";
	
	@Override
	public void configure(Properties p) {
		super.configure(p);
		sql = PropertiesConstants.getString(p,"sql","");
		dbcp = PropertiesConstants.getString(p,"dbcpId","default");
		delimeter = PropertiesConstants.getString(p,"delimeter", delimeter);
	}

	@Override
	public CacheObject load(String id, boolean cacheAllowed) {
		if (StringUtils.isEmpty(delimeter)){
			return loadObject(id,new String[]{id});
		}else{
			return loadObject(id,id.split(delimeter));
		}
	}
	
	protected CacheObject loadObject(String id,Object[] ids) {
		ConnectionPool pool = DbcpSource.getPool(dbcp);
		if (pool == null) {
			throw new BaseException("core.e1003",
					"Can not get a connection pool named " + dbcp);
		}
		Connection conn = pool.getConnection();
		try {
			Map<String,Object> result = DBTools.selectAsObjects(conn,sql, ids);
			if (result != null){
				CacheObject found = new CacheObject.Simple(id);
				found.fromJson(result);
				return found;
			}
			return null;
		} finally {
			pool.recycle(conn);
		}
	}	
}
