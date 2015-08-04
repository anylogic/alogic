package com.alogic.timer.matcher.util;

import java.util.HashSet;


/**
 * 基于可选值的集合的日期值匹配器
 * 
 * @author duanyy
 * @since 1.6.3.37
 */
public class SetValueMatcher implements ValueMatcher {
	/**
	 * 可选值集合
	 */
	protected HashSet<Integer> set = new HashSet<Integer>();
	
	/**
	 * 是否所有值
	 */
	protected boolean all = false;
	
	protected DateItemParser parser = null;
	
	public boolean match(int value) {
		return all ? all : set.contains(Integer.valueOf(value));
	}	
	
	public SetValueMatcher(){
		this(null);
	}
	public SetValueMatcher(DateItemParser __parser){
		this("00",__parser);
	}
	
	public SetValueMatcher(String _pattern,DateItemParser __parser){
		if (__parser == null){
			parser = new DateItemParser.Default();
		}else{
			parser = __parser;
		}
		parsePattern(_pattern);
	}

	protected void clear(){
		set.clear();
		all = false;
	}
	
	public boolean isEmpty(){
		return all ? false:set.isEmpty();
	}
	
	/**
	 * 解析模板
	 * @param _pattern 模板
	 */
	public void parsePattern(String _pattern) {
		clear();
		
		if (_pattern.equals("*")){
			all = true;
			return ;
		}
		
		all = false;
		
		String [] __items = _pattern.split(",");
		for (int i = 0 ; i < __items.length ; i ++){
			int [] __values = parser != null ?parser.parseItem(__items[i]):null;
			if (__values != null){
				for (int j = 1 ; j < __values.length && j <= __values[0] ; j++){
					set.add(Integer.valueOf(__values[j]));
				}
			}
		}
	}
}
