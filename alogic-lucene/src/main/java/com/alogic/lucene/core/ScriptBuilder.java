package com.alogic.lucene.core;

import java.util.HashMap;
import org.apache.lucene.index.IndexWriter;
import org.w3c.dom.Element;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.Script;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.json.JsonObject;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * 采用脚本的Index构建器
 * @author yyduan
 * @since 1.6.11.31
 */
public class ScriptBuilder extends IndexBuilder.Abstract{
	protected Script script = null;
	
	@Override
	protected void onBuild(IndexWriter writer) {
		if (script != null){
			LogicletContext logicletContext = new LogicletContext(Settings.get());
			logicletContext.setObject("$indexer-writer",writer);
			try {
				XsObject doc = new JsonObject("root",new HashMap<String,Object>());
				script.execute(doc,doc, logicletContext, null);
			}finally{
				logicletContext.removeObject("$indexer-writer");
			}			
		}
	}

	@Override
	public void configure(Element e, Properties props){
		Properties p = new XmlElementProperties(e,props);
		
		Element elem = XmlTools.getFirstElementByPath(e, "script");
		if (elem != null){
			script = Script.create(elem, p);
		}
		
		configure(p);
	}
}
