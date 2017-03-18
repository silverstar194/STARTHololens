package amazon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

public class KeyManagement {
	
	
	 	public KeyPair genKeyPair(String algorithm, int bitLength) throws NoSuchAlgorithmException {
		    SecureRandom srand = new SecureRandom();
	        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(algorithm);
	        keyGenerator.initialize(1024, srand);
	        return keyGenerator.generateKeyPair();
	    }

	    public void saveKeyPair(String dir, KeyPair keyPair)
	            throws IOException {
	        PrivateKey privateKey = keyPair.getPrivate();
	        PublicKey publicKey = keyPair.getPublic();

	        // encode and store public key
	        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
	                publicKey.getEncoded());
	        // write x509EncodedKeySpec.getEncoded() to database

	        // encode and store private key
	        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
	                privateKey.getEncoded());
	        // write pkcs8EncodedKeySpec.getEncoded() to database
	    }

	    public KeyPair loadKeyPair(int userId, String algorithm)
	            throws IOException, NoSuchAlgorithmException,
	            InvalidKeySpecException {
	        // read public key from DB
	    	// byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
	       

	        // read private key from DB
	       //  byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
	       
	        // Convert them into KeyPair
	        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
	        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
	                encodedPublicKey);
	        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

	        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
	                encodedPrivateKey);
	        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

	        return new KeyPair(publicKey, privateKey);
	    }

}

