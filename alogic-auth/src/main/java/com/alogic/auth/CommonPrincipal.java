package com.alogic.auth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.util.JsonTools;
import com.anysoft.util.XmlTools;

/**
 * 通用的Principal
 * 
 * @author yyduan
 *
 */
public class CommonPrincipal extends Principal.Abstract{
	
	/**
	 * 属性
	 */
	private Map<String,String> properties = null;
	
	/**
	 * 权限
	 */
	private Set<String> privileges = null;
	
	public CommonPrincipal(String id) {
		super(id);
	}

	public String getUserId() {
		return this.getProperty(USERID, "");
	}

	public String getName() {
		return this.getProperty(NAME, "");
	}

	public String getAvatar() {
		return this.getProperty(AVATAR, "");
	}
	
	/**
	 * 获取登录时间
	 * @return 登录时间(毫秒数)
	 */
	@Override
	public String getLoginTime(){
		return this.getProperty(LOGIN_TIME, "");
	}
	
	/**
	 * 获取登录ip
	 * @return 登录ip
	 */
	@Override
	public String getLoginIp(){
		return this.getProperty(FROM_IP, "");
	}
	
	/**
	 * 获取属性所存储的Map
	 * 
	 * <p>
	 * 用于在使用时创建对象，当该对象不存在的时候，如果create为true,则创建一个，反之返回为null.
	 *
	 * @param create 是否创建 
	 * @return 属性集的Map对象
	 */
	protected Map<String,String> getPropertiesObject(boolean create){
		if (properties == null){
			synchronized (this){
				if (properties == null && create){
					properties = new HashMap<String,String>();
				}
			}
		}
		
		return properties;
	}
	
	/**
	 * 获取权限的Set集合
	 * 
	 * <p>
	 * 用于在使用时创建对象，当对象不存在的时候，如果create为true，则创建一个，反之返回为null.
	 * 
	 * @param create 是否创建
	 * @return Set集合
	 */
	protected Set<String> getPrivilegesObject(boolean create){
		if (privileges == null){
			synchronized (this){
				if (privileges == null && create){
					privileges = new HashSet<String>();
				}
			}
		}
		return privileges;
	}
	
	@Override
	public void setProperty(String id, String value, boolean overwrite) {
		Map<String,String> map = getPropertiesObject(true);

		boolean exist = map.containsKey(id);
		if (exist){
			if (overwrite){
				map.put(id, value);
			}
		}else{
			map.put(id, value);
		}
	}

	@Override
	public String getProperty(String id, String dftValue) {
		Map<String,String> map = getPropertiesObject(false);
		if (map == null){
			return dftValue;
		}
		
		String found = map.get(id);
		return StringUtils.isEmpty(found)?dftValue:found;
	}

	@Override
	public List<String> getPrivileges() {
		List<String> result = new ArrayList<String>();
		
		Set<String> set = getPrivilegesObject(false);
		if (set != null){
			Iterator<String> iter = set.iterator();
			while (iter.hasNext()){
				result.add(iter.next());
			}
		}
		
		return result;
	}

	@Override
	public boolean hasPrivilege(String privilege) {
		Set<String> set = getPrivilegesObject(false);
		
		return set == null? false:set.contains(privilege);
	}

	@Override
	public void addPrivileges(String... privileges) {
		Set<String> set = getPrivilegesObject(true);
		
		for (String p:privileges){
			set.add(p);
		}
	}

	@Override
	public void copyTo(Principal another) {
		Map<String,String> map = getPropertiesObject(false);
		if (map != null){
			Iterator<Entry<String,String>> iter = map.entrySet().iterator();
			
			while (iter.hasNext()){
				Entry<String,String> entry = iter.next();
				another.setProperty(entry.getKey(),entry.getValue(),true);
			}
		}
		
		List<String> privileges = this.getPrivileges();
		
		if (privileges != null && !privileges.isEmpty()){
			another.addPrivileges(privileges.toArray(new String[0]));
		}
	}

	@Override
	public void report(Element xml) {
		if (xml != null){
			XmlTools.setString(xml,"id",getId());
			
			Map<String,String> map = getPropertiesObject(false);
			if (map != null){
				Document doc = xml.getOwnerDocument();
				Iterator<Entry<String,String>> iter = map.entrySet().iterator();
				
				while (iter.hasNext()){
					Entry<String,String> entry = iter.next();
					
					Element property = doc.createElement("property");

					XmlTools.setString(property, "k", entry.getKey());
					XmlTools.setString(property, "v", entry.getValue());
					
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
			
			Map<String,String> map = getPropertiesObject(false);
			if (map != null){
				Iterator<Entry<String,String>> iter = map.entrySet().iterator();
				Map<String,Object> m = new HashMap<String,Object>();
				
				while (iter.hasNext()){
					Entry<String,String> entry = iter.next();
					JsonTools.setString(m,entry.getKey(),entry.getValue());
				}
				
				json.put("property",m);
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
		Map<String,String> map = getPropertiesObject(false);
		if (map != null){
			map.clear();
		}
	}

	@Override
	public void clearPrivileges() {
		Set<String> set = getPrivilegesObject(false);
		if (set != null){
			set.clear();
		}
	}
}
