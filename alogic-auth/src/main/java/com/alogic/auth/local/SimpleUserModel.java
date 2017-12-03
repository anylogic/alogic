package com.alogic.auth.local;

import java.util.Map;

import org.w3c.dom.Element;

import com.alogic.load.Loadable;
import com.anysoft.util.Configurable;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlTools;

/**
 * 简单的用户模型
 * 
 * <p>一个简单的用户模型，用于从本地配置文件中装入
 * 
 * @author duanyy
 * @since 1.6.10.10
 */
public class SimpleUserModel implements Loadable,Configurable,XMLConfigurable{

	/**
	 * 用户登录id
	 */
	protected String userId;
	
	/**
	 * 密码
	 */
	protected String password;
	
	/**
	 * 权限项，支持多个权限项，以“,”号分隔
	 */
	protected String privileges;
	
	@Override
	public void report(Element xml) {
		if (xml != null){
			XmlTools.setString(xml,"userId",userId);
			XmlTools.setString(xml,"password",password);
			XmlTools.setString(xml,"privileges",privileges);
		}
	}

	@Override
	public void report(Map<String, Object> json) {
		if (json != null){
			JsonTools.setString(json, "userId", userId);
		}
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub+
		return null;
	}

	@Override
	public long getTimestamp() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void expire() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configure(Element e, Properties p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configure(Properties p) {
		// TODO Auto-generated method stub
		
	}

}
