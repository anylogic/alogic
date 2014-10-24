package com.logicbus.backend.acm;

import java.util.Map;

import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.remote.client.Client;
import com.logicbus.remote.client.ClientException;
import com.logicbus.remote.client.HttpClient;
import com.logicbus.remote.client.JsonBuffer;
import com.logicbus.remote.client.Parameter;

/**
 * TokenCenter连接器
 * 
 * @author duanyy
 * @since 1.2.3
 */
public class TokenCenterConnector {
	public TokenCenterConnector(Properties props){
		agent = props.GetValue("server.app", "Default");
		tcURL = PropertiesConstants.getString(props, "acm.tcURL", "");
		callback = PropertiesConstants.getString(props,"acm.callback","http://${server.host}:${server.port}${webcontext.path}/services/core/TokenDestroy");
		client = new HttpClient(props);
	}
	
	/**
	 * 本服务器的App
	 */
	protected String agent;
	
	/**
	 * TokenCenter的服务地址
	 */
	protected String tcURL;	
	
	/**
	 * 回调服务地址
	 */
	protected String callback;
	protected Client client = null;
	
	//连续错误次数
	private int errorTimes = 0;
	//上次尝试时间
	private long lastTryTime = 0;
	
	public boolean tokenIsValid(String app,String t){
		//免失败模式开启
		//如果连续错误次数超过3次，则连续10分钟不再尝试，直接返回为true
		if (errorTimes > 3 && System.currentTimeMillis() - lastTryTime < 10 * 60 * 1000) return true;
		if (tcURL.length() <= 0)
			return true;
		
		Parameter para = client.createParameter()
		.param("t",String.valueOf(System.currentTimeMillis()))
		.param("a",app)
		.param("agent",agent)
		.param("id",t)
		.param("callback", callback)
		.param("json", "true");
		
		JsonBuffer result = new JsonBuffer();
		
		try {
			client.invoke(tcURL, para, result);		
			Map<String, Object> map = result.getRoot();			
			Object retCode = map.get("code");			
			boolean ret = retCode != null && retCode.equals("core.ok");
			errorTimes = 0;
			return ret;
		} catch (ClientException e) {
			errorTimes ++;			
		}finally{		
			lastTryTime = System.currentTimeMillis();
		}
		return true;
	}
}
