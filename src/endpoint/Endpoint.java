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


	


}