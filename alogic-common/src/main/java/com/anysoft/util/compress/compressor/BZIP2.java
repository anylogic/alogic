package com.anysoft.util.compress.compressor;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

import com.anysoft.util.compress.AbstractCompressor;

/**
 * 基于ZIP2的压缩/解压器
 * 
 * <br>采用apache comms compress实现。
 * 
 * @author duanyy
 * @since 1.0.11
 */
public class BZIP2 extends AbstractCompressor{

	
	protected InputStream getInputStream(InputStream in) throws Exception {
		return new BZip2CompressorInputStream(in);
	}
	
	protected OutputStream getOutputStream(OutputStream out)throws Exception{
		return new BZip2CompressorOutputStream(out);
	}
}
