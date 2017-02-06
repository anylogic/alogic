package com.alogic.vfs.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import com.anysoft.util.Settings;
import com.anysoft.util.XmlElementProperties;

/**
 * Tool实现
 * 
 * @author duanyy
 *
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 * 
 * @version 1.6.7.11 [20170203 duanyy] <br>
 * - 增加部分条件下缺失的文件信息 <br>
 * 
 * @version 1.6.7.13 [20170206 duanyy] <br>
 * - 支持按照源文件的权限来创建目的文件 <br>
 */
public class ToolImpl implements Tool {
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

		VirtualFileSystem fsSrc = theSrc.getFileSystem();
		if (fsSrc == null) {
			progress(watcher, "Source vfs is null, i can not go ...", "error", 1.0f);
			return;
		}
		String pathSrc = theSrc.getPath();

		VirtualFileSystem fsDest = theDest.getFileSystem();
		if (fsDest == null) {
			progress(watcher, "Destination vfs is null, i can not go ...", "error", 1.0f);
			return;
		}
		String pathDest = theDest.getPath();

		compare(watcher, fsSrc, pathSrc, fsDest, pathDest);
	}

	private void compare(Watcher watcher, VirtualFileSystem fsSrc, String pathSrc, VirtualFileSystem fsDest,
			String pathDest) {
		if (watcher != null) {
			watcher.begin(source, destination);
		}

		FileList fileList = new FileList();

		try {
			buildSrcFileList(fileList, "", fsSrc, pathSrc);
			buildDestFileList(fileList, "", fsDest, pathDest);
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
						String destPath = JsonTools.getString(destFileInfo, "path", "");
						long destFileLength = fsSrc.getFileSize(destPath);
						String destMd5 = getFileDigest(fsSrc, destPath);
						JsonTools.setLong(destFileInfo, "length", destFileLength);
						JsonTools.setString(destFileInfo, "md5", destMd5);						
						progress(watcher, fileInfo, Result.Less, progress / total);
					}
				} else {
					if (destFileInfo == null) {
						// 增加源端的md5和文件大小信息
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
			watcher.end(source, destination);
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

	private void buildSrcFileList(FileList fileList, String uPath, VirtualFileSystem fs, String path) {
		// 先扫描源目录的文件列表
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
					buildSrcFileList(fileList, uid, fs, path + File.separator + name);
				} else {
					fileInfo.put("path", path + File.separator + name);
					// 文件在fileList中是否已经存在
					TheFileInfo found = fileList.get(uid);
					if (found == null) {
						// 不存在，新建
						found = new TheFileInfo(uid, uPath);
						found.srcAttrs(fileInfo);
						fileList.put(uid, found);
					} else {
						found.srcAttrs(fileInfo);
					}
				}
			}
		}
	}

	private void buildDestFileList(FileList fileList, String uPath, VirtualFileSystem fs, String path) {
		// 先扫描源目录的文件列表
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
					buildDestFileList(fileList, uid, fs, path + File.separator + name);
				} else {
					fileInfo.put("path", path + File.separator + name);
					// 文件在fileList中是否已经存在
					TheFileInfo found = fileList.get(uid);
					if (found == null) {
						// 不存在，新建
						found = new TheFileInfo(uid, uPath);
						found.destAttrs(fileInfo);
						fileList.put(uid, found);
					} else {
						found.destAttrs(fileInfo);
					}
				}
			}
		}
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

		VirtualFileSystem fsSrc = theSrc.getFileSystem();
		if (fsSrc == null) {
			progress(watcher, "Source vfs is null, i can not go ...", "error", 1.0f);
			return;
		}
		String pathSrc = theSrc.getPath();

		VirtualFileSystem fsDest = theDest.getFileSystem();
		if (fsDest == null) {
			progress(watcher, "Destination vfs is null, i can not go ...", "error", 1.0f);
			return;
		}
		String pathDest = theDest.getPath();

		sync(watcher, fsSrc, pathSrc, fsDest, pathDest);
	}

	private void sync(Watcher watcher, VirtualFileSystem fsSrc, String pathSrc, VirtualFileSystem fsDest,
			String pathDest) {
		if (watcher != null) {
			watcher.begin(source, destination);
		}

		FileList fileList = new FileList();

		try {
			buildSrcFileList(fileList, "", fsSrc, pathSrc);
			buildDestFileList(fileList, "", fsDest, pathDest);
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
						String destPath = JsonTools.getString(destFileInfo, "path", "");
						long destFileLength = fsSrc.getFileSize(destPath);
						String destMd5 = getFileDigest(fsSrc, destPath);
						JsonTools.setLong(destFileInfo, "length", destFileLength);
						JsonTools.setString(destFileInfo, "md5", destMd5);							
						delete(watcher, fsSrc, pathSrc, fsDest, pathDest, fileInfo, progress / total);
					}
				} else {
					if (destFileInfo == null) {
						// 增加源端的md5和文件大小信息
						String srcPath = JsonTools.getString(srcFileInfo, "path", "");
						long srcFileLength = fsSrc.getFileSize(srcPath);
						String srcMd5 = getFileDigest(fsSrc, srcPath);
						JsonTools.setLong(srcFileInfo, "length", srcFileLength);
						JsonTools.setString(srcFileInfo, "md5", srcMd5);

						add(watcher, fsSrc, pathSrc, fsDest, pathDest, fileInfo, progress / total);
					} else {
						String srcPath = JsonTools.getString(srcFileInfo, "path", "");
						String destPath = JsonTools.getString(destFileInfo, "path", "");

						if (StringUtils.isEmpty(srcPath) || StringUtils.isEmpty(destPath)) {
							// 不可能出现
						} else {
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
								overwrite(watcher, fsSrc, pathSrc, fsDest, pathDest, fileInfo, progress / total);
							} else {
								keep(watcher, fsSrc, pathSrc, fsDest, pathDest, fileInfo, progress / total);
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

	private void keep(Watcher watcher, VirtualFileSystem fsSrc, String pathSrc, VirtualFileSystem fsDest,
			String pathDest, TheFileInfo fileInfo, float f) {
		progress(watcher, fileInfo, Result.Keep, f);
	}

	protected void overwrite(Watcher watcher, VirtualFileSystem fsSrc, String pathSrc, VirtualFileSystem fsDest,
			String pathDest, TheFileInfo fileInfo, float f) {
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

		boolean delete = fsDest.deleteFile(delPath);

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

	protected void add(Watcher watcher, VirtualFileSystem fsSrc, String pathSrc, VirtualFileSystem fsDest,
			String pathDest, TheFileInfo fileInfo, float f) {
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

	protected void delete(Watcher watcher, VirtualFileSystem fsSrc, String pathSrc, VirtualFileSystem fsDest,
			String pathDest, TheFileInfo fileInfo, float f) {
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

		private Map<String, Object> srcAttrs;
		private Map<String, Object> destAttrs;

		public TheFileInfo(String id, String parent) {
			uid = id;
			uParent = parent;
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

	public static void main(String[] args) {
		Settings settings = Settings.get();
		settings.SetValue("ketty.home", "/Users/duanyy/alogic-ketty");
		settings.SetValue("vfs.master", "java:///conf/vfs.test.xml#App");
		Tool tool = new ToolImpl();

		Directory src = new Directory.Default("ketty", "");
		Directory dest = new Directory.Default("temp", "");

		tool.setSource(src);
		tool.setDestination(dest);

		tool.sync(new Watcher() {

			@Override
			public void begin(Directory src, Directory dest) {
				System.out.println("开始比较....");
				System.out.println("源目录:" + src.getPath());
				System.out.println("目的目录:" + src.getPath());
			}

			@Override
			public void progress(Directory src, Directory dest, FileInfo fileInfo, Result result, float progress) {
				System.out
						.println(String.format("[%s]%s --- %2.2f%%", result.sign(), fileInfo.uPath(), progress * 100));
			}

			@Override
			public void progress(Directory src, Directory dest, String message, String level, float progress) {
				System.out.println(String.format("[%s]%s --- %2.2f%%", level, message, progress * 100));
			}

			@Override
			public void end(Directory src, Directory dest) {
				System.out.println("完成比较....");
				System.out.println("源目录:" + src.getPath());
				System.out.println("目的目录:" + src.getPath());
			}
		});
	}
}
