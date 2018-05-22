package com.alogic.xscript;

import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.log.Default;
import com.alogic.xscript.log.LogInfo;
import com.alogic.xscript.plugins.Segment;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;

/**
 * Script
 * @author duanyy
 * 
 * @version 1.6.11.4 [20171222 duanyy] <br>
 * - 增加Container辅助插件<br>
 * 
 * @version 1.6.11.30 [20180514 duanyy] <br>
 * - 增加cookies操作接口 <br>
 */
public class Script extends Segment {
	
	public Script(String tag){
		super(tag, Library.get());
	}
	
	public Script(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void log(LogInfo logInfo){
		if (logHandler == null){
			/**
			 * 当脚本没有配置logger的时候，创建一个缺省的Logger
			 */
			synchronized (this){
				if (logHandler == null){ // NOSONAR
					logHandler = new Default();
				}
			}			
		}
		
		logHandler.handle(logInfo, System.currentTimeMillis());
	}		
	
	/**
	 * 根据XML创建服务脚本
	 * 
	 * @param root XML配置节点
	 * @param p 变量集
	 * @return 脚本实例
	 */
	public static Script create(Element root,Properties p){
		Script script = null;
		
		if (root != null){
			script = new Script("script",Library.get());
			script.configure(root, p);
		}
		
		return script;
	}
	
	/**
	 * 根据XML配置文件的位置创建服务脚本
	 * @param src XML配置文件位置
	 * @param p 变量集
	 * @return 脚本实例
	 */
	public static Script create(String src,Properties p){
		Script script = null;
		
		if (StringUtils.isNotBlank(src)){
			ResourceFactory resourceFactory = Settings.getResourceFactory();
			InputStream in = null;
			try {
				in = resourceFactory.load(src,null);
				Document doc = XmlTools.loadFromInputStream(in);
				
				if (doc != null){
					script = new Script("script",Library.get());
					script.configure(doc.getDocumentElement(), p);
				}
			}catch (Exception ex){
				logger.error("The config file is not a valid file,url = " + src,ex);
			}finally{
				IOTools.close(in);
			}			
		}
		
		return script;		
	}
	
	public static Script create(String bootstrap,String path,Properties p){
		Script script = null;
		
		if (StringUtils.isNotBlank(bootstrap)){
			InputStream in = null;
			try {
				Class<?> clazz = Settings.getClassLoader().loadClass(bootstrap);
				in = clazz.getResourceAsStream(path);
				Document doc = XmlTools.loadFromInputStream(in);
				if (doc != null){
					script = new Script("script",Library.get());
					script.configure(doc.getDocumentElement(), p);
				}
			}catch (Exception ex){
				logger.error("The config file is not a valid file,url = " + path + "#" + bootstrap,ex);
			}finally{
				IOTools.close(in);
			}			
		}
		
		return script;			
	}
	
	/**
	 * XScript库，用于加载全局的Function
	 * 
	 * @author yyduan
	 *
	 */
	public static class Library extends Script{
		/**
		 * 全局唯一实例
		 */
		protected static Script script = null;
		
		protected Library(String tag, Logiclet p) {
			super(tag, p);
		}
		
		public static Script get(){
			if (script == null){
				synchronized (Library.class){
					if (script == null){
						script = new Library("libary",null);
						configure(script,Settings.get());
					}
				}
			}
			
			return script;
		}

		protected static void configure(Script self, Properties p) {
			ResourceFactory resourceFactory = Settings.getResourceFactory();
			InputStream in = null;
			String src = PropertiesConstants.getString(p, "script.library", null);
			
			if (StringUtils.isNotEmpty(src)){
				try {
					in = resourceFactory.load(src,null);
					Document doc = XmlTools.loadFromInputStream(in);
					
					if (doc != null){
						self.configure(doc.getDocumentElement(), p);
					}
				}catch (Exception ex){
					logger.error("The config file is not a valid file,url = " + src,ex);
				}finally{
					IOTools.close(in);
				}
			}
		}
	}
	
	/**
	 * 脚本容器
	 * @author yyduan
	 *
	 */
	public static class Container extends AbstractLogiclet{
		protected Script script = null;
		
		public Container(String tag){
			super(tag,Script.Library.get());
		}
		
		public Container(String tag, Logiclet p) {
			super(tag, p);
		}
		
		@Override
		public void configure(Element e, Properties p) {
			Properties props = new XmlElementProperties(e,p);
			
			Element elem = XmlTools.getFirstElementByPath(e, "script");
			if (elem != null){
				script = new Script("script",this);
				script.configure(elem, props);
			}else{
				logger.error("Can not find script element in " + XmlTools.node2String(e));
			}
			
			configure(props);
		}
		
		@Override
		public void execute(XsObject root,XsObject current,LogicletContext ctx,ExecuteWatcher watcher){
			if (script != null){
				script.execute(root, current, ctx, watcher);
			}
		}
	}
}
