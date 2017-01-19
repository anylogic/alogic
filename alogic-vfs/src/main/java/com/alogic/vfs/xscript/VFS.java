package com.alogic.vfs.xscript;

import com.alogic.xscript.Logiclet;
import com.alogic.xscript.plugins.Segment;

/**
 * VFS Namespce
 * @author yyduan
 *
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
	}
}
