package com.anysoft.util.compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import com.anysoft.util.IOTools;

/**
 * Compressor的虚基类
 * @author duanyy
 * @version 1.0.11
 * @version 1.6.4.17 [20151216 duanyy] <br>
 * - 根据sonar建议优化代码 <br>
 */
public abstract class AbstractCompressor implements Compressor {

	protected static final int BUFFER_SIZE = 1024;
	
	@Override
	public byte[] compress(byte[] data) throws Exception {
		ByteArrayInputStream bais = null;
		ByteArrayOutputStream baos = null;
		try {
			bais = new ByteArrayInputStream(data); 
			baos = new ByteArrayOutputStream(); 
			
			compress(bais, baos);
			
			baos.flush();
			return baos.toByteArray();
		}finally{
			IOTools.closeStream(bais,baos);
		}
	}

	@Override
	public byte[] decompress(byte[] data) throws Exception {
        ByteArrayInputStream bais = null;
        ByteArrayOutputStream baos = null;
        
        try {
	        bais = new ByteArrayInputStream(data);  
	        baos = new ByteArrayOutputStream();  
	  
	        decompress(bais, baos);    
	        baos.flush();
	        return baos.toByteArray(); 
        }finally {
        	IOTools.closeStream(bais,baos);
        }
	}

	@Override
	public void compress(InputStream in, OutputStream out) throws Exception {
		OutputStream wrapped = null;
		
		try {
			wrapped = getOutputStream(out);
			
			int count = 0;  
	        byte [] data = new byte[BUFFER_SIZE];  
	        while ((count = in.read(data, 0, BUFFER_SIZE)) != -1) {  
	        	wrapped.write(data, 0, count);  
	        }  
	
	        wrapped.close();  
		}finally {
			IOTools.closeStream(wrapped);
		}
	}

	 
	@Override
	public void decompress(InputStream in, OutputStream out) throws Exception {
		InputStream wrapped = null;		
		try {
			wrapped = getInputStream(in);
	        int count = 0;  
	        byte [] data = new byte[BUFFER_SIZE];  
	        while ((count = wrapped.read(data, 0, BUFFER_SIZE)) != -1) {  
	        	out.write(data, 0, count);  
	        }  
	  
	        wrapped.close(); 
		} finally {
			IOTools.closeStream(wrapped);
		}
	}
	
	protected abstract OutputStream getOutputStream(OutputStream out) throws Exception; // NOSONAR
	protected abstract InputStream getInputStream(InputStream in) throws Exception; // NOSONAR
}
