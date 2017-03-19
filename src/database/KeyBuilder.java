package database;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Admin
 *
 */
public class KeyBuilder {


	public static byte[] getPublicKey(String id){

		DataBaseDriver dataBase = new DataBaseDriver();
		Connection dataBaseConn = null;
		byte[] bytesOut = null;
		try {
			dataBaseConn = dataBase.getConnection();

			String command ="SELECT `accessKey` FROM `accessKey` WHERE userID='"+id+"'";
			
			System.out.println(command);
			ResultSet rs = dataBase.getDataBaseInfo(dataBaseConn, command);

			Blob blob = rs.getBlob(1);
			bytesOut = blob.getBytes(1, (int)blob.length());

			System.out.println("=====PRIVATE KEY CREATED FROM DATABASE=====");

		} catch (Exception e) {
			System.out.println("=====ERROR GETTING PRIVATE KEY FROM DATABASE=====");
			e.printStackTrace();
		}finally{
			try {
				dataBaseConn.close();
			} catch (SQLException e) {
				System.out.println("CANNOT CLOSE MYSQL CONNECTION");
				e.printStackTrace();
			}
		}

		return bytesOut;
	}



	public static byte[] getPrivateKey(String id){
		DataBaseDriver dataBase = new DataBaseDriver();
		Connection dataBaseConn = null;
		byte[] bytesOut = null;
		try {
			dataBaseConn = dataBase.getConnection();

			String command ="SELECT `secretKey` FROM `secretKey` WHERE userID='"+id+"'";
			ResultSet rs = dataBase.getDataBaseInfo(dataBaseConn, command);

			Blob blob = rs.getBlob(1);
			bytesOut = blob.getBytes(1, (int)blob.length());

			System.out.println("=====PRIVATE KEY CREATED FROM DATABASE=====");

		} catch (Exception e) {
			System.out.println("=====ERROR GETTING PRIVATE KEY FROM DATABASE=====");
			e.printStackTrace();
		}finally{
			try {
				dataBaseConn.close();
			} catch (SQLException e) {
				System.out.println("CANNOT CLOSE MYSQL CONNECTION");
				e.printStackTrace();
			}
		}

		return bytesOut;

	}

}
