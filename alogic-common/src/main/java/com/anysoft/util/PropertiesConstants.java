package com.anysoft.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.Date;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;


/**
 * Properties工具
 * 
 * <p>为Properties提供更多数据类型的存取操作，例如int,Color,Font,Rectangle,Dimension等。</p>
 * 
 * <p>例如，我们希望从一个Properties中读取一个名为"BgColor"的Color值。</p>
 * <pre class="java">
 * Color color = PropertiesConstants.getInt(props,"BgColor",null);
 * </pre>
 * 
 * @author szduanyy
 * @since 1.0
 * 
 * @version 1.4.3 [20140904 duanyy]
 * - 增加getDouble和setDouble方法
 * @version 1.6.4.16 [duanyy 20151110] <br>
 * - 根据sonar建议优化代码 <br>
 * 
 * @version 1.6.4.27 [20160125 duanyy] <br>
 * - 根据sonar建议优化代码 <br>
 * 
 * @version 1.6.5.33 [20160722 duanyy[ <br>
 * - 增加getRaw方法 <br>
 * 
 * @version 1.6.10.2 [20170925 duanyy] <br>
 * - 增加transform系列方法 <br>
 */
public class PropertiesConstants {
	public static final String DEFAULT_DATE_PATTERN = "yyyyMMddHHmmss";
	public static final String BOOL_TRUE = "true";
	public static final String BOOL_FALSE = "false";
	
	private PropertiesConstants(){
		
	}
	
	/**
	 * 转换模板
	 * 
	 * @param p 属性集
	 * @param pattern 模板
	 * @param dftValue 缺省值
	 * @return 转换值
	 */
	public static String transform(Properties p,String pattern,String dftValue){
		String value = p.transform(pattern);
		return StringUtils.isNotEmpty(value) ? value : dftValue;
	}
	
	/**
	 * 转换模板
	 * 
	 * @param p 属性集
	 * @param pattern 模板
	 * @param dftValue 缺省值
	 * @return 转换值
	 */
	public static long transform(Properties p, String pattern, long dftValue) {
		String value = p.transform(pattern);
		if (StringUtils.isEmpty(value)) {
			return dftValue;
		}

		try {
			return Long.parseLong(value);
		} catch (NumberFormatException ex) {
			return dftValue;
		}
	}
	
	/**
	 * 转换模板
	 * 
	 * @param p 属性集
	 * @param pattern 模板
	 * @param dftValue 缺省值
	 * @return 转换值
	 */
	public static int transform(Properties p, String pattern, int dftValue) {
		String value = p.transform(pattern);
		if (StringUtils.isEmpty(value)) {
			return dftValue;
		}

		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException ex) {
			return dftValue;
		}
	}	
	
	/**
	 * 转换模板
	 * 
	 * @param p 属性集
	 * @param pattern 模板
	 * @param dftValue 缺省值
	 * @return 转换值
	 */
	public static double transform(Properties p, String pattern, double dftValue) {
		String value = p.transform(pattern);
		if (StringUtils.isEmpty(value)) {
			return dftValue;
		}

		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException ex) {
			return dftValue;
		}
	}	
	
	/**
	 * 转换模板
	 * 
	 * @param p 属性集
	 * @param pattern 模板
	 * @param dftValue 缺省值
	 * @return 转换值
	 */
	public static float transform(Properties p, String pattern, float dftValue) {
		String value = p.transform(pattern);
		if (StringUtils.isEmpty(value)) {
			return dftValue;
		}

		try {
			return Float.parseFloat(value);
		} catch (NumberFormatException ex) {
			return dftValue;
		}
	}	
	
	/**
	 * 转换模板
	 * 
	 * @param p 属性集
	 * @param pattern 模板
	 * @param dftValue 缺省值
	 * @return 转换值
	 */
	public static boolean transform(Properties p, String pattern, boolean dftValue) {
		String value = p.transform(pattern);
		if (StringUtils.isEmpty(value)) {
			return dftValue;
		}

		try {
			return BooleanUtils.toBoolean(value);
		} catch (NumberFormatException ex) {
			return dftValue;
		}
	}	
	
	/**
	 * 向Properties设置String值
	 * @param props Properties实例
	 * @param name 变量名
	 * @param value 变量值
	 */
	public static void setString(Properties props,String name,String value){
		if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(value)){
			props.SetValue(name, value);
		}
	}
	
	/**
	 * 获取String 值
	 * @param props Properties实例
	 * @param name 变量名
	 * @param defaultValue 缺省值
	 * @return 变量值
	 */
	public static String getString(Properties props,String name,String defaultValue){
		return props == null ? defaultValue : props.GetValue(name, defaultValue,true,false);
	}
	
	/**
	 * 获取原始未经过计算的值
	 * @param p Properties实例
	 * @param name 变量名
	 * @param defaultValue 缺省值
	 * @return 原始值
	 */
	public static String getRaw(Properties p,String name,String defaultValue){
		return p == null ? defaultValue : p.GetValue(name, defaultValue, false, true);
	}
	
	/**
	 * 获取String值
	 * @param props Properties实例
	 * @param name 变量名
	 * @param defaultValue 缺省值
	 * @param noParent 不取父节点
	 * @return String value
	 * @since 1.0.16
	 */
	public static String getString(Properties props,String name,String defaultValue,
			boolean noParent){
		return props == null ? defaultValue : props.GetValue(name, defaultValue,true,noParent);
	}
	
	/**
	 * 向Properties设置int值
	 * @param props Properties实例
	 * @param name 变量名
	 * @param value 变量值
	 * @see #getInt(Properties, String, int)
	 * @since 1.0
	 */
	public static void setInt(Properties props,String name,int value){
		if (StringUtils.isNotEmpty(name)){
			props.SetValue(name, String.valueOf(value));
		}
	}
	
	/**
	 * 向Properties设置Date值，格式:yyyyMMddHHmmss
	 * @param props Properties实例
	 * @param name 变量名
	 * @param date 变量值
	 */
	public static void setDate(Properties props,String name,Date date){
		if (StringUtils.isNotEmpty(name) && date != null){
			String value = DateUtil.formatDate(date, DEFAULT_DATE_PATTERN);
			props.SetValue(name,value);
		}
	}
	
	/**
	 * 从Properties获取Date值，格式：yyyyMMddHHmmss
	 * @param props Properties实例
	 * @param name 变量名
	 * @param defaultValue 缺省值
	 * @return 变量值
	 */
	public static Date getDate(Properties props,String name,Date defaultValue){
		String value = props.GetValue(name, "",true,false);
		return DateUtil.parseDate(value, DEFAULT_DATE_PATTERN,defaultValue);
	}

	/**
	 * 从Properties获取Date值，格式：yyyyMMddHHmmss
	 * @param props Properties实例
	 * @param name 变量名
	 * @param defaultValue 缺省值
	 * @param noParent 不取父节点属性
	 * @return 变量值
	 * 
	 * @since 1.0.16
	 */
	public static Date getDate(Properties props,String name,Date defaultValue,boolean noParent){
		String value = props.GetValue(name, "",true,noParent);
		return DateUtil.parseDate(value, DEFAULT_DATE_PATTERN,defaultValue);		
	}
	
	/**
	 * 从Properties中读取int值
	 * @param props Properties实例
	 * @param name 变量名
	 * @param defaultValue 缺省值
	 * @return int值
	 * @see #setInt(Properties, String, int)
	 * @since 1.0
	 * 
	 * @since 1.0.8 
	 * - 修改为可从父节点取变量
	 * 
	 */
	public static int getInt(Properties props,String name,int defaultValue){
		String sInt = props.GetValue(name,"",true,false);
		if (sInt.length() <= 0){
			return defaultValue;
		}
		try {
			return Integer.parseInt(sInt);
		}catch (NumberFormatException ex){
			return defaultValue;
		}		
	}

	/**
	 * 从Properties中读取int值
	 * @param props Properties实例
	 * @param name 变量名
	 * @param defaultValue 缺省值
	 * @param noParent 不取父节点
	 * @return int值
	 * @see #setInt(Properties, String, int)
	 * 
	 * @since 1.0.16
	 * 
	 */	
	public static int getInt(Properties props,String name,int defaultValue,boolean noParent){
		String sInt = props.GetValue(name,"",true,noParent);
		if (sInt.length() <= 0){
			return defaultValue;
		}
		try {
			return Integer.parseInt(sInt);
		}catch (NumberFormatException ex){
			return defaultValue;
		}			
	}
	
	/**
	 * 向Properties设置long值
	 * @param props Properties实例
	 * @param name 变量名
	 * @param value 变量值
	 */
	public static void setLong(Properties props,String name,long value){
		if (StringUtils.isNotEmpty(name)){
			props.SetValue(name, String.valueOf(value));
		}
	}	
	/**
	 * 从Properties中读取long值
	 * @param props Properties实例
	 * @param name 变量名
	 * @param defaultValue 缺省值
	 * @return long值
	 * @see #setLong(Properties, String, long)
	 * 
	 * @since 1.0.8 <br>
	 * - 修改为可从父节点取变量 <br>
	 */	
	public static long getLong(Properties props,String name,long defaultValue){
		String sLong = props.GetValue(name,"",true,false);
		if (sLong.length() <= 0){
			return defaultValue;
		}
		try{
			return Long.parseLong(sLong);
		}catch (NumberFormatException ex){
			return defaultValue;
		}
	}
	
	/**
	 * 从Properties中读取long值
	 * @param props Properties实例
	 * @param name 变量名
	 * @param defaultValue 缺省值
	 * @param noParent 不取父节点
	 * @return long值
	 * @see #setLong(Properties, String, long)
	 * @since 1.0.16 
	 */		
	public static long getLong(Properties props,String name,long defaultValue,boolean noParent){
		String sLong = props.GetValue(name,"",true,noParent);
		if (sLong.length() <= 0){
			return defaultValue;
		}
		try{
			return Long.parseLong(sLong);
		}catch (NumberFormatException ex){
			return defaultValue;
		}		
	}
	
	/**
	 * 向Properties设置double值
	 * @param props Properties实例
	 * @param name 变量名
	 * @param value 变量值
	 *
	 * @since 1.4.3
	 */
	public static void setDouble(Properties props,String name,double value){
		if (StringUtils.isNotEmpty(name)){
			props.SetValue(name, String.valueOf(value));
		}
	}	
	/**
	 * 从Properties中读取double值
	 * @param props Properties实例
	 * @param name 变量名
	 * @param defaultValue 缺省值
	 * @return double值
	 * @see #setDouble(Properties, String, double)
	 * 
	 * @since 1.4.3
	 */	
	public static double getDouble(Properties props,String name,double defaultValue){
		String sLong = props.GetValue(name,"",true,false);
		if (sLong.length() <= 0){
			return defaultValue;
		}
		try{
			return Double.parseDouble(sLong);
		}catch (NumberFormatException ex){
			return defaultValue;
		}
	}
	
	/**
	 * 从Properties中读取double值
	 * @param props Properties实例
	 * @param name 变量名
	 * @param defaultValue 缺省值
	 * @param noParent 不取父节点
	 * @return double值
	 * @see #setDouble(Properties, String, double)
	 * 
	 * @since 1.4.3
	 */		
	public static double getDouble(Properties props,String name,double defaultValue,boolean noParent){
		String sLong = props.GetValue(name,"",true,noParent);
		if (sLong.length() <= 0){
			return defaultValue;
		}
		try{
			return Double.parseDouble(sLong);
		}catch (NumberFormatException ex){
			return defaultValue;
		}		
	}	
	
	/**
	 *  从Properties中获取boolean值
	 * @param props Properties实例
	 * @param name 变量名
	 * @param defaultValue 缺省值
	 * @return boolean值
	 * @see #setBoolean(Properties, String, boolean)
	 * @since 1.0 
	 * 
	 * @since 1.0.8 <br>
	 * - 修改为可从父节点取变量 <br>
	 */
	public static boolean getBoolean(Properties props,String name,boolean defaultValue){
		String sBoolean = props.GetValue(name,BooleanUtils.toStringTrueFalse(defaultValue),true,false);
		return BooleanUtils.toBoolean(sBoolean);
	}

	/**
	 *  从Properties中获取boolean值
	 * @param props Properties实例
	 * @param name 变量名
	 * @param defaultValue 缺省值
	 * @param noParent 不取父节点属性
	 * @return boolean值
	 * @see #setBoolean(Properties, String, boolean)
	 * @since 1.0.6 
	 */	
	public static boolean getBoolean(Properties props,String name,boolean defaultValue,boolean noParent){
		String sBoolean = props.GetValue(name,BooleanUtils.toStringTrueFalse(defaultValue),true,noParent);
		return BooleanUtils.toBoolean(sBoolean);
	}
	
	/**
	 * 向Properties中设置boolean值
	 * @param props Properties实例
	 * @param name 变量名
	 * @param value 变量值
	 * @see #getBoolean(Properties, String, boolean)
	 * @since 1.0
	 */
	public static void setBoolean(Properties props,String name,boolean value){
		if (StringUtils.isNotEmpty(name)){
			if (value){
				props.SetValue(name, BOOL_TRUE);
			}else{
				props.SetValue(name, BOOL_FALSE);
			}
		}
	}
	
	/**
	 * 从Properties中获取Font值
	 * @param props Properties实例
	 * @param name 变量名
	 * @param defaultValue 缺省值
	 * @return Font值
	 * @see #setFont(Properties, String, Font)
	 * @since 1.0
	 * 
	 * @since 1.0.8 <br>
	 * - 修改为可从父节点取变量 <br>
	 */
	public static Font getFont(Properties props,String name,Font defaultValue){
		String sFont = props.GetValue(name,"",true,false);
		if (sFont.length() <= 0){
			return defaultValue;
		}
		Font f = null;
		try{
			f = Font.decode(sFont);
		}catch (NumberFormatException t){
			f = defaultValue;
		}
		return f;
	}
	
	/**
	 * 从Properties中获取Font值
	 * @param props Properties实例
	 * @param name 变量名
	 * @param defaultValue 缺省值
	 * @param noParent 不取父节点
	 * @return Font值
	 * @see #setFont(Properties, String, Font)
	 * @since 1.0.16
	 */
	public static Font getFont(Properties props,String name,Font defaultValue,boolean noParent){
		String sFont = props.GetValue(name,"",true,noParent);
		if (sFont.length() <= 0){
			return defaultValue;
		}
		Font f = null;
		try{
			f = Font.decode(sFont);
		}catch (NumberFormatException t){
			f = defaultValue;
		}
		return f;
	}	
	
	/**
	 * 向Properties中设置Font值
	 * @param props Properties实例
	 * @param name 变量名
	 * @param f font值
	 * @since 1.0
	 * @see #getFont(Properties, String, Font)
	 */
	public static void setFont(Properties props,String name,Font f){
		if (StringUtils.isNotEmpty(name) && f != null){
			String fontName = f.getFamily();
			int style = f.getStyle();
			String styleName;
			switch (style) {
			case Font.BOLD:
				styleName = "BOLD";
				break;
			case Font.ITALIC:
				styleName = "ITALIC";
				break;
			case Font.BOLD | Font.ITALIC:
				styleName = "BOLDITALIC";
				break;
			default:
				styleName = "PLAIN";
			}
			int size = f.getSize();
			
			props.SetValue(name,fontName + "-" + styleName + "-" + size);
		}
	}
	
	/**
	 * 从Properties中获取Color值
	 * @param props Properties实例
	 * @param name 变量名
	 * @param defaultValue 缺省值
	 * @return Color值
	 * @see #setColor(Properties, String, Color)
	 * @since 1.0
	 * 
	 * @since 1.0.8 <br>
	 * - 修改为可从父节点取变量 <br>
	 */
	public static Color getColor(Properties props,String name,Color defaultValue){
		String sColor = props.GetValue(name,"",true,false);
		if (sColor.length() <= 0)
			return defaultValue;
		
		Color color = null;
		try{
			color = Color.decode(sColor);
		}catch (NumberFormatException ex){
			color = defaultValue;
		}
		return color;
	}
	
	/**
	 * 从Properties中获取Color值
	 * @param props Properties实例
	 * @param name 变量名
	 * @param defaultValue 缺省值
	 * @param noParent 不取父节点属性
	 * @return Color值
	 * @see #setColor(Properties, String, Color)
	 * @since 1.0.16
	 */
	public static Color getColor(Properties props,String name,Color defaultValue,boolean noParent){
		String sColor = props.GetValue(name,"",true,noParent);
		if (sColor.length() <= 0)
			return defaultValue;
		
		Color color = null;
		try{
			color = Color.decode(sColor);
		}catch (NumberFormatException ex){
			color = defaultValue;
		}
		return color;
	}
	
	/**
	 * 向Properties中设置Color值
	 * @param props Properties实例
	 * @param name 变量名
	 * @param value Color值
	 * @since 1.0
	 * @see #getColor(Properties, String, Color)
	 */
	public static void setColor(Properties props,String name,Color value){
		if (StringUtils.isNotEmpty(name) && value != null){
			String r = Integer.toHexString(value.getRed());
			String g = Integer.toHexString(value.getGreen());
			String b = Integer.toHexString(value.getBlue());
			
			if (r.length() == 1){
				r = "0" + r;
			}
			if (g.length() == 1){
				g = "0" + g;
			}
			if (b.length() == 1){
				b = "0" + b;
			}
			
			String sColor =  "#" + r + g + b;
			props.SetValue(name, sColor);
		}
	}
	
	/**
	 * 从Properties中读取Rectangle值
	 * @param props Properties实例
	 * @param name 变量名
	 * @param defaultValue 缺省值
	 * @return Rectangle值
	 * @since 1.0
	 * @see #setRectangle(Properties, String, Rectangle)
	 * @since 1.0.8 <br>
	 * - 修改为可从父节点取变量 <br>
	 */
	public static Rectangle getRectangle(Properties props,String name,Rectangle defaultValue){
		String bounds = props.GetValue(name,"",true,false);
		
		if (bounds == null || bounds.length() <= 0)
			return defaultValue;
		
		String [] bound = bounds.split(",");
		if (bound.length != 4)
			return defaultValue;
		
		try {
			int x = Integer.parseInt(bound[0]);
			int y = Integer.parseInt(bound[1]);
			int width = Integer.parseInt(bound[2]);
			int height = Integer.parseInt(bound[3]);
			return new Rectangle(x, y, width, height);
		} catch (NumberFormatException t) {
			return defaultValue;
		}
	}
	
	/**
	 * 从Properties中读取Rectangle值
	 * @param props Properties实例
	 * @param name 变量名
	 * @param defaultValue 缺省值
	 * @param noParent 不取父节点
	 * @return Rectangle值
	 * @since 1.0.16
	 * @see #setRectangle(Properties, String, Rectangle)
	 */
	public static Rectangle getRectangle(Properties props,String name,Rectangle defaultValue,boolean noParent){
		String bounds = props.GetValue(name,"",true,noParent);
		
		if (bounds == null || bounds.length() <= 0)
			return defaultValue;
		
		String [] bound = bounds.split(",");
		if (bound.length != 4)
			return defaultValue;
		
		try {
			int x = Integer.parseInt(bound[0]);
			int y = Integer.parseInt(bound[1]);
			int width = Integer.parseInt(bound[2]);
			int height = Integer.parseInt(bound[3]);
			return new Rectangle(x, y, width, height);
		} catch (NumberFormatException t) {
			return defaultValue;
		}
	}	
	
	/**
	 * 向Properties中设置Rectangle值
	 * @param props Properties实例
	 * @param name 变量名
	 * @param rect 变量值
	 * @see #getRectangle(Properties, String, Rectangle)
	 * @since 1.0
	 */
	public static void setRectangle(Properties props,String name,Rectangle rect){
		if (StringUtils.isNotEmpty(name) && rect != null){
			String value = rect.x + "," + rect.y + "," + rect.width + "," + rect.height;
			props.SetValue(name, value);
		}
	}
	
	/**
	 * 从Properties中获取Dimension值
	 * @param props Properties实例
	 * @param name 变量名
	 * @param defaultValue 缺省值
	 * @return 变量值
	 * @see #setDimension(Properties, String, Dimension)
	 * @since 1.0
	 * @since 1.0.8 
	 * - 修改为可从父节点取变量
	 */
	public static Dimension getDimension(Properties props,String name,Dimension defaultValue){
		String bounds = props.GetValue(name,"",true,false);
		
		if (bounds == null || bounds.length() <= 0)
			return defaultValue;
		
		String [] bound = bounds.split(",");
		if (bound.length != 2)
			return defaultValue;
		
		try {
			int width = Integer.parseInt(bound[0]);
			int height = Integer.parseInt(bound[1]);
			return new Dimension(width, height);
		} catch (NumberFormatException t) {
			return defaultValue;			
		}
	}
	
	/**
	 * 从Properties中获取Dimension值
	 * @param props Properties实例
	 * @param name 变量名
	 * @param defaultValue 缺省值
	 * @param noParent
	 * @return 变量值
	 * @see #setDimension(Properties, String, Dimension)
	 * @since 1.0.16
	 */
	public static Dimension getDimension(Properties props,String name,Dimension defaultValue,boolean noParent){
		String bounds = props.GetValue(name,"",true,noParent);
		
		if (bounds == null || bounds.length() <= 0)
			return defaultValue;
		
		String [] bound = bounds.split(",");
		if (bound.length != 2)
			return defaultValue;
		
		try {
			int width = Integer.parseInt(bound[0]);
			int height = Integer.parseInt(bound[1]);
			return new Dimension(width, height);
		} catch (NumberFormatException t) {
			return defaultValue;			
		}
	}
		
	
	/**
	 * 向Properties中设置Dimension值
	 * @param props Properties实例
	 * @param name 变量名
	 * @param dim 变量值
	 * @see #getDimension(Properties, String, Dimension)
	 * @since 1.0
	 */
	public static void setDimension(Properties props,String name,Dimension dim){
		if (StringUtils.isNotEmpty(name) && dim != null){
			String value = dim.width + "," + dim.height;
			props.SetValue(name, value);
		}
	}
	
	/**
	 * 从Properties中获取Insets值
	 * @param props Properties实例
	 * @param name 变量名
	 * @param defaultValue 缺省值
	 * @return Insets值
	 * @see #setInsets(Properties, String, Insets)
	 * @since 1.0
	 * @since 1.0.8 <br>
	 * - 修改为可从父节点取变量 <br>
	 */
	public static Insets getInsets(Properties props,String name,Insets defaultValue){
		String insets = props.GetValue(name,"",true,false);
		if (insets == null || insets.length() <= 0){
			return defaultValue;
		}
		
		String[] inset = insets.split(",");
		if (inset.length != 4){
			return defaultValue;
		}
		
		try {
			int top = Integer.parseInt(inset[0]);
			int left = Integer.parseInt(inset[1]);
			int bottom = Integer.parseInt(inset[2]);
			int right = Integer.parseInt(inset[3]);
			return new Insets(top,left,bottom,right);
		}catch (NumberFormatException t){
			return defaultValue;			
		}
	}
	
	
	/**
	 * 从Properties中获取Insets值
	 * @param props Properties实例
	 * @param name 变量名
	 * @param defaultValue 缺省值
	 * @param noParent 不取父节点属性
	 * @return Insets值
	 * @see #setInsets(Properties, String, Insets)
	 * @since 1.0.16
	 */
	public static Insets getInsets(Properties props,String name,Insets defaultValue,boolean noParent){
		String insets = props.GetValue(name,"",true,noParent);
		if (insets == null || insets.length() <= 0){
			return defaultValue;
		}
		
		String[] inset = insets.split(",");
		if (inset.length != 4){
			return defaultValue;
		}
		
		try {
			int top = Integer.parseInt(inset[0]);
			int left = Integer.parseInt(inset[1]);
			int bottom = Integer.parseInt(inset[2]);
			int right = Integer.parseInt(inset[3]);
			return new Insets(top,left,bottom,right);
		}catch (NumberFormatException t){
			return defaultValue;			
		}
	}	
	
	/**
	 * 向Properties中设置Insets值
	 * @param props Properties实例
	 * @param name 变量名
	 * @param insets 变量值
	 * @since 1.0
	 * @see #getInsets(Properties, String, Insets)
	 */
	public static void setInsets(Properties props,String name,Insets insets){
		if (StringUtils.isNotEmpty(name) && insets != null){
			String value = insets.top + "," + insets.left + "," + insets.bottom + "," + insets.right;
			props.SetValue(name, value);
		}
	}

	public static String getAttribute(Properties props,String name,String defaultValue){
		return props.GetValue(name,defaultValue,true,true);
	}
	
	public static void setAttribute(Properties props,String name,String value){
		if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(value)){
			props.SetValue(name,value);
		}
	}	
}
