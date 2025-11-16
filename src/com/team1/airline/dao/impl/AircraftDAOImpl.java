package com.team1.airline.dao.impl;

import com.team1.airline.dao.AircraftDAO;
import com.team1.airline.entity.Aircraft;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AircraftDAOImpl implements AircraftDAO {

    private String aircraftFilePath = "data/Aircraft.txt";

    private String aircraftToLine(Aircraft aircraft) {
        return String.join(" ",
                aircraft.getAircraftId(),
                aircraft.getModelName(),
                String.valueOf(aircraft.getTotalSeats()),
                String.valueOf(aircraft.getEconomy()),
                String.valueOf(aircraft.getBusiness()));
    }

    @Override
    public void saveAircraft(Aircraft aircraft) {
        String aircraftLine = aircraftToLine(aircraft) + System.lineSeparator();
        try {
            Files.write(Paths.get(aircraftFilePath), aircraftLine.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        } catch (IOException e) {
            System.err.println("Error saving aircraft to file: " + e.getMessage());
        }
    }

    @Override
    public Aircraft findByAircraftId(String aircraftId) {
        return readAllAircraftsFromFile().stream()
                .filter(a -> a.getAircraftId().equals(aircraftId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Aircraft> findAll() {
        return readAllAircraftsFromFile();
    }

    @Override
    public void updateAircraft(Aircraft aircraft) {
        List<Aircraft> aircrafts = readAllAircraftsFromFile();
        List<String> lines = aircrafts.stream()
                .map(a -> a.getAircraftId().equals(aircraft.getAircraftId()) ? aircraftToLine(aircraft) : aircraftToLine(a))
                .collect(Collectors.toList());
        try {
            Files.write(Paths.get(aircraftFilePath), lines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error updating aircraft in file: " + e.getMessage());
        }
    }

    @Override
    public void deleteAircraft(String aircraftId) {
        List<Aircraft> aircrafts = readAllAircraftsFromFile();
        List<String> lines = aircrafts.stream()
                .filter(a -> !a.getAircraftId().equals(aircraftId))
                .map(this::aircraftToLine)
                .collect(Collectors.toList());
        try {
            Files.write(Paths.get(aircraftFilePath), lines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error deleting aircraft from file: " + e.getMessage());
        }
    }

    private List<Aircraft> readAllAircraftsFromFile() {
        List<Aircraft> aircrafts = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(aircraftFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] values = line.split("\\s+");
                if (values.length == 5) {
                    Aircraft aircraft = new Aircraft(
                            values[0], // aircraftId
                            values[1], // modelName
                            Integer.parseInt(values[2]), // totalSeats
                            Integer.parseInt(values[3]), // economy
                            Integer.parseInt(values[4])  // business
                    );
                    aircrafts.add(aircraft);
                }
            }
        } catch (IOException | NumberFormatException e) {
            if (!(e instanceof java.io.FileNotFoundException)) {
                System.err.println("Error reading or parsing Aircraft.txt: " + e.getMessage());
            }
        }
        return aircrafts;
    }
}
