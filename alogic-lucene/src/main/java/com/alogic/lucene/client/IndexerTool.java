package com.alogic.lucene.client;

import java.util.Map;

import org.w3c.dom.Element;

import com.alogic.lucene.context.IndexerSource;
import com.alogic.lucene.core.Indexer;


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
		IndexerSource src = IndexerSource.get();
		return src.get(id);
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
		IndexerSource src = IndexerSource.get();
		return src.current();
	}
	
	/**
	 * 报告Context配置及使用情况
	 * 
	 * @param json 输出
	 */
	public static void report(Map<String,Object> json){
		IndexerSource src = IndexerSource.get();
		src.report(json);
	}
	
	/**
	 * 报告Context配置及使用情况
	 * 
	 * @param element 输出
	 */
	public static void report(Element element){
		IndexerSource src = IndexerSource.get();
		src.report(element);
	}
}
