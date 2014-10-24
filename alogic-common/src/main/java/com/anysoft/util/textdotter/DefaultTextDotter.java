package com.anysoft.util.textdotter;

import java.util.Vector;
import java.util.regex.Pattern;


/**
 * 缺省的TextDotter
 * @author szduanyy
 *
 */
public class DefaultTextDotter implements TextDotter {
	private Vector<TextDotterItem> items = new Vector<TextDotterItem>();
	/**
	 * 构造函数
	 * @param id ID
	 */
	public DefaultTextDotter(String _id){
		id = _id;
	}
	/**
	 * 获取Dotter项目
	 * @return Dotter项目列表
	 */		
	public TextDotterItem[] getDotterItem() {
		return items.toArray(new TextDotterItem[0]);
	}
	
	/**
	 * 添加匹配项
	 * @param expr 匹配项的正则表达式
	 * @param flags 匹配项标记
	 * @param className class名称
	 */
	public void addItem(String expr,int flags,String className){
		TextDotterItem item = new TextDotterItem();
		item.ClassName = className;
		item.Expr = expr;
		item.Flags = flags;
		
		items.add(item);
	}
	/**
	 * 添加匹配项
	 * @param expr 匹配项的正则表达式
	 * @param flags 匹配项标记
	 * @param className class名称
	 */
	public void addItem(String expr,String flags,String className){
		TextDotterItem item = new TextDotterItem();
		item.ClassName = className;
		item.Expr = expr;
		item.Flags = parseFlags(flags);
		items.add(item);
	}
	
	/**
	 * 翻译keywords
	 * @param keywords 关键字列表，以空格分隔
	 * @return 翻译的正则表达式
	 */
	public static String transKeywords(String keywords){
		String [] keys = keywords.split(" ");
		String ret = "";
		for (int i = 0 ; i < keys.length ; i ++){
			if (keys[i].length() <= 0)
				continue;
			if (i == keys.length - 1){
				ret += "\\b" + keys[i] + "\\b";
			}else{
				ret += "\\b" + keys[i] + "\\b|";
			}
		}
		return ret;
	}
	
	/**
	 * 解析标记
	 * @param flags 标记
	 * @return int标记
	 */
	public static int parseFlags(String _flags){
		if (_flags.length() <= 0)
			return 0;
		String [] flags = _flags.split("\\|");
		int ret = 0;
		for (int i = 0 ;i < flags.length ; i ++){
			String flag = flags[i].trim();
			if (flag.equals("UNIX_LINES")){
				ret |= Pattern.UNIX_LINES;
				continue;
			}
			if (flag.equals("CASE_INSENSITIVE")){
				ret |= Pattern.CASE_INSENSITIVE;
				continue;
			}
			if (flag.equals("COMMENTS")){
				ret |= Pattern.COMMENTS;
				continue;
			}
			if (flag.equals("MULTILINE")){
				ret |= Pattern.MULTILINE;
				continue;
			}
			if (flag.equals("LITERAL")){
				ret |= Pattern.LITERAL;
				continue;
			}
			if (flag.equals("DOTALL")){
				ret |= Pattern.DOTALL;
				continue;
			}
			if (flag.equals("UNICODE_CASE")){
				ret |= Pattern.UNICODE_CASE;
				continue;
			}
			if (flag.equals("CANON_EQ")){
				ret |= Pattern.CANON_EQ;
				continue;
			}
		}
		return ret;
	}

	protected String id;
	public String getId() {
		return id;
	}
}
