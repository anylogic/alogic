package com.anysoft.util.textdotter;


/**
 * Html装饰器
 * 
 * 将指定的文本装饰成Html文档
 * @author szduanyy
 *
 */
public class HtmlTextDecorator implements TextDecorator {
	
	/**
	 * 构造函数
	 * @param _content
	 */
	public HtmlTextDecorator(String _content,boolean _ignoreDelimiter){
		content = _content;
		result = "";
	}
	/**
	 * 构造函数
	 * @param _content
	 */
	public HtmlTextDecorator(String _content){
		this(_content,true);
	}	
	/**
	 * 初始的文本
	 */
	protected String content;
	
	/**
	 * 经过装饰的结果文本
	 */
	protected String result;
	
	/**
	 * 获取结果
	 * @return　经过装饰的结果文本
	 */
	public String getResult(){return result;}
			
	/**
	 * 以指定的class装饰文本
	 * @param start 开始位置
	 * @param length 长度
	 * @param text 原始文本
	 * @param className 文本对应的class
	 */	
	public void decorate(int start, int length, String text, String className) {
		if (className == null || className.length() <= 0){
			result += processEscape(text);
		}else{
			result += "<span class='" + className + "'>" + processEscape(text) + "</span>";
		}
	}
	public char charAt(int index) {
		return content.charAt(index);
	}
	public int length() {
		return content.length();
	}
	public CharSequence subSequence(int start, int end) {
		return content.subSequence(start,end);
	}
	public String getSubText(int start, int end) {
		return content.substring(start,end);
	}
	
	/**
	 * 处理转义字符
	 * @param content 处理前文本
	 * @return　处理后文本
	 */
	public String processEscape(String content){
		String data = "";
		for (int i = 0; i < content.length(); i++) {
			switch (content.charAt(i)) {
			case '&':
				data += "&amp;";
				break;
			case '<':
				data += "&lt;";
				break;
			case '>':
				data += "&gt;";
				break;
			case ' ':
				data += "&nbsp;";
				break;
			case '\t':				
				data += "&nbsp;&nbsp;&nbsp;&nbsp;";
				break;
			default:
				data += content.charAt(i);
			}
		}
		return data;
	}
}
