package com.alogic.lucene.demo;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;

import com.alogic.lucene.core.IndexBuilder;


public class MyIndexBuilder extends IndexBuilder.Abstract{

	protected void onBuild(IndexWriter writer) {
		try {
			{
				Document doc = newDocument();
			
				doc.add(new TextField("title", "Lucene in Action", Field.Store.YES));
				doc.add(new StringField("isbn", "193398817", Field.Store.YES));
				
				commitDocument(writer,doc);
			}
			{
				Document doc = newDocument();
			
				doc.add(new TextField("title", "Lucene for Dummies", Field.Store.YES));
				doc.add(new StringField("isbn", "55320055Z", Field.Store.YES));
				
				commitDocument(writer,doc);
			}
			{
				Document doc = newDocument();
			
				doc.add(new TextField("title", "Managing Gigabytes", Field.Store.YES));
				doc.add(new StringField("isbn", "55063554A", Field.Store.YES));
				
				commitDocument(writer,doc);
			}
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}

}
