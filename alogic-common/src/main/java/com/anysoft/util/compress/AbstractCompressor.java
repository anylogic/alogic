package com.anysoft.util.compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.anysoft.util.CommandLine;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.KeyGen;
import com.anysoft.util.Settings;


/**
 * Compressor的虚基类
 * @author duanyy
 * @version 1.0.11
 * 
 */
abstract public class AbstractCompressor implements Compressor {

	
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

	protected static int BufferSize = 1024;
	
	
	public void compress(InputStream in, OutputStream out) throws Exception {
		OutputStream _out = null;
		
		try {
			_out = getOutputStream(out);
			
			int count = 0;  
	        byte data[] = new byte[BufferSize];  
	        while ((count = in.read(data, 0, BufferSize)) != -1) {  
	        	_out.write(data, 0, count);  
	        }  
	
	        _out.close();  
		}finally {
			IOTools.closeStream(_out);
		}
	}

	abstract protected OutputStream getOutputStream(OutputStream out) throws Exception;

	
	public void decompress(InputStream in, OutputStream out) throws Exception {
		InputStream _in = null;
		
		try {
			_in = getInputStream(in);
	        int count = 0;  
	        byte data[] = new byte[BufferSize];  
	        while ((count = _in.read(data, 0, BufferSize)) != -1) {  
	        	out.write(data, 0, count);  
	        }  
	  
	        _in.close(); 
		} finally {
			IOTools.closeStream(_in);
		}
	}

	abstract protected InputStream getInputStream(InputStream in) throws Exception;
	
	
	public static void main(String [] args){
		CommandLine cmd = new CommandLine(args);
		Settings settings = Settings.get();	
		settings.addSettings(cmd);
		
		ClassLoader cl = (ClassLoader)settings.get("classLoader");
		
		Factory<Compressor> factory = new Compressor.TheFatory(cl);
		
		
		
		String data = KeyGen.getKey(100);
		
		System.out.println("The data is " + data);
		System.out.println("The data size is " + data.length());
		
		try {
			Compressor compressor = factory.newInstance("BZIP2");
			
			byte [] compressed = compressor.compress(data.getBytes());
			
			System.out.println("BZIP2 is " + compressed.length);
			
			
			System.out.println(new String(compressor.decompress(compressed)));
		}catch (Exception ex){
			ex.printStackTrace();
		}
		try {
			Compressor compressor = factory.newInstance("GZIP");
			
			byte [] compressed = compressor.compress(data.getBytes());
			
			System.out.println("GZIP is " + compressed.length);
			
			
			System.out.println(new String(compressor.decompress(compressed)));
		}catch (Exception ex){
			ex.printStackTrace();
		}	
	
	}
}
