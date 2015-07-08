package com.alogic.blob.demo;


import java.io.OutputStream;

import com.alogic.blob.client.BlobTool;
import com.alogic.blob.core.BlobManager;
import com.alogic.blob.core.BlobWriter;
import com.alogic.blob.service.Download;
import com.anysoft.util.Settings;

public class BlobCreate {

	public static void main(String[] args) {
		Settings settings = Settings.get();
		
		settings.SetValue("ketty.home", "d://ketty");
		
		BlobManager manager = BlobTool.getBlobManager();
		
		BlobWriter writer = manager.newFile();
		
		OutputStream out = writer.getOutputStream();
		
		String content = "hello,create a blob file";
		
		try {
			out.write(content.getBytes());
			
			out.close();
			
			System.out.println("This file id is " + writer.id());
		}catch (Exception ex){
			ex.printStackTrace();
		}
		
		System.out.println(Download.TheNormalizer.class.getName());
	}

}
