package com.anysoft.util;

import java.util.Vector;

/**
 * 简单的字符串匹配器
 * 
 * <p>有的时候，正则表达式匹配显得过于厚重，我们需要简单的匹配器来提升效率。StringMatcher仅支持*号匹配，适用于大多数场合。</p>
 * <p>例如：</p>
 * <pre>
 * ~~~~~~~~~~~~~~~~~~~~~{.java}
 * StringMatcher matcher = new StringMatcher("*.sina.com.cn");
 * matcher.match("www.sina.com.cn"); --true
 * matcher.match("images.sina.com.cn"); --true
 * matcher.match("www.sohu.com.cn"); -- false
 * ~~~~~~~~~~~~~~~~~~~~~
 * </pre>
 * @author duanyy
 *
 */
public class StringMatcher {
	/**
	 * 字符串模板的段落
	 */
	protected Vector<String> segments = new Vector<String>();
	
	/**
	 * 构造函数并编译模板
	 * @param pattern
	 */	
	public StringMatcher(String pattern){
		compile(pattern);
	}
	
	/**
	 * 编译模板，生成段落
	 * @param pattern 模板
	 */
	protected void compile(String pattern){
		String segment = "";
		for (int i = 0 ; i < pattern.length() ; i ++){
			if (pattern.charAt(i) == '*'){
				if (segment.length() > 0){
					segments.add(segment);
					segment = "";
				}
				segments.add("*");
			}else{
				segment += pattern.charAt(i);
			}
		}
		if (segment.length() > 0){
			segments.add(segment);
			segment = "";
		}		
	}
	
	/**
	 * 进行字符串匹配
	 * @param data 待匹配的数据
	 * @return 是否匹配
	 */
	public boolean match(String data){
		int current = 0;
		boolean any = false;
		for (String segment:segments){
			if (segment.equals("*")){
				any = true;
				continue;
			}
			int found = data.indexOf(segment,current);
			boolean matched = any ? found >= current : found == current;
			if (!matched){
				return false;
			}			
			current = found + segment.length();
			any = false;
		}
		return true;
	}	

}

