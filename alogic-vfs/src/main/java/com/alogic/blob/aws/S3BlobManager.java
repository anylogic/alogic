package com.alogic.blob.aws;

import com.alogic.blob.BlobInfo;
import com.alogic.blob.BlobManager;
import com.alogic.blob.BlobReader;
import com.alogic.blob.BlobWriter;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * 基于AWS s3的blob管理器
 * @author hyh
 */
public class S3BlobManager extends BlobManager.Abstract{

	/**
	 * a logger of log4j
	 */
	private static final Logger LOG = LoggerFactory.getLogger(S3BlobManager.class);

	private AmazonS3 s3;
	private String
            accessKey = "Your Access Key",
            secretKey = "Your Secret Key",
            endpoint = "Your End Point";

	private String
            bucketName = "ytcloud",
            contentType = "text/plain";

    private void initS3()
    {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setProtocol(Protocol.HTTP);
        s3 = new AmazonS3Client(credentials, clientConfiguration);
        s3.setEndpoint(endpoint);
        s3.setS3ClientOptions(S3ClientOptions.builder().setPathStyleAccess(true).build());
/*
        BasicAWSCredentials awsCreds = new BasicAWSCredentials("accessKey", "secretKey");
        s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
        s3.setRegion(Region.getRegion(Regions.DEFAULT_REGION)); // 此处根据自己的 s3 地区位置改变*/
    }


	@Override
	public BlobWriter newFile(String id) {
		return new S3BlobWriter(s3,bucketName,id,contentType);
	}

	@Override
	public BlobReader getFile(String id) {
		return s3.doesObjectExist(bucketName,id)?
				new S3BlobReader(getObject(bucketName,id),id , contentType):null;
	}

	@Override
	public boolean existFile(String id) {
		return s3.doesObjectExist(bucketName,id);
	}

	@Override
	public boolean deleteFile(String id) {
		try {
			s3.deleteObject(bucketName, id);
		} catch (AmazonServiceException e) {
			LOG.error("Delete File Exception ." , e);
			return false;
		}
		return true;
	}


	@Override
	public void configure(Properties p){
		super.configure(p);

		bucketName = PropertiesConstants.getString(p,"bucketName", bucketName);
        contentType =  PropertiesConstants.getString(p,"contentType", contentType,true);
        accessKey =  PropertiesConstants.getString(p,"accessKey", accessKey,true);
        secretKey =  PropertiesConstants.getString(p,"secretKey", secretKey,true);
        endpoint =  PropertiesConstants.getString(p,"endpoint", endpoint,true);
        initS3();
		createBucket(bucketName);

	}

    private void createBucket(String bucketName) {
        if (s3.doesBucketExistV2(bucketName)) {
            LOG.info("Bucket {} already exists.\n", bucketName);
        } else {
            try {
                s3.createBucket(bucketName);
            } catch (AmazonS3Exception e) {
                LOG.error("Create bucket error.\n", e);
            }
        }
    }

    private InputStream getObject(String bucketName , String keyName)
    {
        if(!s3.doesObjectExist(bucketName,keyName))
        {
            return null;
        }
        S3ObjectInputStream s3is = null;
        try {
            S3Object o = s3.getObject(bucketName, keyName);
            s3is = o.getObjectContent();
        } catch (AmazonServiceException e) {
            LOG.error("GET S3ObjectInputStream ERROR ." , e);
        }
        return s3is;
    }

    public static void close(Closeable... closeables) {
        for (Closeable c:closeables){
            if (null != c){
                try{
                    c.close();
                }catch (Exception ex){
                    LOG.error("Close stream exception ." , ex);
                }
            }
        }
    }


    public static class S3BlobReader implements BlobReader {

		private InputStream inputStream;
		private String id;
		private String contentType;

		S3BlobReader(InputStream inputStream , String id , String contentType)
		{
			this.inputStream = inputStream;
			this.id = id;
			this.contentType = contentType;
		}


		@Override
		public InputStream getInputStream(long offset) {
			return this.inputStream;
		}

		@Override
		public void finishRead(InputStream in) {
			close(in);
		}

		@Override
		public BlobInfo getBlobInfo() {
			return new BlobInfo.Abstract(id, contentType);
		}
	}

	public static class S3BlobWriter implements BlobWriter {

	    //最大上传大小10M
        private static final int MAX_UPLOAD_SIZE = 1024*1024*10;
        private PipedInputStream pipedIS = new PipedInputStream(MAX_UPLOAD_SIZE);

        private AmazonS3 s3;
		private String bucketName ;
		private String id;
        private String contentType;

		S3BlobWriter(AmazonS3 s3 ,String bucketName, String id , String contentType) {
		    this.s3 = s3;
			this.bucketName = bucketName;
			this.id = id;
			this.contentType = contentType;
		}

		@Override
		public OutputStream getOutputStream() {
			PipedOutputStream pipedOS = new PipedOutputStream();
			try {
				pipedIS.connect(pipedOS);
			}
			catch (IOException e) {
				LOG.error("Connect stream exception.",e);
			}

			return pipedOS;
		}

		@Override
		public void finishWrite(OutputStream out) {

			close(out);
            //将PipedInputStream读入buf 最大10M
            byte[] buf = new byte[MAX_UPLOAD_SIZE];
            int offset = 0;
            int total = 0;
            while(true) {
                try {
                    int len = pipedIS.read(buf , offset , 1024);
                    if(len == -1)
                    {
                        break;
                    }
                    offset += len;
                    total += len;
                } catch (IOException e) {
                    LOG.error("Read stream exception.",e);
                    return;
                }
            }

			close(pipedIS);

            //上传到S3
            ByteArrayInputStream byteIS = new ByteArrayInputStream(buf,0,total);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(total);
            s3.putObject(bucketName,id,byteIS,metadata);

		}

		@Override
		public BlobInfo getBlobInfo() {
			return new BlobInfo.Abstract(id, contentType);
		}
	}
}
