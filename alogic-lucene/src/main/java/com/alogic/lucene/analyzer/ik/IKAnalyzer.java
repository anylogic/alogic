package com.alogic.lucene.analyzer.ik;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alogic.ha.FailoverController.Null;
import com.alogic.ik.configuration.DictionaryConfiguration;
import com.alogic.ik.dic.Dictionary;
import com.alogic.lucene.analyzer.ik.dic.FromFile;
import com.anysoft.util.Configurable;
import com.anysoft.util.Factory;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * IK分词器
 * 
 * @author yyduan
 * @since 1.6.11.31
 */
public class IKAnalyzer extends Analyzer implements Configurable,XMLConfigurable{
	/**
	 * a logger of slf4j
	 */
	protected final static Logger LOG = LoggerFactory.getLogger(IKAnalyzer.class);
	
	private DictionaryConfiguration dicConf = null;
	
	private Dictionary dic = new Dictionary();
	
	private boolean smartMode = true;
	
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
		
		configure(props);
		
		NodeList nodeList = XmlTools.getNodeListByPath(e, "dic-loader");
		for (int i = 0 ;i < nodeList.getLength() ; i ++){
			Node node = nodeList.item(i);
			
			if (Node.ELEMENT_NODE != node.getNodeType()){
				continue;
			}
			
			Element elem = (Element)node;			
			try {			
				DictionaryConfiguration conf = f.newInstance(elem, p, "dic", Null.class.getName());
				if (conf != null){
					dic.addConfiguration(conf);
				}
			}catch (Exception ex){
				LOG.error("can not create dic loader:" + XmlTools.node2String(e));
			}
		}
	}

	@Override
	public void configure(Properties p) {
		
		smartMode = PropertiesConstants.getBoolean(p, "smartMode", true);
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
	}

}
