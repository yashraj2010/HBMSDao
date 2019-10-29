package com.xyz.hbms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.xyz.hbms.db.ConnectionFactory;
import com.xyz.hbms.model.Hotel;

/*
 * Hotel DAO implementation
 * It implements Hotel DAO Interface
 * It performs Hotel related operations
 * 
 */
public class HotelDaoImpl implements HotelDao{


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
	 * Generates a report of all the hotels present in the database
	 */
	@Override
	public List<Hotel> showAll() throws SQLException {
		List<Hotel> hotelList = new ArrayList<Hotel>();
		ResultSet rs=null;
		String SQL = "SELECT * FROM HOTEL";
		Connection conn = getConnection();
		try {
			PreparedStatement stat = conn.prepareStatement(SQL);
			rs = stat.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		while(rs.next())
		{
			Hotel hotel = new Hotel();
			hotel.setAddress(rs.getString("ADDRESS"));
			hotel.setAvgRatePerNight(rs.getDouble("AVG_RATE_PER_NIGHT"));
			hotel.setCity(rs.getString("CITY"));
			hotel.setDescription(rs.getString("DESCRIPTION"));
			hotel.setEmail(rs.getString("EMAIL"));
			hotel.setFax(rs.getString("FAX"));
			hotel.setHotelId(rs.getString("HOTEL_ID"));
			hotel.setHotelName(rs.getString("HOTEL_NAME"));
			hotel.setPhoneNo1(rs.getString("PHONE_NO1"));
			hotel.setPhoneNo2(rs.getString("PHONE_NO2"));
			hotel.setRating(rs.getString("RATING"));
			hotelList.add(hotel);
		
		}
		return hotelList;
	}
	/*
	 * Gives a list of hotels in a particular price range
	 * 
	 * Input:- Minimum and Maximum Price
	 * Output:- List of Hotels
	 * 
	 */
	public List<Hotel> searchByPrice(int min, int max) {
		List<Hotel> hotelList = new ArrayList<Hotel>();
		ResultSet rs=null;
		Connection conn = getConnection();
		String query = "SELECT * FROM HOTEL WHERE AVG_RATE_PER_NIGHT BETWEEN "+min+" AND "+max;
		try {
			PreparedStatement stat = conn.prepareStatement(query);
			rs = stat.executeQuery();
			while(rs.next())
			{
				Hotel hotel = new Hotel();
				hotel.setAddress(rs.getString("ADDRESS"));
				hotel.setAvgRatePerNight(rs.getDouble("AVG_RATE_PER_NIGHT"));
				hotel.setCity(rs.getString("CITY"));
				hotel.setDescription(rs.getString("DESCRIPTION"));
				hotel.setEmail(rs.getString("EMAIL"));
				hotel.setFax(rs.getString("FAX"));
				hotel.setHotelId(rs.getString("HOTEL_ID"));
				hotel.setHotelName(rs.getString("HOTEL_NAME"));
				hotel.setPhoneNo1(rs.getString("PHONE_NO1"));
				hotel.setPhoneNo2(rs.getString("PHONE_NO2"));
				hotel.setRating(rs.getString("RATING"));
				hotelList.add(hotel);
			
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
		return hotelList;
	}
	/*
	 *Gives a list of hotels in a particular location
	 *
	 *Input:- City name
	 *Output:- List of Hotels
	 *
	 */
	public List<Hotel> searchByLocation(String city) {
		List<Hotel> hotelList = new ArrayList<Hotel>();
		ResultSet rs=null;
		Connection conn = getConnection();
		city=city.toUpperCase();
		String query = "SELECT * FROM HOTEL WHERE CITY = '"+city + "'";
		try {
			PreparedStatement stat = conn.prepareStatement(query);
			rs = stat.executeQuery();
			while(rs.next())
			{
				Hotel hotel = new Hotel();
				hotel.setAddress(rs.getString("ADDRESS"));
				hotel.setAvgRatePerNight(rs.getDouble("AVG_RATE_PER_NIGHT"));
				hotel.setCity(rs.getString("CITY"));
				hotel.setDescription(rs.getString("DESCRIPTION"));
				hotel.setEmail(rs.getString("EMAIL"));
				hotel.setFax(rs.getString("FAX"));
				hotel.setHotelId(rs.getString("HOTEL_ID"));
				hotel.setHotelName(rs.getString("HOTEL_NAME"));
				hotel.setPhoneNo1(rs.getString("PHONE_NO1"));
				hotel.setPhoneNo2(rs.getString("PHONE_NO2"));
				hotel.setRating(rs.getString("RATING"));
				hotelList.add(hotel);
			
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
		return hotelList;
	}

	/*
	 * Function to change the hotel's description
	 * 
	 * Input:- Hotel ID and the description string
	 * Output:- Boolean for success or failure
	 * 
	 */
	@Override
	public boolean updateHotelDescription(String hotelId, String hotelDescription) {
		String SQL = "UPDATE HOTEL SET DESCRIPTION = '" + hotelDescription + "' where hotel_id ='" + hotelId
				+ "'";
		Connection connection = getConnection();
		int result = 0;
		try {
			PreparedStatement prepareStatement = connection.prepareStatement(SQL);
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
	 * Private method to get the hotelId using a sequence,
	 * here a sequence of the name hotSeq is used
	 */
	private String getHotelId() {
		Connection conn = getConnection();
		String seqqueryString = "SELECT hotSeq.NEXTVAL FROM DUAL";
		PreparedStatement stat;
		int hotelId = 0;
		try {
			stat = conn.prepareStatement(seqqueryString);
			ResultSet rs = stat.executeQuery();
			while (rs.next())
				hotelId = rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "H"+hotelId;

	}
	
	
	
	/*
	 *Registration of a new hotel is done
	 *
	 *Input:- Hotel details
	 *Output:- Boolean for success or failure
	 *
	 */
	@Override
	public boolean addHotel(Hotel hotel) throws SQLException {
		Connection conn = getConnection();
		String query = "Insert into Hotel values (?,?,?,?,?,?,?,?,?,?,?)";
		String hotelId = getHotelId();
		PreparedStatement stat = conn.prepareStatement(query);
		stat.setString(1,hotelId);
		stat.setString(2, hotel.getCity().toUpperCase());
		stat.setString(3, hotel.getHotelName());
		stat.setString(4, hotel.getAddress());
		stat.setString(5, hotel.getDescription());
		stat.setDouble(6, hotel.getAvgRatePerNight());
		stat.setString(7, hotel.getPhoneNo1());
		stat.setString(8, hotel.getPhoneNo2());
		stat.setString(9, hotel.getRating());
		stat.setString(10, hotel.getEmail());
		stat.setString(11, hotel.getFax());

	int rs = stat.executeUpdate();	
	if(rs==1)	
		return true;
	return false;
	}

	/*
	 * Removes a hotel from the list with the hotel id entered
	 * 
	 * Input:- Hotel ID
	 * Output:- Boolean for success or failure
	 */
	@Override
	public boolean deleteHotel(String id) throws SQLException {
		Connection conn = getConnection();
		String query = "Delete from Hotel where hotel_id = ?";
		PreparedStatement stat = conn.prepareStatement(query);
		stat.setString(1, id);
		
		int rs = stat.executeUpdate();	
		if(rs==1)	
			return true;
		return false;
	}

	/*
	 * Function to include special offers for a hotel
	 * 
	 * Input:- Hotel ID and discount percentage
	 * Output:- Boolean for success or failure
	 * 
	 */
	@Override
	public boolean updateSpecialOffers(String hotelId, double percentageDiscount) {
		
		
		String query= "Update ROOMDETAILS set PER_NIGHT_RATE = "+(1-(percentageDiscount/100))+ "* PER_NIGHT_RATE WHERE HOTEL_ID = '"+hotelId+"'";
		
		Connection connection = getConnection();
		int result = 0;
		try {
			PreparedStatement prepareStatement = connection.prepareStatement(query);
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

}
