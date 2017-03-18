package endpoint;

import java.util.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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

import org.json.JSONException;
import org.json.JSONObject;

import com.sun.jersey.core.header.FormDataContentDisposition;

import database.ObjectBuilder;
import database.ObjectSaver;
import user.User;

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
	 * @throws JSONException 
	 */
	@POST
	@Path("/getAllItems")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getAllItems(String request) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, JSONException{
		System.out.println(request);
		JSONObject jsonObject = new JSONObject(request);

		String userID = jsonObject.getString("userID");
		//google how to get auth token
		String authToken = jsonObject.getString("");
		//parse auth token

		if(userID == null || authToken == null){
			System.out.println("=====userID ERROR=====");
			return Response.status(400).entity("{\"Error\":\"Provide email and token\"}").build();
		}
		// match auth token
		// get bucket name from user
		// fetch all bucket keys with bucket name
		// iterate over bucket keys and fetch file
		// create a new object of {file type, file} for each one
		// json encode each one, add to a list
		// serve list as response
		
		
		// create auth token and store
		System.out.println("=====SERVED JSON TO USER=====");
		return Response.ok().build();
			
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
	 * @throws JSONException 
	 */
	@POST
	@Path("/login")
	@Produces(MediaType.TEXT_PLAIN)
	public Response userLogin(String request) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, JSONException{
		System.out.println(request);
		JSONObject jsonObject = new JSONObject(request);

		String userID = jsonObject.getString("userID");
		String password = jsonObject.getString("password");

		if(userID == null || password == null){
			System.out.println("=====userID ERROR=====");
			return Response.status(400).entity("{\"Error\":\"Provide email and password\"}").build();
		}

		User user = new ObjectBuilder(userID).getUser();

		//no user
		if(user == null){
			return Response.serverError().build();
		}

		if(!user.verifyPassword(password)){
			System.out.println("====Password ERROR=====");
			return Response.status(400).entity("{\"Error\":\"Password Incorret\"}").build();
			
		}
		
		// create auth token and store
		System.out.println("=====SERVED JSON TO USER=====");
		return Response.ok().build();
			
	}


	/**
	 * Allows generation of new users which are added to MySQL database.
	 *
	 * @param email: email of user
	 * @param password: user password (SHA-256 Hashed and Salted)
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws IOException 
	 * @throws UnirestException 
	 * @throws CloneNotSupportedException 
	 * @throws JSONException 
	 */
	@POST
	@Path("/newuser")
	@Produces(MediaType.TEXT_PLAIN)
	public Response newUser(String request) throws InstantiationException, IllegalAccessException, ClassNotFoundException, CloneNotSupportedException, IOException, JSONException{

		JSONObject jsonObject = new JSONObject(request);

		String userID = jsonObject.getString("userID");
		String password = jsonObject.getString("password");

		if(userID == null || password == null) {
			return Response.status(400).entity("{\"Error\":\"Provide all parameters\"}").build();
		}
		try{

			new ObjectSaver(new User(password,"placeholder pin" ,userID)).saveUser();
			// create data keys and store
			// create bucket and store
			System.out.println("=====SERVED JSON TO USER=====");
			return Response.status(200).entity("{\"Message\":\"User Added to Database\", \"userID\":\""+userID+"\"}").build();


		}catch(com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e){
			System.out.println("=====DUPLICATE USERNAME=====");

			return Response.status(400).entity("{\"Error\":\"Duplicate Username\"}").build();

		} catch(SQLException e){
			System.out.println("=====General Error Adding New User to Database=====");
			e.printStackTrace();
			return Response.status(400).entity("{\"Error\":\"General Error Adding User to Database\"}").build();
		}
	}
	
	
	/**
	 * Allows generation of new users which are added to MySQL database.
	 *
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws IOException 
	 * @throws UnirestException 
	 * @throws CloneNotSupportedException 
	 * @throws JSONException 
	 */
	@POST
	@Path("/uploadFile")
	@Produces(MediaType.TEXT_PLAIN)
	public Response uploadFile(String request) throws InstantiationException, IllegalAccessException, ClassNotFoundException, CloneNotSupportedException, IOException, JSONException{

		System.out.println(request);
		JSONObject jsonObject = new JSONObject(request);

		String userID = jsonObject.getString("userID");
		String fileData = jsonObject.getString("file");
		String dataType = jsonObject.getString("dataType");
		String description = jsonObject.getString("description");
		
		//google how to get auth token
		String authToken = jsonObject.getString("");

		if(userID == null || authToken == null || fileData == null || dataType == null || description == null){
			System.out.println("=====userID ERROR=====");
			return Response.status(400).entity("{\"Error\":\"Provide stuff\"}").build();
		}
		// match auth token
		// get bucket name from user
		// upload data using amazon client
		// store returned key, bucket name, filetype, filedescription in db
				
		System.out.println("=====SERVED JSON TO USER=====");
		return Response.ok().build();
	}
	
	/**
	 * Allows generation of new users which are added to MySQL database.
	 *
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws IOException 
	 * @throws UnirestException 
	 * @throws CloneNotSupportedException 
	 * @throws JSONException 
	 */
	@POST
	@Path("/getFile")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getFile(String request) throws InstantiationException, IllegalAccessException, ClassNotFoundException, CloneNotSupportedException, IOException, JSONException{

		System.out.println(request);
		JSONObject jsonObject = new JSONObject(request);

		String userID = jsonObject.getString("userID");
		String fileKey = jsonObject.getString("key");
		
		//google how to get auth token
		String authToken = jsonObject.getString("");

		if(userID == null || authToken == null || fileKey == null){
			System.out.println("=====userID ERROR=====");
			return Response.status(400).entity("{\"Error\":\"Provide stuff\"}").build();
		}
		// match auth token to see it's a logged in user
		// get bucket name from user
		// download data using amazon client with bucketname and key
		// get file type using bucket key
		// create a new object of {file type, file} and return
				
		System.out.println("=====SERVED JSON TO USER=====");
		return Response.ok().build();
	}

	/**
	 * Allows generation of new users which are added to MySQL database.
	 *
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws IOException 
	 * @throws UnirestException 
	 * @throws CloneNotSupportedException 
	 * @throws JSONException 
	 */
	@POST
	@Path("/getFileDescription")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getFileDescription(String request) throws InstantiationException, IllegalAccessException, ClassNotFoundException, CloneNotSupportedException, IOException, JSONException{

		System.out.println(request);
		JSONObject jsonObject = new JSONObject(request);

		String userID = jsonObject.getString("userID");
		String fileKey = jsonObject.getString("key");
		
		//google how to get auth token
		String authToken = jsonObject.getString("");

		if(userID == null || authToken == null || fileKey == null){
			System.out.println("=====userID ERROR=====");
			return Response.status(400).entity("{\"Error\":\"Provide email and token\"}").build();
		}
		// match auth token to see it's a logged in user
		// get bucket name from user
		// get file description using bucket key and name
		// return file description as response
		
		System.out.println("=====SERVED JSON TO USER=====");
		return Response.ok().build();
	}
	//		/**
	//		 * Allows users to login with username and password.
	//		 * @param userName: username
	//		 * @param password: user password (SHA-256 Hashed and Salted)
	//		 * @return JSON output with userID
	//		 * @throws SQLException 
	//		 * @throws InstantiationException the instantiation exception
	//		 * @throws IllegalAccessException the illegal access exception
	//		 * @throws ClassNotFoundException the class not found exception
	//		 */
	//			@POST
	//			@Path("/verifypin")
	//			@Produces(MediaType.TEXT_PLAIN)
	//			public Response verifyPin(@QueryParam("pin") String pin,@QueryParam("username") String userName) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
	//		
	//				if(pin == null || userName == null){
	//					System.out.println("=====userID ERROR=====");
	//					return Response.status(400).entity("{\"Error\":\"Provide PIN\"}").build();
	//				}
	//		
	//				User newUser = new User(User.getUserID(userName));
	//		
	//				if(newUser.verifyPin(pin)){
	//					String json = newUser.getUserInfo();
	//		
	//					System.out.println("=====SERVED JSON TO USER=====");
	//					return Response.status(200).entity(json).build();
	//				}else{
	//					System.out.println("====Password ERROR=====");
	//					return Response.status(400).entity("{\"Error\":\"Password Incorret\"}").build();
	//				}
	//		
	//			}


	//////////////////////////START OF DATA CALLS /////////////////////////////

	//			/**
	//			 * Retrieves user from database based on ID
	//			 *
	//			 * @param userID the user id
	//			 * @param password: user password (SHA-256 Hashed and Salted)
	//			 * @return JSON output of user info.
	//			 */
	//			@POST
	//			@Path("/getbucketname")
	//			@Produces(MediaType.TEXT_PLAIN)
	//			public Response getBucketName(@QueryParam("userID") String userID){
	//		
	//				if(userID != null){
	//		
	//					User newUser = new User(userID);
	//		
	//					String json = newUser.;
	//		
	//					System.out.println("=====SERVED JSON TO USER=====");
	//					return Response.status(200).entity(json).build();
	//		
	//				}
	//		
	//				System.out.println("=====userID ERROR=====");
	//				return Response.status(400).entity("{\"Error\":\"Provide userID\"}").build();
	//
	//	}

}
