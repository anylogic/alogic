package com.alogic.vfs.context;

import com.alogic.vfs.core.VirtualFileSystem;
import com.alogic.vfs.local.LocalFileSystem;
import com.anysoft.context.XMLResource;

/**
 * 基于XMLResource的Context
 * 
 * @author duanyy
 */
public class XRC extends XMLResource<VirtualFileSystem>{

	@Override
	public String getObjectName() {
		return "fs";
	}

	@Override
	public String getDefaultClass() {
		return LocalFileSystem.class.getName();
	}

	@Override
	public String getDefaultXrc() {
		return "java:///com/alogic/vfs/context/xrc.default.xml#" + XRC.class.getName();
	}

}
