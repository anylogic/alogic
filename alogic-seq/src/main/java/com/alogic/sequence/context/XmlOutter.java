package com.alogic.sequence.context;

import com.alogic.sequence.core.SequenceGenerator;
import com.anysoft.context.XMLResource;


/**
 * XmlOutter
 * @author yyduan
 * @deprecated
 */
public class XmlOutter extends XMLResource<SequenceGenerator>{

	@Override
	public String getObjectName() {
		return "seq";
	}

	@Override
	public String getDefaultClass() {
		return SequenceGenerator.Simple.class.getName();
	}

	@Override
	public String getDefaultXrc() {
		return "java:///com/alogic/sequence/context/seq.default.xml#com.alogic.sequence.context.XmlOutter";
	}

}
