package com.alogic.cube.xscript;

import com.alogic.xscript.Logiclet;
import com.alogic.xscript.plugins.Segment;

/**
 * namespace
 * @author yyduan
 * @since 1.6.11.35
 */
public class NS extends Segment{

	public NS(String tag, Logiclet p) {
		super(tag, p);
		
		registerModule("cube",XsTable.class);
		registerModule("cube-row",XsSetDim.Row.class);
		registerModule("cube-col",XsSetDim.Column.class);
		registerModule("cube-measure",XsAddMeasure.class);
		registerModule("cube-cell",XsCell.class);
		registerModule("cube-cell-total",XsTotalCell.class);
		registerModule("cube-header",XsHeader.class);
		registerModule("cube-value",XsValue.class);
		registerModule("cube-sum",XsSum.class);
		registerModule("cube-report",XsReport.class);
	}

}
