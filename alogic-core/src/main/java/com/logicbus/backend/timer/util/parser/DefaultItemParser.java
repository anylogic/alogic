package com.logicbus.backend.timer.util.parser;

import com.logicbus.backend.timer.util.ItemParser;

/**
 * 缺省的ItemParser
 *  
 * @author user
 *
 */
public class DefaultItemParser implements ItemParser {
	public int[] parseItem(String _item) {
		return parseSlash(_item);
	}
	protected int[] parseSlash(String _item){
		//先找到斜杠
		int __slashFound = _item.indexOf("/");
		if (__slashFound < 0){
			//没有找到斜杠
			return parseDash(_item);
		}
		
		String __fromtoItem;
		String __nItem;
		if (__slashFound == 0){
			//如果是"/n"模式，当作"*/n"处理
			__fromtoItem = "*";
			__nItem = _item.substring(1);
		}else{
			__fromtoItem = _item.substring(0,__slashFound);
			__nItem = _item.substring(__slashFound + 1);
		}
		
		int [] __fromto = parseDash(__fromtoItem);
		int __n = parseN(__nItem);
		if (__fromto == null || __n <= 0){
			return null;
		}
		 
		int [] ret = new int[__fromto.length + 1];
		int __length = 0;
		for (int i = 1 ; i < __fromto.length && i <= __fromto[0] + 1; i +=__n,__length++){
			ret[__length + 1] = __fromto[i];
		}
		ret[0] = __length;
		return ret;
	}
	
	protected int parseN(String _item){
		try {
			return Integer.parseInt(_item);
		}catch (Exception ex){
			return 0;
		}		
	}
	
	protected int[] parseDash(String _item){
		int __from ;
		int __to ;
		int [] range = getRange();
		if (_item.equals("*")){
			__from = range[1];
			__to = range[2];
		}else{
			//先找到破折号
			int dashFound = _item.indexOf("-");
			if (dashFound < 0){
				//没有找到斜杠
				int __single = parseSingleItem(_item);
				return __single < 0 ? null:new int[]{1,__single};
			}	
			if (dashFound == 0){
				//-n模式，当作0-min
				__from = range[1];
			}else{
				__from = parseSingleItem(_item.substring(0,dashFound));
			}
			
			if (dashFound == _item.length() - 1){
				//m-模式,当作m-max
				__to = range[2];
			}else{
				__to = parseSingleItem(_item.substring(dashFound + 1));
			}
		}
		
		if (__from < 0 || __to < 0){
			return null;
		}
		
		int [] ret = new int [range[0] + 1];
		if (__from < __to){
			for (int i = 0 ; i <= __to - __from ; i ++){
				ret[i + 1] = __from + i;
				ret[0] = i;
			}
		}else{
			for (int i = 0 ; i <= __to + range[0] - __from; i ++){
				ret[i + 1] = (__from + i) % range[0];
				ret[0] = i;
			}
		}
		return ret;
	}
	public int parseSingleItem(String _item) {
		try {
			return Integer.parseInt(_item);
		}catch (Exception ex){
			return -1;
		}
	}
	public int [] getRange(){
		return new int[]{60,0,59};
	}
}
