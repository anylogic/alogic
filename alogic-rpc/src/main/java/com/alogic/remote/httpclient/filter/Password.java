package com.alogic.remote.httpclient.filter;

import com.alogic.remote.httpclient.HttpCientFilter;
import com.alogic.remote.httpclient.HttpClientRequest;
import com.alogic.remote.httpclient.HttpClientResponse;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.code.Coder;
import com.anysoft.util.code.CoderFactory;

/**
 * 基于密码的服务验证
 * 
 * @author yyduan
 * @since 1.6.10.6
 */
public class Password extends HttpCientFilter.Abstract{
	/**
	 * 应用id
	 */
	protected String key; 
	
	/**
	 * 服务调用密码
	 */
	protected String password;
	
	protected String timestampId = "x-alogic-now";
	protected String pwdId = "x-alogic-pwd";
	protected String keyId = "x-alogic-app";
	
	protected Coder des3Coder = null;
	
	@Override
	public void onRequest(HttpClientRequest request) {
		String timestamp = String.valueOf(System.currentTimeMillis());
		String encryptPwd = des3Coder.encode(password, key + "$" + timestamp);
		request.setHeader(timestampId, timestamp);
		request.setHeader(keyId, key);		
		request.setHeader(pwdId, encryptPwd);
	}

	@Override
	public void onResponse(HttpClientResponse response) {

	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		
		key = PropertiesConstants.getString(p,"key","");
		password = PropertiesConstants.getString(p,"pwd","");
		
		timestampId = PropertiesConstants.getString(p,"timestampId", timestampId);		
		pwdId = PropertiesConstants.getString(p,"pwdId",pwdId);
		keyId = PropertiesConstants.getString(p,"keyId",keyId);
		
		des3Coder = CoderFactory.newCoder("DES3");
	}

}
