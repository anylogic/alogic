package com.anysoft.util;

import java.util.Random;

/**
 * Key随机生成工具
 * <p>所生成的Key为可见字符，由Base64的字符表中字符组成。</p>
 * @author duanyy
 *
 */
public class KeyGen {
	
	protected static final char [] chars = {
			'0','1','2','3','4','5','6','7','8','9',
			'A','B','C','D','E','F','G','H','I','J',
			'K','L','M','N','O','P','Q','R','S','T',
			'U','V','W','X','Y','Z',
			'a','b','c','d','e','f','g','h','i','j',
			'k','l','m','n','o','p','q','r','s','t',
			'u','v','w','x','y','z'
	};
	
	/**
	 * 生成指定长度的uuid
	 * 
	 * @param length
	 * @param redix
	 * @return uuid字符串
	 */
	public static String uuid(int length,int redix){
		if (length <= 0){
			return uuid();
		}
		
		int r = redix <= 0 || redix > chars.length ? chars.length : redix;
		int l = length <= 0 ? 20 : length;
		
		char [] uuid = new char[length];
		
		Random rand = new Random();
		for (int i = 0 ;i < l ; i ++){
			uuid[i] = chars[rand.nextInt(r) % r];
		}
		
		return new String(uuid);
	}
	
	/**
	 * 生成标准的uuid
	 * @return 标准的uuid
	 */
	public static String uuid(){		
		char [] uuid = new char[36];
		
		uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
		uuid[14] = '4';
	      
		Random rand = new Random();
		for (int i = 0 ;i < 36 ; i ++){
			if (uuid[i] <= 0){
				int r = rand.nextInt(16) % 16;
				uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
			}
		}
		
		return new String(uuid);
	}	

	/**
	 * 按照指定宽度生成Key
	 * @param width Key的宽度
	 * @return 生成好的Key
	 */
	static public String getKey(int width){
		return uuid(width,0);
	}
	
	/**
	 * 生成20位的Key
	 * @return Key
	 */
	static public String getKey(){
		return uuid(20,0);
	}
}
