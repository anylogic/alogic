package com.anysoft.util;

import java.lang.reflect.Constructor;

import org.w3c.dom.Element;

/**
 * 对象的工厂类
 * @author duanyy
 *
 * @param <object> 对象的类名
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
 */
public class Factory<object> {
	
	/**
	 * 创建所需的ClassLoader
	 */
	protected ClassLoader classLoader = null;
	/**
	 * 缺省构造函数
	 */
	public Factory(){
		
	}
	/**
	 * 定制ClassLoader的构造函数
	 * @param _cl ClassLoader实例
	 */
	public Factory(ClassLoader _cl){
		classLoader = _cl;
	}
	
	/**
	 * 创建新的对象实例
	 * @param _xml 创建对象所需的XML参数
	 * @param _properties 所需的变量集
	 * @return 对象实例
	 * @throws BaseException 
	 * @see {@link #newInstance(Element, Properties, String)}
	 */
	public object newInstance(Element _xml,Properties _properties) throws BaseException{
		return newInstance(_xml,_properties,"module");
	}

	/**
	 * 创建新的对象实例
	 * <p>如果对象为{@link XMLConfigurable}的实例，则调用{@link XMLConfigurable#configure(Element, Properties)}来初始化对象.</p>
	 * @param _xml 创建对象所需的XML参数
	 * @param _properties 所需的变量集
	 * @param moduleAttr 表示module属性的属性名称
	 * @return 对象实例
	 * @throws BaseException
	 * @see XMLConfigurable
	 * @see #newInstance(String)
	 */
	public object newInstance(Element _xml,Properties _properties,String moduleAttr) throws BaseException{
		String module = _xml.getAttribute(moduleAttr);
		if (module == null || module.length() <= 0){
			throw new BaseException(Factory.class.getName(),
					"Can not find attr in the element,attr id = " + moduleAttr);
		}
		
		object instance = newInstance(module);
		
		if (instance instanceof XMLConfigurable){
			((XMLConfigurable)instance).configure(_xml, _properties);
		}
		
		return instance;
	}	
	
	/**
	 * 创建新的对象实例
	 * <p>如果对象为{@link XMLConfigurable}的实例，则调用{@link XMLConfigurable#configure(Element, Properties)}来初始化对象.</p>
	 * @param _xml 创建对象所需的XML参数
	 * @param _properties 所需的变量集
	 * @param moduleAttr 表示module属性的属性名称
	 * @param dftClass 缺省的类
	 * @return
	 * @throws BaseException
	 * 
	 * @since 1.3.5
	 */
	public object newInstance(Element _xml,Properties _properties,String moduleAttr,String dftClass) throws BaseException{
		String module = _xml.getAttribute(moduleAttr);
		if (module == null || module.length() <= 0){
			module = dftClass;
		}
		
		object instance = newInstance(module);
		
		if (instance instanceof XMLConfigurable){
			((XMLConfigurable)instance).configure(_xml, _properties);
		}
		
		return instance;
	}
	
	/**
	 * 按照指定的module来创建对象实例
	 * <p>module不完全是对象的类名，在使用之前需调用{@link #getClassName(String)}进行转换。如果module不使用类名的话，
	 * 可以override函数{@link #getClassName(String)}将module转换为类名.</p>
	 * <p>在某些时候需要选定ClassLoader来创建实例，需定制{@link #classLoader}.</p>
	 * @param _module 类型或者类名
	 * @return 对象实例
	 * @throws BaseException 创建过程中抛出此异常
	 */
	@SuppressWarnings("unchecked")
	public object newInstance(String _module)throws BaseException{
		String className = getClassName(_module);
		try {
			if (classLoader == null){
				classLoader = Settings.getClassLoader();
			}
			return (object)classLoader.loadClass(className).newInstance();
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
	 * @param _module 对象实例
	 * @param props 初始化参数
	 * @return 对象实例
	 * @throws BaseException
	 * 
	 * @since 1.0.9
	 * 
	 */
	@SuppressWarnings("unchecked")
	public object newInstance(String _module,Properties props) throws BaseException{
		String className = getClassName(_module);
		try {
			if (classLoader == null){
				classLoader = Settings.getClassLoader();
			}
			Class<?> clazz = classLoader.loadClass(className);
			Constructor<?> constructor = clazz.getConstructor(new Class[]{Properties.class});
			if (constructor != null){
				return (object)constructor.newInstance(new Object[]{props});
			}else{
				return (object)clazz.newInstance();
			}
		} catch (Exception ex){
			throw new BaseException(Factory.class.getName(),
					"Can not create instance of " + className,ex);
		}
	}
	
	/**
	 * 将module转化为全路径类名
	 * @param _module module名
	 * @return 全路径类名
	 * @throws BaseException
	 */
	public String getClassName(String _module) throws BaseException{
		return _module;
	}
}
