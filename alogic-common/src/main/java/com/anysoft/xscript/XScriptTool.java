package com.anysoft.xscript;

import java.io.InputStream;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;

/**
 * 工具类
 * @author duanyy
 * @since 1.6.3.22
 */
public class XScriptTool {
	/**
	 * a logger of log4j
	 */
	public static final Logger logger = LogManager.getLogger(XScriptTool.class);
	
	/**
	 * 将Xml编译为语句
	 * 
	 * @param root XML节点
	 * @param p 编译参数
	 * @return 语句实例
	 */
	public static Statement compile(Element root,Properties p) {
		Script script = null;
		
		if (root != null){
			script = new Script("script",null);
			script.configure(root, p);
		}
		
		return script;
	}
	
	/**
	 * 将URL所指向的XML文档编译为语句
	 * @param url URL
	 * @param p 编译参数
	 * @return 语句实例
	 */
	public static Statement compile(String url,Properties p){
		ResourceFactory rm = Settings.getResourceFactory();
		
		InputStream in = null;
		Element root = null;
		try {
			in = rm.load(url,null);
			Document doc = XmlTools.loadFromInputStream(in);		
			root = doc.getDocumentElement();	
		}catch (Throwable ex){
			logger.error("Error occurs when load xml file,source=" + url, ex);
		}finally {
			IOTools.closeStream(in);
		}
		
		return compile(root,p);
	}
	
	/**
	 * 执行语句
	 * @param stmt 语句
	 * @param p 执行参数
	 * @param watcher 观察者
	 * @return 执行结果
	 */
	public static int execute(Statement stmt,Properties p,ExecuteWatcher watcher) {
		if (stmt != null){
			return stmt.execute(p, watcher);
		}
		return -1;
	}
}
