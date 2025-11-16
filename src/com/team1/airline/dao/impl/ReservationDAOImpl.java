package com.team1.airline.dao.impl;

import com.team1.airline.dao.ReservationDAO;
import com.team1.airline.entity.Reservation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReservationDAOImpl implements ReservationDAO {

    private String reservationFilePath = "data/Reservation.txt";

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
        String reservationLine = reservationToLine(reservation) + System.lineSeparator();
        try {
            Files.write(Paths.get(reservationFilePath), reservationLine.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        } catch (IOException e) {
            System.err.println("Error saving reservation to file: " + e.getMessage());
        }
    }

    @Override
    public Reservation findByReservationId(String reservationId) {
        return readAllReservationsFromFile().stream()
                .filter(r -> r.getReservationId().equals(reservationId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Reservation> findReservationsByFlightId(String flightId) {
        return readAllReservationsFromFile().stream()
                .filter(r -> r.getFlightId().equals(flightId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findReservationsByUserId(String userId) {
        return readAllReservationsFromFile().stream()
                .filter(r -> r.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findAll() {
        return readAllReservationsFromFile();
    }

    @Override
    public void updateReservation(Reservation reservation) {
        List<Reservation> reservations = readAllReservationsFromFile();
        List<String> lines = reservations.stream()
                .map(r -> r.getReservationId().equals(reservation.getReservationId()) ? reservationToLine(reservation) : reservationToLine(r))
                .collect(Collectors.toList());
        try {
            Files.write(Paths.get(reservationFilePath), lines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error updating reservation in file: " + e.getMessage());
        }
    }

    @Override
    public void deleteReservation(String reservationId) {
        List<Reservation> reservations = readAllReservationsFromFile();
        List<String> lines = reservations.stream()
                .filter(r -> !r.getReservationId().equals(reservationId))
                .map(this::reservationToLine)
                .collect(Collectors.toList());
        try {
            Files.write(Paths.get(reservationFilePath), lines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error deleting reservation from file: " + e.getMessage());
        }
    }

    private List<Reservation> readAllReservationsFromFile() {
        List<Reservation> reservations = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(reservationFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] values = line.split("\\s+");
                if (values.length == 6) {
                    Reservation reservation = new Reservation(
                            values[0], // reservationId
                            values[1], // userId
                            values[2], // flightId
                            values[3], // seatNumber
                            Double.parseDouble(values[4]), // finalPrice
                            values[5]  // status
                    );
                    reservations.add(reservation);
                }
            }
        } catch (FileNotFoundException e) {
            // This is not an error if the file doesn't exist yet. It will be created on the first reservation.
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading or parsing " + reservationFilePath + ": " + e.getMessage());
        }
        return reservations;
    }
}
