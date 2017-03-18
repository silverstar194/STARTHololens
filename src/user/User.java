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
	public User(String email, String password) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{

		//sanitize for sql
		if (email.contains("'") || password.contains("'")) 
		{
			email = email.replace("'", "''");
			password = password.replace("'", "''");
		}

		this.userID = idGenerator();
		this.email = email;
		this.passHash = hashPassword(passHash+this.userID);

		System.out.println("=====Generated New User=====");
	}

	/**
	 * Instantiates a new user.
	 *
	 * @param id: user id
	 * @throws SQLException 
	 */
	public User(String id){
		ObjectBuilder fetchFromDB = new ObjectBuilder(id);
		return fetchFromDB.getUser();
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
	 * Save new user to database.
	 *
	 * @throws SQLException the SQL exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 */
	public void saveNewUserToDatabase() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		DataBaseDriver dataBase = new DataBaseDriver();

		Connection dataBaseConn = dataBase.getConnection();

		String command ="INSERT INTO "
				+ "`electro956_db`.`User` (`userID`,`firstName`,`lastName`,`email`,`password`,`userName`,`bio`)"
				+ "VALUES"
				+ "('"+this.userID+"','"+this.firstName+"','"+this.lastName+"','"+this.email+"','"+this.password+"','"+this.userName+"','"+this.bio+"');";



		String commandUserMap = "INSERT INTO `electro956_db`.`UserMap` (`userName`, `userID`) VALUES ('"+this.userName+"', '"+this.userID+"');";

		dataBase.executeUpdate(dataBaseConn, command);
		dataBase.executeUpdate(dataBaseConn, commandUserMap);


		System.out.println("=====USER SENT TO DATABASE=====");

	}

	/**
	 * Update user in database.
	 *
	 * @throws SQLException the SQL exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 */
	public void update() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		DataBaseDriver dataBase = new DataBaseDriver();

		Connection dataBaseConn = dataBase.getConnection();

		String command ="UPDATE User"
				+ " SET firstName='"+this.firstName+"', lastName='"+this.lastName+"', email='"+this.email+"', password='"+this.password+"', userName='"+this.userName+"', bio='"+this.bio+"', spamCount='"+this.spamCount+"'"
				+  "WHERE userID='"+this.userID+"';";

		String commandUserMap = "UPDATE UserMap SET userName='"+this.userName+"' WHERE userID='"+this.userID+"';";

		dataBase.executeUpdate(dataBaseConn, command);
		dataBase.executeUpdate(dataBaseConn, commandUserMap);


		System.out.println("=====USER UPDATED IN DATABASE=====");

	}

	/**
	 * Gets the user info.
	 *
	 * @return the user info
	 */
	public String getUserInfo() {

		Map<String,String> userMap = new HashMap<String, String>();

		String emailNew = null;
		String bioNew = null;
		if(this.email.contains("''") || this.bio.contains("''")){
			emailNew = this.email.replace("''", "'");
			bioNew = this.bio.replace("''", "'");
		}else{
			emailNew = this.email;
			bioNew = this.bio;
		}


		userMap.put("userID", this.userID);
		userMap.put("firstName", this.firstName);
		userMap.put("lastName", this.lastName);
		userMap.put("email", emailNew);
		userMap.put("userName",this.userName);
		userMap.put("bio", bioNew);
		userMap.put("messageCount", ""+messageCount);
		userMap.put("postCount", ""+postCount);

		JSONObject json = new JSONObject(userMap);

		System.out.println("=====USER CONVERTED TO JSON=====");

		return json.toString();


	}

	/**
	 * Delete user in database.
	 *
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws SQLException the SQL exception
	 */
	public void deleteUser() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{

		DataBaseDriver dataBase = new DataBaseDriver();

		Connection dataBaseConn = dataBase.getConnection();

		String command ="DELETE FROM `electro956_db`.`User` WHERE userID='"+this.userID+"';";

		dataBase.executeUpdate(dataBaseConn, command);
		this.lastLocation.deleteUser();
		System.out.println("=====USER UPDATED IN DATABASE=====");


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
	 * Fetches userID from database.
	 *
	 * @param userName
	 * @return userID
	 */public static String getUserID(String userName) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		 String userIDReturn=null;

		 DataBaseDriver dataBase = new DataBaseDriver();

		 Connection dataBaseConn = dataBase.getConnection();
		 try{
			 String command ="SELECT * FROM `electro956_db`.`UserMap` WHERE userName='"+userName+"';";

			 ResultSet rs = dataBase.getDataBaseInfo(dataBaseConn, command);

			 while(rs.next()){
				 userIDReturn = rs.getString("userID");
			 }

		 }finally{
			 try {
				 dataBaseConn.close();

			 } catch (SQLException e) {
				 System.out.println("CANNOT CLOSE MYSQL CONNECTION");
				 e.printStackTrace();
			 }
		 }

		 System.out.println("=====USER ID FETCHED FROM DATABASE=====");
		 return userIDReturn;

	 }

	 /**
	  * Updates userImage in database.
	  *
	  * @throws SQLException the SQL exception
	  * @throws InstantiationException the instantiation exception
	  * @throws IllegalAccessException the illegal access exception
	  * @throws ClassNotFoundException the class not found exception
	  */
	 public void updateUserImageInDatabase(UserImage userImageSmall, UserImage userImageLarge) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		 DataBaseDriver dataBase = new DataBaseDriver();

		 Connection dataBaseConn = dataBase.getConnection();

		 String command ="UPDATE UserImage"
				 + " SET userID='"+this.userID+"', userImageSmall='"+userImageSmall.getBase64Image()+"', userImageLarge='"+userImageLarge.getBase64Image()+"'"
				 +  "WHERE userID='"+this.userID+"';";


		 dataBase.executeUpdate(dataBaseConn, command);


		 System.out.println("=====USER IMAGE UPDATED IN DATABASE=====");

	 }

	 /**
	  * Save new userImage to database.
	  *
	  * @throws SQLException the SQL exception
	  * @throws InstantiationException the instantiation exception
	  * @throws IllegalAccessException the illegal access exception
	  * @throws ClassNotFoundException the class not found exception
	  */
	 public void saveUserImageToDatabase(UserImage userImageSmall, UserImage userImageLarge) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		 DataBaseDriver dataBase = new DataBaseDriver();

		 Connection dataBaseConn = dataBase.getConnection();

		 String command ="INSERT INTO "
				 + "`electro956_db`.`UserImage` (`userID`,`userImageSmall`, `userImageLarge`)"
				 + "VALUES"
				 + "('"+this.userID+"','"+userImageSmall.getBase64Image()+"','"+userImageLarge.getBase64Image()+"');";




		 dataBase.executeUpdate(dataBaseConn, command);


		 System.out.println("=====USER IMAGE SENT TO DATABASE=====");

	 }

	 /**
	  * Get userImage from database.
	  *
	  * @throws SQLException the SQL exception
	  * @throws InstantiationException the instantiation exception
	  * @throws IllegalAccessException the illegal access exception
	  * @throws ClassNotFoundException the class not found exception
	  */
	 public Map<String, String> getUserImageFromDatabase() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		 Map<String, String> picArray = new HashMap();

		 DataBaseDriver dataBase = new DataBaseDriver();

		 Connection dataBaseConn = dataBase.getConnection();

		 String command ="SELECT * FROM `electro956_db`.`UserImage` WHERE userID='"+this.userID+"';";



		 ResultSet rs = dataBase.getDataBaseInfo(dataBaseConn, command);

		 while(rs.next()){
			 String imageBase64Small= rs.getString("userImageSmall");
			 String imageBase64Large= rs.getString("userImageLarge");

			 picArray.put("userImageSmall", imageBase64Small);
			 picArray.put("userImageLarge", imageBase64Large);


			 System.out.println("=====USER IMAGE FETCHED FROM DATABASE=====");
		 }
		 return picArray;
	 }


	 /**
	  * Verify password.
	  *
	  * @param password the password
	  * @return true, if successful
	  */
	 public boolean verifyPassword(String password){
		 String testString = hashPassword(password+this.userID);

		 if(testString.equals(this.password)){
			 return true;
		 }
		 return false;
	 }

}
