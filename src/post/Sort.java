package post;

import java.util.*;
import database.DataBase;
import user.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;



/**
 * @author Sean Maloney
 * 
 */

/**
 * Allows sorting of Posts.
 */
public class Sort  {
	static final int MAX_SPAM_COUNT = 5;

	/** The sort method. */
	private static String sortMethod;

	/** The posts. */
	private ArrayList<Post> posts = new ArrayList<Post>();

	/**
	 * Instantiates a new sort.
	 *
	 * @param max: the max amount of return post
	 * @param sortMethod: the sort method
	 * @throws NumberFormatException the number format exception
	 * @throws SQLException the SQL exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 */
	@SuppressWarnings("static-access")
	public Sort(int max, String sortMethod) throws NumberFormatException, SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		this.sortMethod = sortMethod;
		DataBase dataBase = new DataBase();
		Connection dataBaseConn;

		dataBaseConn = dataBase.getConnection();

		String command ="SELECT * FROM Post WHERE deleted='false' LIMIT "+max;

		ResultSet rs = dataBase.getDataBaseInfo(dataBaseConn, command);

		while(rs.next()){
			Post newPost = new Post(rs.getString("title"), rs.getString("content"), rs.getString("userID"),
					rs.getString("postID"), rs.getLong("time"), Long.parseLong(rs.getString("endTime")), 
					Boolean.parseBoolean(rs.getString("deleted")), Integer.parseInt(rs.getString("spamCount")), Boolean.parseBoolean(rs.getString("spam")));

			if(newPost.getSpamCount()<=MAX_SPAM_COUNT){
				posts.add(newPost);
			}else{
				newPost.delete();//deletes posts marked as spam
				newPost.update();
				newPost.spam(); 
				
				User newUser = new User(newPost.getUserId());
				newUser.addToSpamCount(1);
			
				
			}

		}	
	}

	/**
	 * Sort by desired parameter.
	 */
	@SuppressWarnings("unchecked")
	private void sort(){
		if(sortMethod.equals("endtime")){
			Collections.reverse(posts);
		}

		Collections.sort(posts);

	}

	/**
	 * Gets the sorted posts.
	 *
	 * @return the sorted posts
	 */
	public ArrayList<Post> getSortedPosts(){
		sort();
		return posts;
	}

	/**
	 * Gets the sort method.
	 *
	 * @return the sort method
	 */
	public static String getSortMethod(){
		return sortMethod;
	}

}

