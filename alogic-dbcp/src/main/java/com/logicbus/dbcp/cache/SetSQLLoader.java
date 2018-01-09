package com.logicbus.dbcp.cache;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alogic.cache.CacheObject;
import com.alogic.load.Loader;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.dbcp.context.DbcpSource;
import com.logicbus.dbcp.core.ConnectionPool;
import com.logicbus.dbcp.sql.DBTools;
import com.logicbus.dbcp.sql.ObjectMappingAdapter;
import com.logicbus.dbcp.sql.oma.Single;


/**
 * 通过SQL语句装入Set类型的对象数据
 * 
 * @author yyduan
 * @since 1.6.11.6
 * 
 * @version 1.6.11.8 [20180109] duanyy <br>
 * - 优化缓存相关的xscript插件 <br>
 */
public class SetSQLLoader extends Loader.Abstract<CacheObject>{
	protected String sql;
	protected String dbcp;
	protected String delimeter = "";
	protected ObjectMappingAdapter<String> single = new Single();
	
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
	
	public CacheObject loadObject(String id,Object[] ids) {
		ConnectionPool pool = DbcpSource.getPool(dbcp);
		if (pool == null) {
			throw new BaseException("core.e1003",
					"Can not get a connection pool named " + dbcp);
		}
		Connection conn = pool.getConnection();
		try {
			List<String> result = new ArrayList<String>();
			DBTools.list(conn, result,single,sql,ids);		
			if (!result.isEmpty()){
				CacheObject found = new CacheObject.Simple(id);
				
				for (String item:result){
					found.sAdd(CacheObject.DEFAULT_GROUP, item);
				}
				
				if (found.isValid()){
					return found;
				}
			}
			return null;
		} finally {
			pool.recycle(conn);
		}
	}
}
