package com.alogic.auth.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.alogic.auth.Principal;
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
	 * 权限项集合
	 */
	protected Set<String> privileges;
	
	/**
	 * 装载数据的时间戳
	 */
	protected long timestamp = System.currentTimeMillis();
	
	/**
	 * 数据的生存时间
	 */
	protected final long ttl = 5 * 60 * 1000L;
	
	public String getUserId() {
		return userId;
	}

	public String getName() {
		return name;
	}

	public String getAvatar() {
		return avatar;
	}	
	
	protected Set<String> getPrivilegeSet(boolean create){
		if (privileges == null){
			synchronized (this){
				if (privileges == null && create){
					privileges = new HashSet<String>();
				}
			}
		}
		return privileges;
	}
	
	protected void addPrivileges(String... privileges) {
		Set<String> set = this.getPrivilegeSet(true);
		for (String p:privileges){
			set.add(p);
		}
	}
	
	@Override
	public String getPassword(){
		return password;
	}
	
	public List<String> getPrivileges(){
		List<String> result = new ArrayList<String>();
		Set<String> set = getPrivilegeSet(false);
		if (set != null){
			Iterator<String> iter = set.iterator();
			while (iter.hasNext()){
				result.add(iter.next());
			}
		}
		return result;
	}
	
	public boolean hasPrivilege(String privilege) {
		Set<String> set = getPrivilegeSet(false);
		return set == null ? false : set.contains(privilege);
	}
	
	@Override
	public void report(Element xml) {
		if (xml != null){
			XmlTools.setString(xml,"userId",userId);
			XmlTools.setString(xml,"name",name);
			XmlTools.setString(xml, "avatar", avatar);
			
			List<String> privileges = this.getPrivileges();
			if (privileges != null && !privileges.isEmpty()){
				Document doc = xml.getOwnerDocument();
				
				for (String p:privileges){
					Element elem = doc.createElement("privilege");
					XmlTools.setString(elem, "value", p);
					xml.appendChild(elem);
				}
			}
		}
	}

	@Override
	public void report(Map<String, Object> json) {
		if (json != null){
			JsonTools.setString(json,"userId", userId);
			JsonTools.setString(json,"name",name);
			JsonTools.setString(json,"avatar",avatar);
			
			List<String> privileges = this.getPrivileges();
			if (privileges != null && !privileges.isEmpty()){
				json.put("privilege", privileges);
			}
		}
	}
	
	@Override
	public void copyTo(Principal another) {
		another.setProperty("userId", userId,true);
		another.setProperty("name",name,true);
		another.setProperty("avatar",avatar,true);
		
		List<String> privileges = this.getPrivileges();
		if (privileges != null && !privileges.isEmpty()){
			for (String p:privileges){
				another.addPrivileges(p);
			}
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
		
		addPrivileges(PropertiesConstants.getString(p,"privileges", "").split(","));
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
