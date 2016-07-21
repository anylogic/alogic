package com.logicbus.backend;

import java.util.Map;

import com.anysoft.util.Properties;

/**
 * Web服务器的会话，用于替代HttpSession
 * 
 * @author duanyy
 * 
 * @since 1.6.2.6
 * 
 * @version 1.6.5.31 [duanyy 20160721] <br>
 * - 增加set的exist接口 <br>
 * 
 */
abstract public class Session extends Properties{
	abstract public String hGet(String id,String field,String dftValue);
	
	abstract public void hSet(String id,String field,String value);
	
	abstract public boolean hExist(String id,String field);
	
	abstract public Map<String,String> hGetAll(String id);

	abstract public int hLen(String id);
	
	abstract public String[] hKeys(String id);
	
	abstract public String[] hValues(String id);
	
	abstract public void sAdd(String id,String...member);
	
	abstract public void sDel(String id,String...member);
	
	abstract public int sSize(String id);
	
	abstract public String[] sMembers(String id);
	
	abstract public boolean sExist(String id,String member);
	
	abstract public long getCreateTime();
	
	abstract public void invalidate();
	
	abstract public String getId();
	
	abstract public void del(String id);
}
