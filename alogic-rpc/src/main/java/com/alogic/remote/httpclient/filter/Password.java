package com.alogic.remote.httpclient.filter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.alogic.remote.httpclient.HttpCientFilter;
import com.alogic.remote.httpclient.HttpClientRequest;
import com.alogic.remote.httpclient.HttpClientResponse;
import com.alogic.sda.SDAFactory;
import com.alogic.sda.SecretDataArea;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.code.Coder;
import com.anysoft.util.code.CoderFactory;

/**
 * 基于密码的服务验证
 * 
 * @author yyduan
 * @since 1.6.10.6
 * 
 * @version 1.6.10.8 [20171122 duanyy] <br>
 * - 支持实时从SDA获取信息 <br>
 * 
 * @version 1.6.11.39 [duanyy 20180628] <br>
 * - 增加x-alogic-ac参数，以便服务端识别acGroup <br>
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
	
	protected String sdaId = "";
	
	protected String timestampId = "x-alogic-now";
	protected String pwdId = "x-alogic-pwd";
	protected String keyId = "x-alogic-app";
	protected String acGroupKeyId = "x-alogic-ac";
	protected String acGroup = "app";
	
	protected Coder des3Coder = null;
	
	@Override
	public void onRequest(HttpClientRequest request) {
		SecretDataArea sda = null;
		if (StringUtils.isNotEmpty(sdaId)){
			//从sda中装入信息
			try {
				sda = SDAFactory.getDefault().load(sdaId, true);
			}catch (Exception ex){
				LOG.error("Can not find sda : " + sdaId);
				LOG.error(ExceptionUtils.getStackTrace(ex));
			}
		}
		
		if (sda != null){
			String theKey = sda.getField("http.key.id", key);
			String thePasswd = sda.getField("http.key.pwd", password);
			onRequest(request,theKey,thePasswd);
		}else{
			onRequest(request,key,password);
		}
	}
	
	protected void onRequest(HttpClientRequest request,String theKey,String thePasswd){
		String timestamp = String.valueOf(System.currentTimeMillis());
		String encryptPwd = des3Coder.encode(thePasswd, theKey + "$" + timestamp);
		request.setHeader(timestampId, timestamp);
		request.setHeader(keyId, theKey);		
		request.setHeader(pwdId, encryptPwd);	
		request.setHeader(acGroupKeyId, acGroup);
	}

	@Override
	public void onResponse(HttpClientResponse response) {

	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		
		key = PropertiesConstants.getString(p,"key","");
		password = PropertiesConstants.getString(p,"keyContent","");
		
		timestampId = PropertiesConstants.getString(p,"timestampId", timestampId);		
		pwdId = PropertiesConstants.getString(p,"pwdId",pwdId);
		keyId = PropertiesConstants.getString(p,"keyId",keyId);
		sdaId = PropertiesConstants.getString(p,"sda",sdaId);
		acGroupKeyId = PropertiesConstants.getString(p,"acGroupKeyId", acGroupKeyId);
		acGroup = PropertiesConstants.getString(p,"acGroupId", acGroup);		
		des3Coder = CoderFactory.newCoder("DES3");
	}

}
