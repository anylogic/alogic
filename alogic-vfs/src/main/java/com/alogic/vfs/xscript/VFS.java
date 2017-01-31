package com.alogic.vfs.xscript;

import com.alogic.xscript.Logiclet;
import com.alogic.xscript.plugins.Segment;

/**
 * VFS Namespce
 * @author yyduan
 *
 * @version 1.6.7.8 [20170128 duanyy]
 * - 增加文件内容读取，保存，文件删除等xscript插件
 */
public class VFS extends Segment{

	public VFS(String tag, Logiclet p) {
		super(tag, p);
		registerModule("vfs",FileSystem.class);
		registerModule("vfs-list",FileList.class);
		registerModule("vfs-sync",Sync.class);
		registerModule("vfs-check",Compare.class);
		registerModule("vfs-report",Report.class);
		registerModule("vfs-report-json",JsonReport.class);
		registerModule("vfs-src",FileSystem.Source.class);
		registerModule("vfs-dest",FileSystem.Destination.class);
		registerModule("vfs-exist",FileExist.class);
		registerModule("vfs-save",FileSave.class);
		registerModule("vfs-load",FileLoad.class);
		registerModule("vfs-del",FileDelete.class);
	}
}
