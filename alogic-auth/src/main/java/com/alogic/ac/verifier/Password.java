package com.alogic.ac.verifier;

import org.apache.commons.lang3.StringUtils;

import com.alogic.ac.AccessAppKey;
import com.alogic.ac.AccessVerifier;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.code.Coder;
import com.anysoft.util.code.CoderFactory;
import com.logicbus.backend.Context;

/**
 * 密码验证
 * 
 * @author yyduan
 * @since 1.6.10.6
 * 
 * @version 1.6.10.9 [20171124 duanyy] <br>
 * - 请求生存周期ttl可以通过参数ttl进行配置 <br>
 */
public class Password extends AccessVerifier.Abstract{
	protected Coder des3Coder = null;
	protected Coder md5Coder = null;
	protected String timestampId = "x-alogic-now";
	protected String pwdId = "x-alogic-pwd";
	protected long ttl = 10*1000L;
	
	@Override
	public void configure(Properties p) {		
		super.configure(p);
		des3Coder = CoderFactory.newCoder("DES3");
		md5Coder = CoderFactory.newCoder("MD5");
		timestampId = PropertiesConstants.getString(p,"timestampId", timestampId);		
		pwdId = PropertiesConstants.getString(p,"pwdId",pwdId);
		ttl = PropertiesConstants.getLong(p, "ttl", ttl);
	}

	@Override
	public boolean verify(AccessAppKey key, Context ctx) {		
		checkTimestamp(key,ctx);
		checkPassword(key,ctx);
		return true;
	}
	
	protected void checkPassword(AccessAppKey key, Context ctx){
		String now = ctx.getRequestHeader(timestampId);
		String id = key.getId();	
		String pwd = ctx.getRequestHeader(pwdId);
		if (StringUtils.isEmpty(pwd)){
			pwd = PropertiesConstants.getString(ctx, pwdId, "");
		}
		
		if (StringUtils.isEmpty(pwd)){
			throw new BaseException("clnt.e2000",String.format("Can not find argument %s.",pwdId));
		}
		
		String pwdDecrypted = des3Coder.decode(pwd, id + "$" + now);
		String pwdMd5 = md5Coder.encode(pwdDecrypted, id);
		
		if (!pwdMd5.equals(key.getKeyContent())){
			throw new BaseException("clnt.e2001","The password is not correct.");
		}		
		
	}
	
	protected void checkTimestamp(AccessAppKey key, Context ctx){
		String now = ctx.getRequestHeader(timestampId);
		long timestamp = getLong(now,0);
		if (timestamp <= 0){
			throw new BaseException("clnt.e2000",String.format("Can not find argument %s.",timestampId));
		}
		
		if (Math.abs(System.currentTimeMillis() - timestamp) > ttl){
			throw new BaseException("clnt.e2006",String.format("Timestamp %d is expired.",timestamp));
		}
	}
	
	private long getLong(String now, long i) {
		try {
			return Long.parseLong(now);
		}catch (NumberFormatException ex){
			return i;
		}
	}
}