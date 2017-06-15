package com.anysoft.util;

import java.util.Random;

/**
 * Key随机生成工具
 * <p>所生成的Key为可见字符，由Base64的字符表中字符组成。</p>
 * @author duanyy
 *
 * @version 1.6.6.13 [20170111 duanyy] <br>
 * - 修改随机字符串生成规则 <br>
 */
public class KeyGen {
	
	protected static final char [] chars = {
		'0','1','2','3','4','5','6','7','8','9',		
		'a','b','c','d','e','f','g','h','i','j',
		'k','l','m','n','o','p','q','r','s','t',
		'u','v','w','x','y','z',		
		'A','B','C','D','E','F','G','H','I','J',
		'K','L','M','N','O','P','Q','R','S','T',
		'U','V','W','X','Y','Z'
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
	 * 生成指定长度的uuid
	 * @param length 字符串长度
	 * @param start 字符开始位置
	 * @param end 字符结束位置
	 * @return 生成的字符串
	 * 
	 * @since 1.6.6.13
	 */
	public static String uuid(int length,int start,int end){
		if (length <= 0){
			return uuid();
		}
		
		int e = end < 0 || end >= chars.length ? chars.length - 1 : end;
		int s = start < 0 || start >= e ? e: start;
		int l = length <= 0 ? 20 : length;

		char [] uuid = new char[length];
		
		Random rand = new Random();
		for (int i = 0 ;i < l ; i ++){
			uuid[i] = chars[s + rand.nextInt(e - s + 1) % (e - s + 1)];
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
	public static String getKey(int width){
		return uuid(width,0);
	}
	
	/**
	 * 生成20位的Key
	 * @return Key
	 */
	public static String getKey(){
		return uuid(20,0);
	}
	
	/**
	 * 生成全数字的随机字符串
	 * @param length 字符串长度
	 * @return 生成的字符串
	 * 
	 * @since 1.6.6.13
	 */
	public static String num(int length){
		return uuid(length,0,9);
	}
	
	/**
	 * 生成全小写的随机字符串
	 * @param length 字符串长度
	 * @return 生成的字符串
	 * 
	 * @since 1.6.6.13
	 */
	public static String lowercase(int length){
		return uuid(length,10,35);
	}
	
	/**
	 * 生成全大写的随机字符串
	 * @param length 字符串长度
	 * @return 生成的字符串
	 * 
	 * @since 1.6.6.13
	 */
	public static String uppercase(int length){
		return uuid(length,36,61);
	}
	
	public static void main(String [] args){
		// 10位数字
		System.out.println(uuid(10,0,9));
		
		// 10位小写字母
		System.out.println(uuid(10,10,35));
		
		// 10位大写字母
		System.out.println(uuid(10,36,61));		
		
		// 10位小写加数字
		System.out.println(uuid(10,0,35));
		
		// 10位大小写
		System.out.println(uuid(10,10,61));
		
		// 10位大小写
		System.out.println(uuid(10,0,61));
		
		// 64为数字(16进制)
		System.out.println(uuid(8,0,15));
	}
}
