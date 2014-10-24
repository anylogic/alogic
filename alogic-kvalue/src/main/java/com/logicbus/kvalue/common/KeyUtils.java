package com.logicbus.kvalue.common;

/**
 * Key
 * @author duanyy
 *
 */
public class KeyUtils {
	public static String key(String...keys){
		StringBuffer buf = new StringBuffer();
		for (int i = 0 ;i < keys.length ; i ++){
			if (i > 0){
				buf.append(':');
			}
			buf.append(keys[i]);
		}
		return buf.toString();
	}
}
