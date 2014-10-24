package com.anysoft.util.textdotter;



/**
 * 文本点缀接口
 *  
 * 文本点缀接口，为文本中的某些匹配项设置class，作为点缀。
 * @author szduanyy
 *
 */
public interface TextDotter {
	/**
	 * 获取ID
	 * @return Id
	 */
	public String getId();
	/**
	 * 获取Dotter项目
	 * @return Dotter项目列表
	 */
	public TextDotterItem[] getDotterItem();
}
