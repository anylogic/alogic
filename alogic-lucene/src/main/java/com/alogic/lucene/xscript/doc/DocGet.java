package com.alogic.lucene.xscript.doc;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
import com.alogic.lucene.xscript.XsDocOperation;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 读取文档值
 * @author yyduan
 * @since 1.6.11.31
 */
public class DocGet extends XsDocOperation{
	
	protected String $id;
	protected String $field;
	protected String $dft;
	
	public DocGet(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		$id = PropertiesConstants.getRaw(p,"id",$id);
		$field = PropertiesConstants.getRaw(p,"field",$id);
		$dft = PropertiesConstants.getRaw(p,"dft", $dft);
	}

	@Override
	protected void onExecute(Document doc, XsObject root, XsObject current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		String id = PropertiesConstants.transform(ctx, $id, "");
		String field = PropertiesConstants.transform(ctx, $field, $id);
		String dft = PropertiesConstants.transform(ctx, $dft, "");
		
		if (StringUtils.isNotEmpty(id)){
			String value = doc.get(field);
			if (StringUtils.isEmpty(value)){
				value = dft;
			}
			if (StringUtils.isNotEmpty(value)){
				ctx.SetValue(id, value);
			}
		}
	}

}

