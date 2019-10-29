package com.xyz.hbms.dao;

import java.sql.SQLException;

import com.xyz.hbms.model.User;

public interface UserDao {

	 public boolean addUser(User user);
	public String getRole(String username, String password) throws SQLException;
	public String getUserId(String username, String password) throws SQLException;
}
