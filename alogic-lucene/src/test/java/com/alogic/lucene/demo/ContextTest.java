package com.alogic.lucene.demo;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;

import com.alogic.lucene.client.IndexerTool;
import com.alogic.lucene.core.Indexer;
import com.anysoft.util.IOTools;
import com.anysoft.util.Settings;


public class ContextTest {

	public static void main(String[] args) {
		Settings settings = Settings.get();
		
		settings.SetValue("indexer.master", "java:///indexer.xml");
		
		Indexer indexer = IndexerTool.getIndexer();
		
		if (indexer != null){
			IndexReader reader = indexer.newReader();
			try {
				IndexSearcher searcher = new IndexSearcher(reader);
				Query query = indexer.newQuery("title", "lu Dummies");
				
				TopScoreDocCollector collector = TopScoreDocCollector.create(10);
				searcher.search(query, collector);
				ScoreDoc[] hits = collector.topDocs().scoreDocs;
				    
				for (ScoreDoc doc:hits){
					 int docId = doc.doc;
				     Document d = searcher.doc(docId);
				     System.out.println(d.get("title") + d.get("isbn"));
				}
			}catch (Exception ex){
				ex.printStackTrace();
			}finally{
				IOTools.close(reader);
			}
		}
	}

}
