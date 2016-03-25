package com.alogic.vfs.context;

import com.alogic.vfs.core.VirtualFileSystem;
import com.alogic.vfs.local.LocalFileSystem;
import com.anysoft.context.Inner;

/**
 * Source文件中内置的context
 * 
 * @author duanyy
 * 
 */
public class InnerContext  extends Inner<VirtualFileSystem>{

	@Override
	public String getObjectName() {
		return "fs";
	}

	@Override
	public String getDefaultClass() {
		return LocalFileSystem.class.getName();
	}

}
