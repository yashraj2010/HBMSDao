package com.xyz.hbms.dao;

import java.sql.SQLException;
import java.util.List;

import com.xyz.hbms.model.Hotel;

public interface HotelDao {
	
	public List<Hotel> showAll() throws SQLException;
	public boolean updateHotelDescription(String hotelId, String hotelDescription);
	public List<Hotel> searchByPrice(int min, int max);
	public List<Hotel> searchByLocation(String city);
	public boolean addHotel(Hotel hotel) throws SQLException;
	public boolean deleteHotel(String id) throws SQLException;
	public boolean updateSpecialOffers(String hotelId, double percentageDiscount); 
}
