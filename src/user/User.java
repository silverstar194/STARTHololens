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
import database.DataBase;
import message.Message;
import post.Post;


/**
 * @author Sean Maloney
 * 
 */

/**
 * Creates users and saves them to database.
 */

public class User {

	static final int MAX_SPAM_COUNT=3;

	/** The user id. */
	//generated server side
	private String userID;

	/** The first name. */
	//passed by app
	private String firstName;

	/** The last name. */
	private String lastName;

	/** The last location. */
	private Location lastLocation;

	/** The email. */
	private String email;

	/** The password. */
	private String password;

	/** The user name. */
	private String userName;

	private String bio;

	/** The number of new messages. */
	private int messageCount;

	/** The number of user posts. */
	private int postCount;

	/** The number spam reports. */
	private int spamCount;

	/**
	 * Instantiates a new user.
	 *
	 * @param firstName: user first name
	 * @param lastName: user last name
	 * @param latitude: user latitude
	 * @param longitude: user longitude
	 * @param email: user email
	 * @param password: user password
	 * @param userName: user name
	 * @param work: user place of work
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public User(String firstName, String lastName, double latitude, double longitude, 
			String email, String password, String userName, String bio) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{

		if (bio.contains("'") || email.contains("'")) 
		{
			bio = bio.replace("'", "''");
			email = email.replace("'", "''");
		}

		this.userID = idGenerator();
		this.firstName = firstName;
		this.lastName = lastName;
		this.lastLocation = new Location(latitude, longitude, this.userID);
		this.email = email;
		this.password = hashPassword(password+this.userID);
		this.userName = userName;
		this.bio = bio;

		Message newMessage = new Message();
		this.messageCount = newMessage.getNewMessageCountByUser(this);
		Post newPost = new Post();
		this.postCount = newPost.getPostCountByUser(this);

		System.out.println("=====Generated New User=====");

	}

	/**
	 * Instantiates a new user.
	 *
	 * @param id: user id
	 * @throws SQLException 
	 */
	public User(String id){
		DataBase dataBase = new DataBase();
		Connection dataBaseConn = null;
		try {
			dataBaseConn = dataBase.getConnection();


			String command ="SELECT * FROM User WHERE userID='"+id+"'";

			ResultSet rs = dataBase.getDataBaseInfo(dataBaseConn, command);
			while(rs.next()){
				this.userID = rs.getString("userID");
				this.firstName = rs.getString("firstName");
				this.lastName = rs.getString("lastName");
				this.email = rs.getString("email");
				this.lastLocation = new Location(this);
				this.password = rs.getString("password");
				this.userName = rs.getString("userName");
				this.bio = rs.getString("bio").replace("'", "''");
				this.spamCount = Integer.parseInt(rs.getString("spamCount"));

				Message newMessage = new Message();
				this.messageCount = newMessage.getNewMessageCountByUser(this);
				Post newPost = new Post();
				this.postCount = newPost.getPostCountByUser(this);
			}

			System.out.println("=====USER CREATED FROM DATABASE=====");

		} catch (Exception e) {
			System.out.println("=====ERROR GETTING USER FROM DATABASE=====");
			e.printStackTrace();
		}finally{
			try {
				dataBaseConn.close();
			} catch (SQLException e) {
				System.out.println("CANNOT CLOSE MYSQL CONNECTION");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Blank Constructor
	 */
	public User(){

	}


	/**
	 * Gets the first name.
	 *
	 * @return the first name
	 */
	//getters
	public String getfirstName(){
		return this.firstName;
	}

	/**
	 * Gets the last name.
	 *
	 * @return the last name
	 */
	public String getlastName(){
		return this.lastName;
	}

	/**
	 * Gets the last location.
	 *
	 * @return the last location
	 */
	public Location getlastLocation(){
		return this.lastLocation;
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
		return this.password;
	}

	/**
	 * Gets the user name.
	 *
	 * @return the user name
	 */
	public String getuserName(){
		return this.userName;
	}

	/**
	 * Gets the bio.
	 *
	 * @return the bio
	 */
	public String getBio(){
		return this.bio;
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
	 * Sets the first name.
	 *
	 * @param firstName the new first name
	 */
	//setters
	public void setfirstName(String firstName){
		this.firstName = firstName;
	}

	/**
	 * Sets the last name.
	 *
	 * @param lastName the new last name
	 */
	public void setlastName(String lastName){
		this.lastName = lastName;
	}

	/**
	 * Sets the last location.
	 *
	 * @param lastLocation the new last location
	 */
	public void setlastLocation(Location lastLocation){
		this.lastLocation = lastLocation;
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
		this.password = hashPassword(password+this.userID);
	}

	/**
	 * Sets the user name.
	 *
	 * @param userName the new user name
	 */
	public void setuserName(String userName){
		this.userName = userName;
	}

	/**
	 * Sets the bio.
	 *
	 * @param bio the new bio
	 */
	public void setBio(String bio){
		if (bio.contains("'")) 
		{
			bio = bio.replace("'", "''");
		}
		this.bio = bio;
	}

	/**
	 * Adds one to the user's spam count
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public void addToSpamCount(int spamValue) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		this.spamCount +=spamValue;
		this.update();
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
		DataBase dataBase = new DataBase();

		Connection dataBaseConn = dataBase.getConnection();

		String command ="INSERT INTO "
				+ "`electro956_db`.`User` (`userID`,`firstName`,`lastName`,`email`,`password`,`userName`,`bio`)"
				+ "VALUES"
				+ "('"+this.userID+"','"+this.firstName+"','"+this.lastName+"','"+this.email+"','"+this.password+"','"+this.userName+"','"+this.bio+"');";

	
		
		String commandUserMap = "INSERT INTO `electro956_db`.`UserMap` (`userName`, `userID`) VALUES ('"+this.userName+"', '"+this.userID+"');";

		dataBase.executeUpdate(dataBaseConn, command);
		dataBase.executeUpdate(dataBaseConn, commandUserMap);

		this.lastLocation.saveNewUserLocationToDatabase();

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
		DataBase dataBase = new DataBase();

		Connection dataBaseConn = dataBase.getConnection();

		String command ="UPDATE User"
				+ " SET firstName='"+this.firstName+"', lastName='"+this.lastName+"', email='"+this.email+"', password='"+this.password+"', userName='"+this.userName+"', bio='"+this.bio+"', spamCount='"+this.spamCount+"'"
				+  "WHERE userID='"+this.userID+"';";

		String commandUserMap = "UPDATE UserMap SET userName='"+this.userName+"' WHERE userID='"+this.userID+"';";

		dataBase.executeUpdate(dataBaseConn, command);
		dataBase.executeUpdate(dataBaseConn, commandUserMap);

		this.lastLocation.updateUser();

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

		DataBase dataBase = new DataBase();

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

		 DataBase dataBase = new DataBase();

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

		 System.out.println("=====USERID FETCHED FROM DATABASE=====");
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
		 DataBase dataBase = new DataBase();

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
		 DataBase dataBase = new DataBase();

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

		 DataBase dataBase = new DataBase();

		 Connection dataBaseConn = dataBase.getConnection();

		 String command ="SELECT * FROM `electro956_db`.`UserImage` WHERE userID='"+this.userID+"';";
		 


		 ResultSet rs = dataBase.getDataBaseInfo(dataBaseConn, command);

		 while(rs.next()){
			 String imageBase64Small= rs.getString("userImageSmall");
			 String imageBase64Large= rs.getString("userImageLarge");

			 picArray.put("userImageSmall", imageBase64Small);
			 picArray.put("userImageLarge", imageBase64Large);
			 picArray.put("firstName", this.firstName);
			 picArray.put("lastName", this.lastName);
		 }

		 System.out.println("=====USER IMAGE FETCHED FROM DATABASE=====");

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

	 /**
	  * Checks user is not spammy.
	  *
	  * @return true, if ok to pass
	  */
	 public boolean spamCheck(){

		 if(this.spamCount<=MAX_SPAM_COUNT){
			 return true;
		 }
		 return false;
	 }
}
