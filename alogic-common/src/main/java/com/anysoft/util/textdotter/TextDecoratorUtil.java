package com.anysoft.util.textdotter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 文本装饰工具类
 * 
 * @author szduanyy
 */
public class TextDecoratorUtil {
	
	/**
	 * 装饰
	 * @param start 开始位置
	 * @param end 结束位置
	 * @param decorator 装饰器
	 * @param dotter 点缀器
	 */
	static public void decorate(int start,int end,TextDecorator decorator,TextDotter dotter){
		MatchItem [] matches;
		//寻找匹配项
		{
			Vector<MatchItem> _matches = new Vector<MatchItem>();
			TextDotterItem [] dotterItems = dotter.getDotterItem();
			for (int i = 0 ; i < dotterItems.length ; i ++){
				Pattern pattern;
				if (dotterItems[i].Flags == 0)
					pattern = Pattern.compile(dotterItems[i].Expr);
				else
					pattern = Pattern.compile(dotterItems[i].Expr,dotterItems[i].Flags);				
				Matcher matcher = pattern.matcher(decorator.subSequence(start, end));
				while (matcher.find()){					
					MatchItem item = new MatchItem();
					item.Start = matcher.start();
					item.ClassName = dotterItems[i].ClassName;
					item.Text = matcher.group();
					_matches.add(item);
				}
			}
			matches = _matches.toArray(new MatchItem[0]); 
		}
		if (matches.length <= 0){
			//没有任何匹配项
			decorator.decorate(start, end - start,decorator.getSubText(start, end),"");
			return ;
		}
		//对Matches进行排序
		Arrays.sort(matches,(new MatchItemComparator()));
		
		//去掉内联的匹配项
		for (int i = 0 ;i < matches.length ; i ++){			
			MatchItem item = matches[i];
			if (item == null || item.length() <= 0)
				continue;
			for (int j = 0 ; j < matches.length ; j ++){
				if (matches[j] == null || matches[j].length() <= 0)
					continue;
				if (j == i){
					continue;
				}
				if ((item.Start >= matches[j].Start) && (item.Start < matches[j].Start + matches[j].length())){
					matches[i] = null;
					break;
				}
			}
		}
		
		//输出文本
		{
			int position = start;
			for (int i = 0 ;i < matches.length ; i ++){								
				MatchItem item = matches[i];	
				if (item == null || item.length() <= 0){
					continue;
				}
				decorator.decorate(position
						,item.Start + start - position
						,decorator.getSubText(position, item.Start + start)
						,null);
				decorator.decorate(item.Start + start
						,item.length()
						,item.Text
						,item.ClassName);		
				position = start + item.Start + item.length();
			}
			
			decorator.decorate(position
					,end - position
					,decorator.getSubText(position, end)
					,null);			
		}
	}
	
	/**
	 * 装饰
	 * @param decorator 装饰器
	 * @param dotter 点缀器
	 */
	static public void decorate(TextDecorator decorator,TextDotter dotter){
		decorate(0,decorator.length(),decorator,dotter);
	}
	
	private static class MatchItem{
		public int Start;
		public String ClassName;
		public String Text;
		public int length(){return Text.length();}
	};
	
	private static class MatchItemComparator implements Comparator<Object>{
		public int compare(Object o1, Object o2) {
			MatchItem item1 = (MatchItem)o1;
			MatchItem item2 = (MatchItem)o2;
			if (item1.Start < item2.Start){
				return -1;
			}
			if (item1.Start > item2.Start){
				return 1;
			}
			if (item1.length() < item2.length()){
				return -1;
			}
			if (item1.length() > item2.length()){
				return 1;
			}			
			return 0;
		}
	}
}
