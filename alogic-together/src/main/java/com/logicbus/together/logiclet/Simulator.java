package com.logicbus.together.logiclet;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.w3c.dom.Element;

import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.Message;
import com.logicbus.together.AbstractLogiclet;
import com.logicbus.together.ExecuteWatcher;
import com.logicbus.together.LogicletFactory;


/**
 * 模拟器
 * 
 * <br>
 * 模拟一个logiclet的运行过程，耗时满足正态分布
 * 
 * @author duanyy
 * @since 1.1.0
 * 
 * @version 1.2.0 增加对JSON支持
 */
public class Simulator extends AbstractLogiclet {

	
	protected void onCompile(Element config, Properties myProps,LogicletFactory factory)
			throws ServantException {
		XmlElementProperties props = new XmlElementProperties(config,myProps);		
		avg = PropertiesConstants.getInt(props, "avg", avg);		
	}

	
	protected void onExecute(Element target, Message msg, Context ctx,ExecuteWatcher watcher)
			throws ServantException {
		int _avg = getArgument("avg",avg,target,msg,ctx);
		
		Random r = new Random();	
		int duration = (int)((r.nextGaussian()/4 + 1) * _avg);
		try {
			TimeUnit.MILLISECONDS.sleep(duration);
		}catch (Exception ex){
			
		}

		target.setAttribute("msg", "I have sleep " + duration + " ms.");
	}

	/**
	 * 获取Int型的参数
	 * @param id 参数ID
	 * @param defaultValue 缺省值
	 * @param msg 消息
	 * @param ctx 上下文
	 * @return
	 * @throws ServantException
	 */
	protected int getArgument(String id,int defaultValue,Element target,Message msg,Context ctx)
			throws ServantException{
		String value = getArgument(id, "", target,msg, ctx);
		if (value == null || value.length() <= 0){
			return defaultValue;
		}
		
		try {
			return Integer.parseInt(value);
		}catch (Exception ex){
			return defaultValue;
		}
	}
	
	@SuppressWarnings("rawtypes")
	protected int getArgument(String id,int defaultValue,Map target,Message msg,Context ctx)
			throws ServantException{
		String value = getArgument(id, "", target,msg, ctx);
		if (value == null || value.length() <= 0){
			return defaultValue;
		}
		
		try {
			return Integer.parseInt(value);
		}catch (Exception ex){
			return defaultValue;
		}
	}	
	
	/**
	 * 平均值,缺省10ms
	 */
	protected int avg = 10;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	
	protected void onExecute(Map target, Message msg, Context ctx,
			ExecuteWatcher watcher) throws ServantException {
		int _avg = getArgument("avg",avg,target,msg,ctx);
		
		Random r = new Random();	
		int duration = (int)((r.nextGaussian()/4 + 1) * _avg);
		try {
			TimeUnit.MILLISECONDS.sleep(duration);
		}catch (Exception ex){
			
		}

		target.put("msg", "I have sleep " + duration + " ms.");
	}
}
