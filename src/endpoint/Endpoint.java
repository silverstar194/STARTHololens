package endpoint;

import java.util.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author Sean Maloney
 * 
 */


import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;


import com.sun.jersey.core.header.FormDataContentDisposition;

import message.Message;
import post.Sort;
import post.Post;
import user.Location;
import user.User;
import user.UserImage;

import  javax.ws.rs.QueryParam;


/**
 * The Class provides endpoint URLs for the Loop API. This API provides a REST backend for Loop app.
 * Handles user and post creation and management as well as messaging between users.  This class routes the
 * URLs and handles hold high level view of API. </br>
 * <p>This API also manages MYSQL database for generated data.</p>
 * <p>All responses and requests are made in JSON.</p>
 * 
 */
@Path("/")
public class Endpoint {

	/**
	 * Hello. "Hello World!" for Halo API
	 *
	 * @return "Hello World!"
	 */
	@GET
	@Path("/hello")
	@Produces(MediaType.TEXT_PLAIN)
	public Response hello() {

		System.out.println("=====SERVED JSON TO USER=====");
		return Response.status(200).entity("{\"Hello World!\": \"The Loop API is working!\"}").build();
	} 

	/**
	 * Allows users to login with username and password.
	 * @param userName: username
	 * @param password: user password (SHA-256 Hashed and Salted)
	 * @return JSON output with userID
	 * @throws SQLException 
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 */
	@GET
	@Path("/login")
	@Produces(MediaType.TEXT_PLAIN)
	public Response userLogin(@QueryParam("userName") String userName, @QueryParam("password") String password) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{

		if(userName != null && password != null){

			User newUser = new User(User.getUserID(userName));
			if(!newUser.spamCheck()){
				return Response.status(400).entity("{\"Error\":\"Too much Spam\"}").build();
			}
			if(newUser.verifyPassword(password)){
				String json = newUser.getUserInfo();

				System.out.println("=====SERVED JSON TO USER=====");
				return Response.status(200).entity(json).build();
			}else{
				System.out.println("====Password ERROR=====");
				return Response.status(400).entity("{\"Error\":\"Password Incorret\"}").build();
			}

		}

		System.out.println("=====userID ERROR=====");
		return Response.status(400).entity("{\"Error\":\"Provide userID\"}").build();

	}  


	/**
	 * Allows generation of new users which are added to MySQL database.
	 *
	 * @param firstName: first name of user
	 * @param lastName: last name of user
	 * @param latitude: latitude of user
	 * @param longitude: longitude of user
	 * @param email: email of user
	 * @param password: user password (SHA-256 Hashed and Salted)
	 * @param userName: username
	 * @param work: place of work for user
	 * @return JSON output with userID
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws IOException 
	 * @throws UnirestException 
	 * @throws CloneNotSupportedException 
	 */
	@GET
	@Path("/newuser")
	@Produces(MediaType.TEXT_PLAIN)
	public Response newUser(@QueryParam("email") String email,  
			@QueryParam("password") String password) throws InstantiationException, IllegalAccessException, ClassNotFoundException, CloneNotSupportedException, IOException{
		try{
			if(email != null && password != null) {
				
				User newUser = new User(email, password);

				newUser.saveNewUserToDatabase();

				System.out.println("=====SERVED JSON TO USER=====");
				return Response.status(200).entity("{\"Message\":\"User Added to Database\", \"userID\":\""+newUser.getUserID()+"\"}").build();
			}

		}catch(com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e){
			System.out.println("=====DUPLICATE USERNAME=====");
			
			return Response.status(400).entity("{\"Error\":\"Duplicate Username\"}").build();

		} catch(SQLException e){
			System.out.println("=====General Error Adding New User to Database=====");
			e.printStackTrace();
			return Response.status(400).entity("{\"Error\":\"General Error Adding User to Database\"}").build();
		}

		return Response.status(400).entity("{\"Error\":\"Provide all parameters\"}").build();
	}   

	/**
	 * Retrieves user from database based on ID
	 *
	 * @param userID the user id
	 * @param password: user password (SHA-256 Hashed and Salted)
	 * @return JSON output of user info.
	 */
	@GET
	@Path("/getuser")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getUser(@QueryParam("userID") String userID){

		if(userID != null){

			User newUser = new User(userID);

			String json = newUser.getUserInfo();

			System.out.println("=====SERVED JSON TO USER=====");
			return Response.status(200).entity(json).build();

		}

		System.out.println("=====userID ERROR=====");
		return Response.status(400).entity("{\"Error\":\"Provide userID\"}").build();

	} 
	
	 /* Allows updates to user password.
	 *
	 * @param userID: userID
	 * @param firstName: first name of user
	 * @param lastName: last name of user
	 * @param latitude: user latitude
	 * @param longitude: user longitude
	 * @param email: user email
	 * @param password: user password (SHA-256 Hashed and Salted)
	 * @param userName: username
	 * @param work: user place of work
	 * @return confirms user was updated
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 */
	@GET
	@Path("/updatepassword")
	@Produces(MediaType.TEXT_PLAIN)
	public Response updateLocation(@QueryParam("userID") String userID, @QueryParam("passwordold") String passwordOld, @QueryParam("passwordnew") String passwordNew) throws InstantiationException, IllegalAccessException, ClassNotFoundException  {
		try{
			if(userID != null && passwordOld != null && passwordNew != null) {

				User newUser = new User(userID);
				if(newUser.verifyPassword(passwordOld)){
					newUser.setpassword(passwordNew);
					newUser.update();

					System.out.println("=====SERVED JSON TO USER=====");
					return Response.status(200).entity("{\"Message\":\"User Password Updated in Database\", \"userID\":\""+newUser.getUserID()+"\"}").build();

				}
				return Response.status(400).entity("{\"Error\":\"Password or Username Incorrect\"}").build();

			}	
		} catch(SQLException e){
			System.out.println("=====General Error Updating User=====");
			e.printStackTrace();
			return Response.status(400).entity("{\"Error\":\"General Error Adding User to Database\"}").build();
		}

		return Response.status(400).entity("{\"Error\":\"Provide all parameters\"}").build();
	}   
	
	/**
	 * Delete user from database.
	 *
	 * @param userID: user id
	 * @param password: user password (SHA-256 Hashed and Salted)
	 * @return Confirms user was deleted from database
	 */
	@GET
	@Path("/deleteuser")
	@Produces(MediaType.TEXT_PLAIN)
	public Response deleteUser(@QueryParam("userID") String userID, @QueryParam("password") String password){

		if(userID != null && password != null){
			User newUser = new User(userID);

			if(newUser.verifyPassword(password)){
				try {
					newUser.deleteUser();

					System.out.println("=====SERVED JSON TO USER=====");
					return Response.status(200).entity("{\"Message\":\"User Deleted from Database\", \"userID\":\""+newUser.getUserID()+"\"}").build();

				} catch (Exception e) {
					System.out.println("=====ERROR DELETING USER=====");
					e.printStackTrace();
					return Response.status(200).entity("{\"Error\":\"General Error Deleting User\"}").build();
				}

			}
		}
		return Response.status(200).entity("{\"Error\":\"Provide userID and password\"}").build();

	}



	/**
	 * Creates new post and stores in database.
	 *
	 * @param title: post title
	 * @param content: post content
	 * @param latitude: post latitude
	 * @param longitude: post longitude
	 * @param userID: user id from user who created post
	 * @param endTime: time post expires
	 * @param password:  user password (SHA-256 Hashed and Salted)
	 * @return Confirms post
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 */
	@GET
	@Path("/newpost")
	@Produces(MediaType.TEXT_PLAIN)
	public Response newPost(@QueryParam("title") String title, @QueryParam("content") String content, @QueryParam("latitude") String latitude,  
			@QueryParam("longitude") String longitude,  @QueryParam("userID") String userID, @QueryParam("endTime") String endTime,  
			@QueryParam("password") String password) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		try{
			if(title != null && content != null && latitude != null && longitude 
					!= null && userID != null && password != null && endTime != null) {

				User postUser = new User(userID);
				
				if(!postUser.spamCheck()){
					return Response.status(400).entity("{\"Error\":\"Too much Spam\"}").build();
				}
				
				if(postUser.verifyPassword(password)){
					Post newPost =  new Post(title, content, Double.parseDouble(latitude), Double.parseDouble(longitude), userID, Long.parseLong(endTime), false, 0, false);
					newPost.saveNewPostToDatabase();

					System.out.println("=====SERVED JSON TO USER=====");
					return Response.status(200).entity("{\"Message\":\"Post Added to Database\", \"postID\":\""+newPost.getPostId()+"\"}").build();
				}

				return Response.status(400).entity("{\"Account Error\":\"User Not Vaild Check Password\"}").build();
			}

		} catch(SQLException e){
			System.out.println("=====General Error Adding New Post to Database=====");
			e.printStackTrace();
			return Response.status(400).entity("{\"Error\":\"General Error Adding Post to Database\"}").build();
		}

		return Response.status(400).entity("{\"Error\":\"Provide all parameters\"}").build();
	}


	/**
	 * Retrieves post from database based on userID
	 *
	 * @param postID the post id
	 * @return JSON output of post info.
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	@GET
	@Path("/getallpostbyid")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getPostById(@QueryParam("userID") String userID, @QueryParam("password") String password) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{

		if(userID != null && password != null){

			User newUser = new User(userID);
			if(newUser.verifyPassword(password)){
				Post newPost = new Post();
				ArrayList<Post> postList = newPost.getPostByUserID(newUser);
				if(postList.size()==0){
					return Response.status(200).entity("{\"Message\": \"No Posts\"}").build();
				}
				String output="[";
				for(int i=0; i<postList.size(); i++){
					output+=postList.get(i).getPostInfo()+",";
				}

				String json = output.substring(0,output.length()-1)+"]";


				System.out.println("=====SERVED JSON TO USER=====");
				return Response.status(200).entity(json).build();
			}
		}

		System.out.println("=====userID or password ERROR=====");
		return Response.status(400).entity("{\"Error\":\"Provide userID and Password\"}").build();

	} 
	


	/**
	 * Update post in database.
	 *
	 *@param title: post title
	 * @param content: post content
	 * @param latitude: post latitude
	 * @param longitude: post longitude
	 * @param userID: user id from user who created post
	 * @param endTime: time post expires
	 * @param password:user password (SHA-256 Hashed and Salted) user password (SHA-256 Hashed and Salted)
	 * @param postID: the post id
	 * @return Confirms post was updated
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 */
	@GET
	@Path("/updatepost")
	@Produces(MediaType.TEXT_PLAIN)
	public Response updatePost(@QueryParam("postID") String postID, @QueryParam("title") String title, 
			@QueryParam("content") String content,  @QueryParam("latitude") String latitude,  
			@QueryParam("longitude") String longitude, @QueryParam("userID") String userID,  
			@QueryParam("password") String password, @QueryParam("endTime") String endTime) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		try{
			if(userID != null && postID != null && title != null && content != null && longitude 
					!= null && latitude != null && userID != null && password != null && endTime != null) {


				User postUser = new User(userID);
				
				if(!postUser.spamCheck()){
					return Response.status(400).entity("{\"Error\":\"Too much Spam\"}").build();
				}
				
				Post newPost = new Post(postID);

				if(postUser.verifyPassword(password) && newPost.isUserPost(postUser)){

					newPost.setTitle(title);
					newPost.setContent(content);
					newPost.setLocation(new Location(Double.parseDouble(latitude), Double.parseDouble(longitude), newPost.getPostId()));
					newPost.setEndTime(Long.parseLong(endTime));
					newPost.setTime(System.currentTimeMillis());

					newPost.update();

					System.out.println("=====SERVED JSON TO USER=====");
					return Response.status(200).entity("{\"Message\":\"Post Updated in Database\", \"userID\":\""+newPost.getPostId()+"\"}").build();

				}
				return Response.status(400).entity("{\"Error\":\"Password or Username Incorrect\"}").build();

			}	
		} catch(SQLException e){
			System.out.println("=====General Error Updating Post=====");
			e.printStackTrace();
			return Response.status(400).entity("{\"Error\":\"General Error Adding User to Database\"}").build();
		}



		return Response.status(400).entity("{\"Error\":\"Provide all parameters\"}").build();
	}   

	/**
	 * Delete post.
	 *
	 * @param userID: the user id on post
	 * @param password :user password (SHA-256 Hashed and Salted)
	 * @param postID: the post id
	 * @return Confirms post was deleted
	 */
	@GET
	@Path("/deletepost")
	@Produces(MediaType.TEXT_PLAIN)
	public Response deletePost(@QueryParam("userID") String userID, @QueryParam("password") String password, @QueryParam("postID") String postID){

		if(userID != null && password != null && postID != null){
			User newUser = new User(userID);
			
			Post newPost = new Post(postID);

			if(newUser.verifyPassword(password) && newPost.isUserPost(newUser)){
				try {
					newPost.deletePost();

					System.out.println("=====SERVED JSON TO USER=====");
					return Response.status(200).entity("{\"Message\":\"Post Deleted from Database\", \"userID\":\""+newUser.getUserID()+"\"}").build();

				} catch (Exception e) {

					System.out.println("=====ERROR DELETING POST=====");
					e.printStackTrace();
					return Response.status(200).entity("{\"Error\":\"General Error\"}").build();
				}

			}
		}
		return Response.status(200).entity("{\"Error\":\"Provide all parameters\"}").build();

	}

	/** Saves image to database.
	 *
	 * @param userID: the user id
	 * @param password: user password (SHA-256 Hashed and Salted)
	 * @parsm imageBase64 user image encoded as base64
	 * @return the new messages for the user
	 */
	@POST
	@Path("/savedata")
	@Produces(MediaType.TEXT_PLAIN)
	public Response setUserImage(@FormParam("userID") String userID, @FormParam("password") String password, @FormParam("image") String imageBase64) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{

		if(userID != null && password != null){
			User newUser = new User(userID);

			if(newUser.verifyPassword(password)){
				UserImage data = new UserImage(imageBase64);

				System.out.println("=====SERVED JSON TO USER=====");
				return Response.status(200).entity("{\"Message\":\"Image Saved to DataBase\"}").build();

			}
		}
		return Response.status(400).entity("{\"Error\":\"Provide all parameters\"}").build();

	}
	
	/* Updates image in database.
	 *
	 * @param userID: the user id
	 * @param password: user password (SHA-256 Hashed and Salted)
	 * @parsm imageBase64 user image encoded as base64
	 * @return the new messages for the user
	 */
	@POST
	@Path("/updateimage")
	@Produces(MediaType.TEXT_PLAIN)
	public Response updateUserImage(@FormParam("userID") String userID, @FormParam("password") String password, @FormParam("image") String imageBase64) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{

		if(userID != null && password != null){
			User newUser = new User(userID);

			if(newUser.verifyPassword(password)){
				UserImage image = new UserImage(imageBase64);

				newUser.updateUserImageInDatabase(image);

			}else{

				return Response.status(400).entity("{\"Error\":\"Provide all parameters\"}").build();

			}
		}
		return Response.status(200).entity("{\"Message\":\"Image Updated\"}").build();
	}

	/* Fetch image from database.
	 *
	 * @param userID: the user id
	 * @return the image for the user as base64
	 */
	@GET
	@Path("/getimage")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getUserImage(@QueryParam("userID") String userID) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{

		if(userID != null){
			User newUser = new User(userID);


			Map<String, String> imageBase64 = newUser.getUserImageFromDatabase();
			JSONObject imageMap = new JSONObject(imageBase64);

			System.out.println("=====SERVED JSON TO USER=====");

			return Response.status(200).entity(imageMap.toString()).build();

		}
		return Response.status(400).entity("{\"Error\":\"Provide all parameters\"}").build();

	}

	


}