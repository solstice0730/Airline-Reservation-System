package com.team1.airline.dao.impl;

import com.team1.airline.entity.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataManager {
    private static DataManager instance;

    private List<User> users;
    private List<Aircraft> aircrafts;
    private List<Airport> airports;
    private List<Flight> flights;
    private List<Reservation> reservations;
    private List<Route> routes;

    private static final String DATA_DIR = "data/";
    private static final String USER_FILE = DATA_DIR + "User.txt";
    private static final String AIRCRAFT_FILE = DATA_DIR + "Aircraft.txt";
    private static final String AIRPORT_FILE = DATA_DIR + "Airport.txt";
    private static final String FLIGHT_FILE = DATA_DIR + "Flight.txt";
    private static final String RESERVATION_FILE = DATA_DIR + "Reservation.txt";
    private static final String ROUTE_FILE = DATA_DIR + "Route.txt";

    public static final DateTimeFormatter FLIGHT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private DataManager() {
        users = new ArrayList<>();
        aircrafts = new ArrayList<>();
        airports = new ArrayList<>();
        flights = new ArrayList<>();
        reservations = new ArrayList<>();
        routes = new ArrayList<>();
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public void loadAllData() {
        System.out.println("DataManager: Loading all data...");
        users = loadUsers();
        aircrafts = loadAircrafts();
        airports = loadAirports();
        flights = loadFlights();
        reservations = loadReservations();
        routes = loadRoutes();
        System.out.println("DataManager: All data loaded.");
    }

    public void saveAllData() {
        System.out.println("DataManager: Saving all data...");
        saveUsers();
        saveAircrafts();
        saveAirports();
        saveFlights();
        saveReservations();
        saveRoutes();
        System.out.println("DataManager: All data saved.");
    }

    // --- Load methods ---
    private List<User> loadUsers() {
        List<User> loadedUsers = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] values = line.split("\\s+");
                if (values.length == 5) {
                    loadedUsers.add(new User(values[0], values[1], values[2], values[3], values[4]));
                }
            }
        } catch (IOException e) {
            if (!(e instanceof java.io.FileNotFoundException)) {
                System.err.println("Error reading or parsing " + USER_FILE + ": " + e.getMessage());
            } else {
                System.err.println("DataManager: " + USER_FILE + " not found. Skipping user data loading.");
            }
        }
        return loadedUsers;
    }

    private List<Aircraft> loadAircrafts() {
        List<Aircraft> loadedAircrafts = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(AIRCRAFT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] values = line.split("\\s+");
                if (values.length == 5) {
                    loadedAircrafts.add(new Aircraft(values[0], values[1], Integer.parseInt(values[2]), Integer.parseInt(values[3]), Integer.parseInt(values[4])));
                }
            }
        } catch (IOException | NumberFormatException e) {
            if (!(e instanceof java.io.FileNotFoundException)) {
                System.err.println("Error reading or parsing " + AIRCRAFT_FILE + ": " + e.getMessage());
            } else {
                System.err.println("DataManager: " + AIRCRAFT_FILE + " not found. Skipping aircraft data loading.");
            }
        }
        return loadedAircrafts;
    }

    private List<Airport> loadAirports() {
        System.out.println("DataManager: Attempting to load " + AIRPORT_FILE);
        List<Airport> loadedAirports = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(AIRPORT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] values = line.split("\\s+");
                if (values.length == 4) {
                    loadedAirports.add(new Airport(values[0], values[1], values[2], values[3]));
                }
            }
            System.out.println("DataManager: Successfully loaded " + loadedAirports.size() + " airports from " + AIRPORT_FILE);
        } catch (IOException e) {
            if (!(e instanceof java.io.FileNotFoundException)) {
                System.err.println("DataManager: Error reading or parsing " + AIRPORT_FILE + ": " + e.getMessage());
            } else {
                System.err.println("DataManager: " + AIRPORT_FILE + " not found. Skipping airport data loading.");
            }
        }
        return loadedAirports;
    }

    private List<Flight> loadFlights() {
        List<Flight> loadedFlights = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FLIGHT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] values = line.split("\\s+", 6);
                if (values.length == 6) {
                    loadedFlights.add(new Flight(values[0], values[1], values[2],
                            LocalDateTime.parse(values[3], FLIGHT_DATE_TIME_FORMATTER),
                            LocalDateTime.parse(values[4], FLIGHT_DATE_TIME_FORMATTER),
                            values[5]));
                }
            }
        } catch (IOException | java.time.format.DateTimeParseException e) {
            if (!(e instanceof java.io.FileNotFoundException)) {
                System.err.println("Error reading or parsing " + FLIGHT_FILE + ": " + e.getMessage());
            } else {
                System.err.println("DataManager: " + FLIGHT_FILE + " not found. Skipping flight data loading.");
            }
        }
        return loadedFlights;
    }

    private List<Reservation> loadReservations() {
        List<Reservation> loadedReservations = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(RESERVATION_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] values = line.split("\\s+");
                if (values.length == 6) {
                    loadedReservations.add(new Reservation(values[0], values[1], values[2], values[3],
                            Double.parseDouble(values[4]), values[5]));
                }
            }
        } catch (IOException | NumberFormatException e) {
            if (!(e instanceof java.io.FileNotFoundException)) {
                System.err.println("Error reading or parsing " + RESERVATION_FILE + ": " + e.getMessage());
            } else {
                System.err.println("DataManager: " + RESERVATION_FILE + " not found. Skipping reservation data loading.");
            }
        }
        return loadedReservations;
    }

    private List<Route> loadRoutes() {
        List<Route> loadedRoutes = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ROUTE_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] values = line.split("\\s+");
                if (values.length == 5) {
                    loadedRoutes.add(new Route(values[0], values[1], values[2],
                            Double.parseDouble(values[3]), Integer.parseInt(values[4])));
                }
            }
        } catch (IOException | NumberFormatException e) {
            if (!(e instanceof java.io.FileNotFoundException)) {
                System.err.println("Error reading or parsing " + ROUTE_FILE + ": " + e.getMessage());
            } else {
                System.err.println("DataManager: " + ROUTE_FILE + " not found. Skipping route data loading.");
            }
        }
        return loadedRoutes;
    }

    // --- Save methods ---
    private void saveUsers() {
        List<String> lines = users.stream()
                .map(user -> String.join(" ", user.getUserId(), user.getPassword(), user.getUserName(), user.getPassportNumber(), user.getPhone()))
                .collect(Collectors.toList());
        try {
            Files.write(Paths.get(USER_FILE), lines, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error saving users to file: " + e.getMessage());
        }
    }

    private void saveAircrafts() {
        List<String> lines = aircrafts.stream()
                .map(aircraft -> String.join(" ", aircraft.getAircraftId(), aircraft.getModelName(),
                        String.valueOf(aircraft.getTotalSeats()), String.valueOf(aircraft.getEconomy()), String.valueOf(aircraft.getBusiness())))
                .collect(Collectors.toList());
        try {
            Files.write(Paths.get(AIRCRAFT_FILE), lines, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error saving aircrafts to file: " + e.getMessage());
        }
    }

    private void saveAirports() {
        List<String> lines = airports.stream()
                .map(airport -> String.join(" ", airport.getAirportCode(), airport.getAirportName(), airport.getCity(), airport.getCountry()))
                .collect(Collectors.toList());
        try {
            Files.write(Paths.get(AIRPORT_FILE), lines, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error saving airports to file: " + e.getMessage());
        }
    }

    private void saveFlights() {
        List<String> lines = flights.stream()
                .map(flight -> String.join(" ", flight.getFlightId(), flight.getRouteId(), flight.getAircraftId(),
                        flight.getDepartureTime().format(FLIGHT_DATE_TIME_FORMATTER),
                        flight.getArrivalTime().format(FLIGHT_DATE_TIME_FORMATTER),
                        flight.getStatus()))
                .collect(Collectors.toList());
        try {
            Files.write(Paths.get(FLIGHT_FILE), lines, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error saving flights to file: " + e.getMessage());
        }
    }

    private void saveReservations() {
        List<String> lines = reservations.stream()
                .map(reservation -> String.join(" ", reservation.getReservationId(), reservation.getUserId(),
                        reservation.getFlightId(), reservation.getSeatNumber(),
                        String.valueOf(reservation.getFinalPrice()), reservation.getStatus()))
                .collect(Collectors.toList());
        try {
            Files.write(Paths.get(RESERVATION_FILE), lines, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error saving reservations to file: " + e.getMessage());
        }
    }

    private void saveRoutes() {
        List<String> lines = routes.stream()
                .map(route -> String.join(" ", route.getRouteId(), route.getDepartureAirportCode(),
                        route.getArrivalAirportCode(), String.valueOf(route.getPrice()), String.valueOf(route.getDuration())))
                .collect(Collectors.toList());
        try {
            Files.write(Paths.get(ROUTE_FILE), lines, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error saving routes to file: " + e.getMessage());
        }
    }

    // --- Getter methods ---
    public List<User> getUsers() {
        return users;
    }

    public List<Aircraft> getAircrafts() {
        return aircrafts;
    }

    public List<Airport> getAirports() {
        return airports;
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public List<Route> getRoutes() {
        return routes;
    }
}
