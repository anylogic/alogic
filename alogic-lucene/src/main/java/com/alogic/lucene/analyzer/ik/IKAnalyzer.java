package com.alogic.lucene.analyzer.ik;

import java.util.HashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.alogic.ik.configuration.DictionaryConfiguration;
import com.alogic.ik.dic.Dictionary;
import com.alogic.lucene.analyzer.ik.dic.FromFile;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.Script;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.json.JsonObject;
import com.anysoft.util.Configurable;
import com.anysoft.util.Factory;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * IK分词器
 * 
 * @author yyduan
 * @since 1.6.11.31
 * @version 1.6.11.34 [20180606 duanyy] <br>
 * - 增加脚本插件支持扩展字典 <br>
 */
public class IKAnalyzer extends Analyzer implements Configurable,XMLConfigurable,Runnable{
	/**
	 * a logger of slf4j
	 */
	protected final static Logger LOG = LoggerFactory.getLogger(IKAnalyzer.class);
	
	private DictionaryConfiguration dicConf = null;
	
	private Dictionary dic = new Dictionary();
	
	private boolean smartMode = true;
	
	/**
	 * 扩展字典加载器只加载一次
	 */
	private boolean extDicLoadOnce = false;
	
	/**
	 * 扩展的字典加载器
	 */
	private Script extDicLoader = null;
	
	/**
	 * 执行线程池
	 */
	protected static ScheduledThreadPoolExecutor exec = new  ScheduledThreadPoolExecutor(1);	
	
	public IKAnalyzer(){

	}
	
	public void addDictionaryConfiguration(DictionaryConfiguration conf){
		if (conf != null){
			dic.addConfiguration(conf);
		}
	}
	
	@Override
	protected TokenStreamComponents createComponents(String field) {
        Tokenizer _IKTokenizer = new IKTokenizer(dic,smartMode);  
        return new TokenStreamComponents(_IKTokenizer);  
	}

	@Override
	public void configure(Element e, Properties p) {
		Properties props = new XmlElementProperties(e,p);
		Factory<DictionaryConfiguration> f = new Factory<DictionaryConfiguration>();
		try {			
			dicConf = f.newInstance(e, p, "dic", FromFile.class.getName());
		}catch (Exception ex){
			LOG.error("can not create dic loader:" + XmlTools.node2String(e));
		}		
		
		Element extDic = XmlTools.getFirstElementByPath(e, "ext-dic-loader");
		if (extDic != null){
			extDicLoader = Script.create(extDic, props);
		}
		
		configure(props);
	}

	@Override
	public void configure(Properties p) {
		
		smartMode = PropertiesConstants.getBoolean(p, "smartMode", true);
		extDicLoadOnce = PropertiesConstants.getBoolean(p, "dic.load.once", false);
		if (dicConf == null){
			String dicLoader = PropertiesConstants.getString(p,"dic",FromFile.class.getName());
			try {
				Factory<DictionaryConfiguration> f = new Factory<DictionaryConfiguration>();
				dicConf = f.newInstance(dicLoader, p);
			}catch (Exception ex){
				LOG.error("can not create dic loader:" + dicLoader);
			}				
		}
		
		if (dicConf != null){
			dic.addConfiguration(dicConf);
		}
		
		if (extDicLoader != null){
			run();
			if (!extDicLoadOnce){
				//增量加载
				long interval = PropertiesConstants.getLong(p,"dic.load.interval",60*1000L);
				exec.scheduleWithFixedDelay(this, 0, interval, TimeUnit.MILLISECONDS);
			}
		}
	}

	@Override
	public void run() {
		if (extDicLoader != null){
			LogicletContext logicletContext = new LogicletContext(Settings.get());
			logicletContext.setObject("$indexer-dic",dic);
			try {
				XsObject doc = new JsonObject("root",new HashMap<String,Object>());
				extDicLoader.execute(doc,doc, logicletContext, null);
			}finally{
				logicletContext.removeObject("$indexer-dic");
			}			
		}		
	}

}
