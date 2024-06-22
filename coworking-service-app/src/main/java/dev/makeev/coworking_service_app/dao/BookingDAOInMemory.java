package dev.makeev.coworking_service_app.dao;

import dev.makeev.coworking_service_app.model.Booking;
import dev.makeev.coworking_service_app.model.UserBooking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingDAOInMemory implements BookingDAO {

    private final Map<String, List<UserBooking>> mapOfBooking = new HashMap<>();

    @Override
    public void add(String loginOfUser, Booking booking) {
        if (mapOfBooking.containsKey(loginOfUser)) {
            mapOfBooking.get(loginOfUser).add(new UserBooking(loginOfUser,booking));
        } else {
            List<UserBooking> bookingList = new ArrayList<>();
            bookingList.add(new UserBooking(loginOfUser,booking));
            mapOfBooking.put(loginOfUser, bookingList);
        }
    }

    @Override
    public List<UserBooking> getAllForUser(String loginOfUser) {
        return mapOfBooking.get(loginOfUser).isEmpty() ? new ArrayList<>() : mapOfBooking.get(loginOfUser);
    }

    @Override
    public Map<String, List<UserBooking>> getAll(){
        return mapOfBooking;
    }


    @Override
    public void delete(String loginOfUser, long idOfBooking) {
        mapOfBooking.get(loginOfUser).removeIf(userBooking -> userBooking.booking().id() == idOfBooking);
    }
}
