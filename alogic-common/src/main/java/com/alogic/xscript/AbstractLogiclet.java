package com.alogic.xscript;

import java.lang.reflect.Constructor;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.json.JsonObject;
import com.alogic.xscript.doc.xml.XmlObject;
import com.alogic.xscript.log.LogInfo;
import com.alogic.xscript.plugins.Divide;
import com.alogic.xscript.plugins.Locker;
import com.alogic.xscript.plugins.Plus;
import com.alogic.xscript.plugins.Array;
import com.alogic.xscript.plugins.ArrayItem;
import com.alogic.xscript.plugins.ArraySet;
import com.alogic.xscript.plugins.ArrayString;
import com.alogic.xscript.plugins.Asynchronized;
import com.alogic.xscript.plugins.Check;
import com.alogic.xscript.plugins.CheckAndSetDefault;
import com.alogic.xscript.plugins.Constants;
import com.alogic.xscript.plugins.Count;
import com.alogic.xscript.plugins.Decr;
import com.alogic.xscript.plugins.Decrypt;
import com.alogic.xscript.plugins.Duration;
import com.alogic.xscript.plugins.Encrypt;
import com.alogic.xscript.plugins.Eval;
import com.alogic.xscript.plugins.Except;
import com.alogic.xscript.plugins.ForEach;
import com.alogic.xscript.plugins.Formula;
import com.alogic.xscript.plugins.FromEnv;
import com.alogic.xscript.plugins.FromSettings;
import com.alogic.xscript.plugins.FunctionCall;
import com.alogic.xscript.plugins.FunctionCallback;
import com.alogic.xscript.plugins.FunctionDeclare;
import com.alogic.xscript.plugins.Get;
import com.alogic.xscript.plugins.GetAsJson;
import com.alogic.xscript.plugins.IfEqual;
import com.alogic.xscript.plugins.IfExist;
import com.alogic.xscript.plugins.IfFalse;
import com.alogic.xscript.plugins.IfNotEqual;
import com.alogic.xscript.plugins.IfNotExist;
import com.alogic.xscript.plugins.IfTrue;
import com.alogic.xscript.plugins.Include;
import com.alogic.xscript.plugins.Incr;
import com.alogic.xscript.plugins.Load;
import com.alogic.xscript.plugins.Location;
import com.alogic.xscript.plugins.Log;
import com.alogic.xscript.plugins.Lowercase;
import com.alogic.xscript.plugins.Match;
import com.alogic.xscript.plugins.Message;
import com.alogic.xscript.plugins.Multiply;
import com.alogic.xscript.plugins.NewLine;
import com.alogic.xscript.plugins.Obj;
import com.alogic.xscript.plugins.Now;
import com.alogic.xscript.plugins.Rand;
import com.alogic.xscript.plugins.RegexMatcher;
import com.alogic.xscript.plugins.Remove;
import com.alogic.xscript.plugins.Repeat;
import com.alogic.xscript.plugins.Scope;
import com.alogic.xscript.plugins.Segment;
import com.alogic.xscript.plugins.Select;
import com.alogic.xscript.plugins.Set;
import com.alogic.xscript.plugins.SetAsJson;
import com.alogic.xscript.plugins.SetMulti;
import com.alogic.xscript.plugins.Sleep;
import com.alogic.xscript.plugins.StringProcess;
import com.alogic.xscript.plugins.Substr;
import com.alogic.xscript.plugins.Switch;
import com.alogic.xscript.plugins.Template;
import com.alogic.xscript.plugins.Throw;
import com.alogic.xscript.plugins.Tree;
import com.alogic.xscript.plugins.Trim;
import com.alogic.xscript.plugins.UUid;
import com.alogic.xscript.plugins.Uppercase;
import com.alogic.xscript.plugins.Using;
import com.alogic.xscript.plugins.Hash;
import com.alogic.metrics.Fragment;
import com.alogic.metrics.stream.MetricsCollector;
import com.alogic.metrics.stream.MetricsHandlerFactory;
import com.alogic.tracer.Tool;
import com.alogic.tracer.TraceContext;
import com.anysoft.stream.Handler;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;


/**
 * 虚基类
 * 
 * @author duanyy
 * @version 1.6.5.13 [20160612 duanyy] <br>
 * - 增加Encrypt,Decrypt,Check,CheckAndSetDefault等插件 <br>
 * 
 * @version 1.6.6.1 [20160823 duanyy] <br>
 * - 增加getAsJson和setAsJson插件 <br>
 * 
 * @version 1.6.6.13 [20170109 duanyy] <br>
 * - 采用新的指标接口
 * 
 * @version 1.6.7.3 [20170118 duanyy] <br>
 * - 对tlog的开启开关进行了统一 <br>
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 * 
 * @version 1.6.8.4 [20170329 duanyy] <br>
 * - 增加match插件 <br>
 * 
 * @version 1.6.8.5 [20170331 duanyy] <br>
 * - 增加rem插件 <br>
 * 
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
 * 
 * @version 1.6.9.9 [20170829 duanyy] <br>
 * - 增加Duration插件 <br>
 * 
 * @version 1.6.10.1 [20170911 duanyy] <br>
 * - 增加incr,decr指令 <br>
 * 
 * @version 1.6.11.27 [20180417 duanyy] <br>
 * - 增加xscript的函数相关的插件func-declare,func-call,func-callback <br>
 * 
 * @version 1.6.11.28 [20180420 duanyy] <br>
 * - 增加array-string和set-multi插件; <br>
 * 
 * @version 1.6.11.34 [20180606 duanyy] <br>
 * - alogic-common:增加字符串处理插件sp; <br>
 * - alogic-common:增加集合处理插件array-set; <br>
 * 
 * @version 1.6.11.36 [20180613 duanyy] <br>
 * - 增加plus,mul,div插件 <br>
 * 
 * @version 1.6.11.57 [20180828 duanyy] <br>
 * - 增加浏览器会话id的传递 <br>
 * 
 * @version 1.6.11.58 [20180829 duanyy] <br>
 * - 增加tree,tree-node,tree-output,tree-traverse指令 <br>
 * 
 */
public abstract class AbstractLogiclet implements Logiclet,MetricsCollector{

	/**
	 * a logger of log4j
	 */
	public static final Logger logger = LoggerFactory.getLogger(Logiclet.class);	
	
	/**
	 * 父节点
	 */
	private Logiclet parent = null;
	
	/**
	 * xml tag
	 */
	private String xmlTag = null;	
	
	/**
	 * 当前活动
	 */
	private String activity;
	
	private boolean traceEnable = false;
	
	/**
	 * 静态注册表（全局有效）
	 */
	protected static Hashtable<String,Class<? extends Logiclet>> staticModules = 
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
	public static final String STMT_GET = "get";
	public static final String STMT_SET = "set";
	public static final String STMT_SET_MULTI = "set-multi";
	public static final String STMT_SELECT = "select";
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
	public static final String STMT_CONSTANTS = "constants";
	public static final String STMT_LOGGER = "logger";
	public static final String STMT_LOG = "log";
	public static final String STMT_SLEEP = "sleep";
	public static final String STMT_FOREACH = "foreach";
	public static final String STMT_OBJ = "obj";
	public static final String STMT_ARRAY = "array";
	public static final String STMT_ARRAYITEM = "array-item";
	public static final String STMT_ARRAYSTRING = "array-string";
	public static final String STMT_TRIM = "trim";
	public static final String STMT_UPPERCASE = "uppercase";
	public static final String STMT_LOWERCASE = "lowercase";
	public static final String STMT_SUBSTR = "substr";
	public static final String STMT_SETTING = "setting";
	public static final String STMT_ENV = "env";
	public static final String STMT_GETASJSON = "getAsJson";
	public static final String STMT_SETASJSON = "setAsJson";
	public static final String STMT_HASH = "hash";
	public static final String STMT_MATCH = "match";
	public static final String STMT_REM = "rem";
	public static final String STMT_DURATION = "duration";
	public static final String STMT_INCR = "incr";
	public static final String STMT_DECR = "decr";
	public static final String STMT_RAND = "rand";
	public static final String STMT_IF_TRUE = "if-true";
	public static final String STMT_IF_FALSE = "if-false";
	public static final String STMT_IF_EXIST = "if-exist";
	public static final String STMT_IF_NOT_EXIST = "if-n-exist";
	public static final String STMT_IF_EQUAL = "if-equal";
	public static final String STMT_IF_NOT_EQUAL = "if-n-equal";
	public static final String STMT_NEW_LINE = "newline";
	public static final String STMT_FUNC_DECLARE = "func-declare";
	public static final String STMT_FUNC_CALL = "func-call";
	public static final String STMT_FUNC_CALLBACK = "func-callback";
	public static final String STMT_EVAL = "eval";
	public static final String STMT_LOAD = "load";
	public static final String STMT_LOCK = "lock";
	public static final String STMT_COUNTER = "counter";
	public static final String STMT_ARRAY_SET = "array-set";
	public static final String STMT_STRING_PROCESS = "sp";
	public static final String STMT_PLUS = "plus";
	public static final String STMT_MUL = "mul";
	public static final String STMT_DIVIDE = "div";
	public static final String STMT_REGEX_MATCHER = "regex-match";
	public static final String STMT_TREE = "tree";
	public static final String STMT_TREE_NODE = "tree-node";
	public static final String STMT_TREE_OUTPUT = "tree-output";
	public static final String STMT_TREE_TRAVERSE = "tree-traverse";
	
	protected static Handler<Fragment> metricsHandler = null;
	
	static{
		staticModules.put(STMT_INCLUDE, Include.class);
		staticModules.put(STMT_SEGMENT, Segment.class);
		staticModules.put(STMT_USING, Using.class);
		staticModules.put(STMT_ASYNC, Asynchronized.class);
		staticModules.put(STMT_THROW, Throw.class);
		staticModules.put(STMT_EXCEPTION, Except.class);
		staticModules.put(STMT_FINALLY, Except.class);
		staticModules.put(STMT_TEMPLATE, Template.class);
		staticModules.put(STMT_MSG, Message.class);
		staticModules.put(STMT_LOCATION, Location.class);
		staticModules.put(STMT_SCOPE, Scope.class);
		staticModules.put(STMT_GET, Get.class);
		staticModules.put(STMT_SET, Set.class);
		staticModules.put(STMT_SET_MULTI, SetMulti.class);
		staticModules.put(STMT_SELECT, Select.class);
		staticModules.put(STMT_ENCRYPT,Encrypt.class);
		staticModules.put(STMT_DECRYPT,Decrypt.class);
		staticModules.put(STMT_CHECK,Check.class);
		staticModules.put(STMT_CHECK_AND_SET,CheckAndSetDefault.class);
		staticModules.put(STMT_REPEAT,Repeat.class);
		staticModules.put(STMT_SWITCH, Switch.class);
		staticModules.put(STMT_FORMULA, Formula.class);
		staticModules.put(STMT_NOW,Now.class);
		staticModules.put(STMT_UUID,UUid.class);
		staticModules.put(STMT_CONSTANTS, Constants.class);
		staticModules.put(STMT_LOGGER, com.alogic.xscript.plugins.Logger.class);
		staticModules.put(STMT_LOG, Log.class);
		staticModules.put(STMT_SLEEP, Sleep.class);
		staticModules.put(STMT_FOREACH, ForEach.class);
		staticModules.put(STMT_OBJ, Obj.class);
		staticModules.put(STMT_ARRAY, Array.class);
		staticModules.put(STMT_ARRAYITEM, ArrayItem.class);
		staticModules.put(STMT_ARRAYSTRING, ArrayString.class);
		staticModules.put(STMT_ARRAY_SET,ArraySet.class);
		staticModules.put(STMT_TRIM, Trim.class);
		staticModules.put(STMT_UPPERCASE,Uppercase.class);
		staticModules.put(STMT_LOWERCASE,Lowercase.class);
		staticModules.put(STMT_SUBSTR,Substr.class);
		staticModules.put(STMT_SETTING, FromSettings.class);
		staticModules.put(STMT_ENV,FromEnv.class);
		staticModules.put(STMT_GETASJSON, GetAsJson.class);
		staticModules.put(STMT_SETASJSON, SetAsJson.class);
		staticModules.put(STMT_HASH, Hash.class);
		staticModules.put(STMT_MATCH, Match.class);
		staticModules.put(STMT_REM,Remove.class);
		staticModules.put(STMT_DURATION,Duration.class);
		staticModules.put(STMT_INCR,Incr.class);
		staticModules.put(STMT_DECR, Decr.class);
		staticModules.put(STMT_RAND, Rand.class);
		staticModules.put(STMT_IF_TRUE, IfTrue.class);
		staticModules.put(STMT_IF_FALSE, IfFalse.class);
		staticModules.put(STMT_IF_EXIST, IfExist.class);
		staticModules.put(STMT_IF_NOT_EXIST, IfNotExist.class);
		staticModules.put(STMT_IF_EQUAL, IfEqual.class);
		staticModules.put(STMT_IF_NOT_EQUAL, IfNotEqual.class);
		staticModules.put(STMT_NEW_LINE, NewLine.class);
		staticModules.put(STMT_FUNC_DECLARE,FunctionDeclare.class);
		staticModules.put(STMT_FUNC_CALL,FunctionCall.class);
		staticModules.put(STMT_FUNC_CALLBACK, FunctionCallback.class);
		staticModules.put(STMT_EVAL, Eval.class);
		staticModules.put(STMT_LOAD, Load.class);
		staticModules.put(STMT_LOCK, Locker.class);
		staticModules.put(STMT_COUNTER, Count.class);
		staticModules.put(STMT_STRING_PROCESS, StringProcess.class);
		staticModules.put(STMT_PLUS, Plus.class);
		staticModules.put(STMT_MUL, Multiply.class);
		staticModules.put(STMT_DIVIDE, Divide.class);
		staticModules.put(STMT_REGEX_MATCHER,RegexMatcher.class);
		staticModules.put(STMT_TREE, Tree.class);
		staticModules.put(STMT_TREE_NODE, Tree.Node.class);
		staticModules.put(STMT_TREE_OUTPUT, Tree.Output.class);
		staticModules.put(STMT_TREE_TRAVERSE, Tree.Traverse.class);
		
		metricsHandler = MetricsHandlerFactory.getClientInstance();
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
		activity = PropertiesConstants.getString(p,"activity",xmlTag);
		traceEnable = PropertiesConstants.getBoolean(p, "tracer.xscript.enable", traceEnable);
	}	
	
	@Override
	public void configure(Element e, Properties p) {
		Properties props = new XmlElementProperties(e,p);
		configure(props);
	}
	
	@Override
	public void log(LogInfo logInfo){
		if (parent != null){
			parent.log(logInfo);
		}
	}
	
	public void log(String message,String level,int progress){
		log(new LogInfo(activity,message,level,progress));
	}
	
	public void log(String message,String level){
		log(message,level,-2);
	}
	
	public void log(String message,int progress){
		log(message,"info",progress);
	}
	
	public void log(String message){
		log(message,-2);
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
	
	public void execute(XsObject root,XsObject current,LogicletContext ctx,ExecuteWatcher watcher){
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
	
	@SuppressWarnings("unchecked")
	protected void onExecute(XsObject root,XsObject current,final LogicletContext ctx,final ExecuteWatcher watcher){
		if (root instanceof JsonObject){
			onExecute((Map<String, Object>)root.getContent(),(Map<String, Object>)current.getContent(),ctx,watcher);
		}else{
			if (root instanceof XmlObject){
				onExecute((Element)root.getContent(),(Element)current.getContent(),ctx,watcher);
			}else{
				throw new BaseException("core.e1000",String.format("Tag %s does not support protocol %s",this.xmlTag,root.getClass().getName()));	
			}
		}
	}
	
	protected void onExecute(final Map<String, Object> root, final Map<String, Object> current,final LogicletContext ctx,final ExecuteWatcher watcher){
		throw new BaseException("core.e1000",String.format("Tag %s does not support protocol %s",this.xmlTag,root.getClass().getName()));	
	}
	
	protected void onExecute(final Element root, final Element current,final LogicletContext ctx,final ExecuteWatcher watcher){
		throw new BaseException("core.e1000",String.format("Tag %s does not support protocol %s",this.xmlTag,root.getClass().getName()));	
	}
	
	public static String getArgument(String id,String dftValue,LogicletContext ctx){
		return ctx.GetValue(id, dftValue);
	}
	
	public static long getArgument(String id,long dftValue,LogicletContext ctx){
		String value = getArgument(id,String.valueOf(dftValue),ctx);
		
		try {
			return Long.parseLong(value);
		}catch (NumberFormatException ex){
			return dftValue;
		}
	}	
	
	public static int getArgument(String id,int dftValue,LogicletContext ctx){
		String value = getArgument(id,String.valueOf(dftValue),ctx);
		
		try {
			return Integer.parseInt(value);
		}catch (NumberFormatException ex){
			return dftValue;
		}
	}
	
	public static boolean getArgument(String id,boolean dftValue,LogicletContext ctx){
		String value = getArgument(id,Boolean.toString(dftValue),ctx);
		
		try {
			return BooleanUtils.toBoolean(value);
		}catch (NumberFormatException ex){
			return dftValue;
		}
	}	
	
	public static String getArgument(String id,LogicletContext ctx){
		String value = ctx.GetValue(id, "");
		if (StringUtils.isEmpty(value)){
			throw new BaseException("clnt.e2000","Can not find parameter:" + id);
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
		Logiclet found = onCreateLogiclet(xmlTag,parent);
		
		if (found == null){
			Logiclet p = parent();
			if (p != null){
				found = p.createLogiclet(xmlTag, parent);
			}
		}
		
		if (found == null){
			Class<? extends Logiclet> clazz = staticModules.get(xmlTag);
			if (clazz != null){
				found = createLogiclet(clazz,xmlTag,parent);
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
	public void registerLogger(Handler<LogInfo> logHandler) {
		logger.warn("Log handler is not supported,Ignored.");
	}	
	
	@Override
	public void registerFunction(String id,Logiclet function){
		if (parent != null){
			parent.registerFunction(id, function);
		}
	}
	
	@Override
	public Logiclet getFunction(String id){
		return parent != null ? parent.getFunction(id):null;
	}
	
	@Override
	public void metricsIncr(Fragment fragment){
		if (metricsHandler != null){
			metricsHandler.handle(fragment,System.currentTimeMillis());
		}
	}	
}
