package com.alogic.auth;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
 * 
 * @version 1.6.11.1 [20171215 duanyy] <br>
 * - 增加获取登录id的方法<br>
 */
public class SessionPrincipal implements Principal{	
	/**
	 * 数据存放在Session中
	 */
	protected Session session = null;
	protected String id;
	public SessionPrincipal(String id,Session session){
		this.id = id;
		this.session = session;
	}
	
	@Override
	public String getId() {
		return id;
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
	public String getLoginId(){
		return this.session.hGet(USER_GROUP,USERID,"anonymous");
	}
	
	public String getUserId() {
		return this.session.hGet(USER_GROUP,USERID, "anonymous");
	}

	public String getName() {
		return this.session.hGet(USER_GROUP,NAME, "anonymous");
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
			XmlTools.setString(xml,"id",this.getId());
			
			List<Pair<String,String>> entries = this.session.hGetAll(USER_GROUP,"*");
			if (entries != null && !entries.isEmpty()){
				Document doc = xml.getOwnerDocument();				
				for (Pair<String,String> entry:entries){
					Element property = doc.createElement("property");
					XmlTools.setString(property, "k",entry.key());
					XmlTools.setString(property,"v",entry.value());
					xml.appendChild(property);
				}
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
			JsonTools.setString(json, "id", getId());
			
			List<Pair<String,String>> entries = this.session.hGetAll(USER_GROUP,"*");
			
			if (entries != null){
				Map<String,Object> map = new HashMap<String,Object>();
				
				for (Pair<String,String> p:entries){
					JsonTools.setString(map,p.key(), p.value());
				}
				
				json.put("property", map);
			}
			
			List<String> privileges = this.getPrivileges();
			if (privileges != null && !privileges.isEmpty()){
				json.put("privilege", privileges);
			}
		}
	}

	@Override
	public void toJson(Map<String, Object> json) {
		report(json);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void fromJson(Map<String, Object> json) {
		if (json != null){
			Object found = json.get("property");
			if (found != null && found instanceof Map){
				Map<String,Object> property = (Map<String,Object>)found;
				
				Iterator<Entry<String,Object>> iter = property.entrySet().iterator();
				
				while (iter.hasNext()){
					Entry<String,Object> entry = iter.next();
					setProperty(entry.getKey(), entry.getValue().toString(), true);
				}
			}
			
			found = json.get("privilege");
			if (found != null && found instanceof List){
				List<String> privileges = (List<String>)found;
				
				for (String p:privileges){
					addPrivileges(p);
				}
			}
		}
	}
	
	@Override
	public void clearProperties() {
		this.session.hDel(Session.USER_GROUP);
	}

	@Override
	public void clearPrivileges() {
		this.session.sDel(Session.PRIVILEGE_GROUP);
	}
}