package com.team1.airline.dao;

import com.team1.airline.entity.Reservation;
import java.util.List;

public interface ReservationDAO {
    void saveReservation(Reservation reservation);
    Reservation findById(String reservationId);
    List<Reservation> findAll();
    void updateReservation(Reservation reservation);
    void deleteReservation(String reservationId);
    List<Reservation> findByUserId(String userId);
    List<Reservation> findByFlightId(String flightId);
}
