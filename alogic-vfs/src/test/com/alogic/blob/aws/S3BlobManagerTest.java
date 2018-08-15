package com.alogic.blob.aws;

import com.alogic.blob.BlobWriter;
import com.amazonaws.AmazonServiceException;
import com.anysoft.util.IOTools;
import org.junit.Test;

import java.io.*;


public class S3BlobManagerTest {

    private S3BlobManager manager = new S3BlobManager();

    String bucket_name = "ytcloud";
    String file_path = "C:\\maven\\settings.xml";
    String key_name = "settings.xml";

    @Test
    public void newFile() {
        BlobWriter writer = manager.newFile(key_name);
        System.out.println(manager.existFile(key_name));
        OutputStream out = null;
        InputStream in = null;
        try {
            in = new FileInputStream("C:\\maven\\settings.xml");
            out = writer.getOutputStream();
            int size = 0;
            byte[] buffer = new byte[1024];
            while ((size = in.read(buffer)) != -1) {
                out.write(buffer, 0, size);
            }
            out.flush();
        } catch (Exception ex) {
            System.out.println(ex.getCause());
        } finally {
            IOTools.close(in);
            writer.finishWrite(out);
        }

        System.out.println(manager.existFile(key_name));

    }

    @Test
    public void getFile() {
        if(manager.getFile(key_name) == null)
        {
            System.out.println("File not Found.");
            return;
        }
        System.out.println(manager.getFile(key_name).getBlobInfo().getId());
        InputStream is = manager.getFile(key_name).getInputStream(0);
        byte[] buff;
        while (true)
        {
            buff = new byte[1024];
            try {
                int len = is.read(buff);
                if(len == -1)
                {
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println(new String(buff,0,buff.length));
        }
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void existFile() {
        System.out.println(manager.existFile(key_name));

    }

    @Test
    public void deleteFile() {
        System.out.println(manager.deleteFile(key_name));
    }

    @Test
    public void configure() {
    }

}
