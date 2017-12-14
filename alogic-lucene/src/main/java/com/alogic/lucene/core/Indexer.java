package com.alogic.lucene.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.anysoft.util.Configurable;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;


/**
 * Index库
 * 
 * @author duanyy
 * @since 1.6.4.1
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 */
public interface Indexer extends Configurable,XMLConfigurable,Reportable{
	
	/**
	 * 获取本库的Analyzer
	 * @return Analyzer
	 */
	public Analyzer getAnalyzer();
	
	/**
	 * 获取本库的Directory
	 * @return Directory
	 */
	public Directory getDirectory();
	
	/**
	 * 新建Query
	 * @param field 缺省field
	 * @param querystr 查询字符串
	 * @return Query
	 */
	public Query newQuery(String field,String querystr);
	
	/**
	 * 新建Reader
	 * @return Reader
	 */
	public IndexReader newReader();
	
	/**
	 * 新建Writer
	 * @return Writer
	 */
	public IndexWriter newWriter(boolean create);
	
	/**
	 * 建索引
	 * @param create 是否重建
	 */
	public void build(boolean create);
	
	/**
	 * 加入IndexBuilder
	 * @param builder IndexBuilder
	 */
	public void addBuilder(IndexBuilder builder);
	
	/**
	 * 删除IndexBuilder
	 * @param builder IndexBuilder
	 */
	public void removeBuilder(IndexBuilder builder);
	
	/**
	 * Abstract实现
	 * @author duanyy
	 * 
	 * @since 1.6.4.1
	 */
	abstract public static class Abstract implements Indexer{
		/**
		 * a logger of log4j
		 */
		protected static Logger logger = LoggerFactory.getLogger(Indexer.class);
		
		/**
		 * index builders
		 */
		private List<IndexBuilder> builders = new ArrayList<IndexBuilder>();
		
		/**
		 * analyzer class name
		 */
		private String analyzer = SmartChineseAnalyzer.class.getName();
		
		public void configure(Properties p) {
			analyzer = PropertiesConstants.getString(p,"analyzer", analyzer);
		}
		
		public void configure(Element _e, Properties _properties) {
			Properties p = new XmlElementProperties(_e,_properties);
			
			NodeList nodeList = XmlTools.getNodeListByPath(_e, "builder");
			
			Factory<IndexBuilder> factory = new Factory<IndexBuilder>();
			
			for (int i = 0 ;i < nodeList.getLength() ; i ++){
				Node n = nodeList.item(i);
				
				if (n.getNodeType() != Node.ELEMENT_NODE){
					continue;
				}
				
				Element e = (Element)n;
				
				try {
					IndexBuilder newBuilder = factory.newInstance(e, p, "module");
					if (newBuilder != null){
						builders.add(newBuilder);
					}
				}catch (Exception ex){
					logger.error("Can not create index builder",ex);
				}
			}
			
			configure(p);
		}
		
		public void build(boolean create) {
			IndexWriter writer = newWriter(create);
			if (writer != null){
				try {
					logger.info("Start to build index..");
					logger.info("Create = " + create);
					logger.info("Builders = " + builders.size());
					
					for (IndexBuilder builder:builders){
						if (builder != null){
							builder.build(writer);
						}
					}
					writer.commit();
				}catch (Exception ex){
					logger.error("Failed to commit indexes",ex);
				}finally{
					logger.info("End.");
					IOTools.close(writer);
				}
			}
		}

		public void addBuilder(IndexBuilder builder) {
			builders.add(builder);
		}

		public void removeBuilder(IndexBuilder builder) {
			builders.remove(builder);
		}

		public void report(Element xml) {
			if (xml != null){
				Document doc = xml.getOwnerDocument();
				
				for (IndexBuilder builder:builders){
					Element _builder = doc.createElement("builder");
					
					builder.report(_builder);
					
					xml.appendChild(_builder);
				}
			}
		}

		public void report(Map<String, Object> json) {
			if (json != null){
				List<Object> _builders = new ArrayList<Object>();
				
				for (IndexBuilder builder:builders){
					Map<String,Object> _builder = new HashMap<String,Object>();
					
					builder.report(_builder);
					
					_builders.add(_builder);
				}
				
				json.put("builder", _builders);
			}
		}
		
		@Override
		public Analyzer getAnalyzer() {
			try {
				return analyzerFactory.newInstance(analyzer);
			}catch (Exception ex){
				logger.error("Can not create analyzer,use default");
				return new SmartChineseAnalyzer();
			}
		}
		
		protected static Factory<Analyzer> analyzerFactory = new Factory<Analyzer>();
		
		public Query newQuery(String field, String querystr) {
			try {
				return new QueryParser(field,getAnalyzer()).parse(querystr);
			} catch (ParseException ex) {
				logger.error("Can not create query",ex);
				return null;
			}
		}

		public IndexReader newReader() {
			try {
				return DirectoryReader.open(getDirectory());
			} catch (IOException ex) {
				logger.error("Can not open index",ex);
				return null;
			}
		}

		public IndexWriter newWriter(boolean create) {
			IndexWriterConfig config = new IndexWriterConfig(getAnalyzer())
				.setOpenMode(create?OpenMode.CREATE:OpenMode.APPEND);
			try {
				return new IndexWriter(getDirectory(),config);
			} catch (IOException ex) {
				logger.error("Can not create index writer",ex);
				return null;
			}
		}
	}
}
