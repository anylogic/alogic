package com.logicbus.together.logiclet;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.Message;
import com.logicbus.together.AbstractLogiclet;
import com.logicbus.together.ExecuteWatcher;
import com.logicbus.together.LogicletFactory;

/**
 * Helloworld
 * 
 * <br>
 * 这是一个测试的logiclet，负责在目标节点下，增加一个Helloworld节点
 * @author duanyy
 *
 * @since 1.1.0
 * @version 1.2.0 增加对JSON支持
 */
public class Helloworld extends AbstractLogiclet {

	
	protected void onCompile(Element config, Properties myProps,LogicletFactory factory)
			throws ServantException {
		welcome = PropertiesConstants.getString(myProps, "welcome", welcome);
	}

	
	protected void onExecute(Element target, Message msg, Context ctx,ExecuteWatcher watcher)
			throws ServantException {
		
		String _welcome = this.getArgument("welcome", welcome, target, msg, ctx);
		
		Document doc = target.getOwnerDocument();
		Element say = doc.createElement("say");
		say.appendChild(doc.createTextNode(_welcome));
		//在异步执行模式下，在最后提交到target上时，进行同步控制
		synchronized (target){
			target.appendChild(say);
		}
	}

	/**
	 * 欢迎语
	 */
	protected String welcome = "Hello world";

	@SuppressWarnings({ "rawtypes", "unchecked" })
	
	protected void onExecute(Map target, Message msg, Context ctx,
			ExecuteWatcher watcher) throws ServantException {
		String _welcome = this.getArgument("welcome", welcome, target, msg, ctx);
		synchronized (target){
			target.put("say", _welcome);
		}
	}
}
