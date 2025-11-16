package com.team1.airline.dao.impl;

import com.team1.airline.dao.RouteDAO;
import com.team1.airline.entity.Route;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RouteDAOImpl implements RouteDAO {

    private String routeFilePath = "data/Route.txt";

    private String routeToLine(Route route) {
        return String.join(" ",
                route.getRouteId(),
                route.getDepartureAirportCode(),
                route.getArrivalAirportCode(),
                String.valueOf(route.getPrice()),
                String.valueOf(route.getDuration()));
    }

    @Override
    public void saveRoute(Route route) {
        String routeLine = routeToLine(route) + System.lineSeparator();
        try {
            Files.write(Paths.get(routeFilePath), routeLine.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        } catch (IOException e) {
            System.err.println("Error saving route to file: " + e.getMessage());
        }
    }

    @Override
    public Route findByRouteId(String routeId) {
        return readAllRoutesFromFile().stream()
                .filter(r -> r.getRouteId().equals(routeId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Route> findRoutesByAirports(String departureAirportCode, String arrivalAirportCode) {
        return readAllRoutesFromFile().stream()
                .filter(r -> r.getDepartureAirportCode().equalsIgnoreCase(departureAirportCode)
                        && r.getArrivalAirportCode().equalsIgnoreCase(arrivalAirportCode))
                .collect(Collectors.toList());
    }

    @Override
    public List<Route> findAll() {
        return readAllRoutesFromFile();
    }

    @Override
    public void updateRoute(Route route) {
        List<Route> routes = readAllRoutesFromFile();
        List<String> lines = routes.stream()
                .map(r -> r.getRouteId().equals(route.getRouteId()) ? routeToLine(route) : routeToLine(r))
                .collect(Collectors.toList());
        try {
            Files.write(Paths.get(routeFilePath), lines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error updating route in file: " + e.getMessage());
        }
    }

    @Override
    public void deleteRoute(String routeId) {
        List<Route> routes = readAllRoutesFromFile();
        List<String> lines = routes.stream()
                .filter(r -> !r.getRouteId().equals(routeId))
                .map(this::routeToLine)
                .collect(Collectors.toList());
        try {
            Files.write(Paths.get(routeFilePath), lines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error deleting route from file: " + e.getMessage());
        }
    }

    private List<Route> readAllRoutesFromFile() {
        List<Route> routes = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(routeFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // Skip empty lines
                String[] values = line.split("\\s+");
                if (values.length == 5) {
                    Route route = new Route(
                            values[0], // routeId
                            values[1], // departureAirportCode
                            values[2], // arrivalAirportCode
                            Double.parseDouble(values[3]), // price
                            Integer.parseInt(values[4]) // duration
                    );
                    routes.add(route);
                }
            }
        } catch (IOException | NumberFormatException e) {
            if (!(e instanceof java.io.FileNotFoundException)) {
                System.err.println("Error reading or parsing Route.txt: " + e.getMessage());
            }
        }
        return routes;
    }
}
