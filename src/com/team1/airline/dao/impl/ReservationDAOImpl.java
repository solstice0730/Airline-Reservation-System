package com.team1.airline.dao.impl;

import com.team1.airline.dao.ReservationDAO;
import com.team1.airline.entity.Reservation;
import com.team1.airline.dao.impl.DataManager;

import java.util.List;
import java.util.stream.Collectors;

public class ReservationDAOImpl implements ReservationDAO {

    private String reservationToLine(Reservation reservation) {
        return String.join(" ",
                reservation.getReservationId(),
                reservation.getUserId(),
                reservation.getFlightId(),
                reservation.getSeatNumber(),
                String.valueOf(reservation.getFinalPrice()),
                reservation.getStatus());
    }

    @Override
    public void addReservation(Reservation reservation) {
        DataManager.getInstance().getReservations().add(reservation);
    }

    @Override
    public Reservation findByReservationId(String reservationId) {
        return DataManager.getInstance().getReservations().stream()
                .filter(r -> r.getReservationId().equals(reservationId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Reservation> findReservationsByFlightId(String flightId) {
        return DataManager.getInstance().getReservations().stream()
                .filter(r -> r.getFlightId().equals(flightId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findReservationsByUserId(String userId) {
        return DataManager.getInstance().getReservations().stream()
                .filter(r -> r.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findAll() {
        return DataManager.getInstance().getReservations();
    }

    @Override
    public void updateReservation(Reservation reservation) {
        List<Reservation> reservations = DataManager.getInstance().getReservations();
        for (int i = 0; i < reservations.size(); i++) {
            if (reservations.get(i).getReservationId().equals(reservation.getReservationId())) {
                reservations.set(i, reservation);
                return;
            }
        }
    }

    @Override
    public void deleteReservation(String reservationId) {
        List<Reservation> reservations = DataManager.getInstance().getReservations();
        reservations.removeIf(r -> r.getReservationId().equals(reservationId));
    }
}
