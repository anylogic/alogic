package com.anysoft.util;

import java.util.Random;

/**
 * Key随机生成工具
 * <p>所生成的Key为可见字符，由Base64的字符表中字符组成。</p>
 * @author duanyy
 *
 */
public class KeyGen {
	
	/**
	 * Base64字符表
	 */
	protected static final char[] Base64Char = {
	      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
	      'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
	      'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
	      'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
	      'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
	      'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
	      '8', '9', '+', '/'
	  };
	
	/**
	 * 按照指定宽度生成Key
	 * @param _width Key的宽度
	 * @return 生成好的Key
	 */
	static public String getKey(int _width){
		int width = _width <= 0 ? 6 : _width;
		char [] ret = new char[width];
		Random ran = new Random();
		for (int i = 0 ; i < width ; i ++){
			int intValue = ran.nextInt(64) % 64;
			ret[i] = Base64Char[intValue];
		}
		
		return new String(ret);
	}
	
	/**
	 * 生成20位的Key
	 * @return Key
	 */
	static public String getKey(){
		return getKey(20);
	}
}
