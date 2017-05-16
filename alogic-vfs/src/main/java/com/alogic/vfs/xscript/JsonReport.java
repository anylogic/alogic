package com.alogic.vfs.xscript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.alogic.vfs.client.Directory;
import com.alogic.vfs.client.Tool.FileInfo;
import com.alogic.vfs.client.Tool.Result;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 输出结果到json文档
 * @author yyduan
 *
 * @version 1.6.9.1 [20170516 duanyy] <br>
 * - 修复部分插件由于使用新的文档模型产生的兼容性问题 <br>
 */
public class JsonReport extends Report {

	protected String tag = "data";
	/**
	 * 文件列表
	 */
	protected Map<String,Object> result = new ConcurrentHashMap<String,Object>();
	
	public JsonReport(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		tag = PropertiesConstants.getRaw(p, "tag", tag);
	}
	
	@Override
	public void progress(Directory src, Directory dest, FileInfo fileInfo,
			Result result, float progress) {
		// 按照源端的VFSid分组
		String id = src.getFileSystem().id();
		List<Object> list = getFileList(id);
		Map<String,Object> map = new HashMap<String,Object>();	
		map.put("srcPath", src.getPath());
		map.put("destPath", dest.getPath());
		map.put("uPath", fileInfo.uPath());
		map.put("result", result.sign());
		map.put("src", fileInfo.srcAttrs());
		map.put("dest", fileInfo.destAttrs());		
		list.add(map);	
		
		super.progress(src, dest, fileInfo, result, progress);
	}

	@Override
	protected void begin(XsObject current, LogicletContext ctx) {
		result.clear();	
		super.begin(current, ctx);
	}
		
	/**
	 * 获取指定VFS的文件列表
	 * 
	 * <p>
	 * 如果不存在则创建一个
	 * 
	 * @param id vfs id
	 * @return 文件列表
	 */
	@SuppressWarnings("unchecked")
	protected List<Object> getFileList(String id){
		List<Object> list = null;
		
		Object found = result.get(id);
		if (found == null){
			list = new ArrayList<Object>();
			result.put(id, list);
		}else{
			if (found instanceof List){
				list = (List<Object>)found;
			}
		}
		
		return list;		
	}

}
