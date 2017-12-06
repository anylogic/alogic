package com.alogic.auth.local;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import com.alogic.auth.UserModel;
import com.alogic.load.Loader;
import com.anysoft.util.Configurable;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * 简单的用户模型
 * 
 * <p>一个简单的用户模型，用于从本地配置文件中装入
 * 
 * @author duanyy
 * @since 1.6.10.10
 */
public class SimpleUser implements UserModel,Configurable,XMLConfigurable{

	/**
	 * 用户登录id
	 */
	protected String userId;
	
	/**
	 * 用户名称
	 */
	protected String name = "anonymous";
	
	/**
	 * 密码
	 */
	protected String password;
	
	/**
	 * 头像
	 */
	protected String avatar = "1442218377666tM0CkU";
	
	/**
	 * 权限项，支持多个权限项，以“,”号分隔
	 */
	protected String privileges;
	
	/**
	 * 装载数据的时间戳
	 */
	protected long timestamp = System.currentTimeMillis();
	
	/**
	 * 数据的生存时间
	 */
	protected final long ttl = 5 * 60 * 1000L;
	
	@Override
	public String getUserId() {
		return userId;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getAvatar() {
		return avatar;
	}	
	
	@Override
	public String getPassword(){
		return password;
	}
	
	@Override
	public List<String> getPrivileges(){
		List<String> result = new ArrayList<String>();
		
		if (StringUtils.isNotEmpty(privileges)){
			String [] array = privileges.split(",");
			for (String item:array){
				result.add(item);
			}
		}
		
		return result;
	}
	
	@Override
	public boolean hasPrivilege(String privilege) {
		return privileges.indexOf(privilege) >= 0;
	}
	
	@Override
	public void report(Element xml) {
		if (xml != null){
			XmlTools.setString(xml,"userId",userId);
			XmlTools.setString(xml,"password",password);
			XmlTools.setString(xml,"privileges",privileges);
			XmlTools.setString(xml,"name",name);
			XmlTools.setString(xml, "avatar", avatar);
		}
	}

	@Override
	public void report(Map<String, Object> json) {
		if (json != null){
			JsonTools.setString(json,"userId", userId);
			JsonTools.setString(json,"password",password);
			JsonTools.setString(json,"privileges",privileges);
			JsonTools.setString(json,"name",name);
			JsonTools.setString(json,"avatar",avatar);
		}
	}
	
	@Override
	public String getId() {
		return userId;
	}

	@Override
	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public boolean isExpired() {
		return System.currentTimeMillis() - timestamp > ttl;
	}

	@Override
	public void expire() {
		timestamp = timestamp - ttl;
	}

	@Override
	public void configure(Element e, Properties p) {
		Properties props = new XmlElementProperties(e,p);
		configure(props);
	}

	@Override
	public void configure(Properties p) {
		userId = PropertiesConstants.getString(p,"id","");
		password = PropertiesConstants.getString(p,"pwd", "");
		name = PropertiesConstants.getString(p, "name", name);
		
		privileges = PropertiesConstants.getString(p,"privileges", "");
	}

	/**
	 * 从热部署文件中获取
	 * @author yyduan
	 *
	 */
	public static class LoadFromHotFile extends Loader.HotFile<SimpleUser>{
		@Override
		protected String getObjectXmlTag() {
			return "user";
		}
		@Override
		protected String getObjectDftClass() {
			return SimpleUser.class.getName();
		}
	}
	
	/**
	 * 通过ResourceLoader框架装入
	 * @author yyduan
	 *
	 */
	public static class LoadFromInner extends Loader.XmlResource<SimpleUser>{

		@Override
		protected String getObjectXmlTag() {
			return "user";
		}

		@Override
		protected String getObjectDftClass() {
			return SimpleUser.class.getName();
		}
	}


}
