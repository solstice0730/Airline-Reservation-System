package com.team1.airline.dao.impl;

import com.team1.airline.dao.AirportDAO;
import com.team1.airline.entity.Airport;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AirportDAOImpl implements AirportDAO {

    private String airportFilePath = "data/Airport.txt";

    private String airportToLine(Airport airport) {
        return String.join(" ",
                airport.getAirportCode(),
                airport.getAirportName(),
                airport.getCity(),
                airport.getCountry());
    }

    @Override
    public void saveAirport(Airport airport) {
        String airportLine = airportToLine(airport) + System.lineSeparator();
        try {
            Files.write(Paths.get(airportFilePath), airportLine.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        } catch (IOException e) {
            System.err.println("Error saving airport to file: " + e.getMessage());
        }
    }

    @Override
    public Airport findByAirportCode(String airportCode) {
        return readAllAirportsFromFile().stream()
                .filter(a -> a.getAirportCode().equals(airportCode))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Airport> findAll() {
        return readAllAirportsFromFile();
    }

    @Override
    public void updateAirport(Airport airport) {
        List<Airport> airports = readAllAirportsFromFile();
        List<String> lines = airports.stream()
                .map(a -> a.getAirportCode().equals(airport.getAirportCode()) ? airportToLine(airport) : airportToLine(a))
                .collect(Collectors.toList());
        try {
            Files.write(Paths.get(airportFilePath), lines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error updating airport in file: " + e.getMessage());
        }
    }

    @Override
    public void deleteAirport(String airportCode) {
        List<Airport> airports = readAllAirportsFromFile();
        List<String> lines = airports.stream()
                .filter(a -> !a.getAirportCode().equals(airportCode))
                .map(this::airportToLine)
                .collect(Collectors.toList());
        try {
            Files.write(Paths.get(airportFilePath), lines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error deleting airport from file: " + e.getMessage());
        }
    }

    private List<Airport> readAllAirportsFromFile() {
        List<Airport> airports = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(airportFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] values = line.split("\\s+");
                if (values.length == 4) {
                    Airport airport = new Airport(
                            values[0], // airportCode
                            values[1], // airportName
                            values[2], // city
                            values[3]  // country
                    );
                    airports.add(airport);
                }
            }
        } catch (IOException e) {
            if (!(e instanceof java.io.FileNotFoundException)) {
                System.err.println("Error reading or parsing Airport.txt: " + e.getMessage());
            }
        }
        return airports;
    }
}
