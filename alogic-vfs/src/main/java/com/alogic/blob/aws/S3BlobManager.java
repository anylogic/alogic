package com.alogic.blob.aws;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

import com.alogic.blob.BlobInfo;
import com.alogic.blob.BlobManager;
import com.alogic.blob.BlobReader;
import com.alogic.blob.BlobWriter;
import com.alogic.sda.SDAFactory;
import com.alogic.sda.SecretDataArea;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于AWS s3的blob管理器
 * @author hyh
 * 
 * @since 1.6.11.53
 */
public class S3BlobManager extends BlobManager.Abstract implements AWSCredentialsProvider{

	/**
	 * a logger of log4j
	 */
	private static final Logger LOG = LoggerFactory.getLogger(S3BlobManager.class);

	private AmazonS3 s3 = null;
	private String
            accessKey = "",
            secretKey = "",
            endpoint = "",
            region="";
	/**
	 * 访问控制
	 */
	private CannedAccessControlList acl = CannedAccessControlList.PublicRead;
	
	private String sdaId = "";

	private String
            bucketName = "",
            contentType = "text/plain";
	
	private AWSCredentials credentials = null;	
	
	/**
	 * 共享模式：blob(blob共享),public(public链接),s3share(s3的共享连接)
	 */
	private String shareMode = "blob";

	private long shareTTL = 6 * 24 * 60 * 60 * 1000;
	private String sharePath = "%s/%s/%s";
	
	@Override
	public AWSCredentials getCredentials() {
		if (StringUtils.isEmpty(sdaId)){
			return credentials;
		}
		
		SecretDataArea sda = null;
		try {
			sda = SDAFactory.getDefault().load(sdaId, true);
		}catch (Exception ex){
			LOG.error("Can not find sda : " + sdaId);
			LOG.error(ExceptionUtils.getStackTrace(ex));
		}		
		if (sda == null){
			return credentials;
		}
		
		String newAccessKey = sda.getField("accessKey", accessKey);
		String newSecrectKey = sda.getField("secretKey",secretKey);
		
		if (newAccessKey.equals(accessKey) && newSecrectKey.equals(secretKey)){
			return credentials;
		}
		
		// accessKey和secretKey已经变化，创建一个新的credentials
		synchronized(this){
			credentials = new BasicAWSCredentials(newAccessKey,newSecrectKey);
			accessKey = newAccessKey;
			secretKey = newSecrectKey;
			return credentials;
		}
	}

	@Override
	public void refresh() {
		// nothing to do
	}
	
    private void initS3()
    {
    	credentials = new BasicAWSCredentials(accessKey, secretKey);
    	
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setProtocol(Protocol.HTTP);
        s3 = AmazonS3ClientBuilder.standard()
    	.withPathStyleAccessEnabled(true)
    	.withCredentials(this)      
    	.withClientConfiguration(clientConfiguration)
    	.withEndpointConfiguration(new EndpointConfiguration(endpoint, region))
    	.build(); 	
    }

	@Override
	public String getSharePath(String fileId,String filename,String contentType){ 
		if (shareMode.equalsIgnoreCase("public")){
			return String.format(sharePath, endpoint,bucketName,fileId);
		}else{
			if (shareMode.equalsIgnoreCase("s3share")){
		        GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(bucketName, fileId);  
		        urlRequest.setExpiration(new Date(System.currentTimeMillis() + shareTTL));  
		        URL url = s3.generatePresignedUrl(urlRequest);    
		        return url==null?"":url.toString(); 				
			}else{
				return super.getSharePath(fileId,filename,contentType);
			}
		}
	}

	@Override
	public BlobWriter newFile(String id) {
		return new S3BlobWriter(s3,bucketName,id,contentType,acl);
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

		bucketName = PropertiesConstants.getString(p,"bucket", bucketName);
        contentType =  PropertiesConstants.getString(p,"contentType", contentType,true);
        accessKey =  PropertiesConstants.getString(p,"accessKey", accessKey,true);
        secretKey =  PropertiesConstants.getString(p,"secretKey", secretKey,true);
        endpoint =  PropertiesConstants.getString(p,"endpoint", endpoint,true);
        region = PropertiesConstants.getString(p,"region", region,true);
        sdaId = PropertiesConstants.getString(p,"sdaId", sdaId,true);
        shareMode = PropertiesConstants.getString(p,"share.mode", shareMode,true);
        shareTTL = PropertiesConstants.getLong(p,"share.ttl", shareTTL,true);
        sharePath = PropertiesConstants.getString(p,"share.path", sharePath,true);
        
        acl = CannedAccessControlList.valueOf(PropertiesConstants.getString(p,"acl", acl.name(),true));
        
        initS3();
        makeBucketExist(bucketName);
	}

    private void makeBucketExist(String bucketName) {
        if (!s3.doesBucketExistV2(bucketName)) {
            LOG.info("Bucket {} does not already exists.Create it.\n", bucketName);
            try {
            	CreateBucketRequest request = new CreateBucketRequest(bucketName);
            	request.setCannedAcl(acl);
                s3.createBucket(request);
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

    public static class S3BlobReader implements BlobReader {

		private InputStream inputStream;
		private String id;
		private String contentType;

		public S3BlobReader(InputStream inputStream , String id , String contentType)
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
			IOTools.close(in);
		}

		@Override
		public BlobInfo getBlobInfo() {
			return new BlobInfo.Abstract(id, contentType);
		}
	}

    /**
     * S3BlobWriter
     * @author yyduan
     *
     */
	public static class S3BlobWriter implements BlobWriter {
        private AmazonS3 s3;
		private String bucketName ;
		private String id;
        private String contentType;
        private CannedAccessControlList acl;

        public S3BlobWriter(AmazonS3 s3 ,String bucketName, String id , String contentType,CannedAccessControlList acl) {
		    this.s3 = s3;
			this.bucketName = bucketName;
			this.id = id;
			this.contentType = contentType;
			this.acl = acl;
		}

		@Override
		public BlobInfo getBlobInfo() {
			return new BlobInfo.Abstract(id, contentType);
		}

		@Override
		public void write(InputStream in, long contentLength,
				boolean toCloseStreamWhenFinished) {
			try {
	            ObjectMetadata metadata = new ObjectMetadata();
	            if (contentLength > 0){
	            	metadata.setContentLength(contentLength);
	            }
	            PutObjectRequest request = new PutObjectRequest(bucketName,id,in,metadata);
	            request.withCannedAcl(acl);
	            s3.putObject(request);
			}finally{
				if (toCloseStreamWhenFinished){
					IOTools.close(in);
				}
			}
		}
		
		@Override
		public void write(byte[] content) {
            ByteArrayInputStream byteIS = new ByteArrayInputStream(content,0,content.length);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(content.length);
            PutObjectRequest request = new PutObjectRequest(bucketName,id,byteIS,metadata);
            request.withCannedAcl(acl);            
            s3.putObject(request);
		}
	}
}
