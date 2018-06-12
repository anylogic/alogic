package com.alogic.poi.xscript;

import com.alogic.xscript.Logiclet;
import com.alogic.xscript.plugins.Segment;

/**
 * 
 * @author yyduan
 * @since 1.6.11.35
 */
public class NS extends Segment{

	public NS(String tag, Logiclet p) {
		super(tag, p);
		
		registerModule("xls",XsHSSFWorkbook.class);
		registerModule("xls-summary",XsHSSFSummary.class);
		registerModule("xls-sheet",XsWorksheet.class);
		registerModule("xls-row",XsRow.class);
		registerModule("xls-cell",XsCell.class);
		registerModule("xls-numeric",XsCell.NumericValue.class);
		registerModule("xls-localfile",XsAsLocalFile.class);
		registerModule("xls-service",XsAsServant.class);
		registerModule("xls-merge",XsMergedRegion.class);
		registerModule("xls-style",XsStyleManager.class);
		registerModule("xls-style-template",XsStyleManager.Template.class);
		registerModule("xls-column-width",XsColumnWidth.class);
		
		registerModule("xlsx",XsXSSFWorkbook.class);
		registerModule("xlsx-sheet",XsWorksheet.class);
		registerModule("xlsx-row",XsRow.class);
		registerModule("xlsx-cell",XsCell.class);
		registerModule("xlsx-numeric",XsCell.NumericValue.class);
		registerModule("xlsx-localfile",XsAsLocalFile.class);	
		registerModule("xlsx-service",XsAsServant.class);
		registerModule("xlsx-merge",XsMergedRegion.class);
		registerModule("xlsx-style",XsStyleManager.class);
		registerModule("xlsx-style-template",XsStyleManager.Template.class);
		registerModule("xlsx-column-width",XsColumnWidth.class);
	}

}
