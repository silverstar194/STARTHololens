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
public class Post implements Comparable{

	
	/** The title. */
	private String title;
	
	/** The content. */
	private String content;
	
	/** The location. */
	private Location location;
	
	/** The time. */
	private long time;
	
	/** The user id. */
	private String userID;
	
	/** The post id. */
	private String postID;
	
	/** The end time. */
	private long endTime;

	/** The user. */
	private User user;
	
	private boolean deleted; // always false unless deleted
	
	/** The number spam reports. */
	private int spamCount;

	/** Is the post marked as spam */
	private boolean spam;

	/**
	 * Instantiates a new post.
	 *
	 * @param title: the title
	 * @param content: the content
	 * @param latitude: the latitude of post
	 * @param longitude: the longitude of post
	 * @param userID: the user id
	 * @param endTime: the expiration time
	 */
	public Post(String title, String content, double latitude, double longitude, String userID, long endTime, boolean deleted , int spamCount, boolean spam){
		if (content.contains("'") || title.contains("'")) 
		{
			content = content.replace("'", "''");
			title = title.replace("'", "''");
		}
		
		this.title = title;
		this.content = content;
		this.postID = idGenerator();
		this.location = new Location(latitude, longitude, this.postID);
		this.userID = userID;
		this.endTime = endTime;
		this.time=System.currentTimeMillis();
		this.deleted = deleted;
		this.spamCount = spamCount;
		this.spam = spam;

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
	public Post(String title, String content, String userID, String postID, long time, long endTime, boolean deleted, int spamCount, boolean spam){
		if (content.contains("'") || title.contains("'")) 
		{
			content = content.replace("'", "''");
			title = title.replace("'", "''");
		}
		this.title = title;
		this.content = content;
		this.postID = postID;
		this.location = new Location(this);
		this.userID = userID;
		this.endTime = endTime;
		this.time=time; 
		this.user = new User(userID);
		this.deleted = deleted;
		this.spamCount = spamCount;
		this.spam = spam;
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
				this.postID = id;
				this.location = new Location(this);
				this.endTime = Long.parseLong(rs.getString("endTime"));
				this.time = rs.getLong("time");
				this.deleted = Boolean.parseBoolean(rs.getString("deleted"));
				this.spamCount = Integer.parseInt(rs.getString("spamCount"));
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
	 * Gets the spam count.
	 *
	 * @return the spam count
	 */
	public int getSpamCount(){
		return this.spamCount;
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
	 * Sets the location.
	 *
	 * @param location the new location
	 */
	public void setLocation(Location location){
		this.location = location;
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
	 * Sets the end time.
	 *
	 * @param endTme the new end time
	 */
	public void setEndTime(long endTme){
		this.endTime = endTime;
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
	 * Sets the spam count.
	 *
	 * 
	 */
	public void addToSpamCount(int spamCount){
		this.spamCount += spamCount;
	}
	
	/** marks post as spam **/
	public void spam(){
		this.spam = true;
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
		userMap.put("endTime", ""+this.endTime);
		userMap.put("latitude", ""+this.location.getLatitude());
		userMap.put("longitude", ""+this.location.getLongitude());
		userMap.put("deleted", Boolean.toString(this.deleted));
		userMap.put("spam", Boolean.toString(this.spam));
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
	

	//for sorting
	/*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::                                                                         :*/
	/*::  This routine calculates the distance between two points (given the     :*/
	/*::  latitude/longitude of those points). It is being used to calculate     :*/
	/*::  the distance between two locations using GeoDataSource (TM) prodducts  :*/
	/*::                                                                         :*/
	/*::  Definitions:                                                           :*/
	/*::    South latitudes are negative, east longitudes are positive           :*/
	/*::                                                                         :*/
	/*::  Passed to function:                                                    :*/
	/*::    lat1, lon1 = Latitude and Longitude of point 1 (in decimal degrees)  :*/
	/*::    lat2, lon2 = Latitude and Longitude of point 2 (in decimal degrees)  :*/
	/*::    unit = the unit you desire for results                               :*/
	/*::           where: 'M' is statute miles (default)                         :*/
	/*::                  'K' is kilometers                                      :*/
	/*::                  'N' is nautical miles                                  :*/
	/*::  Worldwide cities and other features databases with latitude longitude  :*/
	/*::  are available at http://www.geodatasource.com                          :*/
	/*::                                                                         :*/
	/*::  For enquiries, please contact sales@geodatasource.com                  :*/
	/*::                                                                         :*/
	/*::  Official Web site: http://www.geodatasource.com                        :*/
	/*::                                                                         :*/
	/*::           GeoDataSource.com (C) All Rights Reserved 2015                :*/
	/*::                                                                         :*/
	/*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/



	/** The post list. */
	@SuppressWarnings("rawtypes")
	private List postList = new ArrayList<Post>(); 


	/**
	 * Distance from me.
	 *
	 * @param lat2 the lat2
	 * @param lon2 the lon2
	 * @return the double
	 */
	private Double distanceFromMe(double lat2, double lon2) {

		Location currentLocation = this.user.getlastLocation();
		double theta = currentLocation.getLongitude() - lon2;
		double dist = Math.sin(deg2rad(currentLocation.getLatitude())) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(currentLocation.getLatitude())) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		dist = dist * 1.609344;

		return dist;
	}

	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::	This function converts decimal degrees to radians						 :*/
	/**
	 * Deg2rad.
	 *
	 * @param deg the deg
	 * @return the double
	 */
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	private static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::	This function converts radians to decimal degrees						 :*/
	/**
	 * Rad2deg.
	 *
	 * @param rad the rad
	 * @return the double
	 */
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	private static double rad2deg(double rad) {
		return (rad * 180 / Math.PI);
	}

	/**
	 * Sort by location.
	 *
	 * @return the list
	 */
	@SuppressWarnings("unchecked")
	public List<Sort> sortByLocation(){
		Collections.sort(postList);
		return this.postList;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Object o) {
		if(o instanceof Post){
			Post e = (Post)o;

			if(Sort.getSortMethod().equals("location")){
				
				Double thisDistance = this.distanceFromMe(this.getLocation().getLatitude(), this.getLocation().getLongitude());
				Double eDistance =  distanceFromMe(e.getLocation().getLatitude(), e.getLocation().getLongitude());
				
				return thisDistance.compareTo(eDistance);
				
		
			} else if(Sort.getSortMethod().equals("starttime")){

				return ((Long)e.getTime()).compareTo(this.time);
				
			} else if(Sort.getSortMethod().equals("endtime")){

				return ((Long)e.getEndTime()).compareTo(this.endTime);
			}
		}
		
		
		return 0;
	}
}
