package com.anysoft.util.compress.compressor;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.anysoft.util.compress.AbstractCompressor;

/**
 * 基于GZIP的压缩/解压器
 * 
 * <br>采用Java内置GZIP实现。
 * 
 * @author duanyy
 * @since 1.0.11
 * 
 * @version 1.6.4.17 [20151216 duanyy] <br>
 * - 根据sonar建议优化代码 <br>
 */
public class GZIP  extends AbstractCompressor{

	@Override
	protected InputStream getInputStream(InputStream in) throws Exception {
		return new GZIPInputStream(in);
	}
	
	@Override
	protected OutputStream getOutputStream(OutputStream out)throws Exception{
		return new GZIPOutputStream(out);
	}
}