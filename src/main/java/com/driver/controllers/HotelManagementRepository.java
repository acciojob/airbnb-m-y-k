package com.driver.controllers;

import com.driver.model.Booking;
import com.driver.model.Facility;
import com.driver.model.Hotel;
import com.driver.model.User;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class HotelManagementRepository {

    HashMap<Integer, User> userDatabase = new HashMap<>(); // userId, User hashMap

    HashMap<String, Hotel> hotelDatabase = new HashMap<>(); // hotelId, Hotel hashMap

    HashMap<String, Booking> bookingDatabase = new HashMap<>(); // bookingId, Booking hashMap


    public String addHotel(Hotel hotel) {

        // You need to add an hotel to the database

        // 1. incase the hotelName is null or the hotel Object is null return an empty a FAILURE
        if (hotel.equals(null) || hotel.getHotelName().equals(null)) {
            return "FAILURE";
        }

        String key = hotel.getHotelName();

        // 2. Incase somebody is trying to add the duplicate hotelName return FAILURE
        if (hotelDatabase.containsKey(key)) {
            return "FAILURE";
        }

        // 3. in all other cases return SUCCESS after successfully adding the hotel to the hotelDb.
        hotelDatabase.put(key, hotel);
        return "SUCCESS";

    }


    public Integer addUser(User user) {

        //You need to add a User Object to the database
        //Assume that user will always be a valid user and return the aadharCardNo of the user

        Integer key = user.getaadharCardNo();
        userDatabase.put(key, user);

        return key;
    }

    public String getHotelWithMostFacilities() {

        //Out of all the hotels we have added so far, we need to find the hotelName with most no of facilities

        // 1. Incase there is a tie return the lexicographically smaller hotelName
        // 2. Incase there is not even a single hotel with atleast 1 facility return "" (empty string)

        // get all hotel names, sort them as keys
        List<String> hotelNames = new ArrayList<>();

        for (String hotelName : hotelDatabase.keySet()) {
            hotelNames.add(hotelName);
        }
        Collections.sort(hotelNames);

        // now extract Hotel object from hotelDb and filter ans
        String hotelWithMostFacilities = "";
        int noOfFacilitiesInAHotel = 0;

        for (String key : hotelNames) {

            Hotel hotel = hotelDatabase.get(key);

            if (hotel.getFacilities().size() > noOfFacilitiesInAHotel) {

                hotelWithMostFacilities = hotel.getHotelName();
                noOfFacilitiesInAHotel = hotel.getFacilities().size();
            }
        }
        return hotelWithMostFacilities;
    }

    public int bookARoom(Booking booking) {

        //The booking object coming from postman will have all the attributes except bookingId and amountToBePaid;
        //Have bookingId as a random UUID generated String
        //save the booking Entity and keep the bookingId as a primary key
        //Calculate the total amount paid by the person based on no. of rooms booked and price of the room per night.
        //If there arent enough rooms available in the hotel that we are trying to book return -1
        //in other case return total amount paid

        // check if the asked no of rooms are available in the hotel or not
        int noOfRoomsAskedForBooking = booking.getNoOfRooms();
        Hotel hotel = hotelDatabase.get(booking.getHotelName());
        if(noOfRoomsAskedForBooking > hotel.getAvailableRooms()) {
            return -1;
        }

        // booking a room updating it in bookingDb
        String bookingId = UUID.randomUUID().toString();
        booking.setBookingId(bookingId);
        bookingDatabase.put(bookingId, booking);

        // to book a hotel
        // 1. we need to decrease available rooms by room demand in new request
        hotel.setAvailableRooms(hotel.getAvailableRooms() - noOfRoomsAskedForBooking);
        // 2.
        int price = noOfRoomsAskedForBooking * hotel.getPricePerNight();
        return price;
    }

    public int getBookings(Integer aadharCard) {

        // bookings done by a person
        // 1. extract Booking object from bookingDb
        // 2. match bookingAadharCard

        int bookingCount = 0;

        for (Booking booking : bookingDatabase.values()) {

            if (booking.getBookingAadharCard() == aadharCard) {
                bookingCount++;
            }
        }
        return bookingCount;
    }

    public Hotel updateFacilities(List<Facility> newFacilities, String hotelName) {

        //We are having a new facilites that a hotel is planning to bring.

        // 1. If the hotel is already having that facility ignore that facility otherwise add that facility in the hotelDb
        // 2. return the final updated List of facilities and also update that in your hotelDb
        // 3. Note that newFacilities can also have duplicate facilities possible

        // get all hotel object
        Hotel hotel = hotelDatabase.get(hotelName);

        // check if the hotel has newFacility already, if yes, ignore, otherwise add
        List<Facility> facilityList = hotel.getFacilities();

        for (Facility facility : newFacilities) {

            // if the hotel does not have this facilty, put
            if (facilityList.contains(facility) == false) {

                facilityList.add(facility);
            }
        }

        // put this new updated faciliry list to hotel facilities list
        hotel.setFacilities(facilityList);

        return hotel;
    }
}
