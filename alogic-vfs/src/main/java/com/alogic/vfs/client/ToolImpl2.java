package com.alogic.vfs.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.alogic.vfs.core.VirtualFileSystem;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlElementProperties;

/**
 * Tool实现
 * 
 * @author duanyy
 *
 * @since 1.6.7.14
 * 
 */
public class ToolImpl2 implements Tool {
	/**
	 * a logger of log4j
	 */
	protected static final Logger LOG = LoggerFactory.getLogger(Tool.class);

	/**
	 * 源目录
	 */
	private Directory source;

	/**
	 * 目的目录
	 */
	private Directory destination;
	
	/**
	 * 扩展的源目录
	 */
	private List<Directory> extSources = new ArrayList<Directory>();

	@Override
	public void configure(Properties p) {
		// nothing to do
	}

	@Override
	public void configure(Element e, Properties p) {
		XmlElementProperties props = new XmlElementProperties(e, p);
		configure(props);
	}

	@Override
	public void setSource(Directory src) {
		source = src;
	}

	@Override
	public void addSource(Directory src){
		if (source == null){
			source = src;
		}else{
			extSources.add(src);
		}
	}
	
	@Override
	public Directory getSource() {
		return source;
	}

	@Override
	public void setDestination(Directory dest) {
		destination = dest;
	}

	@Override
	public Directory getDestination() {
		return destination;
	}

	protected void progress(Watcher watcher, String message, String level, float progress) {
		if (watcher != null) {
			watcher.progress(source, destination, message, level, progress);
		}
	}

	protected void progress(Watcher watcher, FileInfo fileInfo, Result result, float progress) {
		if (watcher != null) {
			watcher.progress(source, destination, fileInfo, result, progress);
		}
	}

	@Override
	public void compare(Watcher watcher) {
		Directory theSrc = getSource();
		Directory theDest = getDestination();

		if (theSrc == null) {
			progress(watcher, "Source directory is null,i can not go..", "error", 1.0f);
			return;
		}

		if (theDest == null) {
			progress(watcher, "Destination directory is null, i can not go..", "error", 1.0f);
			return;
		}
		compare(watcher,theSrc,theDest,extSources);
	}

	private void compare(Watcher watcher, Directory theSrc, Directory theDest,
			List<Directory> extSrcs) {
		if (watcher != null) {
			watcher.begin(theSrc, theDest);
		}

		FileList fileList = new FileList();

		try {
			buildSrcFileList(fileList, "", theSrc,theSrc.getPath());
			for (Directory extSrc:extSrcs){
				buildSrcFileList(fileList, "", extSrc,extSrc.getPath());
			}
			buildDestFileList(fileList, "", theDest,theDest.getPath());
		} catch (Exception e) {
			progress(watcher, "Getting FileList is error,please check your parameters...", "error", 1.0f);
		}

		if (!fileList.isEmpty()) {
			// 遍历FileList
			float total = fileList.size();
			int progress = 0;

			Iterator<TheFileInfo> iterator = fileList.values().iterator();
			while (iterator.hasNext()) {
				TheFileInfo fileInfo = iterator.next();
				progress++;

				Map<String, Object> srcFileInfo = fileInfo.srcAttrs();
				Map<String, Object> destFileInfo = fileInfo.destAttrs();

				if (srcFileInfo == null) {
					if (destFileInfo == null) {
						// 不可能出现
					} else {
						VirtualFileSystem fsDest = fileInfo.getDestDirectory().getFileSystem();
						String destPath = JsonTools.getString(destFileInfo, "path", "");
						long destFileLength = fsDest.getFileSize(destPath);
						String destMd5 = getFileDigest(fsDest, destPath);
						JsonTools.setLong(destFileInfo, "length", destFileLength);
						JsonTools.setString(destFileInfo, "md5", destMd5);						
						progress(watcher, fileInfo, Result.Less, progress / total);
					}
				} else {
					if (destFileInfo == null) {
						// 增加源端的md5和文件大小信息
						VirtualFileSystem fsSrc = fileInfo.getSrcDirectory().getFileSystem();
						String srcPath = JsonTools.getString(srcFileInfo, "path", "");
						long srcFileLength = fsSrc.getFileSize(srcPath);
						String srcMd5 = getFileDigest(fsSrc, srcPath);
						JsonTools.setLong(srcFileInfo, "length", srcFileLength);
						JsonTools.setString(srcFileInfo, "md5", srcMd5);

						progress(watcher, fileInfo, Result.More, progress / total);
					} else {
						String srcPath = JsonTools.getString(srcFileInfo, "path", "");
						String destPath = JsonTools.getString(destFileInfo, "path", "");

						if (StringUtils.isEmpty(srcPath) || StringUtils.isEmpty(destPath)) {
							// 不可能出现
						} else {
							VirtualFileSystem fsDest = fileInfo.getDestDirectory().getFileSystem();
							VirtualFileSystem fsSrc = fileInfo.getSrcDirectory().getFileSystem();
							long srcFileLength = fsSrc.getFileSize(srcPath);
							long destFileLength = fsDest.getFileSize(destPath);

							String srcMd5 = getFileDigest(fsSrc, srcPath);
							String destMd5 = getFileDigest(fsDest, destPath);

							boolean same = false;
							if (srcFileLength == destFileLength) {
								if (!StringUtils.isEmpty(srcMd5) && !StringUtils.isEmpty(destMd5)) {
									same = srcMd5.equals(destMd5);
								}
							}

							JsonTools.setLong(srcFileInfo, "length", srcFileLength);
							JsonTools.setLong(destFileInfo, "length", destFileLength);
							JsonTools.setString(srcFileInfo, "md5", srcMd5);
							JsonTools.setString(destFileInfo, "md5", destMd5);

							progress(watcher, fileInfo, same ? Result.Same : Result.Differ, progress / total);
						}
					}
				}
			}
		} else {
			progress(watcher, "The file list is empty,I have nothing to do...", "warning", 1.0f);
		}

		if (watcher != null) {
			watcher.end(theSrc, theDest);
		}		
	}
	
	private void buildDestFileList(FileList fileList, String uPath,
			Directory theDest,String path) {
		VirtualFileSystem fs = theDest.getFileSystem();
		if (fs == null) {
			return;
		}			
		List<Map<String, Object>> srcList = getFileList(fs, path);
		if (srcList != null) {
			for (Map<String, Object> fileInfo : srcList) {
				String name = JsonTools.getString(fileInfo, "name", "");
				if (StringUtils.isEmpty(name)) {
					LOG.warn("Can not find name attr in file info.");
					continue;
				}
				String uid = uPath + File.separator + name;
				boolean isDir = JsonTools.getBoolean(fileInfo, "dir", false);
				if (isDir) {
					buildDestFileList(fileList, uid, theDest, path + File.separator + name);
				} else {
					fileInfo.put("path", path + File.separator + name);
					// 文件在fileList中是否已经存在
					TheFileInfo found = fileList.get(uid);
					if (found == null) {
						// 不存在，新建
						found = new TheFileInfo(uid, uPath);
						found.destAttrs(fileInfo);
						found.setDestDirectory(theDest);
						fileList.put(uid, found);
					} else {
						found.destAttrs(fileInfo);
						found.setDestDirectory(theDest);
					}
				}
			}
		}
	}

	private void buildSrcFileList(FileList fileList, String uPath,
			Directory theSrc,String path) {
		// 先扫描源目录的文件列表
		VirtualFileSystem fs = theSrc.getFileSystem();
		if (fs == null) {
			return;
		}	
		List<Map<String, Object>> srcList = getFileList(fs, path);
		if (srcList != null) {
			for (Map<String, Object> fileInfo : srcList) {
				String name = JsonTools.getString(fileInfo, "name", "");
				if (StringUtils.isEmpty(name)) {
					LOG.warn("Can not find name attr in file info.");
					continue;
				}
				String uid = uPath + File.separator + name;
				boolean isDir = JsonTools.getBoolean(fileInfo, "dir", false);
				if (isDir) {
					buildSrcFileList(fileList, uid, theSrc, path + File.separator + name);
				} else {
					fileInfo.put("path", path + File.separator + name);
					// 文件在fileList中是否已经存在
					TheFileInfo found = fileList.get(uid);
					if (found == null) {
						// 不存在，新建
						found = new TheFileInfo(uid, uPath);
						found.srcAttrs(fileInfo);
						found.setSrcDirectory(theSrc);
						fileList.put(uid, found);
					} else {
						found.srcAttrs(fileInfo);
						found.setSrcDirectory(theSrc);
					}
				}
			}
		}
	}

	private String getFileDigest(VirtualFileSystem fs, String path) {
		InputStream in = null;
		try {
			in = fs.readFile(path);
			if (in != null) {
				return DigestUtils.md5Hex(in);
			}
		} catch (IOException e) {
			LOG.error("IO Exception when reading " + path, e);
		} finally {
			fs.finishRead(path, in);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getFileList(VirtualFileSystem fs, String path) {
		Map<String, Object> result = new HashMap<String, Object>();
		fs.listFiles(path, result, 0, Integer.MAX_VALUE);
		return (List<Map<String, Object>>) result.get("file");
	}

	@Override
	public void sync(Watcher watcher) {
		Directory theSrc = getSource();
		Directory theDest = getDestination();

		if (theSrc == null) {
			progress(watcher, "Source directory is null,i can not go..", "error", 1.0f);
			return;
		}

		if (theDest == null) {
			progress(watcher, "Destination directory is null, i can not go..", "error", 1.0f);
			return;
		}

		sync(watcher, theSrc,theDest,extSources);
	}

	private void sync(Watcher watcher, Directory theSrc,Directory theDest,List<Directory> extSrcs) {
		if (watcher != null) {
			watcher.begin(theSrc, theDest);
		}

		FileList fileList = new FileList();

		try {
			buildSrcFileList(fileList, "", theSrc,theSrc.getPath());
			for (Directory extSrc:extSrcs){
				buildSrcFileList(fileList, "", extSrc,extSrc.getPath());
			}
			buildDestFileList(fileList, "", theDest,theDest.getPath());
		} catch (Exception e) {
			progress(watcher, "Getting FileList is error,please check your parameters...", "error", 1.0f);
		}

		if (!fileList.isEmpty()) {
			// 遍历FileList
			float total = fileList.size();
			int progress = 0;

			Iterator<TheFileInfo> iterator = fileList.values().iterator();
			while (iterator.hasNext()) {
				TheFileInfo fileInfo = iterator.next();
				progress++;

				Map<String, Object> srcFileInfo = fileInfo.srcAttrs();
				Map<String, Object> destFileInfo = fileInfo.destAttrs();

				if (srcFileInfo == null) {
					if (destFileInfo == null) {
						// 不可能出现
					} else {
						VirtualFileSystem fsDest = fileInfo.getDestDirectory().getFileSystem();					
						String destPath = JsonTools.getString(destFileInfo, "path", "");
						long destFileLength = fsDest.getFileSize(destPath);
						String destMd5 = getFileDigest(fsDest, destPath);
						JsonTools.setLong(destFileInfo, "length", destFileLength);
						JsonTools.setString(destFileInfo, "md5", destMd5);							
						delete(watcher, fileInfo, progress / total);
					}
				} else {
					if (destFileInfo == null) {
						// 增加源端的md5和文件大小信息
						VirtualFileSystem fsSrc = fileInfo.getSrcDirectory().getFileSystem();						
						String srcPath = JsonTools.getString(srcFileInfo, "path", "");
						long srcFileLength = fsSrc.getFileSize(srcPath);
						String srcMd5 = getFileDigest(fsSrc, srcPath);
						JsonTools.setLong(srcFileInfo, "length", srcFileLength);
						JsonTools.setString(srcFileInfo, "md5", srcMd5);

						add(watcher,fileInfo, progress / total);
					} else {
						String srcPath = JsonTools.getString(srcFileInfo, "path", "");
						String destPath = JsonTools.getString(destFileInfo, "path", "");

						if (StringUtils.isEmpty(srcPath) || StringUtils.isEmpty(destPath)) {
							// 不可能出现
						} else {
							VirtualFileSystem fsDest = fileInfo.getDestDirectory().getFileSystem();
							VirtualFileSystem fsSrc = fileInfo.getSrcDirectory().getFileSystem();						
							long srcFileLength = fsSrc.getFileSize(srcPath);
							long destFileLength = fsDest.getFileSize(destPath);

							String srcMd5 = getFileDigest(fsSrc, srcPath);
							String destMd5 = getFileDigest(fsDest, destPath);

							boolean same = false;
							if (srcFileLength == destFileLength) {
								if (!StringUtils.isEmpty(srcMd5) && !StringUtils.isEmpty(destMd5)) {
									same = srcMd5.equals(destMd5);
								}
							}

							JsonTools.setLong(srcFileInfo, "length", srcFileLength);
							JsonTools.setLong(destFileInfo, "length", destFileLength);
							JsonTools.setString(srcFileInfo, "md5", srcMd5);
							JsonTools.setString(destFileInfo, "md5", destMd5);

							if (!same) {
								overwrite(watcher, fileInfo, progress / total);
							} else {
								keep(watcher,fileInfo, progress / total);
							}
						}
					}
				}
			}
		} else {
			progress(watcher, "The file list is empty,I have nothing to do...", "warning", 1.0f);
		}

		if (watcher != null) {
			watcher.end(source, destination);
		}
	}

	private void keep(Watcher watcher,TheFileInfo fileInfo, float f) {
		progress(watcher, fileInfo, Result.Keep, f);
	}

	protected void overwrite(Watcher watcher, TheFileInfo fileInfo, float f) {
		Map<String, Object> destAttrs = fileInfo.destAttrs();
		if (destAttrs == null) {
			// 不可能发生
			progress(watcher, fileInfo, Result.Failed, f);
			return;
		}

		String delPath = (String) destAttrs.get("path");
		if (StringUtils.isEmpty(delPath)) {
			// 不可能发生
			progress(watcher, fileInfo, Result.Failed, f);
			return;
		}
		VirtualFileSystem fsDest = fileInfo.getDestDirectory().getFileSystem();
		VirtualFileSystem fsSrc = fileInfo.getSrcDirectory().getFileSystem();
		boolean delete = fsDest.deleteFile(delPath);
		String pathDest = fileInfo.getDestDirectory().getPath();
		if (delete) {
			Map<String, Object> fileAttrs = fileInfo.srcAttrs();
			if (fileAttrs == null) {
				// 不可能发生
				progress(watcher, fileInfo, Result.Failed, f);
				return;
			}

			String srcPath = (String) fileAttrs.get("path");
			if (StringUtils.isEmpty(srcPath)) {
				// 不可能发生
				progress(watcher, fileInfo, Result.Failed, f);
				return;
			}

			InputStream in = fsSrc.readFile(srcPath);
			if (in != null) {
				try {
					String destPath = pathDest + fileInfo.uPath();
					String destParent = pathDest + fileInfo.uParent();
					if (!fsDest.exist(destParent)) {
						fsDest.makeDirs(destPath);
					}
					
					int permissions = JsonTools.getInt(fileInfo.srcAttrs, "permission", 0755);
					OutputStream out = fsDest.writeFile(destPath,permissions);
					if (out != null) {
						try {
							byte[] buffer = new byte[10240];
							int size = 0;
							while ((size = in.read(buffer)) > 0) {
								out.write(buffer, 0, size);
							}
							progress(watcher, fileInfo, Result.Overwrite, f);
						} catch (IOException ex) {
							progress(watcher, fileInfo, Result.Failed, f);
						} finally {
							fsDest.finishWrite(destPath, out);
						}
					} else {
						progress(watcher, fileInfo, Result.Failed, f);
					}
				} finally {
					fsSrc.finishRead(srcPath, in);
				}
			} else {
				progress(watcher, fileInfo, Result.Failed, f);
			}
		} else {
			progress(watcher, fileInfo, Result.Failed, f);
		}
	}

	protected void add(Watcher watcher, TheFileInfo fileInfo, float f) {
		Map<String, Object> fileAttrs = fileInfo.srcAttrs();
		if (fileAttrs == null) {
			// 不可能发生
			progress(watcher, fileInfo, Result.Failed, f);
			return;
		}

		String srcPath = (String) fileAttrs.get("path");
		if (StringUtils.isEmpty(srcPath)) {
			// 不可能发生
			progress(watcher, fileInfo, Result.Failed, f);
			return;
		}
		Directory destDir = fileInfo.getDestDirectory();
		if (destDir == null){
			destDir = getDestination();
		}
		VirtualFileSystem fsDest = destDir.getFileSystem();
		VirtualFileSystem fsSrc = fileInfo.getSrcDirectory().getFileSystem();
		String pathDest = destDir.getPath();
		InputStream in = fsSrc.readFile(srcPath);
		if (in != null) {
			try {
				String destPath = pathDest + fileInfo.uPath();
				String destParent = pathDest + fileInfo.uParent();
				if (!fsDest.exist(destParent)) {
					fsDest.makeDirs(destParent);
				}
				int permissions = JsonTools.getInt(fileInfo.srcAttrs, "permission", 0755);
				OutputStream out = fsDest.writeFile(destPath,permissions);
				if (out != null) {
					try {
						byte[] buffer = new byte[10240];
						int size = 0;
						while ((size = in.read(buffer)) > 0) {
							out.write(buffer, 0, size);
						}
						progress(watcher, fileInfo, Result.New, f);
					} catch (IOException ex) {
						progress(watcher, fileInfo, Result.Failed, f);
					} finally {
						fsDest.finishWrite(destPath, out);
					}
				} else {
					progress(watcher, fileInfo, Result.Failed, f);
				}
			} finally {
				fsSrc.finishRead(srcPath, in);
			}
		} else {
			progress(watcher, fileInfo, Result.Failed, f);
		}
	}

	protected void delete(Watcher watcher,TheFileInfo fileInfo, float f) {
		Map<String, Object> fileAttrs = fileInfo.destAttrs();
		if (fileAttrs == null) {
			// 不可能发生
			progress(watcher, fileInfo, Result.Failed, f);
			return;
		}

		String path = (String) fileAttrs.get("path");
		if (StringUtils.isEmpty(path)) {
			// 不可能发生
			progress(watcher, fileInfo, Result.Failed, f);
			return;
		}

		VirtualFileSystem fsDest = fileInfo.getDestDirectory().getFileSystem();
		boolean delete = fsDest.deleteFile(path);

		if (delete) {
			progress(watcher, fileInfo, Result.Del, f);
		} else {
			progress(watcher, fileInfo, Result.Failed, f);
		}
	}

	/**
	 * 文件信息
	 * 
	 * @author duanyy
	 *
	 */
	public static class TheFileInfo implements FileInfo {
		private String uid;
		private String uParent;
		private Directory srcDir;
		private Directory destDir;

		private Map<String, Object> srcAttrs;
		private Map<String, Object> destAttrs;

		public TheFileInfo(String id, String parent) {
			uid = id;
			uParent = parent;
		}

		public void setSrcDirectory(Directory src){
			srcDir = src;
		}
		
		public void setDestDirectory(Directory dest){
			destDir = dest;
		}
		
		public Directory getSrcDirectory(){
			return srcDir;
		}
		
		public Directory getDestDirectory(){
			return destDir;
		}
		
		@Override
		public String uPath() {
			return uid;
		}

		@Override
		public Map<String, Object> srcAttrs() {
			return srcAttrs;
		}

		@Override
		public Map<String, Object> destAttrs() {
			return destAttrs;
		}

		public void destAttrs(Map<String, Object> attrs) {
			destAttrs = attrs;
		}

		public void srcAttrs(Map<String, Object> attrs) {
			srcAttrs = attrs;
		}

		@Override
		public String uParent() {
			return uParent;
		}

	}

	/**
	 * 文件列表
	 * 
	 * @author duanyy
	 *
	 */
	public static class FileList extends Hashtable<String, TheFileInfo> {
		private static final long serialVersionUID = 1L;
	}

}
