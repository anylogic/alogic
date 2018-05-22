package com.alogic.lucene.xscript.doc;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;

import com.alogic.lucene.xscript.XsDocOperation;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 向IndexWriter提交document文档
 * 
 * @author yyduan
 * @since 1.6.11.31
 */
public class DocCommit extends XsDocOperation{
	/**
	 * IndexWriter的对象id
	 */
	protected String writerId =  "$indexer-writer";
	
	protected String $idField;
	
	protected boolean update = true;
	
	public DocCommit(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		writerId = PropertiesConstants.getString(p, "writer", writerId,true);
		update = PropertiesConstants.getBoolean(p,"update",update,true);
		$idField = PropertiesConstants.getRaw(p, "idField", "");
	}

	@Override
	protected void onExecute(Document doc, XsObject root, XsObject current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		IndexWriter writer = ctx.getObject(writerId);
		if (writer == null){
			throw new BaseException("core.e1001","It must be in a lucene-writer context,check your together script.");
		}		
		try {
			String idField = PropertiesConstants.transform(ctx, $idField, "");
			String idValue = doc.get(idField);
			
			if (StringUtils.isNotEmpty(idField) && StringUtils.isNotEmpty(idValue)){
				//通过id字段查找文档，如果已经存在，就进行删除
				if (update){
					writer.updateDocument(new Term(idField,idValue), doc);
				}else{
					writer.deleteDocuments(new Term(idField,idValue));
					writer.addDocument(doc);
				}
			}else{
				writer.addDocument(doc);
			}
		} catch (IOException e) {
			logger.error("Can not commit doc to writer.");
			logger.error(ExceptionUtils.getStackTrace(e));
		}
	}

}
