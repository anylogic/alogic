package com.alogic.vfs.xscript;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.alogic.vfs.core.VirtualFileSystem;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 将指定的文本内容保存到指定的文件
 * 
 * @author duanyy
 *
 * @since 1.6.7.8
 */
public class FileSave extends AbstractLogiclet{
	protected String pid = "$vfs";
	protected String path = "/newfile";
	protected String content = "";
	protected String encoding = "utf-8";
	protected boolean overwrite = true;
	
	public FileSave(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		pid = PropertiesConstants.getString(p,"pid",pid,true);
		path = PropertiesConstants.getRaw(p,"path",path);
		content = PropertiesConstants.getRaw(p, "content", content);
		encoding = PropertiesConstants.getString(p,"encoding",encoding,true);
		overwrite = PropertiesConstants.getBoolean(p,"overwrite",overwrite,true);
	}

	@Override
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		VirtualFileSystem vfs = ctx.getObject(pid);
		if (vfs == null){
			throw new BaseException("core.no_vfs_context",String.format("Can not find vfs:%s", pid));
		}
		
		String pathValue = ctx.transform(path);
		String contentValue = ctx.transform(content);
		
		boolean write = true;
		
		if (vfs.exist(pathValue)){
			if (overwrite){
				vfs.deleteFile(pathValue);
			}else{
				write = false;
			}
		}
		
		if (write){
			OutputStream out = vfs.writeFile(pathValue);
			if (out != null){
				try {
					out.write(contentValue.getBytes(encoding));
				} catch (UnsupportedEncodingException e) {
					throw new BaseException("core.unsupported_encoding",
							String.format("Can not write file %s:%s", pathValue,e.getMessage()));
				} catch (IOException e) {
					throw new BaseException("core.io_exception",
							String.format("Can not write file %s:%s", pathValue,e.getMessage()));
				}finally{
					vfs.finishWrite(pathValue, out);
				}
			}else{
				throw new BaseException("core.cant_create_file",
						String.format("Can not write file %s", pathValue));
			}
		}
	}
}