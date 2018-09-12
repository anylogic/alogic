package com.alogic.xscript.xml;

import com.alogic.xscript.Logiclet;
import com.alogic.xscript.plugins.Segment;

/**
 * Namespace
 * @author yyduan
 * 
 * @since 1.6.11.38
 * 
 * @version 1.6.11.60 [20180912 duanyy] <br>
 * - 增加xml-text-append和xml-text-child指令，用来存取xml节点的直接文本子节点 <br>
 */
public class NS extends Segment{

	public NS(String tag, Logiclet p) {
		super(tag, p);
		
		registerModule("xml-new",XsDocNew.class);
		registerModule("xml-load",XsDocLoad.class);
		registerModule("xml-save",XsDocSave.class);
		registerModule("xml-attr-set",XsAttrSet.class);
		registerModule("xml-attr-get",XsAttrGet.class);
		registerModule("xml-location",XsLocation.class);
		registerModule("xml-elem",XsElement.class);
		registerModule("xml-children",XsChildren.class);
		registerModule("xml-append",XsAppend.class);
		registerModule("xml-text-set",XsTextSet.class);
		registerModule("xml-text-get",XsTextGet.class);
		registerModule("xml-text-append",XsTextAppend.class);
		registerModule("xml-text-child",XsTextChild.class);
	}

}
