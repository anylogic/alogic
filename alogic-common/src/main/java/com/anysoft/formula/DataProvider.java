package com.anysoft.formula;


/**
 * Data provider
 * 
 * <br>
 * <code>DataProvider</code> 用于在公式计算时提供变量值。<br>
 * 
 * ### 上下文的使用<br>
 * 
 * 在某些场合使用上下文，可以显著地提升处理效率。<br>
 * 
 * 例如：现需要对一个关系表进行扫描计算，关系表的Column即为变量，在公式定义中是用列名来作为变量。
 * 传统的思路是：先由列名找到列索引(定义为A)，再由列索引找到列值(定义为B),
 * 如果有n行记录，计算的次数将是(A+B)*n。<br>
 * 
 * 进一步分析发现，对于所有行的计算，A是不变的，因此就有了上下文的设计。
 * 计算框架调用一次{@link #getContext(String)}获取上下文，再多次调用{@link #getValue(String, Object,String)},
 * 这样提升了效率，总的计算次数是A + B*n。<br>
 * 
 * @author duanyy
 * @version 1.0.0
 * @version 1.0.1
 *     + {@link DataProvider#getValue(String, Object, String) getValue}函数的返回类型修改为String
 *     + {@link DataProvider#getValue(String, Object, String) getValue}函数增加缺省值参数
 *     
 * @version 1.6.3.41 [20150820 duanyy] <br>
 * - 增加Env和SystemProperties的实现<br>
 * 
 * @version 1.6.4.21 [20151229 duanyy] <br>
 * - 根据sonar建议优化代码 <br>
 */
public interface DataProvider {
	/**
	 * 获取指定变量的值
	 * 
	 * @param varName 变量名
	 * @param context 上下文
	 * @param defaultValue 缺省值
	 * @return 变量值
	 */
	public String getValue(String varName,Object context,String defaultValue);
	
	/**
	 * 创建变量的上下文
	 * @param varName 变量名
	 * @return context 上下文对象
	 */
	public Object getContext(String varName);
	
	/**
	 * 封装SystemProperties的DataProvider
	 * @author duanyy
	 * @since 1.6.3.41
	 */
	public static class SystemPropertiesDataProvider implements DataProvider{
		@Override
		public String getValue(String varName, Object context,
				String defaultValue) {
			return System.getProperty(varName, defaultValue);
		}
		@Override
		public Object getContext(String varName) {
			return this;
		}
	}
	
	/**
	 * 封装Env的DataProvider
	 * @author duanyy
	 * @since 1.6.3.41
	 */
	public static class EnvDataProvider implements DataProvider{
		@Override
		public String getValue(String varName, Object context,
				String defaultValue) {
			String value = System.getenv(varName);
			if (value == null || value.length() <= 0){
				value = defaultValue;
			}
			return value;
		}

		@Override
		public Object getContext(String varName) {
			return this;
		}
	}
}
