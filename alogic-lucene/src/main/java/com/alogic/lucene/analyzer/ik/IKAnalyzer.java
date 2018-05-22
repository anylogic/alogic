package com.alogic.lucene.analyzer.ik;
import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.wltea.analyzer.configuration.DictionaryConfiguration;
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
	
	public IKAnalyzer(){

	}
	
	public IKAnalyzer(DictionaryConfiguration conf){
		this.dicConf = conf;
	}
	
	public void setDictionaryConfiguration(DictionaryConfiguration conf){
		this.dicConf = conf;
	}
	
	@Override
	protected TokenStreamComponents createComponents(String text) {
		Reader reader = new BufferedReader(new StringReader(text));  
        Tokenizer _IKTokenizer = new IKTokenizer(reader,dicConf);  
        return new TokenStreamComponents(_IKTokenizer);  
	}

	@Override
	public void configure(Element e, Properties p) {
		Properties props = new XmlElementProperties(e,p);
		try {
			Factory<DictionaryConfiguration> f = new Factory<DictionaryConfiguration>();
			dicConf = f.newInstance(e, p, "dic", FromFile.class.getName());
		}catch (Exception ex){
			LOG.error("can not create dic loader:" + XmlTools.node2String(e));
		}		
		configure(props);
	}

	@Override
	public void configure(Properties p) {
		if (dicConf == null){
			String dicLoader = PropertiesConstants.getString(p,"dic",FromFile.class.getName());
			try {
				Factory<DictionaryConfiguration> f = new Factory<DictionaryConfiguration>();
				dicConf = f.newInstance(dicLoader, p);
			}catch (Exception ex){
				LOG.error("can not create dic loader:" + dicLoader);
			}				
		}
	}

}
