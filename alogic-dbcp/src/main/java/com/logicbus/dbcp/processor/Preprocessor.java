package com.logicbus.dbcp.processor;

import java.util.ArrayList;
import java.util.List;

import com.anysoft.formula.DefaultFunctionHelper;
import com.anysoft.formula.ExprValue;
import com.anysoft.formula.Expression;
import com.anysoft.formula.FunctionHelper;
import com.anysoft.formula.Parser;
import com.anysoft.util.DefaultProperties;
import com.anysoft.util.Properties;

/**
 * SQL预处理器
 * @author duanyy
 * @since 1.6.3.30
 * 
 * @version 1.6.11.15 [20180206 duanyy] <br>
 * - 修正Preprocessor的并发性问题 <br>
 * 
 * @version 1.6.11.15 [20180206 duanyy] <br>
 * - 修正Preprocessor的并发性问题 <br>
 * 
 * @version 1.6.11.36 [20180613 duanyy] <br>
 * - 支持对sql语句进行transform<br>
 */
final public class Preprocessor implements BindedListener{
	
	/**
	 * 语句块
	 */
	protected List<Object> segments = new ArrayList<Object>();
	
	private static ThreadLocal<List<Object>> bindedData = new ThreadLocal<List<Object>>();
	
	protected FunctionHelper fh = null;
	
	protected boolean transform = false;
	
	public Preprocessor(){
		this(false);
	}
	
	public Preprocessor(String sql){
		this(false,sql);
	}
	
	public Preprocessor(boolean transform){
		this.transform = transform;
		fh = new DefaultFunctionHelper(new PluginManager(this));
	}
	
	public Preprocessor(boolean transform,String sql){
		this.transform = transform;
		fh = new DefaultFunctionHelper(new PluginManager(this));
		compile(sql);
	}	
	
	public void bind(Object value) {
		List<Object> binded = bindedData.get();
		if (binded != null){
			binded.add(value);
		}
	}
	
	/**
	 * 预处理SQL语句
	 * @param p 变量集
	 * @param binded 绑定对象列表 
	 * @return 处理之后的SQL语句
	 */
	public String process(Properties p,List<Object> binded){
		StringBuffer sb = new StringBuffer();
		try {
			if (binded != null && !binded.isEmpty()){
				binded.clear();			
			}
			bindedData.set(binded);			
			for (Object o:segments){
				if (o instanceof String){
					sb.append(transform?p.transform((String)o):(String)o);
				}else{
					if (o instanceof Expression){
						Expression expr = (Expression)o;
						ExprValue value = expr.getValue(p);
						if (value != null){
							sb.append(value.toString());
						}
					}
				}
			}
			return sb.toString();
		}finally{
			bindedData.remove();
		}
	}
	
	/**
	 * 预编译指定的SQL语句
	 *  
	 * @param sql SQL语句
	 */
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
		Parser parser = new Parser(fh);
		Expression expr = parser.parse(formula);
		
		if (expr != null){
			segments.add(expr);
		}
	}
	
	private boolean isNotNull(String value){
		return value != null && value.length() > 0;
	}
	
	public static void main(String [] args){
		
		Properties p = new DefaultProperties();
		
		p.SetValue("id", "duanyy");
		p.SetValue("name", "dyy");
		p.SetValue("type", "shop");
		
		String sql = "update core_cust set #{not_nvl(name ,'name='+bind('name'))} #{not_nvl(type,',type='+bind('type'))} where cust_id=#{bind('ids')}";

		Preprocessor processor = new Preprocessor(sql);
		
		List<Object> data = new ArrayList<Object>();
		
		System.out.println("The sql is " + processor.process(p, data));
		System.out.println("The binded data is ");
		for (Object o:data){
			System.out.println(o.toString());
		}
	}


}
