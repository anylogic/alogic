package com.logicbus.together.logiclet;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.formula.DataProvider;
import com.anysoft.formula.Expression;
import com.anysoft.formula.Parser;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlTools;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.Message;
import com.logicbus.together.AbstractLogiclet;
import com.logicbus.together.ExecuteWatcher;
import com.logicbus.together.Logiclet;
import com.logicbus.together.LogicletFactory;

/**
 * 条件logiclet
 * 
 * <br>
 * 根据不同的条件，执行不同的子logiclet
 * 
 * @author duanyy
 * @since 1.1.0
 * 
 * @version 1.2.0 增加对JSON支持
 * 
 * @version 1.6.4.43 [20160411 duanyy] <br>
 * - DataProvider增加获取原始值接口 <br>
 */
public class Selector extends AbstractLogiclet{

	
	protected void onCompile(Element config, Properties myProps,LogicletFactory factory)
			throws ServantException {
		condition = PropertiesConstants.getString(myProps, "condition", condition);
		
		conditionExpression = (new Parser()).parse(condition); 
		
		NodeList childrenNodeList = XmlTools.getNodeListByPath(config, "logiclet");
		
		for (int i = 0 ,length = childrenNodeList.getLength() ; i < length ; i ++){
			Node n = childrenNodeList.item(i);
			if (n.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			
			Element e = (Element)n;
			
			String module = e.getAttribute("module");			
			if (module == null || module.length() <= 0){
				logger.warn("Can not find attribute : module, ignored");
				continue;
			}
			
			String option = e.getAttribute("option");
			if (option == null || option.length() <= 0){
				logger.warn("Can not find attribute : option, ignored");
				continue;
			}
			
			Logiclet instance = null;
			if (factory != null){
				instance = factory.newLogiclet(module);
			}
			if (instance == null){
				instance = newLogiclet(module);
			}
			if (instance == null){
				logger.warn("Can not create logiclet , ignored");
				continue;
			}
			
			instance.compile(e, myProps, this,factory);
			children.put(option, instance);
		}
	}

	
	protected void onExecute(Element target, Message msg, Context ctx,ExecuteWatcher watcher)
			throws ServantException {
		if (conditionExpression == null){
			//no condition , do nothing
			return ;
		}
		
		final Message _msg = msg;
		final Context _ctx = ctx;
		final Element _target = target;
		
		String value = conditionExpression.getValue(new DataProvider(){

			@Override
			public String getValue(String varName, Object context,
					String defaultValue) {
				try {
					return getArgument(varName,defaultValue,_target,_msg,_ctx);
				}catch (ServantException ex){
					return "";
				}
			}

			@Override
			public String getRawValue(String varName, Object context, String dftValue) {
				return getValue(varName,context,dftValue);
			}	
			
			@Override
			public Object getContext(String varName) {
				return null;
			}
			
		}).toString();
		
		Logiclet logiclet = children.get(value);
		if (logiclet == null){
			logger.warn("Can not find a logiclet to match the value:" + value);
			return ;
		}
		logiclet.execute(target, _msg, _ctx,watcher);
	}

	/**
	 * 条件公式
	 */
	protected String condition = "true";
	
	/**
	 * 条件表达式
	 */
	protected Expression conditionExpression = null;
	
	/**
	 * 子logiclet
	 */
	protected HashMap<String,Logiclet> children = new HashMap<String,Logiclet>();

	@SuppressWarnings("rawtypes")
	
	protected void onExecute(Map target, Message msg, Context ctx,
			ExecuteWatcher watcher) throws ServantException {
		if (conditionExpression == null){
			//no condition , do nothing
			return ;
		}
		
		final Message _msg = msg;
		final Context _ctx = ctx;
		final Map _target = target;
		
		String value = conditionExpression.getValue(new DataProvider(){

			@Override
			public String getValue(String varName, Object context,
					String defaultValue) {
				try {
					return getArgument(varName,defaultValue,_target,_msg,_ctx);
				}catch (ServantException ex){
					return "";
				}
			}

			@Override
			public String getRawValue(String varName, Object context, String dftValue) {
				return getValue(varName,context,dftValue);
			}				
			
			@Override
			public Object getContext(String varName) {
				return null;
			}
			
		}).toString();
		
		Logiclet logiclet = children.get(value);
		if (logiclet == null){
			logger.warn("Can not find a logiclet to match the value:" + value);
			return ;
		}
		logiclet.execute(target, _msg, _ctx,watcher);
	}
}
