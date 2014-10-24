package com.logicbus.together;

import org.w3c.dom.Element;

import com.anysoft.util.Properties;
import com.logicbus.backend.ServantException;


/**
 * 编译器
 * 
 * @author duanyy
 *
 * @since 1.1.0
 */
public class Compiler {
	
	/**
	 * 编译指定的文档
	 * 
	 * @param root
	 * @param props
	 * @return
	 */
	public static Logiclet compile(Element root,Properties props,LogicletFactory factory) throws ServantException{
		String module = root.getAttribute("module");
		
		if (module == null || module.length() <= 0){
			return null;
		}
		
		Logiclet logiclet = null;
		if (factory != null){
			logiclet = factory.newLogiclet(module);
		}
		if (logiclet == null){
			logiclet = AbstractLogiclet.newLogiclet(module);
		}				
		if (logiclet == null){
			return null;
		}
		
		logiclet.compile(root, props, null,factory);
		
		return logiclet;
	}
}
