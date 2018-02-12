package com.alogic.xscript.plugins;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.formula.DefaultFunctionHelper;
import com.anysoft.formula.Expression;
import com.anysoft.formula.Parser;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 将指定公式的计算值设置到指定上下文变量
 * 
 * @author duanyy
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
 */
public class Formula extends AbstractLogiclet {
	protected String id;
	protected Expression expr = null;
	protected String dftValue = "";
	
	public Formula(String tag, Logiclet p) {
		super(tag, p);
	}

	public void configure(Properties p){
		super.configure(p);
		
		id = PropertiesConstants.getString(p,"id","",true);
		
		String formula = PropertiesConstants.getString(p,"expr","",true);
		if (StringUtils.isNotEmpty(formula)){
			Parser parser = new Parser(new DefaultFunctionHelper(null));			
			expr = parser.parse(formula);
		}
	}

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		if (expr != null){
			try {
				String value = expr.getValue(ctx).toString();
				ctx.SetValue(id, value);
			}catch (Exception ex){
				logger.error(ExceptionUtils.getStackTrace(ex));
			}
		}
	}

}
