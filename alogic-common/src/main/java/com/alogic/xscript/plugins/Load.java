package com.alogic.xscript.plugins;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.commons.lang3.StringUtils;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.resource.ResourceFactory;

/**
 * 从文件中装载内容
 * 
 * @author yyduan
 *
 */
public class Load extends AbstractLogiclet{
	protected String $src = "";
	protected String $secondary = "";
	protected String $dft = "";
	protected String id;
	
	public Load(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		$src = PropertiesConstants.getRaw(p,"src",$src);
		$secondary = PropertiesConstants.getRaw(p,"secondary",$secondary);
		$dft = PropertiesConstants.getRaw(p,"dft",$dft);
		id = PropertiesConstants.getRaw(p,"id","$" + this.getXmlTag());
	}
		
	@Override
	protected void onExecute(XsObject root,XsObject current,LogicletContext ctx, ExecuteWatcher watcher) {
		String src = PropertiesConstants.transform(ctx, $src, "");
		String secondary = PropertiesConstants.transform(ctx, $secondary, "");
		String dft = PropertiesConstants.transform(ctx, $dft, "");
		
		String content = loadContent(src,secondary,dft);
		if (StringUtils.isNotEmpty(id)){
			ctx.SetValue(id, content);
		}
	}	
	
	protected String loadContent(String src, String secondary, String dft) {
		ResourceFactory resourceFactory = Settings.getResourceFactory();
		InputStream in = null;
		try {
			in = resourceFactory.load(src, secondary);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			StringBuffer content = new StringBuffer();
			String line = null;
			while ((line = reader.readLine()) != null) {
				content.append(line);
			}

			return content.toString();
		} catch (Exception ex) {
			logger.error("The config file is not a valid file,url = " + src);
			return dft;
		} finally {
			IOTools.close(in);
		}
	}		
}
