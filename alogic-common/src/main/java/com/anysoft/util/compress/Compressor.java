package com.anysoft.util.compress;

import java.io.InputStream;
import java.io.OutputStream;

import com.anysoft.util.BaseException;
import com.anysoft.util.Factory;

/**
 * 压缩/解压器
 * 
 * @author duanyy
 * @since 1.0.11
 */
public interface Compressor {
	/**
	 * 压缩bytes
	 * @param data 数据
	 * @return 压缩后的数据
	 * @throws Exception
	 */
	public byte[] compress(byte[] data) throws Exception;
	
	/**
	 * 解压bytes
	 * @param data 压缩数据
	 * @return 原始数据
	 * @throws Exception
	 */
	public byte[] decompress(byte[] data) throws Exception;
	
	/**
	 * 压缩
	 * @param in 输入流
	 * @param out 输出流
	 * @throws Exception
	 */
	public void compress(InputStream in,OutputStream out)throws Exception;
	
	/**
	 * 解压
	 * @param in 输入流
	 * @param out 输出流
	 * @throws Exception
	 */
	public void decompress(InputStream in,OutputStream out)throws Exception;
	
	
	public static class TheFatory extends Factory<Compressor>{
		public TheFatory(ClassLoader cl){
			super(cl);
		}
		
		public String getClassName(String _module) throws BaseException{
			if (_module.indexOf('.') >= 0){
				return _module;
			}
			return "com.anysoft.util.compress.compressor." + _module;
		}
	}
}
