package com.alogic.lucene.analyzer.ik.dic;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import com.alogic.lucene.analyzer.ik.DictionaryLoader;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.resource.ResourceFactory;

/**
 * 从配置文件中加载
 * 
 * @author yyduan
 * @since 1.6.11.31
 */
public class FromFile extends DictionaryLoader.Abstract{
	
	/**
	 * 缺省主字典路径
	 */
	public static final String DEFAULT_MAIN = "java:///com/alogic/lucene/analyzer/ik/main.dic#" + FromFile.class.getName();
	
	/**
	 * 缺省分词字典路径
	 */
	public static final String DEFAULT_STOPWORD = "java:///com/alogic/lucene/analyzer/ik/stopword.dic#" + FromFile.class.getName();	

	/**
	 * 缺省量词词字典路径
	 */
	public static final String DEFAULT_QUANTIFIER = "java:///com/alogic/lucene/analyzer/ik/quantifier.dic#" + FromFile.class.getName();	

	/**
	 * 文件编码
	 */
	private String encoding = "utf-8";
	
	/**
	 * 主字典
	 */
	private List<char[]> mainDic = new ArrayList<char[]>();
	
	/**
	 * 分词字典
	 */
	private List<char[]> stopwordDic = new ArrayList<char[]>();
	
	/**
	 * 量词字典
	 */
	private List<char[]> quantifierDic = new ArrayList<char[]>();
	
	@Override
	public List<char[]> getMainDictionary() {
		return mainDic;
	}

	@Override
	public List<char[]> getStopWordDictionary() {
		return stopwordDic;
	}

	@Override
	public List<char[]> getQuantifierDictionary() {
		return quantifierDic;
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		
		encoding = PropertiesConstants.getString(p, "encoding", encoding);
		
		String mainDicPath = PropertiesConstants.getString(p, "dic.main", DEFAULT_MAIN,true);
		if (StringUtils.isNotEmpty(mainDicPath)){
			loadDic(mainDic,mainDicPath);
		}
		String stopwordDicPath = PropertiesConstants.getString(p, "dic.stopword", DEFAULT_STOPWORD,true);
		if (StringUtils.isNotEmpty(mainDicPath)){
			loadDic(stopwordDic,stopwordDicPath);
		}		
		String quantifierDicPath = PropertiesConstants.getString(p, "dic.quantifier", DEFAULT_QUANTIFIER,true);
		if (StringUtils.isNotEmpty(quantifierDicPath)){
			loadDic(quantifierDic,quantifierDicPath);
		}			
	}
	
	@Override
	public void configure(Element e, Properties p) {
		Properties props = new XmlElementProperties(e,p);
		configure(props);
	}	
	
	/**
	 * 从指定路径加载字典
	 * @param holder 字典列表
	 * @param path 路径
	 */
	protected void loadDic(List<char[]> holder,String path){
		ResourceFactory rm = Settings.getResourceFactory();
		InputStream in = null;
		try {
			LOG.info("load dictionary: " + path);
			in = rm.load(path,path,null);
			BufferedReader br = new BufferedReader(new InputStreamReader(in , encoding), 512);
			String theWord = null;
			while ((theWord = br.readLine()) != null){
				if (StringUtils.isNotEmpty(theWord)){
					holder.add(theWord.trim().toLowerCase().toCharArray());
				}
			}	
		} catch (Exception ex){
			LOG.error("Error occurs when load xml file,source=" + path, ex);
		}finally {
			IOTools.closeStream(in);
		}		
	}

}
