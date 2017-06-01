package com.alogic.doer.xscript;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alogic.doer.client.TaskSubmitter;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.DefaultProperties;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.Pair;
import com.anysoft.util.XmlTools;

/**
 * 提交任务脚本插件
 * 
 * @author yyduan
 * @version 1.6.9.2 [20170601 duanyy] <br>
 * - 改造TaskCenter模型，以便提供分布式任务处理支持; <br>
 */
public class Submitter extends AbstractLogiclet{
	protected String task = "";
	protected String queue = "default";
	protected String event = "default";
	protected List<Pair<String,String>> parameters = new ArrayList<Pair<String,String>>();
	
	public Submitter(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		 
		task = PropertiesConstants.getRaw(p,"id",task);
		queue = PropertiesConstants.getRaw(p,"queue",queue);
		event = PropertiesConstants.getRaw(p,"event",event);
	}
	
	@Override
	public void configure(Element e, Properties p) {
		Properties props = new XmlElementProperties(e,p);
		configure(props);
		
		NodeList nodeList = XmlTools.getNodeListByPath(e, "parameter");
		for (int i = 0 ;i < nodeList.getLength() ; i ++){
			Node n = nodeList.item(i);
			if (n.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			
			Element elem = (Element)n;
			String id = elem.getAttribute("id");
			String value = elem.getAttribute("value");
			
			if (StringUtils.isNotEmpty(id) && StringUtils.isNotEmpty(value)){
				parameters.add(new Pair.Default<String, String>(id,value));
			}
		}
	}

	protected void onExecute(XsObject root,XsObject current,final LogicletContext ctx,final ExecuteWatcher watcher){
		String taskId = ctx.transform(task);
		String queueId = ctx.transform(queue);
		String eventId = ctx.transform(event);
		
		DefaultProperties props = new DefaultProperties();
		for (Pair<String,String> pair:parameters){
			String value = ctx.transform(pair.value());
			String id = pair.key();
			if (StringUtils.isNotEmpty(id) && StringUtils.isNotEmpty(value)){
				props.SetValue(id, value);
			}
		}
		
		if (StringUtils.isEmpty(taskId)){
			TaskSubmitter.submit(eventId, queueId, props);
		}else{
			TaskSubmitter.submit(eventId, queueId, taskId, props);
		}
	}
}
