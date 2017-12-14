package com.anysoft.util.compress;

import java.io.InputStream;
import java.io.OutputStream;

import com.anysoft.util.Factory;

/**
 * 压缩/解压器
 * 
 * @author duanyy
 * @since 1.0.11
 * 
 * @version 1.6.4.17 [20151216 duanyy] <br>
 * - 根据sonar建议优化代码 <br>
 */
public interface Compressor {
	/**
	 * 压缩bytes
	 * @param data 数据
	 * @return 压缩后的数据
	 */
	public byte[] compress(byte[] data) ; // NOSONAR
	
	/**
	 * 解压bytes
	 * @param data 压缩数据
	 * @return 原始数据
	 */
	public byte[] decompress(byte[] data) ; // NOSONAR
	
	/**
	 * 压缩
	 * @param in 输入流
	 * @param out 输出流
	 */
	public void compress(InputStream in,OutputStream out);// NOSONAR
	
	/**
	 * 解压
	 * @param in 输入流
	 * @param out 输出流
	 */
	public void decompress(InputStream in,OutputStream out);// NOSONAR
	
	
	public static class TheFatory extends Factory<Compressor>{
		public TheFatory(ClassLoader cl){
			super(cl);
		}
		@Override
		public String getClassName(String module){
			if (module.indexOf('.') >= 0){
				return module;
			}
			return "com.anysoft.util.compress.compressor." + module;
		}
	}
}
