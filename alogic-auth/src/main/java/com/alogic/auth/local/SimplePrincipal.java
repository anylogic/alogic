package com.alogic.auth.local;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.auth.Session;
import com.alogic.auth.UserPrincipal;
import com.anysoft.util.JsonTools;
import com.anysoft.util.XmlTools;

/**
 * 简单的Principal模型
 * 
 * @author yyduan
 * 
 * @since 1.6.10.10
 */
public class SimplePrincipal implements UserPrincipal{
	/**
	 * 数据存放在Session中
	 */
	protected Session session = null;
	
	public SimplePrincipal(Session session){
		this.session = session;
	}
	
	@Override
	public String getId() {
		return this.session.getId();
	}

	@Override
	public long getTimestamp() {
		return this.session.getTimestamp();
	}

	@Override
	public boolean isExpired() {
		return this.session.isExpired();
	}

	@Override
	public void expire() {
		this.session.expire();
	}
	
	@Override
	public void report(Element xml) {
		if (xml != null){
			XmlTools.setString(xml,"userId",getUserId());
			XmlTools.setString(xml, "name", getName());
			XmlTools.setString(xml, "avatar", getAvatar());
			XmlTools.setString(xml, "loginTime", getLoginTime());
			
			List<String> privileges = this.getPrivileges();
			if (privileges != null && !privileges.isEmpty()){
				Document doc = xml.getOwnerDocument();
				
				for (String item:privileges){
					Element elem = doc.createElement("privilege");
					XmlTools.setString(elem, "id", item);
					xml.appendChild(elem);
				}
			}			
		}
	}

	@Override
	public void report(Map<String, Object> json) {
		if (json != null){
			JsonTools.setString(json, "userId", getUserId());
			JsonTools.setString(json, "name",getName());
			JsonTools.setString(json, "avatar",getAvatar());
			JsonTools.setString(json, "loginTime", getLoginTime());
			
			List<String> privileges = this.getPrivileges();
			
			if (privileges != null && !privileges.isEmpty()){
				json.put("privilege", privileges);
			}
		}
	}

	@Override
	public String getUserId() {
		return this.session.hGet("$user.id", "");
	}

	@Override
	public String getName() {
		return this.session.hGet("$user.name", "");
	}

	@Override
	public String getAvatar() {
		return this.session.hGet("$user.avatar", "");
	}

	@Override
	public List<String> getPrivileges() {
		return this.session.sMembers("$user.*");
	}

	@Override
	public boolean hasPrivilege(String privilege){
		return this.session.sExist("$user." + privilege);
	}
	
	/**
	 * 获取登录时间
	 * @return 登录时间(毫秒数)
	 */
	public String getLoginTime(){
		return this.session.hGet("$user.loginTime", "");
	}
}
