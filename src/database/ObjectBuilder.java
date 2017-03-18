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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import user.User;

/**
 * @author Admin
 *
 */
public class ObjectBuilder {

	private final String id;


	public ObjectBuilder(String id){
		this.id = id;
	}


	public User getUser(){
		DataBaseDriver dataBase = new DataBaseDriver();
		Connection dataBaseConn = dataBase.getConnection();
		try {
			dataBaseConn = dataBase.getConnection();


			String command ="SELECT * FROM user WHERE userID='"+id+"'";

			ResultSet rs = dataBase.getDataBaseInfo(dataBaseConn, command);
			while(rs.next()){
				User exportUser = User(rs.getString("email"), rs.getString("passHash"), );
			}


			System.out.println("=====USER CREATED FROM DATABASE=====");

		} catch (Exception e) {
			System.out.println("=====ERROR GETTING USER FROM DATABASE=====");
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
