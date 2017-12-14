package com.anysoft.util;

import java.lang.reflect.Constructor;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

/**
 * 对象的工厂类
 * @author duanyy
 *
 * @param <OBJECT> 对象的类名
 * 
 * @see XMLConfigurable
 * 
 * @version 1.0.9 [20140414 duanyy] <br>
 * - 增加{@link com.anysoft.util.Factory#newInstance(String, Properties) newInstance(String, Properties)}方法，使之能够通过Properties直接初始化.<br>
 * 
 * @version 1.0.16 [20140620 duanyy] <br>
 * - 从Settings中提取classLoader缺省值.<br>
 * 
 * @version 1.3.5 [20140819 duanyy] <br>
 * - 简化classLoader获取方法
 * 
 * @version 1.6.4.27 [20160125 duanyy] <br>
 * - 根据sonar建议优化代码 <br>
 * 
 * @version 1.6.4.29 [20160126 duanyy] <br>
 * - 修正取构造器时的异常 <br>
 */
public class Factory<OBJECT> {
	
	/**
	 * 创建所需的ClassLoader
	 */
	protected ClassLoader classLoader = null;
	
	/**
	 * 缺省构造函数
	 */
	public Factory(){
		// nothing to do
	}
	
	/**
	 * 定制ClassLoader的构造函数
	 * @param cl ClassLoader实例
	 */
	public Factory(ClassLoader cl){
		classLoader = cl;
	}
	
	/**
	 * 创建新的对象实例
	 * @param xml 创建对象所需的XML参数
	 * @param props 所需的变量集
	 * @return 对象实例
	 */
	public OBJECT newInstance(Element xml,Properties props){
		return newInstance(xml,props,"module");
	}

	/**
	 * 创建新的对象实例
	 * <p>如果对象为{@link XMLConfigurable}的实例，则调用{@link XMLConfigurable#configure(Element, Properties)}来初始化对象.</p>
	 * @param xml 创建对象所需的XML参数
	 * @param props 所需的变量集
	 * @param moduleAttr 表示module属性的属性名称
	 * @return 对象实例
	 * @see XMLConfigurable
	 * @see #newInstance(String)
	 */
	public OBJECT newInstance(Element xml,Properties props,String moduleAttr){
		String module = xml.getAttribute(moduleAttr);
		if (StringUtils.isEmpty(module)){
			throw new BaseException(Factory.class.getName(),
					"Can not find attr in the element,attr id = " + moduleAttr);
		}
		
		OBJECT instance = newInstance(module);
		
		if (instance instanceof XMLConfigurable){
			((XMLConfigurable)instance).configure(xml, props);
		}
		
		return instance;
	}	
	
	/**
	 * 创建新的对象实例
	 * <p>如果对象为{@link XMLConfigurable}的实例，则调用{@link XMLConfigurable#configure(Element, Properties)}来初始化对象.</p>
	 * @param xml 创建对象所需的XML参数
	 * @param props 所需的变量集
	 * @param moduleAttr 表示module属性的属性名称
	 * @param dftClass 缺省的类
	 * @return object
	 * 
	 * @since 1.3.5
	 */
	public OBJECT newInstance(Element xml,Properties props,String moduleAttr,String dftClass){
		String module = xml.getAttribute(moduleAttr);
		if (StringUtils.isEmpty(module)){
			module = dftClass;
		}
		
		OBJECT instance = newInstance(module);
		
		if (instance instanceof XMLConfigurable){
			((XMLConfigurable)instance).configure(xml, props);
		}
		
		return instance;
	}
	
	/**
	 * 按照指定的module来创建对象实例
	 * <p>module不完全是对象的类名，在使用之前需调用{@link #getClassName(String)}进行转换。如果module不使用类名的话，
	 * 可以override函数{@link #getClassName(String)}将module转换为类名.</p>
	 * <p>在某些时候需要选定ClassLoader来创建实例，需定制{@link #classLoader}.</p>
	 * @param module 类型或者类名
	 * @return 对象实例
	 */
	@SuppressWarnings("unchecked")
	public OBJECT newInstance(String module){
		String className = getClassName(module);
		try {
			if (classLoader == null){
				classLoader = Settings.getClassLoader();
			}
			return (OBJECT)classLoader.loadClass(className).newInstance();
		} catch (Exception ex){
			throw new BaseException(Factory.class.getName(),
					"Can not create instance of " + className,ex);
		}
	}
	
	/**
	 * 按照指定的module来创建对象实例
	 * 
	 * <br>
	 * 按照指定的module来创建对象实例,如果对象是构造函数为object(Properties)，则采用Properties来构造实例。
	 * 
	 * @param module 对象实例
	 * @param props 初始化参数
	 * @return 对象实例
	 * @throws BaseException
	 * 
	 * @since 1.0.9
	 * @since 1.6.3.37 [duanyy 20150804]<br>
	 * - 支持Configurable的自动初始化 <br>
	 * 
	 */
	@SuppressWarnings("unchecked")
	public OBJECT newInstance(String module,Properties props){
		String className = getClassName(module);
		try {
			if (classLoader == null){
				classLoader = Settings.getClassLoader();
			}
			Class<?> clazz = classLoader.loadClass(className);
			Constructor<?> constructor = getConstructor(clazz);
			if (constructor != null){
				return (OBJECT)constructor.newInstance(new Object[]{props});
			}else{
				OBJECT instance = (OBJECT)clazz.newInstance();
				
				if (instance instanceof Configurable){
					((Configurable)instance).configure(props);
				}
				
				return instance;
			}
		} catch (Exception ex){
			throw new BaseException(Factory.class.getName(),
					"Can not create instance of " + className,ex);
		}
	}
	
	private Constructor<?> getConstructor(Class<?> clazz){
		try {
			return clazz.getConstructor(new Class[]{Properties.class});
		}catch (NoSuchMethodException ex){ // NOSONAR
			return null;
		}
	}
	
	/**
	 * 将module转化为全路径类名
	 * @param module module名
	 * @return 全路径类名
	 */
	public String getClassName(String module){
		return module;
	}
}
