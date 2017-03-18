package post;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import database.DataBase;
import message.Message;
import user.Location;
import user.User;


/**
 * @author Sean Maloney
 * 
 */

/**
 * Creates posts from Users and sends them to the database.
 */
public class Post {

	
	/** The title. */
	private String title;
	
	/** The content. */
	private String content;
	
	/** The time. */
	private long time;
	
	/** The user id. */
	private String userID;
	
	private boolean deleted; // always false unless deleted
	

	/**
	 * Instantiates a new post.
	 *
	 * @param title: the title
	 * @param content: the content
	 * @param userID: the user id
	 */
	public Post(String title, String content, String userID, long endTime, boolean deleted){
		if (content.contains("'") || title.contains("'")) 
		{
			content = content.replace("'", "''");
			title = title.replace("'", "''");
		}
		
		this.title = title;
		this.content = content;
		this.userID = userID;
		this.time=System.currentTimeMillis();
		this.deleted = deleted;

	}

	/**
	 * Instantiates a new post.
	 *
	 * @param title: the title
	 * @param content: the content
	 * @param userID: the user id
	 * @param postID: the post id
	 * @param time: the time post was made
	 * @param endTime: the expiration time
	 */
	public Post(String title, String content, String userID){
		if (content.contains("'") || title.contains("'")) 
		{
			content = content.replace("'", "''");
			title = title.replace("'", "''");
		}
		this.title = title;
		this.content = content;
		this.userID = userID;

	}

	/**
	 * Instantiates a new post.
	 *
	 * @param id: the id of post
	 */
	public Post(String id){
		DataBase dataBase = new DataBase();
		Connection dataBaseConn = null;
		try {
			dataBaseConn = dataBase.getConnection();


			String command ="SELECT * FROM Post WHERE postID='"+id+"'";

			ResultSet rs = dataBase.getDataBaseInfo(dataBaseConn, command);
			while(rs.next()){
				this.title = rs.getString("title");
				this.content = rs.getString("content");
				this.userID = rs.getString("userID");
			}

			System.out.println("=====USER CREATED FROM DATABASE=====");

		} catch (Exception e) {
			System.out.println("=====ERROR GETTING USER FROM DATABASE=====");
			e.printStackTrace();
		}finally{
			try {
				dataBaseConn.close();
			} catch (SQLException e) {
				System.out.println("CANNOT CLOSE CONNECTION TO MYSQL");
				e.printStackTrace();
			}
		}
	}



	/**
	 * Instantiates a new blank post.
	 */
	public Post(){
	}
	
	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	//getters
	public String getTitle(){
		return this.title;
	}

	/**
	 * Gets the content.
	 *
	 * @return the content
	 */
	public String getContent(){
		return this.content;
	}
	
	/**
	 * Gets the location.
	 *
	 * @return the location
	 */
	public Location getLocation(){
		return this.location;
	}

	/**
	 * Gets the time.
	 *
	 * @return the time
	 */
	public long getTime(){
		return this.time;
	}

	/**
	 * Gets the end time.
	 *
	 * @return the end time
	 */
	public long getEndTime(){
		return this.endTime;
	}


	/**
	 * Gets the user id.
	 *
	 * @return the user id
	 */
	public String getUserId(){
		return this.userID;
	}

	/**
	 * Gets the post id.
	 *
	 * @return the post id
	 */
	public String getPostId(){
		return this.postID;
	}

	/**
	 * Sets the title.
	 *
	 * @param title the new title
	 */
	//setters
	public void setTitle(String title){
		if ( title.contains("'")) 
		{
			title = title.replace("'", "''");
		}
		this.title = title;
	}

	/**
	 * Sets the content.
	 *
	 * @param content the new content
	 */
	public void setContent(String content){
		if (content.contains("'")) 
		{
			content = content.replace("'", "''");
		}
		
		this.content = content;
	}


	/**
	 * Sets the time.
	 *
	 * @param time the new time
	 */
	public void setTime(long time){
		this.time = time;
	}


	
	/**
	 * Sets the post to deleted.
	 *
	 * @param endTme the new end time
	 */
	public void delete(){
		this.deleted = true;
	}
	


	/**
	 * Save new post to database.
	 *
	 * @throws SQLException the SQL exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 */
	public void saveNewPostToDatabase() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		DataBase dataBase = new DataBase();

		Connection dataBaseConn = dataBase.getConnection();

		String command ="INSERT INTO "
				+ "`electro956_db`.`Post` (`postID`,`title`,`content`,`time`,`userID`,`endtime`, `deleted`) "
				+ "VALUES"
				+ " ('"+this.postID+"','"+this.title+"','"+this.content+"','"+this.time+"','"+this.userID+"','"+this.endTime+"','"+this.deleted+"');";

		dataBase.executeUpdate(dataBaseConn, command);
		this.location.saveNewPostLocationToDatabase();
		System.out.println("=====POST SENT TO DATABASE=====");

	}

	/**
	 * Update  post in database.
	 *
	 * @throws SQLException the SQL exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 */
	public void update() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		DataBase dataBase = new DataBase();

		Connection dataBaseConn = dataBase.getConnection();

		String command ="UPDATE Post"
				+ " SET postID='"+this.postID+"', title='"+this.title+"', "
				+ "content='"+this.content+"', time='"+this.time+"', userID='"+this.userID+"', endTime='"+this.endTime+"', deleted='"+this.deleted+"', spamCount='"+this.spamCount+"', spam='"+this.spam+"'"
				+  " WHERE postID='"+this.postID+"';";

		dataBase.executeUpdate(dataBaseConn, command);
		this.location.updatePost();
		System.out.println("=====POST UPDATED IN DATABASE=====");



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
	 * Delete post from database.
	 *
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws SQLException the SQL exception
	 */
	public void deletePost() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{

		DataBase dataBase = new DataBase();

		Connection dataBaseConn = dataBase.getConnection();

		String command ="UPDATE Post"
				+ " SET deleted='true' WHERE postID='"+this.postID+"';";
		this.deleted = true;

		dataBase.executeUpdate(dataBaseConn, command);
		System.out.println("=====POST DELETED FROM DATABASE=====");



	}


	/**
	 * Checks if is user's post.
	 *
	 * @param user the user
	 * @return true, if is user post
	 */
	public boolean isUserPost(User user){
		if(this.userID.equals(user.getUserID())){
			return true;
		}

		return false;
	}


	/**
	 * Gets the post info.
	 *
	 * @return the post info
	 */
	public String getPostInfo() {
		DataBase dataBase = new DataBase();

		Map<String,String> userMap = new HashMap<String, String>();
		String titleNew = null;
		String contentNew = null;
		if(this.title.contains("''") || this.content.contains("''")){
			titleNew = this.title.replace("''", "'");
			contentNew = this.content.replace("''", "'");
		}else{
			titleNew = this.title;
			contentNew = this.content;
		}


		userMap.put("postID", this.postID);
		userMap.put("title", titleNew);
		userMap.put("content", contentNew);
		userMap.put("time", ""+this.time);
		userMap.put("userID", this.userID);

		JSONObject json = new JSONObject(userMap);

		System.out.println("=====USER CONVERTED TO JSON=====");

		return json.toString();

	}
	
	/**
	 * Gets the posts by user.
	 *
	 * @param user
	 * @return the message by user
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws SQLException the SQL exception
	 */
	public ArrayList<Post> getPostByUserID(User user) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		DataBase dataBase = new DataBase();
		Connection dataBaseConn = null;
		ArrayList<Post> posts = new ArrayList<Post>();
		try{
		dataBaseConn = dataBase.getConnection();

		String command ="SELECT * FROM Post WHERE userID='"+user.getUserID()+"'";

		ResultSet rs = dataBase.getDataBaseInfo(dataBaseConn, command);

		while(rs.next()){
			Post newPost = new Post(rs.getString("title"), rs.getString("content"), rs.getString("userID"),
					rs.getString("postID"), Long.parseLong(rs.getString("time")), Long.parseLong(rs.getString("endTime")), Boolean.parseBoolean(rs.getString("deleted")), Integer.parseInt(rs.getString("spamCount")), Boolean.parseBoolean(rs.getString("spam")));

			if(true){ //later add a max distance here so a user in NY doesn't get something in CA
						//also will flex area based on num. in DB
			posts.add(newPost);
			}
			
		}
		}finally{
			dataBaseConn.close();
		}
		
		return posts;
	}
	
	public int getPostCountByUser(User user) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		DataBase dataBase = new DataBase();
		Connection dataBaseConn= null;
		int count =0;
		try{
			dataBaseConn = dataBase.getConnection();

			String command ="SELECT * FROM Post WHERE userID='"+user.getUserID()+"'";

			ResultSet rs = dataBase.getDataBaseInfo(dataBaseConn, command);

			while(rs.next()){
				rs.getString("title");
				count++;
			}

		}finally{
			dataBaseConn.close();
		}

		return count;
	}
	
}
