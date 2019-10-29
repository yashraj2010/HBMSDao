package com.xyz.hbms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.xyz.hbms.db.ConnectionFactory;

import com.xyz.hbms.model.RoomDetails;

/*
 * Room DAO implementation
 * It implements Room DAO Interface
 * It performs Room related operations
 * 
 */
public class RoomDaoImpl implements RoomDao {
	
	/*
	 * private method to get a Connection object
	 */
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
	 * Generates a report of all the rooms present in a particular hotel
	 * 
	 * Input:- Hotel ID
	 * Output:- List of Rooms
	 * 
	 */
	@Override
	public List<RoomDetails> showAll(String HotelId) throws SQLException {
		List<RoomDetails> roomList = new ArrayList<RoomDetails>();
		ResultSet resultSet = null;
		String SQL = "SELECT * FROM ROOMDETAILS where HOTEL_ID = '" + HotelId + "'";
		Connection connection = getConnection();
		try {
			PreparedStatement stat = connection.prepareStatement(SQL);
			resultSet = stat.executeQuery();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		while (resultSet.next()) {
			RoomDetails roomDetails = new RoomDetails();
			roomDetails.setHotelId(HotelId);
			roomDetails.setRoomId(resultSet.getString("ROOM_ID"));
			roomDetails.setRoomNo(resultSet.getString("ROOM_NO"));
			roomDetails.setRoomType(resultSet.getString("ROOM_TYPE"));
			roomDetails.setPerNight(resultSet.getInt("PER_NIGHT_RATE"));
			roomDetails.setAvailability(resultSet.getInt("AVAILABILITIES"));
			roomList.add(roomDetails);
		}

		return roomList;
	}

	/*
	 * private method to generate a RoomId using a sequence, 
	 * here a sequence of the name roomSeq is used
	 */
	private String getRoomId() {
		Connection conn = getConnection();
		String seqqueryString = "SELECT roomSeq.NEXTVAL FROM DUAL";
		PreparedStatement stat;
		int roomId = 0;
		try {
			stat = conn.prepareStatement(seqqueryString);
			ResultSet rs = stat.executeQuery();
			while (rs.next())
				roomId = rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "R"+roomId;

	}
	
	
	
	/*
	 * Registers a new Room in the database
	 * 
	 * Input:- Room Details
	 * Output:- Boolean for success or failure
	 * 
	 */
	@Override
	public boolean addRoomDetails(RoomDetails roomDetails) {
		String roomId = getRoomId();
		String SQL = "INSERT INTO ROOMDETAILS VALUES (?, ?, ?, ?, ?, ?)";
		Connection conn = getConnection();
		int result = 0;
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(SQL);
			preparedStatement.setString(1, roomDetails.getHotelId());
			preparedStatement.setString(2, roomId);
			preparedStatement.setString(3, roomDetails.getRoomNo());
			preparedStatement.setString(4, roomDetails.getRoomType());
			preparedStatement.setDouble(5, roomDetails.getPerNight());
			preparedStatement.setInt(6, roomDetails.getAvailability());
			result = preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (result == 1) {
			return true;
		}

		else {
			return false;
		}

	}

	/*
	 * Removes the room details from the database of a particular room
	 * 
	 * Input:- Room ID
	 * Output:- Boolean for success or failure
	 * 
	 */
	@Override
	public boolean deleteRoomDetails(String roomId) {
		String SQL = "DELETE FROM ROOMDETAILS WHERE ROOM_ID= ?";
		Connection connection = getConnection();
		int result = 0;
		try {
			PreparedStatement prepareStatement = connection.prepareStatement(SQL);
			prepareStatement.setString(1, roomId);
			result = prepareStatement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (result == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	/*
	 * Updates the room tariff of a particular room 
	 * 
	 *  Input:- Room ID, Discount Percentage
	 *  Output:- Boolean for success or failure
	 *  
	 */
	public boolean updateRoomPerNight(String roomId, double roomDiscountPercentage) {
		String SQL = "UPDATE ROOMDETAILS SET PER_NIGHT_RATE = ? where ROOM_ID = '" + roomId + "'";
		Connection connection = getConnection();
		int result = 0;
		double updatePerNight = 0;
		try {
			PreparedStatement prepareStatement = connection.prepareStatement(SQL);
			double perNight = getPerNight(roomId);
			updatePerNight = perNight * (1 - (roomDiscountPercentage / 100));
			prepareStatement.setDouble(1, updatePerNight);
			result = prepareStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (result == 0.0) {
			return false;
		} else {
			return true;
		}
	}

	/*
	 * This method is used to extract the per night rate of a particular room
	 * 
	 * Input :- room ID
	 * Output :- per night rate in double
	 */
	public double getPerNight(String roomId) {
		String SQL = "SELECT PER_NIGHT_RATE FROM ROOMDETAILS WHERE ROOM_ID = '" + roomId + "'";
		Connection connection = getConnection();
		double result = 0.0;
		try {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(SQL);
			while (resultSet.next()) {
				result = resultSet.getDouble("PER_NIGHT_RATE");
				break;
			}
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/*
	 * Returns the room details of a particular room
	 * 
	 * Input:- Room ID
	 * Output:- Room Details
	 */
	@Override
	public RoomDetails getRoomDetails(String roomId) throws SQLException {

		ResultSet resultSet = null;
		String SQL = "SELECT * FROM ROOMDETAILS where ROOM_ID = '" + roomId + "'";
		Connection connection = getConnection();
		RoomDetails roomDetails = null;
		try {
			PreparedStatement stat = connection.prepareStatement(SQL);
			resultSet = stat.executeQuery();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		while (resultSet.next()) {
			 roomDetails = new RoomDetails();
			roomDetails.setHotelId(resultSet.getString("HOTEL_ID"));
			roomDetails.setRoomId(roomId);
			roomDetails.setRoomNo(resultSet.getString("ROOM_NO"));
			roomDetails.setRoomType(resultSet.getString("ROOM_TYPE"));
			roomDetails.setPerNight(resultSet.getInt("PER_NIGHT_RATE"));
			roomDetails.setAvailability(resultSet.getInt("AVAILABILITIES"));
		}

		return roomDetails;
	}

	/*
	 * Updates the availability of a particular room
	 * 
	 * Input:- Room ID, updated availability
	 * Output:- Boolean for success or failure
	 * 
	 */
	@Override
	public int updateAvailability(String roomId, int avail) {

		String SQL = "UPDATE ROOMDETAILS SET AVAILABILITIES = ? where ROOM_ID = '" + roomId + "'";
		Connection connection = getConnection();
		if(avail == 1)
			avail=0;
		else avail=1;
		int result = 0;
		try {
			PreparedStatement stat = connection.prepareStatement(SQL);
			stat.setInt(1, avail);
			result = stat.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
		}
	}


