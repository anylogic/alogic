package com.logicbus.dbcp.processor;

import java.util.ArrayList;
import java.util.List;

import com.anysoft.formula.Expression;
import com.anysoft.formula.Parser;
import com.anysoft.util.Properties;

/**
 * SQL预处理器
 * @author duanyy
 * @since 1.6.3.30
 */
final public class Preprocessor implements BindedListener{
	
	/**
	 * 语句块
	 */
	protected List<Object> segments = new ArrayList<>();
	
	public void bind(Object value) {
	
	}	
	
	public void compile(String sql){
		//清除前一次编译结果
		segments.clear();
		compile0(sql);
	}
	
	private void compile0(String sql){
		int begin = sql.indexOf("#{");
		if (begin < 0){
			segments.add(sql);
		}else{
			String segment = sql.substring(0,begin);
			if (isNotNull(segment)){
				segments.add(segment);
			}
			int end = sql.indexOf("}", begin);
			if (end < 0){
				String formula = sql.substring(begin + 2);
				compile1(formula);
			}else{
				String formula = sql.substring(begin + 2,end);
				//处理公事
				compile1(formula);
				String left = sql.substring(end + 1);
				if (isNotNull(left)){
					compile0(left);
				}
			}
		}
	}
	
	private void compile1(String formula){
		Parser parser = new Parser();
		
		Expression expr = parser.parse(formula);
		
		if (expr != null){
			segments.add(expr);
		}
	}
	
	private boolean isNotNull(String value){
		return value != null && value.length() > 0;
	}
	
	public Result process(Properties p){
		return null;
	}
	
	private void report(){
		for (Object o:segments){
			System.out.println(o.toString());
		}
	}
	
	public static interface Result{
		public String getSQL();
		public Object [] getBindedObject();
	}
	
	public static void main(String [] args){
		String sql = "update a set name=#{notEmpty('name','name=' + value('name'))}where cust_id=#{value('id')}";
		Preprocessor processor = new Preprocessor();
		
		processor.compile(sql);
		
		processor.report();
	}


}
