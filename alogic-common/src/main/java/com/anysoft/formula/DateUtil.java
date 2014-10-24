package com.anysoft.formula;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * 日期工具类
 * @author duanyy
 *
 */
public class DateUtil {
	/**
	 * 按照模版解析日期
	 * <p>如果_instance为空,则会创建新的日期实例;如果不为空,则将解析结果填入指定的实例.</p>
	 * @param _value 字符串型日期
	 * @param _pattern 模版
	 * @param _instance 日期实例
	 * @return 日期实例
	 */
	public static Date parseDate(String _value,String _pattern,Date _instance){
		if (_value.length() < _pattern.length()) return _instance;
		int _year = 0;
		int _month = 0;
		int _day = 0;
		int _hour = 0;
		int _minute = 0;
		int _second = 0;
		//to find the year
		int _index = 0;
		_index = _pattern.indexOf("yyyy");
		if (_index >= 0){
			//found
			_year = Integer.parseInt(_value.substring(_index,_index + 4));
		}else{
			_index = _pattern.indexOf("yy");
			if (_index >= 0){
				_year = Integer.parseInt(_value.substring(_index,_index + 2));
				_year += 2000;
			}else{
				return _instance;
			}
		}
		
		//to find the month
		_index = _pattern.indexOf("MM");
		if (_index >= 0){
			_month = Integer.parseInt(_value.substring(_index,_index + 2));
			_month -= 1;
		}else{
			return _instance;
		}
		
		//to find the day
		_index = _pattern.indexOf("dd");
		if (_index >= 0){
			_day = Integer.parseInt(_value.substring(_index,_index + 2));
		}else{
			return _instance;
		}
		
		//to find the hour
		_index = _pattern.indexOf("JJ");
		if (_index >= 0){
			_hour = Integer.parseInt(_value.substring(_index,_index + 2));
		}else{
			_index = _pattern.indexOf("HH");
			if (_index >= 0){
				_hour = Integer.parseInt(_value.substring(_index,_index + 2));
			}else{
				_hour = 0;
			}
		}

		//to find the minute
		_index = _pattern.indexOf("mm");
		if (_index >= 0){
			_minute = Integer.parseInt(_value.substring(_index,_index + 2));
		}else{
			_minute = 0;
		}
		
		//to find the second
		_index = _pattern.indexOf("ss");
		if (_index >= 0){
			_second = Integer.parseInt(_value.substring(_index,_index + 2));
		}else{
			_second = 0;
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(_year, _month, _day, _hour, _minute,_second);
		if (_instance != null){
			_instance.setTime(calendar.getTimeInMillis());
			return _instance;
		}
		return calendar.getTime();
	}
	
	/**
	 * 按照模版解析日期
	 * @param _value 字符串型的日期
	 * @param _pattern 模版
	 * @return 日期实例
	 */
	public static Date parseDate(String _value,String _pattern){
		return parseDate(_value,_pattern,null);
	}
	
	/**
	 * 按照缺省模版解析日期
	 * 
	 * <p>缺省模版为yyyyMMddHHmmss</p>
	 * 
	 * @param _value 字符串型的日期
	 * @return 日期实例
	 */
	public static Date parseDate(String _value){
		return parseDate(_value,"yyyyMMddHHmmss");
	}
	
	/**
	 * 日期格式化工具
	 */
	protected static SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd:HH");
	
	/**
	 * 按照模板格式化日期
	 * @param _date 日期的实例
	 * @param _pattern 模板
	 * @return 格式化结果
	 */
	public static String formatDate(Date _date,String _pattern){
		if (_date == null) return "";
		if (formater == null){
			formater = new SimpleDateFormat("yyyyMMdd:HH");
		}
	    formater.applyPattern(_pattern);
	    return formater.format(_date);	
	}
	
	/**
	 * 将制定字符串型日期按照模板转化为基于其他模板的字符串型日期
	 * @param value 待转换的字符串型日期
	 * @param input_format 原始的模板
	 * @param output_format 新的模板
	 * @return 转化为的结果
	 */
	public static String transform(String value,String input_format,String output_format){
		Date _date = parseDate(value,input_format);
		if (_date == null) return "";
		return formatDate(_date,output_format);		
	}
}
