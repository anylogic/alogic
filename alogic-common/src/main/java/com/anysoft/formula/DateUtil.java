package com.anysoft.formula;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * 日期工具类
 * @author duanyy
 * @version 1.6.4.21 [20151229 duanyy] <br>
 * - 根据sonar建议优化代码 <br>
 */
public class DateUtil {	
	private DateUtil(){
		
	}
	
	
	/**
	 * 按照模版解析日期
	 * <p>如果_instance为空,则会创建新的日期实例;如果不为空,则将解析结果填入指定的实例.</p>
	 * @param value 字符串型日期
	 * @param pattern 模版
	 * @param instance 日期实例
	 * @return 日期实例
	 */
	public static Date parseDate(String value,String pattern,Date instance){ // NOSONAR
		if (value.length() < pattern.length()) 
			return instance;
		int year,month,day,hour,minute,second;
		
		//to find the year
		int index = pattern.indexOf("yyyy");
		if (index >= 0){
			//found
			year = Integer.parseInt(value.substring(index,index + 4));
		}else{
			index = pattern.indexOf("yy");
			if (index >= 0){
				year = Integer.parseInt(value.substring(index,index + 2));
				year += 2000;
			}else{
				return instance;
			}
		}
		
		//to find the month
		index = pattern.indexOf("MM");
		if (index >= 0){
			month = Integer.parseInt(value.substring(index,index + 2));
			month -= 1;
		}else{
			return instance;
		}
		
		//to find the day
		index = pattern.indexOf("dd");
		if (index >= 0){
			day = Integer.parseInt(value.substring(index,index + 2));
		}else{
			return instance;
		}
		
		//to find the hour
		index = pattern.indexOf("JJ");
		if (index >= 0){
			hour = Integer.parseInt(value.substring(index,index + 2));
		}else{
			index = pattern.indexOf("HH");
			if (index >= 0){
				hour = Integer.parseInt(value.substring(index,index + 2));
			}else{
				hour = 0;
			}
		}

		//to find the minute
		index = pattern.indexOf("mm");
		if (index >= 0){
			minute = Integer.parseInt(value.substring(index,index + 2));
		}else{
			minute = 0;
		}
		
		//to find the second
		index = pattern.indexOf("ss");
		if (index >= 0){
			second = Integer.parseInt(value.substring(index,index + 2));
		}else{
			second = 0;
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day, hour, minute,second);
		if (instance != null){
			instance.setTime(calendar.getTimeInMillis());
			return instance;
		}
		return calendar.getTime();
	}
	
	/**
	 * 按照模版解析日期
	 * @param value 字符串型的日期
	 * @param pattern 模版
	 * @return 日期实例
	 */
	public static Date parseDate(String value,String pattern){
		return parseDate(value,pattern,null);
	}
	
	/**
	 * 按照缺省模版解析日期
	 * 
	 * <p>缺省模版为yyyyMMddHHmmss</p>
	 * 
	 * @param value 字符串型的日期
	 * @return 日期实例
	 */
	public static Date parseDate(String value){
		return parseDate(value,"yyyyMMddHHmmss");
	}

	
	/**
	 * 按照模板格式化日期
	 * @param date 日期的实例
	 * @param pattern 模板
	 * @return 格式化结果
	 */
	public static String formatDate(Date date,String pattern){
		if (date == null) 
			return "";
		
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
	    return formatter.format(date);	
	}
	
	/**
	 * 将制定字符串型日期按照模板转化为基于其他模板的字符串型日期
	 * @param value 待转换的字符串型日期
	 * @param inFormat 原始的模板
	 * @param outFormat 新的模板
	 * @return 转化为的结果
	 */
	public static String transform(String value,String inFormat,String outFormat){
		Date date = parseDate(value,inFormat);
		if (date == null) 
			return "";
		return formatDate(date,outFormat);		
	}
}
