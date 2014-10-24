package com.anysoft.util.textdotter;

/**
 * 文本装饰器
 *  
 * 文本装饰器，为文本进行装饰，例如字体，颜色等
 * @author szduanyy
 *
 */
public interface TextDecorator extends CharSequence{
	/**
	 * 以指定的class装饰文本
	 * @param start 开始位置
	 * @param length 长度
	 * @param text 原始文本
	 * @param className 文本对应的class
	 */
	public void decorate(int start,int length,String text,String className);
	
	/**
	 * 获取指定区间的字符串
	 * @param start 开始位置
	 * @param end 结束位置
	 * @return　字符串
	 */
	public String getSubText(int start,int end);
}
