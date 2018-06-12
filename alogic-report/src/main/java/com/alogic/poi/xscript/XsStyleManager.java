package com.alogic.poi.xscript;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.alogic.poi.util.StyleManager;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.plugins.Segment;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 样式管理器
 * @author yyduan
 * @since 1.6.11.35
 */
public class XsStyleManager extends Segment{
	protected String cid = "$stylemanager";
	
	public XsStyleManager(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		cid = PropertiesConstants.getString(p,"cid",cid);
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		StyleManager sm = null;
		try {
			sm = new StyleManager.Default();
			ctx.setObject(cid, sm);			
			super.onExecute(root, current, ctx, watcher);
		}finally{
			if (sm != null){
				sm.clear();
			}
			ctx.removeObject(cid);
		}
	}	
	
	/**
	 * 模板，从已有的excel中获取
	 * @author yyduan
	 *
	 */
	public static class Template extends AbstractLogiclet{
		protected String smId = "$stylemanager";
		protected String pid = "$workbook";
		protected String $id = "";
		protected String $sheet = "style";
		protected String $row = "0";
		protected String $col = "0";
		
		public Template(String tag, Logiclet p) {
			super(tag, p);
		}
		
		@Override
		public void configure(Properties p){
			super.configure(p);
			pid = PropertiesConstants.getString(p,"pid",pid);
			smId = PropertiesConstants.getString(p,"smId",smId);
			
			$id = PropertiesConstants.getRaw(p,"id",$id);
			$row = PropertiesConstants.getRaw(p,"row",$row);
			$col = PropertiesConstants.getRaw(p,"col",$col);
			$sheet = PropertiesConstants.getRaw(p,"sheet",$sheet);
		}
		
		@Override
		protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
			Workbook workbook = ctx.getObject(pid);
			if (workbook == null){
				throw new BaseException("core.e1001","It must be in a workbook context,check your together script.");
			}			
			StyleManager sm = ctx.getObject(smId);
			if (sm == null){
				throw new BaseException("core.e1001","It must be in a style manager context,check your together script.");
			}
			
			String sheet = PropertiesConstants.transform(ctx, $sheet, "sheet1");
			int row = PropertiesConstants.transform(ctx, $row, 0);
			int col = PropertiesConstants.transform(ctx, $col, 0);
			
			String id = PropertiesConstants.transform(ctx, $id, "");
			if (StringUtils.isNotEmpty(id)){
				Sheet worksheet = workbook.getSheet(sheet);
				if (worksheet != null){
					Row workrow = worksheet.getRow(row);
					if (workrow != null){
						Cell cell = workrow.getCell(col);
						if (cell != null){
							sm.addStyle(id, cell.getCellStyle());
						}
					}
				}				
			}
		}			
	}
}
