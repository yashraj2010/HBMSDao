package com.xyz.hbms.dao;

/*
 * Booking DAO implementation
 * It implements Booking DAO Interface
 * It performs booking related operations
 * 
 */
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.xyz.hbms.db.ConnectionFactory;
import com.xyz.hbms.exception.RoomNotFoundException;
import com.xyz.hbms.model.BookingDetails;
import com.xyz.hbms.model.RoomDetails;
import com.xyz.hbms.model.User;
import com.xyz.hbms.service.RoomImplementation;
import com.xyz.hbms.service.RoomInterface;

public class BookingDaoImpl implements BookingDao {

	// making an object of RoomImplementation to use it for performing room operations
	RoomInterface roomImplementation = new RoomImplementation();

	
	//Function to be used for generating a Connection object
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
	 *Gives the total amount of booking
	 *
	 *Input:- Booking Details
	 *Output:- Final Amount
	 */
	public double bookingAmount(BookingDetails bookingDetails) {
		Connection conn = getConnection();
		String amountQuery = "Select Per_Night_Rate from roomdetails where room_id = '" + bookingDetails.getRoomId()
				+ "'";
		ResultSet rs = null;
		double amount = 0;

		PreparedStatement stat;
		try {
			stat = conn.prepareStatement(amountQuery);
			rs = stat.executeQuery();
			while (rs.next())
				amount = rs.getDouble("Per_Night_Rate");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		long diff = 0;
		try {
			diff = new SimpleDateFormat("yyyy-MM-dd").parse(bookingDetails.getBookedTo()).getTime()
					- new SimpleDateFormat("yyyy-MM-dd").parse(bookingDetails.getBookedFrom()).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int days = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		return amount * days;

	}

	/*
	 * Function used to generate a sequence for the booking ID
	 * Here a sequence is used of the name bookingSequence.
	 * 
	 */
	private int getBookingId() {
		Connection conn = getConnection();
		String seqqueryString = "SELECT bookingSequence.NEXTVAL FROM DUAL";
		PreparedStatement stat;
		int bookingId = 0;
		try {
			stat = conn.prepareStatement(seqqueryString);
			ResultSet rs = stat.executeQuery();
			while (rs.next())
				bookingId = rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return bookingId;

	}

	/*
	 * Booking Details are added to the bookingdetails table
	 * 
	 * Input:- Booking Details
	 * Output:- int for success or failure
	 */
	@Override
	public int bookRoom(BookingDetails bookingDetails) throws SQLException, RoomNotFoundException {
		Connection conn = getConnection();
		double amount = bookingAmount(bookingDetails);
		int bookingSeq = getBookingId();
		String SQL = "Insert into bookingdetails values (?,?,?,?,?,?,?,?)";

		PreparedStatement stat;
		int flag = 0;
		try {
			stat = conn.prepareStatement(SQL);
			stat.setInt(1, bookingSeq);
			stat.setString(2, bookingDetails.getRoomId());
			stat.setString(3, bookingDetails.getUserId());
			stat.setDate(4, Date.valueOf(bookingDetails.getBookedFrom()));
			stat.setDate(5, Date.valueOf(bookingDetails.getBookedTo()));
			stat.setInt(6, bookingDetails.getNumberOfAdults());
			stat.setInt(7, bookingDetails.getNumberOfChildren());
			stat.setDouble(8, amount);

			flag = stat.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (flag == 1) {
			RoomDetails roomdetails = roomImplementation.findRoomById(bookingDetails.getRoomId());
			if (roomdetails.getAvailability() == 1)
				roomImplementation.updateAvailability(bookingDetails.getRoomId(), roomdetails.getAvailability());
			return bookingSeq;
		} else
			return flag;
	}

	/*
	 * Report of all Booking Details of a selected Hotel
	 * 
	 * Input:- Hotel Id
	 * Output:-  Booking Details
	 */
	@Override
	public List<BookingDetails> viewBookingByHotelId(String hotelId) {
		List<BookingDetails> bookedList = new ArrayList<BookingDetails>();
		Connection conn = getConnection();
		String seqqueryString = "SELECT * from BOOKINGDETAILS where room_id = (select room_id from roomdetails where hotel_id = ?)";
		PreparedStatement stat;
		
		try {
			stat = conn.prepareStatement(seqqueryString);
			stat.setString(1, hotelId);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				BookingDetails bookingDetails = new BookingDetails();
				bookingDetails.setBookingId(Integer.toString(rs.getInt("BOOKING_ID")));
				bookingDetails.setRoomId(rs.getString("ROOM_ID"));
				bookingDetails.setUserId(rs.getString("USER_ID"));
				bookingDetails.setBookedFrom(rs.getDate("BOOKED_FROM").toString());
				bookingDetails.setBookedTo(rs.getDate("BOOKED_TO").toString());
				bookingDetails.setAmount(rs.getDouble("AMOUNT"));
				bookingDetails.setNumberOfAdults(rs.getInt("NO_OF_ADULTS"));
				bookingDetails.setNumberOfAdults(rs.getInt("NO_OF_CHILDREN"));
				bookedList.add(bookingDetails);
			}
				
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return bookedList;
	}

	/*
	 * Report of all Guests of a selected Hotel
	 * 
	 * Input:- Hotel Id
	 * Output:- Guest List
	 */
	@Override
	public List<User> showGuestList(String hotelId) {
		List<User> guestList = new ArrayList<User>();
		Connection conn = getConnection();
		String seqqueryString = "SELECT * from USERS where user_id IN (select user_id from bookingdetails where room_id IN (select room_id from roomdetails where hotel_id = ?))";
		PreparedStatement stat;
		
		try {
			stat = conn.prepareStatement(seqqueryString);
			stat.setString(1, hotelId);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				User guest = new User();
				guest.setUserId(rs.getString("USER_ID"));
				guest.setUserName(rs.getString("USER_NAME"));
				guest.setMobileNo(rs.getString("MOBILE_NO"));
				guest.setEmail(rs.getString("EMAIL"));
				guestList.add(guest);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return guestList;
		}

	/*
	 * Report of all Bookings made by the user
	 * 
	 * Input:- User ID
	 * Output:- List of Bookings
	 */
	@Override
	public List<BookingDetails> listMyBookings(String userId) {
		
		List<BookingDetails> bookedList = new ArrayList<BookingDetails>();
		Connection conn = getConnection();
		String seqqueryString = "SELECT * from BOOKINGDETAILS where user_id = ?";
		PreparedStatement stat;
		
		try {
			stat = conn.prepareStatement(seqqueryString);
			stat.setString(1, userId);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				BookingDetails bookingDetails = new BookingDetails();
				bookingDetails.setBookingId(Integer.toString(rs.getInt("BOOKING_ID")));
				bookingDetails.setRoomId(rs.getString("ROOM_ID"));
				bookingDetails.setUserId(rs.getString("USER_ID"));
				bookingDetails.setBookedFrom(rs.getDate("BOOKED_FROM").toString());
				bookingDetails.setBookedTo(rs.getDate("BOOKED_TO").toString());
				bookingDetails.setAmount(rs.getDouble("AMOUNT"));
				bookingDetails.setNumberOfAdults(rs.getInt("NO_OF_ADULTS"));
				bookingDetails.setNumberOfAdults(rs.getInt("NO_OF_CHILDREN"));
				bookedList.add(bookingDetails);
			}
				
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return bookedList;
	}

	/*
	 * Report of all Bookings
	 * 
	 * Output:- List of Bookings
	 */
	@Override
	public List<BookingDetails> getAllBookings() {
		List<BookingDetails> bookedList = new ArrayList<BookingDetails>();
		Connection conn = getConnection();
		String seqqueryString = "SELECT * from BOOKINGDETAILS";
		PreparedStatement stat;
		
		try {
			stat = conn.prepareStatement(seqqueryString);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				BookingDetails bookingDetails = new BookingDetails();
				bookingDetails.setBookingId(Integer.toString(rs.getInt("BOOKING_ID")));
				bookingDetails.setRoomId(rs.getString("ROOM_ID"));
				bookingDetails.setUserId(rs.getString("USER_ID"));
				bookingDetails.setBookedFrom(rs.getDate("BOOKED_FROM").toString());
				bookingDetails.setBookedTo(rs.getDate("BOOKED_TO").toString());
				bookingDetails.setAmount(rs.getDouble("AMOUNT"));
				bookingDetails.setNumberOfAdults(rs.getInt("NO_OF_ADULTS"));
				bookingDetails.setNumberOfAdults(rs.getInt("NO_OF_CHILDREN"));
				bookedList.add(bookingDetails);
			}
				
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return bookedList;
	}
		
		
	}


