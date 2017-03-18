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
import org.json.JSONObject;

import com.sun.jersey.core.header.FormDataContentDisposition;

import database.ObjectBuilder;
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
	 */
	@POST
	@Path("/login")
	@Produces(MediaType.TEXT_PLAIN)
	public Response userLogin(@QueryParam("userID") String userID, @QueryParam("password") String password) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{

		if(userID == null || password == null){
			System.out.println("=====userID ERROR=====");
			return Response.status(400).entity("{\"Error\":\"Provide email and password\"}").build();
		}

		User user = new ObjectBuilder(userID).getUser();

		if(user.verifyPassword(password)){
			System.out.println("=====SERVED JSON TO USER=====");
			return Response.status(200).build();
		}else{
			System.out.println("====Password ERROR=====");
			return Response.status(400).entity("{\"Error\":\"Password Incorret\"}").build();
		}
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
	 */
	@POST
	@Path("/newuser")
	@Produces(MediaType.TEXT_PLAIN)
	public Response newUser(@QueryParam("userID") String userID, @QueryParam("password") String password) throws InstantiationException, IllegalAccessException, ClassNotFoundException, CloneNotSupportedException, IOException{

		if(userID == null || password == null) {
			return Response.status(400).entity("{\"Error\":\"Provide all parameters\"}").build();
		}

		try{
			String passHash, String emailPin, String userID

			User newUser = new User(password,"placeholder pin" ,userID);
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


}   

/**
 * Retrieves user from database based on ID
 *
 * @param userID the user id
 * @param password: user password (SHA-256 Hashed and Salted)
 * @return JSON output of user info.
 */
@POST
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
 * Allows users to login with username and password.
 * @param userName: username
 * @param password: user password (SHA-256 Hashed and Salted)
 * @return JSON output with userID
 * @throws SQLException 
 * @throws InstantiationException the instantiation exception
 * @throws IllegalAccessException the illegal access exception
 * @throws ClassNotFoundException the class not found exception
 */
@POST
@Path("/verifypin")
@Produces(MediaType.TEXT_PLAIN)
public Response verifyPin(@QueryParam("pin") String pin,@QueryParam("username") String userName) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{

	if(pin == null || userName == null){
		System.out.println("=====userID ERROR=====");
		return Response.status(400).entity("{\"Error\":\"Provide PIN\"}").build();
	}

	User newUser = new User(User.getUserID(userName));

	if(newUser.verifyPin(pin)){
		String json = newUser.getUserInfo();

		System.out.println("=====SERVED JSON TO USER=====");
		return Response.status(200).entity(json).build();
	}else{
		System.out.println("====Password ERROR=====");
		return Response.status(400).entity("{\"Error\":\"Password Incorret\"}").build();
	}

}


//////////////////////////START OF DATA CALLS /////////////////////////////

/**
 * Retrieves user from database based on ID
 *
 * @param userID the user id
 * @param password: user password (SHA-256 Hashed and Salted)
 * @return JSON output of user info.
 */
@POST
@Path("/getbucketname")
@Produces(MediaType.TEXT_PLAIN)
public Response getBucketName(@QueryParam("userID") String userID){

	if(userID != null){

		User newUser = new User(userID);

		String json = newUser.getUserInfo();

		System.out.println("=====SERVED JSON TO USER=====");
		return Response.status(200).entity(json).build();

	}

	System.out.println("=====userID ERROR=====");
	return Response.status(400).entity("{\"Error\":\"Provide userID\"}").build();

}



}