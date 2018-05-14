package com.alogic.timer.core;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.Script;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.json.JsonObject;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;


/**
 * 脚本处理者
 * 
 * @author duanyy
 * @since 1.6.3.37
 * 
 * @version 1.6.3.38 [duanyy 20150812] <br>
 * - 去掉编译和执行日志 <br>
 * 
 * @version 1.6.4.16 [duanyy 20151110] <br>
 * - 根据sonar建议优化代码 <br>
 * 
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
 * 
 * @version 1.6.9.2 [20170601 duanyy] <br>
 * - 改造TaskCenter模型，以便提供分布式任务处理支持; <br>
 * 
 * @version 1.6.11.30 [20180514 duanyy] <br>
 * - 增加全局xscript脚本函数库 <br>
 */
public class ScriptDoer extends Doer.Abstract{
	protected Logiclet stmt = null;
	
	@Override
	public void execute(Task task) {
		if (stmt == null){
			LOG.error("The script is null");
			return ;
		}		
		try {
			// 向队列报告任务已经开始
			onStart(task.id(),Task.State.Running, 0,"");
			Map<String,Object> root = new HashMap<String,Object>();
			XsObject doc = new JsonObject("root",root);
			
			LogicletContext ctx = new LogicletContext(task.getParameters());
			ctx.SetValue("$task", task.id());
			ctx.SetValue("$event", task.getEventId());
			// 执行任务
			stmt.execute(doc, doc, ctx, null);
			// 任务完成
			onFinish(task.id(),Task.State.Done, 10000,"");
		} catch (Exception t) {
			LOG.error("Failed to execute script", t);
			// 任务失败
			onFinish(task.id(),Task.State.Failed, 10000,"Failed to execute script:" + t.getMessage());
		}
	}

	@Override
	public void configure(Element _e, Properties _properties){
		Properties p = new XmlElementProperties(_e,_properties);
		configure(p);		
		
		Element script = XmlTools.getFirstElementByPath(_e, "script");
		if (script != null){
			stmt = Script.create(script, p);
		}
	}
}
