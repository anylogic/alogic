package com.alogic.lucene.analyzer.ik;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.wltea.analyzer.configuration.DictionaryConfiguration;

import com.anysoft.util.Configurable;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;

/**
 * 字典装载器
 * 
 * @author yyduan
 * @since 1.6.11.31
 */
public interface DictionaryLoader extends DictionaryConfiguration,Configurable,XMLConfigurable{

	/**
	 * 虚基类
	 * @author yyduan
	 *
	 */
	public abstract static class Abstract implements DictionaryLoader{
		
		/**
		 * a logger of slf4j
		 */
		protected final static Logger LOG = LoggerFactory.getLogger(DictionaryLoader.class);
		
		/**
		 * smartMode
		 */
		private boolean smartMode = true;
		
		@Override
		public boolean isSmartMode() {
			return smartMode;
		}

		@Override
		public void setSmartMode(boolean useSmart) {
			smartMode = useSmart;
		}
		
		@Override
		public void configure(Element e, Properties p) {
			Properties props = new XmlElementProperties(e,p);
			configure(props);
		}

		@Override
		public void configure(Properties p) {
			smartMode = PropertiesConstants.getBoolean(p, "smartMode", smartMode);
		}
	}
}
