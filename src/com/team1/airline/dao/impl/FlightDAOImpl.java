package com.team1.airline.dao.impl;

import com.team1.airline.dao.FlightDAO;
import com.team1.airline.entity.Flight;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FlightDAOImpl implements FlightDAO {

    private String flightFilePath = "data/Flight.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private String flightToLine(Flight flight) {
        return String.join(" ",
                flight.getFlightId(),
                flight.getRouteId(),
                flight.getAircraftId(),
                flight.getDepartureTime().format(formatter),
                flight.getArrivalTime().format(formatter),
                flight.getStatus());
    }

    @Override
    public void saveFlight(Flight flight) {
        String flightLine = flightToLine(flight) + System.lineSeparator();
        try {
            Files.write(Paths.get(flightFilePath), flightLine.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        } catch (IOException e) {
            System.err.println("Error saving flight to file: " + e.getMessage());
        }
    }

    @Override
    public Flight findByFlightId(String flightId) {
        return readAllFlightsFromFile().stream()
                .filter(f -> f.getFlightId().equals(flightId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Flight> findFlightsByRouteAndDate(String routeId, LocalDate date) {
        return readAllFlightsFromFile().stream()
                .filter(f -> f.getRouteId().equals(routeId) && f.getDepartureTime().toLocalDate().equals(date))
                .collect(Collectors.toList());
    }

    @Override
    public List<Flight> findAll() {
        return readAllFlightsFromFile();
    }

    @Override
    public void updateFlight(Flight flight) {
        List<Flight> flights = readAllFlightsFromFile();
        List<String> lines = flights.stream()
                .map(f -> f.getFlightId().equals(flight.getFlightId()) ? flightToLine(flight) : flightToLine(f))
                .collect(Collectors.toList());
        try {
            Files.write(Paths.get(flightFilePath), lines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error updating flight in file: " + e.getMessage());
        }
    }

    @Override
    public void deleteFlight(String flightId) {
        List<Flight> flights = readAllFlightsFromFile();
        List<String> lines = flights.stream()
                .filter(f -> !f.getFlightId().equals(flightId))
                .map(this::flightToLine)
                .collect(Collectors.toList());
        try {
            Files.write(Paths.get(flightFilePath), lines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error deleting flight from file: " + e.getMessage());
        }
    }

    private List<Flight> readAllFlightsFromFile() {
        List<Flight> flights = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(flightFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] values = line.split("\\s+", 6);
                if (values.length == 6) {
                    Flight flight = new Flight(
                            values[0], // flightId
                            values[1], // routeId
                            values[2], // aircraftId
                            LocalDateTime.parse(values[3], formatter), // departureTime
                            LocalDateTime.parse(values[4], formatter), // arrivalTime
                            values[5]  // status
                    );
                    flights.add(flight);
                }
            }
        } catch (IOException | java.time.format.DateTimeParseException e) {
            if (!(e instanceof java.io.FileNotFoundException)) {
                System.err.println("Error reading or parsing Flight.txt: " + e.getMessage());
            }
        }
        return flights;
    }
}
