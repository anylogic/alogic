package com.anysoft.stream;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.Script;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.json.JsonObject;
import com.anysoft.util.DataProviderProperties;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;

/**
 * 基于XScript的处理器
 * 
 * @author yyduan
 *
 * @param <data>
 * 
 * @since 1.6.6.13
 * 
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
 * 
 * @version 1.6.11.30 [20180514 duanyy] <br>
 * - 增加全局xscript脚本函数库 <br>
 * 
 */
public class ScriptHandler <data extends Flowable> extends SlideHandler<data> {
	/**
	 * 脚本
	 */
	protected Logiclet stmt = null;
	@Override
	protected void onConfigure(Element e, Properties p) {
		super.onConfigure(e, p);
		Element script = XmlTools.getFirstElementByPath(e, "script");
		if (script != null){
			stmt = Script.create(script, p);
		}else{
			String src = PropertiesConstants.getString(p,"xrc","");
			if (StringUtils.isNotEmpty(src)){
				Document doc = loadDocument(src);
				if (doc != null){
					stmt = Script.create(doc.getDocumentElement(), p);
				}
			}else{
				LOG.error("Can not find script to run.");
			}
		}
	}

	@Override
	protected void onHandle(data _data, long timestamp) {
		if (stmt == null){
			LOG.error("The script is null");
			return ;
		}		
		try {
			// 向队列报告任务已经开始
			Map<String, Object> root = new HashMap<String, Object>();
			XsObject doc = new JsonObject("root",root);
			DataProviderProperties p = new DataProviderProperties(_data);
			LogicletContext ctx = new LogicletContext(p);
			// 执行任务
			stmt.execute(doc, doc, ctx, null);
		} catch (Exception t) {
			LOG.error("Failed to execute script", t);
		}
	}

	@Override
	protected void onFlush(long timestamp) {
		// nothing to do
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
}
