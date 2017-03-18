package user;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.*;
import database.DataBaseDriver;
import database.ObjectBuilder;



/**
 * @author Sean Maloney
 * 
 */

/**
 * Creates users and saves them to database.
 */

public class User {

	/** The user id. */
	//generated server side
	private String userID;

	/** The email. */
	private String email;

	/** The password. */
	private String passHash;

	/** The email pin. */
	private String emailPin;



	/**
	 * Instantiates a new user.
	 *
	 * @param email: user email
	 * @param password: user password
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public User(String password, String emailPin, String userID) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{

		//sanitize for sql
		if (password.contains("'")) 
		{
			password = password.replace("'", "''");
		}
		
		this.userID = idGenerator();
		this.passHash = hashPassword(password+this.userID);

		System.out.println("=====Generated New User=====");
	}

	public User(String userID){

		///get and create a user here
	}


	/**
	 * Gets the email.
	 *
	 * @return the email
	 */
	public String getemail(){
		return this.email;
	}

	/**
	 * Gets the password.
	 *
	 * @return the password
	 */
	public String getpassword(){
		return this.passHash;
	}


	/**
	 * Gets the user id.
	 *
	 * @return the user id
	 */
	public String getUserID(){
		return this.userID;
	}
	
	/**
	 * Gets the email pin.
	 *
	 * @return the user pin
	 */
	public String getEmailPin(){
		return this.userID;
	}



	/**
	 * Sets the email.
	 *
	 * @param email the new email
	 */
	public void setemail(String email){
		if (email.contains("'")) 
		{
			email = email.replace("'", "''");
		}
		this.email = email;
	}

	/**
	 * Sets the password.
	 *
	 * @param password the new password
	 */
	public void setpassword(String password){
		this.passHash = hashPassword(password+this.userID);
	}


	/**
	 * Id generator.
	 *
	 * @return the string
	 */
	private String idGenerator() {
		SecureRandom random = new SecureRandom();
		return new BigInteger(130, random).toString(32);
	}

	/**
	 * Hash password (SHA-256 with Salt).
	 *
	 * @param hashString the hash string
	 * @return the string
	 */
	private String hashPassword(String hashString){
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");

			String text = hashString;

			md.update(text.getBytes("UTF-8")); // Change this to "UTF-16" if needed
			byte[] digest = md.digest();

			System.out.println("=====PASSWORD HASHED=====");

			return String.format("%064x", new java.math.BigInteger(1, digest));

		} catch (Exception e) {
			System.out.println("=====HASH FAILED=====");
			e.printStackTrace();
		}

		return null;
	}


	/**
	 * Verify password.
	 *
	 * @param password the password
	 * @return true, if successful
	 */
	public boolean verifyPassword(String password){
		String testString = hashPassword(password+this.userID);

		if(testString.equals(this.passHash)){
			return true;
		}
		return false;
	}

	/**
	 * Verify pin.
	 *
	 * @param pin the pin
	 * @return true, if successful
	 */
	public boolean verifyPin(String pin){

		if(pin.equals(this.emailPin)){
			return true;
		}
		return false;
	}


}
