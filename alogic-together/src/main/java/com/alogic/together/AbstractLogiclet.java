package com.alogic.together;

import java.lang.reflect.Constructor;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.alogic.together.plugins.Asynchronized;
import com.alogic.together.plugins.Check;
import com.alogic.together.plugins.CheckAndSetDefault;
import com.alogic.together.plugins.Decrypt;
import com.alogic.together.plugins.Encrypt;
import com.alogic.together.plugins.Except;
import com.alogic.together.plugins.Formula;
import com.alogic.together.plugins.Helloworld;
import com.alogic.together.plugins.Location;
import com.alogic.together.plugins.Message;
import com.alogic.together.plugins.Now;
import com.alogic.together.plugins.Repeat;
import com.alogic.together.plugins.Scope;
import com.alogic.together.plugins.Segment;
import com.alogic.together.plugins.Set;
import com.alogic.together.plugins.Switch;
import com.alogic.together.plugins.Template;
import com.alogic.together.plugins.Throw;
import com.alogic.together.plugins.UUid;
import com.alogic.together.plugins.Using;
import com.alogic.together.plugins.Variable;
import com.alogic.tracer.Tool;
import com.alogic.tracer.TraceContext;
import com.anysoft.metrics.core.Dimensions;
import com.anysoft.metrics.core.Fragment;
import com.anysoft.metrics.core.Measures;
import com.anysoft.metrics.core.MetricsCollector;
import com.anysoft.metrics.core.MetricsHandler;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlElementProperties;
import com.logicbus.backend.ServantException;


/**
 * 虚基类
 * 
 * @author duanyy
 * @version 1.6.5.13 [20160612 duanyy] <br>
 * - 增加Encrypt,Decrypt,Check,CheckAndSetDefault等插件 <br>
 */
public abstract class AbstractLogiclet implements Logiclet,MetricsCollector{

	/**
	 * a logger of log4j
	 */
	public static final Logger logger = LogManager.getLogger(Logiclet.class);	
	
	/**
	 * 父节点
	 */
	private Logiclet parent = null;
	
	/**
	 * xml tag
	 */
	private String xmlTag = null;	
	
	private boolean traceEnable = false;
	
	/**
	 * 静态注册表（全局有效）
	 */
	private static Hashtable<String,Class<? extends Logiclet>> staticModules = 
			new Hashtable<String,Class<? extends Logiclet>>();
	
	/**
	 * 注册表（实例有效）
	 */
	private Hashtable<String,Class<? extends Logiclet>> modules = 
			new Hashtable<String,Class<? extends Logiclet>>();	
	
	public static final String STMT_FINALLY = "finally";
	public static final String STMT_EXCEPTION = "except";
	public static final String STMT_ASYNC = "async";
	public static final String STMT_USING = "using";
	public static final String STMT_SEGMENT = "segment";
	public static final String STMT_THROW = "throw";
	public static final String STMT_INCLUDE = "include";
	public static final String STMT_TEMPLATE = "template";
	public static final String STMT_HELLO = "hello";
	public static final String STMT_MSG = "msg";
	public static final String STMT_LOCATION = "location";
	public static final String STMT_SCOPE = "scope";
	public static final String STMT_VAR = "var";
	public static final String STMT_SET = "set";
	public static final String STMT_ENCRYPT = "encrypt";
	public static final String STMT_DECRYPT = "decrypt";
	public static final String STMT_CHECK = "check";
	public static final String STMT_CHECK_AND_SET = "checkAndSet";
	public static final String STMT_REPEAT = "repeat";
	public static final String STMT_CASE = "case";
	public static final String STMT_SWITCH = "switch";
	public static final String STMT_DEFAULT = "default";
	public static final String STMT_FORMULA = "formula";
	public static final String STMT_NOW = "now";
	public static final String STMT_UUID = "uuid";
	
	protected static MetricsHandler metricsHandler = null;
	
	static{
		staticModules.put(STMT_SEGMENT, Segment.class);
		staticModules.put(STMT_USING, Using.class);
		staticModules.put(STMT_ASYNC, Asynchronized.class);
		staticModules.put(STMT_THROW, Throw.class);
		staticModules.put(STMT_EXCEPTION, Except.class);
		staticModules.put(STMT_FINALLY, Except.class);
		staticModules.put(STMT_TEMPLATE, Template.class);
		staticModules.put(STMT_HELLO, Helloworld.class);
		staticModules.put(STMT_MSG, Message.class);
		staticModules.put(STMT_LOCATION, Location.class);
		staticModules.put(STMT_SCOPE, Scope.class);
		staticModules.put(STMT_VAR, Variable.class);
		staticModules.put(STMT_SET, Set.class);
		staticModules.put(STMT_ENCRYPT,Encrypt.class);
		staticModules.put(STMT_DECRYPT,Decrypt.class);
		staticModules.put(STMT_CHECK,Check.class);
		staticModules.put(STMT_CHECK_AND_SET,CheckAndSetDefault.class);
		staticModules.put(STMT_REPEAT,Repeat.class);
		staticModules.put(STMT_SWITCH, Switch.class);
		staticModules.put(STMT_FORMULA, Formula.class);
		staticModules.put(STMT_NOW,Now.class);
		staticModules.put(STMT_UUID,UUid.class);
		
		Settings settings = Settings.get();
		metricsHandler = (MetricsHandler) settings.get("metricsHandler");
	}	
	
	public AbstractLogiclet(String tag,Logiclet p){
		xmlTag = tag;
		parent = p;
	}	
	
	public boolean traceEnable(){
		return traceEnable;
	}

	@Override
	public void configure(Properties p) {
		traceEnable = PropertiesConstants.getBoolean(p, "servant.tracer", traceEnable);
	}	
	
	@Override
	public void configure(Element e, Properties p) {
		Properties props = new XmlElementProperties(e,p);
		configure(props);
	}

	@Override
	public void report(Element xml) {
		if (xml != null){
			xml.setAttribute("module", getClass().getName());
		}
	}

	@Override
	public void report(Map<String, Object> json) {
		if (json != null){
			json.put("module", getClass().getName());
		}
	}

	@Override
	public void execute(final Map<String, Object> root, final Map<String, Object> current,final LogicletContext ctx,final ExecuteWatcher watcher) {
		long start = System.currentTimeMillis();
		boolean error = false;
		String msg = "OK";
		TraceContext tc = null;
		if (traceEnable()){
			tc = Tool.start();
		}		
		try {
			onExecute(root,current,ctx,watcher);
		}catch (Exception ex){
			error = true;
			msg = ex.getMessage();
			throw ex;
		}finally{
			if (watcher != null){
				watcher.executed(this, ctx, error,start, System.currentTimeMillis() - start);
			}
			if (traceEnable()){
				Tool.end(tc, "LOGICLET", getXmlTag(), error?"FAILED":"OK", msg);
			}			
		}
	}

	protected abstract void onExecute(final Map<String, Object> root, final Map<String, Object> current,final LogicletContext ctx,final ExecuteWatcher watcher);
	
	public String getArgument(String id,String dftValue,LogicletContext ctx){
		return ctx.GetValue(id, dftValue);
	}
	
	public long getArgument(String id,long dftValue,LogicletContext ctx){
		String value = getArgument(id,String.valueOf(dftValue),ctx);
		
		try {
			return Long.parseLong(value);
		}catch (NumberFormatException ex){
			return dftValue;
		}
	}	
	
	public int getArgument(String id,int dftValue,LogicletContext ctx){
		String value = getArgument(id,String.valueOf(dftValue),ctx);
		
		try {
			return Integer.parseInt(value);
		}catch (NumberFormatException ex){
			return dftValue;
		}
	}
	
	public boolean getArgument(String id,boolean dftValue,LogicletContext ctx){
		String value = getArgument(id,Boolean.toString(dftValue),ctx);
		
		try {
			return BooleanUtils.toBoolean(value);
		}catch (NumberFormatException ex){
			return dftValue;
		}
	}	
	
	public String getArgument(String id,LogicletContext ctx){
		String value = ctx.GetValue(id, "");
		if (StringUtils.isEmpty(value)){
			throw new ServantException("client.args_not_found",
					"Can not find parameter:" + id);
		}
		return value;
	}	
	
	@Override
	public Logiclet parent() {
		return parent;
	}

	@Override
	public boolean isExecutable() {
		return true;
	}

	@Override
	public String getXmlTag() {
		return xmlTag;
	}

	@Override
	public Logiclet createLogiclet(String xmlTag, Logiclet parent) {
		Logiclet found = null;
		
		//首先到staticModules中查找
		Class<? extends Logiclet> clazz = staticModules.get(xmlTag);
		if (clazz != null){
			found = createLogiclet(clazz,xmlTag,parent);
		}
		
		if (found == null){
			found = onCreateLogiclet(xmlTag,parent);
		}
		
		if (found == null){
			Logiclet p = parent();
			if (p != null){
				found = p.createLogiclet(xmlTag, parent);
			}
		}
		return found;
	}
	
	protected Logiclet onCreateLogiclet(String xml, Logiclet p) {
		Class<? extends Logiclet> clazz = modules.get(xml);
		return clazz == null ? null : createLogiclet(clazz,xml,p);
	}

	protected Logiclet createLogiclet(Class<? extends Logiclet> clazz,String xmlTag,Logiclet parent){
		try {
			Constructor<? extends Logiclet> constructor = clazz.getConstructor(String.class,Logiclet.class);
			return constructor.newInstance(new Object[]{xmlTag,parent});
		} catch (Exception e) {
			logger.error("Can not create segment instance:" + clazz.getName(),e);
			return null;
		}
	}	

	@Override
	public void registerModule(String xmltag, Class<? extends Logiclet> clazz) {
		modules.put(xmltag, clazz);
	}

	@Override
	public void registerExceptionHandler(String id, Logiclet exceptionHandler) {
		logger.warn("Exception handler is not supported,Ignored.");
	}
	
	@Override
	public void metricsIncr(Fragment fragment){
		if (metricsHandler != null){
			metricsHandler.handle(fragment,System.currentTimeMillis());
		}
	}
	
	public void metricsIncr(String id,String [] sDims,Object...values){
		Fragment f = new Fragment(id);
		
		Dimensions dims = f.getDimensions();
		if (dims != null)
			dims.lpush(sDims);
		
		Measures meas = f.getMeasures();
		if (meas != null)
			meas.lpush(values);
		
		metricsIncr(f);
	}
	
	public void metricsIncr(String id,String [] sDims,Double...values){
		Fragment f = new Fragment(id);
		
		Dimensions dims = f.getDimensions();
		if (dims != null)
			dims.lpush(sDims);
		
		Measures meas = f.getMeasures();
		if (meas != null)
			meas.lpush(values);
		
		metricsIncr(f);
	}
	
	public void metricsIncr(String id,String [] sDims,Long...values){
		Fragment f = new Fragment(id);
		
		Dimensions dims = f.getDimensions();
		if (dims != null)
			dims.lpush(sDims);
		
		Measures meas = f.getMeasures();
		if (meas != null)
			meas.lpush(values);
		
		metricsIncr(f);		
	}
	
	public void metricsIncr(String id,Double...values){
		Fragment f = new Fragment(id);
		
		Measures meas = f.getMeasures();
		if (meas != null)
			meas.lpush(values);
		
		metricsIncr(f);		
	}
	
	public void metricsIncr(String id,Long ...values){
		Fragment f = new Fragment(id);
		
		Measures meas = f.getMeasures();
		if (meas != null)
			meas.lpush(values);
		
		metricsIncr(f);	
	}
	
	public void metricsIncr(String id,Object ...values){
		Fragment f = new Fragment(id);
		
		Measures meas = f.getMeasures();
		if (meas != null)
			meas.lpush(values);
		
		metricsIncr(f);	
	}		
}
