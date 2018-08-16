package com.alogic.lucene.xscript;

import java.io.IOException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 删除文档
 * @author yyduan
 * @since 1.6.11.52
 */
public class XsDocDelete extends XsDocOperation{
	/**
	 * IndexWriter的对象id
	 */
	protected String writerId =  "$indexer-writer";
	
	protected String $idField;
	
	protected String $idValue;
	
	public XsDocDelete(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		writerId = PropertiesConstants.getString(p, "writer", writerId,true);
		$idField = PropertiesConstants.getRaw(p, "idField", "");
		$idValue = PropertiesConstants.getRaw(p, "idValue", "");
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
			String idValue = PropertiesConstants.transform(ctx, $idValue, "");
			writer.deleteDocuments(new Term(idField,idValue));
		} catch (IOException e) {
			logger.error("Can not delete doc by writer.");
			logger.error(ExceptionUtils.getStackTrace(e));
		}
	}

}

