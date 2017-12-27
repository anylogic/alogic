package com.alogic.uid;

import com.alogic.uid.naming.IdGenFactory;
import com.anysoft.util.BaseException;

/**
 * 工具类
 * @author yyduan
 * @since 1.6.11.5
 */
public class IdTool {
	/**
	 * 获取指定域的下一个id
	 * @param domain 业务域
	 * @return 字符串形态的id
	 */
	public static String nextId(String domain){
		IdGenerator f = IdGenFactory.get(domain);
		if (f == null){
			throw new BaseException("core.e1003","Can not find id generator :" + domain);
		}
		return f.nextId();
	}
	
	/**
	 * 获取指定域的下一个id
	 * @param domain 业务域
	 * @return long形态的id
	 */
	public static long nextLong(String domain){
		IdGenerator f = IdGenFactory.get(domain);
		if (f == null){
			throw new BaseException("core.e1003","Can not find id generator :" + domain);
		}
		return f.nextLong();
	}
	
	/**
	 * 获取缺省域的下一个id
	 * @return 字符串形态的id
	 */
	public static String nextId(){
		return nextId("default");
	}
	
	/**
	 * 获取缺省域的下一个id
	 * @return 字符串形态的id
	 */
	public static long nextLong(){
		return nextLong("default");
	}
}
