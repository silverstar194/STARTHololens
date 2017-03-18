package message;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import database.DataBase;
import user.User;


/**
 * @author Sean Maloney
 * 
 */

/**
 * Creates Messages to be passed between users.
 */
public class Message implements Comparable{

	/** The content of message. */
	private String content;

	/** Who message is from */
	private String fromID;

	/** Who message is to */
	private String toID;

	/** The time message was created. */
	private long time;

	/** Has message been viewed */
	private boolean viewed;

	/** The message id. */
	private String messageID;

	/** The postid. */
	private String postID;

	/**
	 * Instantiates a new message.
	 *
	 * @param content: message content
	 * @param fromID: who message is from
	 * @param toID: who message is to
	 */
	public Message(String content, String fromID, String toID, String postID){
		if (content.contains("'")) 
		{
			content = content.replace("'", "''");
		}
		this.content = content;
		this.fromID = fromID;
		this.toID = toID;
		this.time = System.currentTimeMillis();
		this.messageID = idGenerator();
		this.postID = postID;
		this.viewed = false;

	}

	/**
	 * Instantiates a new message.
	 *
	 * @param content: message content
	 * @param fromID: who message is from
	 * @param toID: who message is to
	 * @param time: time message was created
	 * @param viewed: has message been viewed
	 * @param messageID: the message id
	 */
	public Message(String content, String fromID, String toID, long time, boolean viewed, String messageID, String postID){
		if (content.contains("'")) 
		{
			content = content.replace("'", "''");
		}
		this.content = content;
		this.fromID = fromID;
		this.toID = toID;
		this.time = time;
		this.viewed = viewed;
		this.messageID = messageID;
		this.postID = postID;
	}

	/**
	 * Instantiates a new blank message.
	 */
	public Message(){
		System.out.println("=====BLANK MESSAGE GENERATED=====");
	}


	/**
	 * Gets the content of message.
	 *
	 * @return the content
	 */
	//getters
	public String getContent(){
		return this.content;
	}

	/**
	 * Gets the from id.
	 *
	 * @return the from id
	 */
	public String getFromID(){
		return this.fromID;
	}

	/**
	 * Gets the from id.
	 *
	 * @return the from id
	 */
	public String getPostID(){
		return this.postID;
	}

	/**
	 * Gets the to id.
	 *
	 * @return the to id
	 */
	public String getToID(){
		return this.toID;
	}

	/**
	 * Gets the viewed status.
	 * 
	 * @return the viewed status
	 */
	public boolean getViewed(){
		return this.viewed;
	}

	/**
	 * Gets the time for message.
	 *
	 * @return the time for message.
	 */
	public long getTime(){
		return this.time;
	}

	/**
	 * Gets the message id.
	 *
	 * @return the message id
	 */
	public String getMessageID(){
		return this.messageID;
	}

	/**
	 * Sets the content.
	 *
	 * @param content the new content
	 */
	//setters
	public void setContent(String content){
		if (content.contains("'")) 
		{
			content = content.replace("'", "''");
		}
		this.content = content;
	}

	/**
	 * Sets the from id.
	 *
	 * @param fromID the new from id
	 */
	public void setFromID(String fromID){
		this.fromID = fromID;
	}

	/**
	 * Sets the to id.
	 *
	 * @param toID the new to id
	 */
	public void setToID(String toID){
		this.toID = toID;
	}

	/**
	 * Sets the viewed.
	 *
	 * @param viewed the new viewed
	 */
	public void setViewed(boolean viewed){
		this.viewed = viewed;
	}

	/**
	 * Sets the time.
	 *
	 * @param time the new time
	 */
	public void setTime(long time ){
		this.time = time;
	}

	/**
	 * Sets the message id.
	 *
	 * @param messageID the new message id
	 */
	public void setMessageID(String messageID){
		this.messageID = messageID;
	}


	/**
	 * Id generator.
	 *
	 * @return unique id
	 */
	private String idGenerator() {
		SecureRandom random = new SecureRandom();
		return new BigInteger(130, random).toString(32);
	}

	/**
	 * Save message to data base.
	 *
	 * @throws SQLException the SQL exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 */
	public void saveMessageToDataBase() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		DataBase dataBase = new DataBase();


		Connection dataBaseConn = dataBase.getConnection();

		String command ="INSERT INTO "
				+ "`electro956_db`.`Message` (`messageID`,`content`,`fromID`,`toID`,`time`,`viewed`, `postID`)"
				+ "VALUES"
				+ "('"+this.messageID+"','"+this.content+"','"+this.fromID+"','"+this.toID+"','"+this.time+"','"+this.viewed+"','"+this.postID+"');";

		dataBase.executeUpdate(dataBaseConn, command);
		System.out.println("=====MESSAGE SENT TO DATABASE=====");



	}

	/**
	 * Update message in database.
	 *
	 * @throws SQLException the SQL exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 */
	public void update() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		DataBase dataBase = new DataBase();

		Connection dataBaseConn = dataBase.getConnection();

		String command ="UPDATE Message"
				+ " SET viewed='"+true+"'"
				+  " WHERE messageID='"+this.messageID+"';";

		dataBase.executeUpdate(dataBaseConn, command);
		System.out.println("=====MESSAGE UPDATED IN DATABASE=====");



	}

	/**
	 * Gets the message info.
	 *
	 * @return the message info
	 */
	public String getMessageInfo() {

		Map<String,String> messageMap = new HashMap<String, String>();
		String contentNew = null;
		
		if(this.content.contains("''")){
			contentNew = this.content.replace("''", "'");
		}else{
			contentNew = this.content;
		}

		messageMap.put("content", contentNew);
		messageMap.put("fromID", this.fromID);
		messageMap.put("toID", this.toID);
		messageMap.put("time", ""+this.time);
		messageMap.put("viewed", ""+this.viewed);
		messageMap.put("postID", ""+this.postID);
		messageMap.put("messageID", ""+this.messageID);


		JSONObject json = new JSONObject(messageMap);

		System.out.println("=====MESSAGE CONVERTED TO JSON=====");

		return json.toString();

	}

	/**
	 * Gets the message by user.
	 *
	 * @param user
	 * @return the message by user
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws SQLException the SQL exception
	 */
	public ArrayList<Message> getMessageByUser(User user) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		DataBase dataBase = new DataBase();
		Connection dataBaseConn = null;
		ArrayList<Message> messages = new ArrayList<Message>();
		try{
			dataBaseConn = dataBase.getConnection();

			String command ="SELECT * FROM Message WHERE toID='"+user.getUserID()+"' OR fromID='"+user.getUserID()+"'";

			ResultSet rs = dataBase.getDataBaseInfo(dataBaseConn, command);

			while(rs.next()){
				Message newMessage = new Message(rs.getString("content"), rs.getString("fromID"), rs.getString("toID"),
						Long.parseLong(rs.getString("time")), Boolean.parseBoolean(rs.getString("viewed")), rs.getString("messageID"), rs.getString("postID"));

				messages.add(newMessage);
			}
		}finally{
			dataBaseConn.close();
		}

		Collections.sort(messages);
		return messages;
	}

	/**
	 * Gets the message by user.
	 *
	 * @param user
	 * @return the message by user
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws SQLException the SQL exception
	 */
	public ArrayList<Message> getMessageByUserAndPost(User user, String postID) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		DataBase dataBase = new DataBase();
		Connection dataBaseConn = null;
		ArrayList<Message> messages = new ArrayList<Message>();
		try{
			dataBaseConn = dataBase.getConnection();

			String command ="SELECT * FROM Message WHERE (toID='"+user.getUserID()+"' OR fromID='"+user.getUserID()+"') AND (postID='"+postID+"');";

			ResultSet rs = dataBase.getDataBaseInfo(dataBaseConn, command);

			while(rs.next()){
				Message newMessage = new Message(rs.getString("content"), rs.getString("fromID"), rs.getString("toID"),
						Long.parseLong(rs.getString("time")), Boolean.parseBoolean(rs.getString("viewed")), rs.getString("messageID"), rs.getString("postID"));

				messages.add(newMessage);
			}
		}finally{
			dataBaseConn.close();
		}

		Collections.sort(messages);

		return messages;
	}

	/**
	 * Gets the message by user.
	 *
	 * @param user
	 * @return the message by user
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws SQLException the SQL exception
	 */
	public ArrayList<Message> getNewMessageByUserAndPost(User user, String postID, String fromID) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		DataBase dataBase = new DataBase();
		Connection dataBaseConn = null;
		ArrayList<Message> messages = new ArrayList<Message>();
		try{
			dataBaseConn = dataBase.getConnection();

			String command ="SELECT * FROM Message WHERE toID='"+user.getUserID()+"' AND postID='"+postID+"' AND viewed='false';";


			ResultSet rs = dataBase.getDataBaseInfo(dataBaseConn, command);

			while(rs.next()){
				Message newMessage = new Message(rs.getString("content"), rs.getString("fromID"), rs.getString("toID"),
						Long.parseLong(rs.getString("time")), Boolean.parseBoolean(rs.getString("viewed")), rs.getString("messageID"), rs.getString("postID"));

				messages.add(newMessage);
			}
		}finally{
			dataBaseConn.close();
		}

		Collections.sort(messages);
		return messages;
	}

	/**
	 * Gets the new message by user.
	 *
	 * @param user
	 * @return the new message by user
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws SQLException the SQL exception
	 */
	public ArrayList<Message> getNewMessageByUser(User user) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		DataBase dataBase = new DataBase();
		Connection dataBaseConn= null;
		ArrayList<Message> messages = new ArrayList<Message>();
		try{
			dataBaseConn = dataBase.getConnection();

			String command ="SELECT * FROM Message WHERE toID='"+user.getUserID()+"' OR fromID='"+user.getUserID()+"'";

			ResultSet rs = dataBase.getDataBaseInfo(dataBaseConn, command);

			while(rs.next()){
				Message newMessage = new Message(rs.getString("content"), rs.getString("fromID"), rs.getString("toID"),
						Long.parseLong(rs.getString("time")), Boolean.parseBoolean(rs.getString("viewed")), rs.getString("messageID"), rs.getString("postID"));

				messages.add(newMessage);
			}
		}finally{
			dataBaseConn.close();
		}

		Collections.sort(messages);
		return messages;
	}

	public int getNewMessageCountByUser(User user) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		DataBase dataBase = new DataBase();
		Connection dataBaseConn= null;
		int count =0;
		try{
			dataBaseConn = dataBase.getConnection();

			String command ="SELECT * FROM Message WHERE toID='"+user.getUserID()+"' AND viewed='false'";

			ResultSet rs = dataBase.getDataBaseInfo(dataBaseConn, command);

			while(rs.next()){
				rs.getString("content");
				count++;
			}

		}finally{
			dataBaseConn.close();
		}

		return count;
	}

	@Override
	public int compareTo(Object o) {
		

		return (int)(this.time-((Message)o).getTime());
	}

}
