package com.anysoft.xscript;

import java.lang.reflect.Constructor;
import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;

/**
 * Segment的虚基类
 * 
 * @author duanyy
 * @since 1.6.3.22
 * @version 1.6.3.23 [20150513 duanyy] <br>
 * - 优化编译模式 <br>
 */
abstract public class AbstractStatement implements Statement{
	/**
	 * a logger of log4j
	 */
	public static final Logger logger = LogManager.getLogger(Statement.class);
	
	/**
	 * 父节点
	 */
	private Statement parent = null;
	private String xmlTag = null;
	
	public String getXmlTag(){
		return xmlTag;
	}
	
	public Statement parent() {
		return parent;
	}
	
	/**
	 * 静态注册表（全局有效）
	 */
	private static Hashtable<String,Class<? extends Statement>> staticModules = 
			new Hashtable<String,Class<? extends Statement>>();
	
	/**
	 * 注册表（实例有效）
	 */
	private Hashtable<String,Class<? extends Statement>> modules = 
			new Hashtable<String,Class<? extends Statement>>();
	
	public void registerModule(String xmltag,Class<?extends Statement> clazz){
		modules.put(xmltag, clazz);
	}
	
	/**
	 * 根据Class来创建Statement实例
	 * @param clazz Class
	 * @param _parent 父节点
	 * @return Segment实例
	 */
	protected Statement createStatement(Class<? extends Statement> clazz,String _xmlTag,Statement _parent){
		try {
			Constructor<? extends Statement> constructor = clazz.getConstructor(String.class,Statement.class);
			return constructor.newInstance(new Object[]{_xmlTag,_parent});
		} catch (Exception e) {
			logger.error("Can not create segment instance:" + clazz.getName());
			return null;
		}
	}
	
	public Statement createStatement(String xmlTag,Statement _parent){
		Statement found = null;
		
		//首先到staticModules中查找
		Class<? extends Statement> clazz = staticModules.get(xmlTag);
		if (clazz != null){
			found = createStatement(clazz,xmlTag,_parent);
		}
		
		if (found == null){
			found = onCreateStatement(xmlTag,_parent);
		}
		
		if (found == null){
			Statement p = parent();
			if (p != null){
				found = p.createStatement(xmlTag, _parent);
			}
		}
		
		return found;
	}
	
	/**
	 * 通过xml tag创建Statement实例
	 * 
	 * <p>提供给子类从自己的注册表中创建Statement
	 * 
	 * @param xmlTag xml tag
	 * @return Segment实例
	 */
	protected Statement onCreateStatement(String xmlTag,Statement _parent){
		Class<? extends Statement> clazz = modules.get(xmlTag);
		return clazz == null ? null : createStatement(clazz,xmlTag,_parent);
	}
	
	public AbstractStatement(String _tag,Statement _parent){
		xmlTag = _tag;
		parent = _parent;
	}

	public void report(Element xml) {
		if (xml != null){
			xml.setAttribute("module", getClass().getName());
		}
	}

	public void report(Map<String, Object> json) {
		if (json != null){
			json.put("module", getClass().getName());
		}
	}

	public int execute(Properties p,ExecuteWatcher watcher) throws BaseException{
		long start = System.currentTimeMillis();
		try {
			return onExecute(p,watcher);
		}finally{
			if (watcher != null){
				watcher.executed(this, p, start, System.currentTimeMillis() - start);
			}
		}
	}
	
	public int compile(Element e,Properties p,CompileWatcher watcher){
		long start = System.currentTimeMillis();
		if (watcher != null){
			watcher.begin(this, start);
		}
		try {
			return compiling(e,p,watcher);
		}catch (Exception ex){
			if (watcher != null){
				watcher.message(this, "error", ex.getMessage());
			}
			return -1;
		}
		finally{
			if (watcher != null){
				long now = System.currentTimeMillis();
				watcher.end(this,now,now - start);
			}
		}
	}
	
	protected abstract int compiling(Element e,Properties p,CompileWatcher watcher);
	
	protected abstract int onExecute(Properties p,ExecuteWatcher watcher) throws BaseException;
	
	public boolean isExecutable(){
		return true;
	}
	
	public void registerExceptionHandler(String id,Statement exceptionHandler){
		logger.warn("Exception handler is not supported,Ignored.");
	}
	
	public static final String STMT_FINALLY = "finally";
	public static final String STMT_EXCEPTION = "except";
	public static final String STMT_VAR = "var";
	public static final String STMT_ASYNC = "async";
	public static final String STMT_SLEEP = "sleep";
	public static final String STMT_LOG = "log";
	public static final String STMT_USING = "using";
	public static final String STMT_SEGMENT = "segment";
	public static final String STMT_THROW = "throw";
	public static final String STMT_CHOOSE = "choose";
	public static final String STMT_SWITCH = "switch";
	public static final String STMT_DEFAULT = "default";
	public static final String STMT_CASE = "case";
	
	static{
		/**
		 * 将内置的Statement实现注册在静态注册表中
		 */
		staticModules.put(STMT_SEGMENT, Segment.class);
		staticModules.put(STMT_USING, Using.class);
		staticModules.put(STMT_LOG,Log.class);
		staticModules.put(STMT_SLEEP, Sleep.class);
		staticModules.put(STMT_ASYNC, Asynchronized.class);
		staticModules.put(STMT_VAR, Variable.class);
		staticModules.put(STMT_EXCEPTION, Except.class);
		staticModules.put(STMT_FINALLY, Except.class);
		staticModules.put(STMT_THROW, Throw.class);
		staticModules.put(STMT_CHOOSE, Choose.class);
		staticModules.put(STMT_SWITCH, Switch.class);
	}
}
