package user;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.DataBase;
import post.Post;


/**
 * @author Sean Maloney
 * 
 */

/**
 * The Class Location.
 */
public class Location {

	/** The latitude. */
	private double latitude;
	
	/** The longitude. */
	private double longitude;
	
	/** The id. */
	private String ID;

	/**
	 * Instantiates a new location.
	 *
	 * @param latitude: the latitude
	 * @param longitude: the longitude
	 * @param ID: the id
	 */
	public Location(double latitude, double longitude, String ID){
		this.latitude = latitude;
		this.longitude = longitude;
		this.ID = ID;
	}
	
	/**
	 * Instantiates a new location for user.
	 *
	 * @param user
	 */
	public Location(User user){
		DataBase dataBase = new DataBase();
		Connection dataBaseConn=null;
		
		try {
			dataBaseConn = dataBase.getConnection();

			String command ="SELECT * FROM UserLocation WHERE userID='"+user.getUserID()+"'";

			ResultSet rs = dataBase.getDataBaseInfo(dataBaseConn, command);
			while(rs.next()){
				this.latitude= Double.parseDouble(rs.getString("latitude"));
				this.longitude =Double.parseDouble(rs.getString("longitude"));
			}
			this.ID = user.getUserID();
			System.out.println("=====USER LOCATION CREATED FROM DATABASE=====");
			
		}catch (Exception e) {
			System.out.println("=====ERROR GETTING LOCATION FROM DATABASE=====");
			e.printStackTrace();
		}finally{
			try {
				dataBaseConn.close();
			} catch (SQLException e) {
				System.out.println("CANNOT CLOSE MYSQL CONNECCION");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Instantiates a new location for post.
	 *
	 * @param post
	 */
	public Location(Post post){
		DataBase dataBase = new DataBase();
		Connection dataBaseConn = null;
		
		try {
			dataBaseConn = dataBase.getConnection();

			String command ="SELECT * FROM PostLocation WHERE postID='"+post.getPostId()+"'";

			ResultSet rs = dataBase.getDataBaseInfo(dataBaseConn, command);
			while(rs.next()){
				this.latitude= Double.parseDouble(rs.getString("latitude"));
				this.longitude = Double.parseDouble(rs.getString("longitude"));
			}
			
			this.ID = post.getPostId();
			System.out.println("=====POST LOCATION CREATED FROM DATABASE=====");
			
		}catch (Exception e) {
			System.out.println("=====ERROR GETTING POST LOCATION FROM DATABASE=====");
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
	 * Gets the latitude.
	 *
	 * @return the latitude
	 */
	//getters
	public double getLatitude(){
		return this.latitude;
	}

	/**
	 * Gets the longitude.
	 *
	 * @return the longitude
	 */
	public double getLongitude(){
		return this.longitude;
	}
	
	/**å
	 * Save new user location to database.
	 *
	 * @throws SQLException the SQL exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 */
	public void saveNewUserLocationToDatabase() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		DataBase dataBase = new DataBase();

		Connection dataBaseConn = dataBase.getConnection();

		String command ="INSERT INTO "
				+ "`electro956_db`.`UserLocation` (`userID`,`latitude`,`longitude`)"
				+ "VALUES"
				+ "('"+this.ID+"','"+this.latitude+"','"+this.longitude+"');";

		dataBase.executeUpdate(dataBaseConn, command);
		System.out.println("=====USER LOCATION SENT TO DATABASE=====");

	}

	/**
	 * Update user location.
	 *
	 * @throws SQLException the SQL exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 */
	public void updateUser() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		DataBase dataBase = new DataBase();

		Connection dataBaseConn = dataBase.getConnection();

		String command ="UPDATE UserLocation"
				+ " SET userID='"+this.ID+"', latitude='"+this.latitude+"', longitude='"+this.longitude+"'"
				+  "WHERE userID='"+this.ID+"';";

		dataBase.executeUpdate(dataBaseConn, command);
		System.out.println("=====USER UPDATED IN DATABASE=====");


	}

	/**
	 * Save new post location to database.
	 *
	 * @throws SQLException the SQL exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 */
	public void saveNewPostLocationToDatabase() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		DataBase dataBase = new DataBase();

		Connection dataBaseConn = dataBase.getConnection();

		String command ="INSERT INTO "
				+ "`electro956_db`.`PostLocation` (`postID`,`latitude`,`longitude`)"
				+ "VALUES"
				+ "('"+this.ID+"','"+this.latitude+"','"+this.longitude+"');";

		dataBase.executeUpdate(dataBaseConn, command);
		System.out.println("=====POST LOCATION SENT TO DATABASE=====");

	}

	/**
	 * Update post location.
	 *
	 * @throws SQLException the SQL exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 */
	public void updatePost() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		DataBase dataBase = new DataBase();

		Connection dataBaseConn = dataBase.getConnection();

		String command ="UPDATE PostLocation"
				+ " SET postID='"+this.ID+"', latitude='"+this.latitude+"', longitude='"+this.longitude+"'"
				+  "WHERE postID='"+this.ID+"';";

		dataBase.executeUpdate(dataBaseConn, command);
		System.out.println("=====POST UPDATED IN DATABASE=====");
		System.out.println(command);


	}

	/**
	 * Delete user location.
	 *
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws SQLException the SQL exception
	 */
	public void deleteUser() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		DataBase dataBase = new DataBase();

		Connection dataBaseConn = dataBase.getConnection();

		String command ="DELETE FROM `electro956_db`.`UserLocation` WHERE userID='"+this.ID+"';";

		dataBase.executeUpdate(dataBaseConn, command);
		System.out.println("=====USER LOCATION DELETED IN DATABASE=====");
		System.out.println(command);

	}

	

}
