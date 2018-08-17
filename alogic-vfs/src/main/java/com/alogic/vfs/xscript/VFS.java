package com.alogic.vfs.xscript;

import com.alogic.xscript.Logiclet;
import com.alogic.xscript.plugins.Segment;

/**
 * VFS Namespce
 * @author yyduan
 *
 * @version 1.6.7.8 [20170128 duanyy] <br>
 * - 增加文件内容读取，保存，文件删除等xscript插件 <br>
 * 
 * @version 1.6.8.5 [20170331 duanyy] <br>
 * - 增加vfs-mkdir指令，用于在VFS中构建目录 <br>
 * 
 * @version 1.6.9.6 [20170706 duanyy] <br>
 * - 增加文件移动和文件拷贝脚本 <br>
 * 
 * @version 1.6.4.37 [duanyy 20151218] <br>
 * - 增加vfs-share和blob-share指令 <br>
 * 
 * @version 1.6.11.53 [20180817 duanyy] <br>
 * - 增加blob相关的指令 <br>
 */
public class VFS extends Segment{

	public VFS(String tag, Logiclet p) {
		super(tag, p);
		registerModule("vfs",FileSystem.class);
		registerModule("vfs-list",FileList.class);
		registerModule("vfs-sync",Sync.class);
		registerModule("vfs-check",Compare.class);
		registerModule("vfs-report",Report.class);
		registerModule("vfs-mv",FileMove.class);
		registerModule("vfs-cp",FileCopy.class);
		registerModule("vfs-report-json",JsonReport.class);
		registerModule("vfs-src",FileSystem.Source.class);
		registerModule("vfs-dest",FileSystem.Destination.class);
		registerModule("vfs-exist",FileExist.class);
		registerModule("vfs-save",FileSave.class);
		registerModule("vfs-load",FileLoad.class);
		registerModule("vfs-del",FileDelete.class);
		registerModule("vfs-mkdir",MakePath.class);
		registerModule("vfs-share",ShareVFS.class);
		registerModule("blob",Blob.class);
		registerModule("blob-load",BlobLoad.class);
		registerModule("blob-save",BlobSave.class);		
		registerModule("blob-share",ShareBlob.class);
		registerModule("upload",Upload.class);
		registerModule("upload-blob",UploadToBlob.class);
		registerModule("upload-vfs",UploadToVFS.class);
		registerModule("download-blob",DownloadFromBlob.class);
		registerModule("download-vfs",DownloadFromVFS.class);		
	}
}
