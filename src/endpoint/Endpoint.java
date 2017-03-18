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
	 * Hello. "Hello World!" for Loop API
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
	public Response newUser(@QueryParam("firstName") String firstName, 
			@QueryParam("lastName") String lastName,  @QueryParam("latitude") String latitude,  
			@QueryParam("longitude") String longitude, @QueryParam("email") String email,  
			@QueryParam("password") String password, @QueryParam("userName") String userName,  
			@QueryParam("bio") String bio, @QueryParam("gender") String gender) throws InstantiationException, IllegalAccessException, ClassNotFoundException, CloneNotSupportedException, IOException{
		try{
			if(firstName != null && lastName != null && latitude != null && longitude 
					!= null && email != null && password != null && userName != null && bio != null) {
				
				bio = "Welome to Loop! Set up your bio so others know who they are joining!";

				User newUser = new User(firstName, lastName, Double.parseDouble(latitude),  Double.parseDouble(longitude), 
						email, password, userName, bio);

				newUser.saveNewUserToDatabase();
				UserImage newUserImageSmall = new UserImage(gender, "");
				UserImage newUserImageLarge = new UserImage(newUserImageSmall);
												
				
				newUserImageLarge.resizeImage(100, 100);
				newUserImageSmall.resizeImage(50, 50);
				
				newUser.saveUserImageToDatabase(newUserImageSmall, newUserImageLarge);

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

	/**
	 * Allows updates to user.
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
	@Path("/updateuser")
	@Produces(MediaType.TEXT_PLAIN)
	public Response updateUser(@QueryParam("userID") String userID, @QueryParam("firstName") String firstName, 
			@QueryParam("lastName") String lastName,  @QueryParam("latitude") String latitude,  
			@QueryParam("longitude") String longitude, @QueryParam("email") String email,  
			@QueryParam("password") String password, @QueryParam("userName") String userName,  
			@QueryParam("bio") String bio) throws InstantiationException, IllegalAccessException, ClassNotFoundException  {
		try{
			if(userID != null && firstName != null && lastName != null && latitude != null && longitude 
					!= null && email != null && password != null && userName != null && bio != null) {

				User newUser = new User(userID);
				if(newUser.verifyPassword(password)){
					newUser.setfirstName(firstName);
					newUser.setlastName(lastName);
					newUser.setlastLocation(new Location(Double.parseDouble(latitude), Double.parseDouble(longitude), newUser.getUserID()));
					newUser.setemail(email);
					newUser.setpassword(password);
					newUser.setuserName(userName);
					newUser.setBio(bio);
					newUser.update();

					System.out.println("=====SERVED JSON TO USER=====");
					return Response.status(200).entity("{\"Message\":\"User Updated in Database\", \"userID\":\""+newUser.getUserID()+"\"}").build();

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
	 * Allows updates to user.
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
	@Path("/updatelocation")
	@Produces(MediaType.TEXT_PLAIN)
	public Response updateLocation(@QueryParam("userID") String userID, @QueryParam("latitude") String latitude,  
			@QueryParam("longitude") String longitude, @QueryParam("password") String password) throws InstantiationException, IllegalAccessException, ClassNotFoundException  {
		try{
			if(userID != null && latitude != null && longitude != null && password != null) {

				User newUser = new User(userID);
				if(newUser.verifyPassword(password)){
					newUser.setlastLocation(new Location(Double.parseDouble(latitude), Double.parseDouble(longitude), newUser.getUserID()));
					newUser.update();

					System.out.println("=====SERVED JSON TO USER=====");
					return Response.status(200).entity("{\"Message\":\"User Location Updated in Database\", \"userID\":\""+newUser.getUserID()+"\"}").build();

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
	 * Retrieves post from database based on postID
	 *
	 * @param postID the post id
	 * @return JSON output of post info.
	 */
	@GET
	@Path("/getpostbyid")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getPostById(@QueryParam("postID") String postID){

		if(postID != null){

			Post newUser = new Post(postID);
			String json = newUser.getPostInfo();

			System.out.println("=====SERVED JSON TO USER=====");
			return Response.status(200).entity(json).build();

		}

		System.out.println("=====userID ERROR=====");
		return Response.status(400).entity("{\"Error\":\"Provide userID\"}").build();

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
	 * Gets the post from database.
	 *
	 * @param userID: user id from user that created post
	 * @param password: user password (SHA-256 Hashed and Salted)
	 * @param max: maximum number of returned posts
	 * @param sortby: how posts are to sorted (Location, End Time, Start Time)
	 * @return the post
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws NumberFormatException the number format exception
	 */
	@GET
	@Path("/getposts")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getPost(@QueryParam("userID") String userID, @QueryParam("password") String password, @QueryParam("max") int max, @QueryParam("sortby") String sortby) throws InstantiationException, IllegalAccessException, ClassNotFoundException, NumberFormatException{
		try{

			if(userID != null && password != null && max > 0 && sortby != null){

				User newUser = new User(userID);
				if(newUser.verifyPassword(password)){
					Sort posts = new Sort(100, sortby);
					ArrayList<Post> sorted = posts.getSortedPosts();

					//check that at least max was returned to prevent out-of-bounds error
					if(sorted.size()<max){
						max = sorted.size();
					}

					String output = "[";
					for(int i=0; i<max; i++){
						output+=sorted.get(i).getPostInfo()+",";
					}

					String json = output.substring(0,output.length()-1)+"]";

					System.out.println("=====SERVED JSON TO USER=====");
					return Response.status(200).entity(json).build();
				}
			} 
		}catch(SQLException e){
			System.out.println("=====General Error Updating User=====");
			e.printStackTrace();
			return Response.status(400).entity("{\"Error\":\"General Error Getting Posts From Database\"}").build();
		}


		System.out.println("=====userID OR password ERROR=====");
		return Response.status(400).entity("{\"Error\":\"Provide all parameters\"}").build();

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


	/**
	 * New message.
	 *
	 * @param userID: the user id of message sender
	 * @param password: user password (SHA-256 Hashed and Salted)
	 * @param content: the content of message
	 * @param toID: the to id
	 * @return Confirms message was created
	 */
	//message endpoints
	@GET
	@Path("/newmessage")
	@Produces(MediaType.TEXT_PLAIN)
	public Response newMessage(@QueryParam("userID") String userID, @QueryParam("password") String password, @QueryParam("content") String content, @QueryParam("toID") String toID, @QueryParam("postID") String postID){

		if(userID != null && password != null && content != null && toID != null && postID != null){
			User newUser = new User(userID);
			Message newMessage = new Message(content, userID, toID, postID);

			if(newUser.verifyPassword(password)){
				try {
					newMessage.saveMessageToDataBase();

					System.out.println("=====SERVED JSON TO USER=====");
					return Response.status(200).entity("{\"Message\":\"Message Posted to Database\", \"messageID\":\""+newMessage.getMessageID()+"\"}").build();

				} catch (Exception e) {

					System.out.println("=====ERROR SENDING MESSAGE TO DATABASE=====");
					e.printStackTrace();
					return Response.status(400).entity("{\"Error\":\"General Error\"}").build();
				}

			}
		}
		return Response.status(400).entity("{\"Error\":\"Provide all parameters\"}").build();

	}

	/**
	 * Gets the message.
	 *
	 * @param userID: the user id
	 * @param password: the password
	 * @return the users message
	 */
	@GET
	@Path("/getmessage")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getMessage(@QueryParam("userID") String userID, @QueryParam("password") String password){

		if(userID != null && password != null){

			User newUser = new User(userID);
			Message newBlankMessage = new Message();

			if(newUser.verifyPassword(password)){
				try {

					ArrayList<Message> messages = newBlankMessage.getMessageByUser(newUser);

					if(messages.size()==0){
						return Response.status(200).entity("{\"Message\": \"No Messages\"}").build();
					}

					String output="[";
					for(int i=0; i<messages.size(); i++){
						output+=messages.get(i).getMessageInfo()+",";
					}

					String json = output.substring(0,output.length()-1)+"]";

					System.out.println("=====SERVED JSON TO USER=====");
					return Response.status(200).entity(json).build();

				} catch (Exception e) {

					System.out.println("=====ERROR SENDING MESSAGE TO DATABASE=====");
					e.printStackTrace();
					return Response.status(400).entity("{\"Error\":\"General Error\"}").build();
				}

			}
		}
		return Response.status(400).entity("{\"Error\":\"Provide all parameters\"}").build();

	}
	@GET
	@Path("/getmessagepost")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getMessagePost(@QueryParam("userID") String userID, @QueryParam("password") String password, @QueryParam("postID") String postID){

		if(userID != null && password != null && postID != null){

			User newUser = new User(userID);
			Message newBlankMessage = new Message();

			if(newUser.verifyPassword(password)){
				try {

					ArrayList<Message> messages = newBlankMessage.getMessageByUserAndPost(newUser, postID);

					if(messages.size()==0){
						return Response.status(200).entity("{\"Message\": \"No Messages\"}").build();
					}

					String output="[";
					for(int i=0; i<messages.size(); i++){
						output+=messages.get(i).getMessageInfo()+",";
						if(messages.get(i).getViewed() != true && userID.equals(messages.get(i).getToID())){
							messages.get(i).setViewed(true);
							messages.get(i).update();
						}
					}

					String json = output.substring(0,output.length()-1)+"]";

					System.out.println("=====SERVED JSON TO USER=====");
					return Response.status(200).entity(json).build();

				} catch (Exception e) {

					System.out.println("=====ERROR SENDING MESSAGE TO DATABASE=====");
					e.printStackTrace();
					return Response.status(400).entity("{\"Error\":\"General Error\"}").build();
				}

			}
		}
		return Response.status(400).entity("{\"Error\":\"Provide all parameters\"}").build();

	}

	@GET
	@Path("/getmessagenewpost")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getMessageNewPost(@QueryParam("userID") String userID, @QueryParam("password") String password, @QueryParam("postID") String postID, @QueryParam("fromID") String fromID){

		if(userID != null && password != null && postID != null && fromID != null){

			User newUser = new User(userID);
			Message newBlankMessage = new Message();

			if(newUser.verifyPassword(password)){
				try {

					ArrayList<Message> messages = newBlankMessage.getNewMessageByUserAndPost(newUser, postID, fromID);

					if(messages.size()==0){
						return Response.status(200).entity("{\"Message\": \"No Messages\"}").build();
					}

					String output="[";
					for(int i=0; i<messages.size(); i++){
						output+=messages.get(i).getMessageInfo()+",";
						if(messages.get(i).getViewed() != true && userID.equals(messages.get(i).getToID())){
							messages.get(i).setViewed(true);
							messages.get(i).update();
						}

					}

					String json = output.substring(0,output.length()-1)+"]";

					System.out.println("=====SERVED JSON TO USER=====");
					return Response.status(200).entity(json).build();

				} catch (Exception e) {

					System.out.println("=====ERROR SENDING MESSAGE TO DATABASE=====");
					e.printStackTrace();
					return Response.status(400).entity("{\"Error\":\"General Error\"}").build();
				}

			}
		}
		return Response.status(400).entity("{\"Error\":\"Provide all parameters\"}").build();

	}

	/**
	 * Gets the new message.
	 *
	 * @param userID: the user id
	 * @param password: user password (SHA-256 Hashed and Salted)
	 * @return the new messages for the user
	 */
	@GET
	@Path("/getnewmessage")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getNewMessage(@QueryParam("userID") String userID, @QueryParam("password") String password){

		if(userID != null && password != null){
			User newUser = new User(userID);
			Message newBlankMessage = new Message();

			if(newUser.verifyPassword(password)){
				try {

					ArrayList<Message> messages = newBlankMessage.getNewMessageByUser(newUser);


					if(messages.size() == 0){
						return Response.status(200).entity("{\"Message:\" \"No Messages\"}").build();
					}

					String output="[";
					for(int i=0; i<messages.size(); i++){
						output+=messages.get(i).getMessageInfo()+",";

					}

					String json = output.substring(0,output.length()-1)+"}";

					System.out.println("=====SERVED JSON TO USER=====");
					return Response.status(200).entity(json).build();

				} catch (Exception e) {
					System.out.println("=====ERROR SENDING MESSAGE TO DATABASE=====");
					e.printStackTrace();
					return Response.status(400).entity("{\"Error\":\"General Error\"}").build();
				}

			}
		}
		return Response.status(400).entity("{\"Error\":\"Provide all parameters\"}").build();

	}

	/* Saves image to database.
	 *
	 * @param userID: the user id
	 * @param password: user password (SHA-256 Hashed and Salted)
	 * @parsm imageBase64 user image encoded as base64
	 * @return the new messages for the user
	 */
	@POST
	@Path("/saveimage")
	@Produces(MediaType.TEXT_PLAIN)
	public Response setUserImage(@FormParam("userID") String userID, @FormParam("password") String password, @FormParam("image") String imageBase64) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{

		if(userID != null && password != null){
			User newUser = new User(userID);

			if(newUser.verifyPassword(password)){
				UserImage newLarge = new UserImage(imageBase64);
				UserImage newSmall = new UserImage(imageBase64);

				newLarge.resizeImage(100, 100);
				newSmall.resizeImage(50, 50);

				newUser.saveUserImageToDatabase(newSmall, newLarge);


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
				UserImage newLarge = new UserImage(imageBase64);
				UserImage newSmall = new UserImage(imageBase64);

				newLarge.resizeImage(100, 100);
				newSmall.resizeImage(50, 50);

				newUser.updateUserImageInDatabase(newSmall, newLarge);

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

	/* Updates post as spam
	 *
	 * @param postID
	 * @return verify message
	 */
	@GET
	@Path("/reportspam")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getRepostSpam(@QueryParam("postID") String postID) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
if(postID != null){
		Post newPost = new Post(postID);
		User newUser = new User(newPost.getUserId());
		
		newPost.addToSpamCount(1);
		newUser.addToSpamCount(1);
		newUser.update();
		newPost.update();
		

			return Response.status(200).entity("{\"Message\": \"Spam Recorded\"}").build();
}
return Response.status(400).entity("{\"Error\": \"Provide all parameters\"}").build();
	}

	


}