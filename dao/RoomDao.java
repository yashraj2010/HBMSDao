package com.xyz.hbms.dao;

import java.sql.SQLException;
import java.util.List;

import com.xyz.hbms.model.RoomDetails;

public interface RoomDao {
	public List<RoomDetails> showAll(String HotelId) throws SQLException;
	public boolean addRoomDetails(RoomDetails roomDetails);
	public boolean deleteRoomDetails(String roomId);
	public boolean updateRoomPerNight(String roomId, double roomDiscountPercentage);
	public RoomDetails getRoomDetails(String roomId) throws SQLException;
	public int updateAvailability(String roomId, int avail);
}
