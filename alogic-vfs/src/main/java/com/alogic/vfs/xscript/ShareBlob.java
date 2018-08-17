package com.alogic.vfs.xscript;

import org.apache.commons.lang3.StringUtils;

import com.alogic.blob.BlobManager;
import com.alogic.blob.naming.BlobManagerFactory;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 将blob中的文件转化为共享url
 * @author yyduan
 * @since 1.6.4.37
 * 
 * @version 1.6.11.53 [20180817 duanyy] <br>
 * - BlobManager模型变更 <br>
 */
public class ShareBlob extends AbstractLogiclet{
	protected String blobId = "default";
	protected String pid = "$blob";
	protected String id;
	protected String $fileId = "";
	protected String $filename = "";
	protected String $contentType = "";
	
	public ShareBlob(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);

		id = PropertiesConstants.getString(p,"id","$" + getXmlTag(),true);
		blobId = PropertiesConstants.getString(p,"blobId",blobId,true);
		$fileId = PropertiesConstants.getRaw(p,"fileId","");
		$filename = PropertiesConstants.getRaw(p,"filename",$filename);
		$contentType = PropertiesConstants.getRaw(p,"contentType",$contentType);
		pid = PropertiesConstants.getString(p,"pid",pid,true);
	}

	@Override
	protected void onExecute(XsObject root,XsObject current,final LogicletContext ctx,final ExecuteWatcher watcher){
		BlobManager bm = ctx.getObject(pid);
		if (bm == null){
			bm = BlobManagerFactory.get(blobId);
			if (bm == null){
				throw new BaseException("core.e1001",String.format("Can not find blob manager:%s", blobId));
			}
		}
		
		String fileId = PropertiesConstants.transform(ctx, $fileId, "");
		if (StringUtils.isNotEmpty(id) && StringUtils.isNotEmpty(fileId)){
			String filename = PropertiesConstants.transform(ctx, $filename, "");
			String contentType = PropertiesConstants.transform(ctx, $contentType, "");
			ctx.SetValue(id, bm.getSharePath(fileId,filename,contentType));
		}
	}
}
