package amazon;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Iterator;
import java.util.UUID;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3EncryptionClient;
import com.amazonaws.services.s3.model.EncryptionMaterials;
import com.amazonaws.services.s3.model.ListVersionsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.amazonaws.services.s3.model.StaticEncryptionMaterialsProvider;
import com.amazonaws.services.s3.model.VersionListing;
import com.amazonaws.util.IOUtils;

public class AmazonClient {
	
	private final KeyManagement keyManagement = new KeyManagement();
	private AmazonS3EncryptionClient encryptionClient;

            
    
	public void createBucket(int userId){
		
		KeyPair keyPair = null;
		try {
			keyPair = keyManagement.loadKeyPair(userId, AmazonConfig.ENCRYPTION_ALGORITHM);
		} catch (Exception e) {
			e.printStackTrace();
		}
		encryptionClient = constructAmazonClient(keyPair);
		String bucketName = UUID.randomUUID().toString();
		encryptionClient.createBucket(bucketName);
		// store bucket name to userId in db
		
	}
	
	
	
	public String uploadFileToS3(byte[] fileData, String bucketName){
		 String bucketKey = UUID.randomUUID().toString();
		 encryptionClient.putObject(new PutObjectRequest(bucketName, bucketKey,
				 new ByteArrayInputStream(fileData), new ObjectMetadata()));
		 return bucketKey;
	}
	
	public byte[] downloadFileFromS3(String bucketName, String bucketKey){
		S3Object downloadedObject = encryptionClient.getObject(bucketName, bucketKey);
        byte[] decrypted = null;
		try {
			decrypted = IOUtils.toByteArray(downloadedObject
			        .getObjectContent());
		} catch (IOException e) {
			e.printStackTrace();
		}
        return decrypted;
	}
	
	private AmazonS3EncryptionClient constructAmazonClient(KeyPair keyPair){
		 EncryptionMaterials encryptionMaterials = new EncryptionMaterials(
				 keyPair);
		 encryptionClient = new AmazonS3EncryptionClient(
	                new ProfileCredentialsProvider(),
	                new StaticEncryptionMaterialsProvider(encryptionMaterials));
		 return encryptionClient;
	}

}