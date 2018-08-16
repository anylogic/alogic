package com.alogic.lucene.xscript;

import com.alogic.lucene.xscript.doc.DocCommit;
import com.alogic.lucene.xscript.doc.DocGet;
import com.alogic.lucene.xscript.doc.DocSetLong;
import com.alogic.lucene.xscript.doc.DocSetString;
import com.alogic.lucene.xscript.doc.DocSetText;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.plugins.Segment;

/**
 * Namespace
 * @author yyduan
 * @since 1.6.11.31
 * 
 * @version 1.6.11.52 [20180816 duanyy] <br>
 * - 增加lucene-doc-del插件 <br>
 */
public class NS extends Segment{

	public NS(String tag, Logiclet p) {
		super(tag, p);
		
		registerModule("lucene",XsIndexer.class);
		registerModule("lucene-build",XsBuild.class);
		registerModule("lucene-writer",XsWriter.class);
		registerModule("lucene-reader",XsReader.class);
		registerModule("lucene-segment",XsSegment.class);
		
		registerModule("lucene-doc-del",XsDocDelete.class);
		registerModule("lucene-doc",XsDoc.class);
		registerModule("lucene-doc-commit",DocCommit.class);
		registerModule("lucene-doc-string",DocSetString.class);
		registerModule("lucene-doc-text",DocSetText.class);
		registerModule("lucene-doc-long",DocSetLong.class);
		registerModule("lucene-doc-get",DocGet.class);
	}
}
