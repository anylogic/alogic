package com.alogic.vfs.xscript;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.alogic.vfs.core.VirtualFileSystem;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 在vfs内部或者vfs之间拷贝文件
 * 
 * @author yyduan
 * 
 * @sicne 1.6.9.6
 */
public class FileCopy extends AbstractLogiclet{
	protected String srcVfs = "$vfs";
	protected String destVfs = "$vfs";
	protected String src = "";
	protected String dest = "";
	protected boolean overwrite = true;
	protected String id = "$vfs-cp";
	
	public FileCopy(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		srcVfs = PropertiesConstants.getString(p,"src.vfs",srcVfs,true);
		destVfs = PropertiesConstants.getString(p,"dest.vfs",destVfs,true);
		
		src = PropertiesConstants.getRaw(p,"src",src);
		dest = PropertiesConstants.getRaw(p,"dest",dest);
		overwrite = PropertiesConstants.getBoolean(p,"overwrite",overwrite);
		id = PropertiesConstants.getString(p,"id",id,true);
	}

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		VirtualFileSystem vfsSrc = ctx.getObject(srcVfs);
		if (vfsSrc == null){
			throw new BaseException("core.no_vfs_context",String.format("Can not find vfs:%s", srcVfs));
		}
		VirtualFileSystem vfsDest = ctx.getObject(destVfs);
		if (vfsDest == null){
			throw new BaseException("core.no_vfs_context",String.format("Can not find vfs:%s", destVfs));
		}		
		
		String srcPath = ctx.transform(src);
		String destPath = ctx.transform(dest);
		
		if (StringUtils.isNotEmpty(srcPath) && StringUtils.isNotEmpty(destPath)){
			if (vfsDest.exist(destPath)){
				if (!overwrite){
					ctx.SetValue(id,BooleanUtils.toStringTrueFalse(false));
					return ;
				}
				vfsDest.deleteFile(destPath);
			}
			InputStream input = vfsSrc.readFile(srcPath);
			if (input == null){
				throw new BaseException("core.file_error","Can not open file " + srcPath);
			}else{				
				OutputStream output = vfsDest.writeFile(destPath);
				if (output == null){
					throw new BaseException("core.file_error","Can not write file " + destPath);
				}
				try {
					byte[] buffer = new byte[102400];
					int size = 0;
					while ((size = input.read(buffer)) > 0) {
						output.write(buffer, 0, size);
					}
					ctx.SetValue(id,BooleanUtils.toStringTrueFalse(true));
				} catch (IOException ex) {
					throw new BaseException("core.file_error",String.format("Failed to copy file,%s-->%s",srcPath,destPath));
				} finally {
					vfsDest.finishWrite(destPath, output);
					vfsSrc.finishRead(srcPath, input);
				}
			}
		}else{
			ctx.SetValue(id,BooleanUtils.toStringTrueFalse(false));
		}
	}
}