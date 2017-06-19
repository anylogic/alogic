package com.alogic.vfs.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alogic.vfs.core.VirtualFileSystem;
import com.anysoft.util.IOTools;

/**
 * 基于本地文件系统的VFS
 * 
 * @author duanyy
 *
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 * 
 * @version 1.6.7.11 [20170203 duanyy] <br>
 * - 增加toString实现 <br>
 * 
 * @version 1.6.7.13 [20170206 duanyy] <br>
 * - 写文件接口增加permissions参数，以便在创建文件时指定文件的权限 <br>
 * 
 * @version 1.6.9.3 [20170615 duanyy] <br>
 * - 增加move的方法 <br>
 * 
 * @version 1.6.9.4 [20170615 duanyy] <br>
 * - 统一各实现的文件名匹配规则为正则表达式匹配 <br>
 */
public class LocalFileSystem extends VirtualFileSystem.Abstract {
	
	public LocalFileSystem(){
		
	}
	
	public String toString(){
		return String.format("%s[file://%s]",id,root);
	}
	
	protected void getFileInfo(File file,Map<String,Object> json){
		json.put("name", file.getName());
		json.put("dir", file.isDirectory());
		json.put("executable", file.canExecute());
		json.put("writable", file.canWrite());
		json.put("readable", file.canRead());
		int permission = 0;
		if (file.canExecute()){
			permission = permission | 00111;
		}
		if (file.canRead()){
			permission = permission | 00444;
		}
		if (file.canWrite()){
			permission = permission | 00222;
		}
		json.put("permission",permission);
		json.put("lastModified", file.lastModified());
		json.put("length", file.length());		
	}
	
	@Override
	public void close() throws Exception {
		// nothing to do
	}
	
	@Override
	public List<String> listFiles(String path, String pattern, final int offset,
			final int limit) {
		String realPath = getRealPath(path);
		
		File dirFile = new File(realPath);

		if (!dirFile.isDirectory()){
			//指定的路径不是一个目录
			return null;
		}
		
		List<String> result = new ArrayList<String>();
		
		String[] files = dirFile.list();
		
		Pattern p = Pattern.compile(pattern);
		
		int current = 0;
		for (String file:files){
			Matcher matcher = p.matcher(file);
			if (matcher.matches()){
				if (current >= offset){
					result.add(file);
				}
				current ++;	
				if (current >= offset + limit){
					break;
				}
			}
		}
		
		return result;
	}

	@Override
	public void listFiles(String path, String pattern,
			Map<String, Object> json, int offset, int limit) {
		String realPath = getRealPath(path);
		
		File dirFile = new File(realPath);
		
		if (!dirFile.isDirectory()){
			//指定的路径不是一个目录
			return ;
		}
		
		List<Object> result = new ArrayList<Object>();
		
		File[] files = dirFile.listFiles();
		
		Pattern p = Pattern.compile(pattern);
		
		int current = 0;
		for (File file:files){
			String filename = file.getName();
			Matcher matcher = p.matcher(filename);
			if (matcher.matches()){
				if (current >= offset && current < offset + limit){
					Map<String,Object> map = new HashMap<String,Object>();
					getFileInfo(file,map);
					map.put("path", path + File.separator + filename);
					result.add(map);
				}
				current ++;	
			}
		}
		
		json.put("file", result);
		json.put("offset", offset);
		json.put("limit", limit);
		json.put("total", current);
		json.put("path", path);
	}
	
	@Override
	public boolean deleteFile(String path) {
		String realPath = getRealPath(path);
		
		File file = new File(realPath);
		
		if (file.exists() && file.canWrite()){
			return file.delete();
		}
		return false;
	}

	@Override
	public boolean exist(String path) {
		String realPath = getRealPath(path);
		File file = new File(realPath);
		return file.exists();
	}

	@Override
	public boolean isDir(String path) {
		String realPath = getRealPath(path);
		File file = new File(realPath);
		return file.isDirectory();
	}

	@Override
	public long getFileSize(String path) {
		String realPath = getRealPath(path);
		File file = new File(realPath);
		return file.isFile()?file.length() :0;
	}

	@Override
	public void getFileInfo(String path, Map<String, Object> json) {
		String realPath = getRealPath(path);
		File file = new File(realPath);
		if (file.exists()){
			getFileInfo(file,json);
			json.put("path", path);
		}
	}
	
	@Override
	public boolean makeDirs(String path) {
		String realPath = getRealPath(path);
		File file = new File(realPath);
		
		if (!file.exists()){
			return file.mkdirs();
		}
		return false;
	}

	@Override
	public InputStream readFile(String path){
		String realPath = getRealPath(path);
		File file = new File(realPath);
		if (file.exists() && file.canRead()){
			try {
				return new FileInputStream(file);
			} catch (FileNotFoundException e) {
				LOG.error(e.getMessage());
				return null;
			}
		}
		return null;
	}


	@Override
	public void finishRead(String path, InputStream in) {
		IOTools.close(in);
	}	
	
	@Override
	public OutputStream writeFile(String path) {
		return writeFile(path,0755);
	}

	@Override
	public OutputStream writeFile(String path, int permissions) {
		String realPath = getRealPath(path);
		File file = new File(realPath);
		if (!file.exists()){
			try {
				file.createNewFile();
				file.setExecutable((permissions & 00100) == 00100);
				file.setReadable((permissions & 00400) == 00400);
				file.setWritable((permissions & 00200) == 00200);
				return new FileOutputStream(file);
			} catch (FileNotFoundException e) {
				LOG.error(e.getMessage());
			} catch (IOException e) {
				LOG.error(e.getMessage());
			}
		}
		return null;
	}	
	
	@Override
	public void finishWrite(String path, OutputStream out) {
		IOTools.close(out);
	}

	@Override
	public boolean move(String src, String dest,boolean overwrite){
		String srcPath = getRealPath(src);
		String destPath = getRealPath(dest);
		
		File srcFile = new File(srcPath);
		if (!srcFile.exists() || !srcFile.canRead()){
			//源文件不存在或不能读
			return false;
		}
		
		File destFile = new File(destPath);
		if (destFile.exists()){
			if (!overwrite){
				return false;
			}
			destFile.delete();
		}
		
		return srcFile.renameTo(destFile);
	}
}
