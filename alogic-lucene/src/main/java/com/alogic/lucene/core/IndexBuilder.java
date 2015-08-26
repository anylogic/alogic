package com.alogic.lucene.core;

import java.io.IOException;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.w3c.dom.Element;

import com.anysoft.util.BaseException;
import com.anysoft.util.Configurable;
import com.anysoft.util.Properties;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;


/**
 * Index构建器
 * 
 * @author duanyy
 * 
 * @since 1.6.4.1
 * 
 */
public interface IndexBuilder extends Configurable,XMLConfigurable,Reportable{
	
	/**
	 * 通过Writer来构建索引
	 * 
	 * @param writer IndexWriter
	 */
	public void build(IndexWriter writer);
	
	/**
	 * 缺省实现
	 * 
	 * @author duanyy
	 * @since 1.6.4.1
	 */
	abstract public static class Abstract implements IndexBuilder{
		protected static final Logger logger = LogManager.getLogger(IndexBuilder.class);
		protected long docCnt = 0;
		
		public void configure(Properties p) throws BaseException {
			
		}

		public void configure(Element _e, Properties _properties)
				throws BaseException {
			Properties p = new XmlElementProperties(_e,_properties);
			configure(p);
		}

		public void report(Element xml) {
			if (xml != null){
				xml.setAttribute("docCnt", String.valueOf(docCnt));
			}
		}

		public void report(Map<String, Object> json) {
			if (json != null){
				json.put("docCnt", docCnt);
			}
		}

		public void build(IndexWriter writer) {
			docCnt = 0;
			onBuild(writer);
		}

		abstract protected void onBuild(IndexWriter writer);

		protected Document newDocument(){
			return new Document();
		}
		
		protected void commitDocument(IndexWriter writer,Document doc)throws IOException{
			docCnt ++;
			writer.addDocument(doc);
		}
	}

}
