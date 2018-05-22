package com.alogic.lucene.xscript.doc;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;

import com.alogic.lucene.xscript.XsDocOperation;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 设置String类型的字段
 * 
 * @author yyduan
 * @since 1.6.11.31
 */
public class DocSetString extends XsDocOperation{
	
	protected String $field;
	protected String $value;
	protected boolean store = true;
	
	public DocSetString(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		$field = PropertiesConstants.getRaw(p,"field",$field);
		$value = PropertiesConstants.getRaw(p,"value", $value);
		store = PropertiesConstants.getBoolean(p, "store", store);
	}

	@Override
	protected void onExecute(Document doc, XsObject root, XsObject current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		String field = PropertiesConstants.transform(ctx, $field, "");
		String value = PropertiesConstants.transform(ctx, $value, "");
		
		if (StringUtils.isNotEmpty(field) && StringUtils.isNotEmpty(value)){
			doc.add(new StringField(field,value,store?Field.Store.YES:Field.Store.NO));
		}
	}

}