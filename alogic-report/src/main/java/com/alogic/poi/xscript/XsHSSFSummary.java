package com.alogic.poi.xscript;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.plugins.Segment;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 设置文档的SummaryInformation
 * 
 * @author yyduan
 * @since 1.6.11.35
 */
public class XsHSSFSummary extends Segment{
	protected String pid = "$workbook";
	
	protected String $category = "";
	protected String $manager = "";
	protected String $company = "";
	protected String $subject = "";
	protected String $title = "";
	protected String $author = "";
	protected String $comment = "";
	
	public XsHSSFSummary(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		pid = PropertiesConstants.getString(p,"pid",pid);
		
		$category = PropertiesConstants.getRaw(p,"category",$category);
		$manager = PropertiesConstants.getRaw(p,"manager",$manager);
		$company = PropertiesConstants.getRaw(p,"company",$company);
		$subject = PropertiesConstants.getRaw(p,"subject",$subject);
		$title = PropertiesConstants.getRaw(p,"title",$title);
		$author = PropertiesConstants.getRaw(p,"author",$author);
		$comment = PropertiesConstants.getRaw(p,"comment",$comment);
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		Workbook workbook = ctx.getObject(pid);
		if (workbook == null){
			throw new BaseException("core.e1001","It must be in a workbook context,check your together script.");
		}
		
		if (workbook instanceof HSSFWorkbook){
			HSSFWorkbook book = (HSSFWorkbook)workbook;
			
			DocumentSummaryInformation  dsi = book.getDocumentSummaryInformation();
			if (dsi == null){
				book.createInformationProperties();
				dsi = book.getDocumentSummaryInformation();
			}
			String category = PropertiesConstants.transform(ctx, $category, "");
			if (StringUtils.isNotEmpty(category)){
				dsi.setCategory(category);
			}			
			String manager = PropertiesConstants.transform(ctx, $manager, "");
			if (StringUtils.isNotEmpty(manager)){
				dsi.setManager(manager);
			}			
			String company = PropertiesConstants.transform(ctx, $company, "");
			if (StringUtils.isNotEmpty(company)){
				dsi.setCompany(company);
			}							
	
			SummaryInformation si = book.getSummaryInformation();
			if (si != null){
				String subject = PropertiesConstants.transform(ctx, $subject, "");
				if (StringUtils.isNotEmpty(subject)){
					si.setSubject(subject);
				}	
				String title = PropertiesConstants.transform(ctx, $title, "");
				if (StringUtils.isNotEmpty(title)){
					si.setTitle(title);
				}	
				String author = PropertiesConstants.transform(ctx, $author, "");
				if (StringUtils.isNotEmpty(title)){
					si.setAuthor(author);
				}		
				String comment = PropertiesConstants.transform(ctx, $comment, "");
				if (StringUtils.isNotEmpty(comment)){
					si.setComments(comment);
				}					
			}
		}

	}	
}
