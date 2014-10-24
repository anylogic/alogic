package com.logicbus.backend.timer.util;

import java.util.HashSet;

import com.logicbus.backend.timer.util.parser.DefaultItemParser;

public class SetValueMatcher implements ValueMatcher {
	protected HashSet<Integer> set = new HashSet<Integer>();
	protected boolean all = false;
	public boolean match(int value) {
		return all ? all : set.contains(Integer.valueOf(value));
	}
	protected ItemParser parser = null;
	
	public SetValueMatcher(){
		this(null);
	}
	public SetValueMatcher(ItemParser __parser){
		this("00",__parser);
	}
	
	public SetValueMatcher(String _pattern,ItemParser __parser){
		if (__parser == null){
			parser = new DefaultItemParser();
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
