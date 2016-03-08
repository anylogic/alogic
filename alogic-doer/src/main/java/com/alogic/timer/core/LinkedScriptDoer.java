package com.alogic.timer.core;

import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;
import com.anysoft.xscript.Script;
import com.anysoft.xscript.ScriptLogInfo;
import com.anysoft.xscript.Statement;

/**
 * 外部脚本处理者
 * 
 * @author duanyy
 *
 * @since 1.6.4.34
 * 
 */
public class LinkedScriptDoer extends Doer.Abstract{
	
	/**
	 * 脚本地址
	 */
	protected String src = null;
	
	@Override
	public void execute(Task task) {
		try {
			// 向队列报告任务已经开始
			reportState(Task.State.Running, 0);
			if (StringUtils.isEmpty(src)) {
				LOG.error("Can not find script source.");
				reportState(Task.State.Failed, 10000);
			} else {
				//装入脚本
				Document doc = loadDocument(src);
				Statement stmt = new TheScript(this, "script", null);
				stmt.compile(doc.getDocumentElement(), task.getParameters(), null);	
				// 执行任务
				stmt.execute(task.getParameters(), null);
				// 任务完成
				reportState(Task.State.Done, 10000);
			}
		} catch (Exception t) {
			LOG.error("Failed to execute script", t);
			// 任务失败
			reportState(Task.State.Failed, 10000);
		}	
	}

	@Override
	public void configure(Element _e, Properties _properties){
		Properties p = new XmlElementProperties(_e,_properties);
		src = PropertiesConstants.getString(p, "src",src);
		configure(p);
	}

	private Document loadDocument(String src) {
		ResourceFactory rm = Settings.getResourceFactory();
		InputStream in = null;
		try {
			in = rm.load(src,null, null);
			return XmlTools.loadFromInputStream(in);
		} catch (Exception ex){
			LOG.error("Error occurs when load xml file,source=" + src, ex);
		}finally {
			IOTools.closeStream(in);
		}
		return null;
	}

	/**
	 * 处理进度报告
	 * 
	 * @param logInfo 日志信息
	 */
	public void report(ScriptLogInfo logInfo) {
		Task currentTask = getCurrentTask();
		if (currentTask != null){
			//当前有任务处理才有意义
			String id = logInfo.activity();
			String taskId = currentTask.id();
			
			if (id.equals(taskId)){
				//报告的是整个任务的进度
				int progress = logInfo.progress();
				
				if (progress >= -1){
					//百分比进度
					reportState(Task.State.Running, progress);
				}
			}
		}
	}	
	
	/**
	 * TheScript
	 * <p>
	 * 对xscript中的Script进行了重写，重点在截取日志信息。
	 * 
	 * @author duanyy
	 *
	 */
	protected static class TheScript extends Script{
		protected LinkedScriptDoer doer = null;
		
		public TheScript(LinkedScriptDoer _doer,String xmlTag, Statement _parent) {
			super(xmlTag, _parent);
			doer = _doer;
		}
		@Override
		public void log(ScriptLogInfo logInfo){
			super.log(logInfo);
			if (doer != null){
				doer.report(logInfo);
			}			
		}
	}
}
