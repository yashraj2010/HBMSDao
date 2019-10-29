package com.xyz.hbms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.xyz.hbms.db.ConnectionFactory;
import com.xyz.hbms.model.User;


/*
 * User DAO implementation
 * It implements User DAO Interface
 * It performs User related operations
 * 
 */
public class UserDaoImpl implements UserDao {

	
	
	
	//getting connection from ConnectionFactory
	private Connection getConnection() {
		Connection connection = null;
		try {
			connection = ConnectionFactory.getInstance().getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connection;
	}
	/*
	 * This private method is used to extract the user ID using a sequence,
	 * here a sequence by the name userSeq is used
	 */
	private String getUserId() {
		Connection conn = getConnection();
		String seqqueryString = "SELECT userSeq.NEXTVAL FROM DUAL";
		PreparedStatement stat;
		int userId = 0;
		try {
			stat = conn.prepareStatement(seqqueryString);
			ResultSet rs = stat.executeQuery();
			while (rs.next())
				userId = rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "U"+userId;

	}
	
	
	/*
	 * Registration of a new user is done
	 * 
	 * Input:- User Details
	 * Output:- Boolean for success or failure
	 * 
	 */
	@Override
	public boolean addUser(User user) {
		String userId = getUserId();
		String SQL = "INSERT INTO USERS VALUES ( ?, ?, ?, ?, ?, ?, ?, ?)";
		Connection conn = getConnection();
		int rs = 0;
		try {
			PreparedStatement stat = conn.prepareStatement(SQL);
			stat.setString(1, userId);
			stat.setString(2, user.getUserPassword());
			stat.setString(3, user.getUserRole());
			stat.setString(4, user.getUserName());
			stat.setString(5, user.getMobileNo());
			stat.setString(6, user.getPhone());
			stat.setString(7, user.getAddress());
			stat.setString(8, user.getEmail());
			 rs =stat.executeUpdate();
			System.out.println(rs + " user registered");
		} catch (SQLException e) {
			e.printStackTrace();
		}
			if(rs == 1)
				return true;
			return false;
	}




	/*
	 * Returns the role of a user
	 * 
	 * Input:- Username, Password
	 * Output:- Customer || Employee
	 * 
	 */
	@Override
	public String getRole(String username, String password) throws SQLException {
		String SQL = "SELECT USER_ROLE FROM USERS WHERE USER_NAME = ? AND USER_PASSWORD = ?";
		Connection conn = getConnection();
		ResultSet rs = null;
		String role = null;
		try {
			PreparedStatement stat= conn.prepareStatement(SQL);
			stat.setString(1, username);
			stat.setString(2, password);
			rs = stat.executeQuery();		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		while(rs.next())
		{
			 role = (rs.getString("USER_ROLE"));
		}
		
		return role;
	}




	/*
	 * Returns the User ID of a particular user
	 * 
	 * Input:- Username, Password
	 * Output:- User ID
	 * 
	 */
	@Override
	public String getUserId(String username, String password) throws SQLException {
		String SQL = "SELECT USER_ID FROM USERS WHERE USER_NAME = ? AND USER_PASSWORD = ?";
		Connection conn = getConnection();
		ResultSet rs = null;
		String role = null;
		try {
			PreparedStatement stat= conn.prepareStatement(SQL);
			stat.setString(1, username);
			stat.setString(2, password);
			rs = stat.executeQuery();		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		while(rs.next())
		{
			 role = (rs.getString("USER_ID"));
		}
		
		return role;
	}

}
