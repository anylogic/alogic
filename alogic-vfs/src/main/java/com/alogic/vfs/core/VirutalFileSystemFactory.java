package com.alogic.vfs.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alogic.vfs.local.LocalFileSystem;
import com.anysoft.util.DefaultProperties;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;

/**
 * 工厂类
 * 
 * @author duanyy
 *
 */
public class VirutalFileSystemFactory extends Factory<VirtualFileSystem>{
	public String getClassName(String module){
		if (module.indexOf('.') < 0){
			String clazz = alias.get(module);
			if (clazz != null){
				return clazz;
			}
		}
		return module;
	}
	
	protected static Map<String,String> alias = new HashMap<String,String>();
	
	static {
		alias.put("local", LocalFileSystem.class.getName());
	}
	
	public static void main(String [] args){
		VirutalFileSystemFactory f = new VirutalFileSystemFactory();
		DefaultProperties p = new DefaultProperties();
		p.SetValue("root", "d:/temp");
		VirtualFileSystem fs = f.newInstance("local", p);
		try {
			List<String> files = fs.listFiles("/", 1, 3);
			for (String file:files){
				System.out.println(file);
			}
		}finally {
			IOTools.close(fs);
		}
	}	
}
