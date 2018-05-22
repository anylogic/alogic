package com.alogic.lucene.client;

import java.util.Map;

import org.w3c.dom.Element;

import com.alogic.lucene.core.Indexer;
import com.alogic.lucene.naming.IndexerFactory;


/**
 * 工具
 * 
 * @author duanyy
 * @since 1.6.4.1
 */
public class IndexerTool {
	
	/**
	 * 获取当前Context中所定义的指定的Indexer
	 * @param id indexer id
	 * @return Indexer
	 */
	public static Indexer getIndexer(String id){
		return IndexerFactory.get(id);
	}
	
	/**
	 * 获取当前缺省的Indexer
	 * @return Indexer
	 */
	public static Indexer getIndexer(){
		return getIndexer("default");
	}
	
	/**
	 * 获取当前缓存的Indexer列表
	 * 
	 * @return Indexer列表
	 */
	public static Indexer[] list(){
		IndexerFactory src = IndexerFactory.get();
		return src.current().toArray(new Indexer[0]);
	}
	
	/**
	 * 报告Context配置及使用情况
	 * 
	 * @param json 输出
	 */
	public static void report(Map<String,Object> json){
		IndexerFactory src = IndexerFactory.get();
		src.report(json);
	}
	
	/**
	 * 报告Context配置及使用情况
	 * 
	 * @param element 输出
	 */
	public static void report(Element element){
		IndexerFactory src = IndexerFactory.get();
		src.report(element);
	}
}
