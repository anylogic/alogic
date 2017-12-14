package com.alogic.ac.loader.acm;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.alogic.load.Loader;
import com.alogic.ac.AccessControlModel;
import com.anysoft.util.BaseException;
import com.anysoft.util.Factory;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.dbcp.context.DbcpSource;
import com.logicbus.dbcp.core.ConnectionPool;
import com.logicbus.dbcp.sql.DBTools;


/**
 * ACM数据库装载器
 * 
 * @author yyduan
 * @since 1.6.10.6
 */
public class ACMFromMysql extends Loader.Abstract<AccessControlModel>{
	
	/**
	 * 数据库连接池id
	 */
	protected String dbcpId = "default";
	
	/**
	 * 应用域
	 */
	protected String domain = "frontend";
	
	protected String acmModule = AccessControlModel.Default.class.getName();
	
	@Override
	public void configure(Properties p) {
		super.configure(p);
		
		dbcpId = PropertiesConstants.getString(p, "dbcpId", dbcpId);
		domain = PropertiesConstants.getString(p, "domain", domain);
		acmModule = PropertiesConstants.getString(p, "acm", acmModule,true);
	}
	
	@Override
	public AccessControlModel load(String id, boolean cacheAllowed) {
		ConnectionPool pool = DbcpSource.getPool(dbcpId);
		if (pool == null){
			LOG.error("Can not find the connection pool named:" + dbcpId);
			return null;
		}
		
		Connection conn = null;
		boolean error = false;
		try {
			conn = pool.getConnection();
			if (conn != null){
				return loadACM(conn,id);
			}else{
				LOG.error("Can not get db connection from pool:" + dbcpId);
				return null;
			}		
		}catch (BaseException ex){
			LOG.error(ExceptionUtils.getStackTrace(ex));
			return null;
		}finally{
			pool.recycle(conn, error);
		}

	}

	protected AccessControlModel loadACM(Connection conn, String id) {
		AccessControlModel acm = null;
		
		Map<String,Object> result = DBTools.selectAsObjects(
				conn,
				"select app_id id,max_thread maxThread,times_per_min maxTimesPerMin,priority from acl_app where app_id = ? and domain=?",
				id,
				domain);
		
		if (result != null){
			Factory<AccessControlModel> f = new Factory<AccessControlModel>();
			
			try {
				acm = f.newInstance(acmModule);
				acm.fromJson(result);
			
				List<Map<String,Object>> aclist = DBTools.listAsObject(
						conn, 
						"select acl_id id,app_id appId,ip,service_id service,max_thread maxThread,times_per_min timesPerMin,priority from acl_app_aclist where app_id = ? and domain = ? and state = 'working'",
						id,
						domain);
				
				for (Map<String,Object> item:aclist){
					String ip = JsonTools.getString(item, "ip", "*");
					String service = JsonTools.getString(item, "service", "*");
					int maxThread = JsonTools.getInt(item,"maxThread",1);
					int timesPerMin = JsonTools.getInt(item,"timesPerMin",10);
					int priority = JsonTools.getInt(item,"priority",-1);
					
					acm.addACI(ip, service, maxThread, timesPerMin, priority);
				}
			}catch (Exception ex){
				LOG.error("Can not create acm instance:" + id);
				LOG.error(ExceptionUtils.getStackTrace(ex));
			}
		}
		
		return acm;
	}

}
