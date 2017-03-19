package database;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.io.IOUtils;



/**
 * @author Admin
 *
 */
public class KeyBuilder {


	public static byte[] getPublicKey(String id){

		DataBaseDriver dataBase = new DataBaseDriver();
		Connection dataBaseConn = null;
		byte[] bytesOut = new byte[162];
		try {
			dataBaseConn = dataBase.getConnection();

			String command ="SELECT `accessKey` FROM `accessKey` WHERE userID='"+id+"'";

			System.out.println(command);
			ResultSet rs = dataBase.getDataBaseInfo(dataBaseConn, command);

			if(rs.next()){
				InputStream stream = rs.getBinaryStream(1);
				stream.read(bytesOut);
			}

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
		byte[] bytesOut = new byte[1024];
		byte[] bytesOutNow = null;
		try {
			dataBaseConn = dataBase.getConnection();

			String command ="SELECT `secretKey` FROM `secretKey` WHERE userID='"+id+"'";
			ResultSet rs = dataBase.getDataBaseInfo(dataBaseConn, command);

			if(rs.next()){
				InputStream stream = rs.getBinaryStream(1);
				int size = stream.read(bytesOut);
				
				bytesOutNow = new byte[size]; 
				
				for(int i=0; i<size; i++){
					bytesOutNow[i] = bytesOut[i];
				}
			}


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

		return bytesOutNow;

	}

}
