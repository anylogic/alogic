package com.alogic.sequence.context;

import com.alogic.sequence.core.SequenceGenerator;
import com.anysoft.context.Inner;

public class XmlInner extends Inner<SequenceGenerator> {

	@Override
	public String getObjectName() {
		return "seq";
	}

	@Override
	public String getDefaultClass() {
		return SequenceGenerator.Simple.class.getName();
	}

}
