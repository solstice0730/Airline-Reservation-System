package com.team1.airline.dao;

import com.team1.airline.entity.Reservation;
import java.util.List;

public interface ReservationDAO {
    void addReservation(Reservation reservation);
    Reservation findByReservationId(String reservationId);
    List<Reservation> findReservationsByFlightId(String flightId);
    List<Reservation> findReservationsByUserId(String userId);
    List<Reservation> findAll();
    void updateReservation(Reservation reservation);
    void deleteReservation(String reservationId);
}