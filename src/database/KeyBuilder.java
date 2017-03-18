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


	public static byte[] getPubicKey(String id){

		DataBaseDriver dataBase = new DataBaseDriver();
		Connection dataBaseConn = null;
		byte[] bytesOut = null;
		try {
			dataBaseConn = dataBase.getConnection();

			String command ="SELECT `accessKey`.`accessKey` FROM `user` WHERE userID='"+id+"'";
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

			String command ="SELECT `sercetKey`.`sercetKey` FROM `user` WHERE userID='"+id+"'";
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
