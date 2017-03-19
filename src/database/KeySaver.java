///////////////////////////////////////////////////////////////////////////////
//                   ALL STUDENTS COMPLETE THESE SECTIONS
// Title:            (program's title)
// Files:            (list of source files)
// Semester:         (course) Fall 2015
//
// Author:           (your name)
// Email:            (your email address)
// CS Login:         (your login name)
// Lecturer's Name:  (name of your lecturer)
// Lab Section:      (your lab section number)
//
//////////////////// PAIR PROGRAMMERS COMPLETE THIS SECTION ////////////////////
//
//                   CHECK ASSIGNMENT PAGE TO see IF PAIR-PROGRAMMING IS ALLOWED
//                   If pair programming is allowed:
//                   1. Read PAIR-PROGRAMMING policy (in cs302 policy) 
//                   2. choose a partner wisely
//                   3. REGISTER THE TEAM BEFORE YOU WORK TOGETHER 
//                      a. one partner creates the team
//                      b. the other partner must join the team
//                   4. complete this section for each program file.
//
// Pair Partner:     (name of your pair programming partner)
// Email:            (email address of your programming partner)
// CS Login:         (partner's login name)
// Lecturer's Name:  (name of your partner's lecturer)
// Lab Section:      (your partner's lab section number)
//
//////////////////// STUDENTS WHO GET HELP FROM OTHER THAN THEIR PARTNER //////
//                   must fully acknowledge and credit those sources of help.
//                   Instructors and TAs do not have to be credited here,
//                   but tutors, roommates, relatives, strangers, etc do.
//
// Persons:          Identify persons by name, relationship to you, and email.
//                   Describe in detail the the ideas and help they provided.
//
// Online sources:   avoid web searches to solve your problems, but if you do
//                   search, be sure to include Web URLs and description of 
//                   of any information you find.
//////////////////////////// 80 columns wide //////////////////////////////////
package database;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Admin
 *
 */
public class KeySaver {

	public static void savePublicKey(String userID, byte[] publicKey){

		DataBaseDriver dataBase = new DataBaseDriver();
		Connection dataBaseConn = null;
		try {
			dataBaseConn = dataBase.getConnection();


			PreparedStatement stmt = dataBaseConn.prepareStatement("INSERT INTO accessKey (userID, accessKey) VALUES (?,?)");
			stmt.setString(1, userID);
			stmt.setBinaryStream(2,new ByteArrayInputStream(publicKey),publicKey.length);
			stmt.execute();

			System.out.println("Pub. Key: "+publicKey);
			System.out.println("SQL:"+stmt);
			System.out.println("=====PRIVATE KEY CREATED FROM DATABASE=====");

		} catch (Exception e) {
			System.out.println("=====ERROR SAVING PRIVATE KEY TO DATABASE=====");
			e.printStackTrace();
		}finally{
			try {
				dataBaseConn.close();
			} catch (SQLException e) {
				System.out.println("CANNOT CLOSE MYSQL CONNECTION");
				e.printStackTrace();
			}
		}

	}


	public static void savePrivateKey(String userID, byte[] secretKey){

		DataBaseDriver dataBase = new DataBaseDriver();
		Connection dataBaseConn = null;
		try {
			dataBaseConn = dataBase.getConnection();


			PreparedStatement stmt = dataBaseConn.prepareStatement("INSERT INTO secretKey (userID, secretKey) VALUES (?, ?)");
			stmt.setString(1, userID);
			stmt.setBinaryStream(2,new ByteArrayInputStream(secretKey),secretKey.length);
			
	

			stmt.execute();

			System.out.println("=====PRIVATE KEY CREATED FOR DATABASE=====");

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

	}

}
