package com.alogic.auth;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.util.JsonTools;
import com.anysoft.util.Pair;
import com.anysoft.util.XmlTools;

/**
 * 基于Session的Principal实现
 * 
 * @author yyduan
 * @since 1.6.10.10
 */
public class SessionPrincipal implements Principal{	
	/**
	 * 数据存放在Session中
	 */
	protected Session session = null;
	
	public SessionPrincipal(Session session){
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

	public String getUserId() {
		return this.session.hGet(USER_GROUP,USERID, "");
	}

	public String getName() {
		return this.session.hGet(USER_GROUP,NAME, "");
	}

	public String getAvatar() {
		return this.session.hGet(USER_GROUP,AVATAR, "");
	}
	
	@Override
	public String getLoginTime(){
		return this.session.hGet(USER_GROUP,LOGIN_TIME, "");
	}
	
	@Override
	public String getLoginIp(){
		return this.session.hGet(USER_GROUP,FROM_IP,"");
	}

	@Override
	public List<String> getPrivileges() {
		return this.session.sMembers(PRIVILEGE_GROUP,"*");
	}

	@Override
	public boolean hasPrivilege(String privilege){
		return this.session.sExist(PRIVILEGE_GROUP,privilege);
	}

	@Override
	public void setProperty(String id, String value, boolean overwrite) {
		this.session.hSet(USER_GROUP, id, value, overwrite);
	}

	@Override
	public String getProperty(String id, String dftValue) {
		return this.session.hGet(USER_GROUP,id, dftValue);
	}

	@Override
	public void addPrivileges(String... privileges) {
		for (String p:privileges){
			this.session.sAdd(PRIVILEGE_GROUP,p);
		}
	}

	@Override
	public void copyTo(Principal another) {
		List<Pair<String,String>> entries = this.session.hGetAll(USER_GROUP,"*");
		
		for (Pair<String,String> entry:entries){
			another.setProperty(entry.key(), entry.value(), true);
		}
		
		List<String> privileges = this.getPrivileges();
		
		if (privileges != null && !privileges.isEmpty()){
			another.addPrivileges(privileges.toArray(new String[0]));
		}
	}

	@Override
	public void report(Element xml) {
		if (xml != null){
			List<Pair<String,String>> entries = this.session.hGetAll(USER_GROUP,"*");
			
			for (Pair<String,String> entry:entries){
				XmlTools.setString(xml, entry.key(), entry.value());
			}
			
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
			List<Pair<String,String>> entries = this.session.hGetAll(USER_GROUP,"*");
			
			for (Pair<String,String> entry:entries){
				JsonTools.setString(json, entry.key(), entry.value());
			}
			
			List<String> privileges = this.getPrivileges();
			if (privileges != null && !privileges.isEmpty()){
				json.put("privilege", privileges);
			}
		}
	}

}