package com.anysoft.util;


/**
 * 封装JRE的系统变量
 * @author szduanyy
 *
 */
public class SystemProperties extends Properties {
	/**
	 * 设置变量
	 * <p>但是，我们不能修改Jre的环境变量，所以本函数不作为。</p>
	 */
	public void _SetValue(String _name, String _value) {
		//Sorry,我们不能修改Jre的环境变量
	}

	/**
	 * 获取变量
	 * @param _name 变量名
	 */
	public String _GetValue(String _name) {
		String value = System.getProperty(_name);
		if (value == null){
			return "";
		}
		return value;
	}

	/**
	 * 清除 
	 * <p>但是，我们不能修改Jre的环境变量，所以本函数不作为。</p>
	 */
	public void Clear() {
	}

}
