package com.team1.airline.dao.impl;

import com.team1.airline.dao.FlightDAO;
import com.team1.airline.entity.Flight;
import com.team1.airline.dao.impl.DataManager;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class FlightDAOImpl implements FlightDAO {

    private String flightToLine(Flight flight) {
        return String.join(" ",
                flight.getFlightId(),
                flight.getRouteId(),
                flight.getAircraftId(),
                flight.getDepartureTime().format(DataManager.FLIGHT_DATE_TIME_FORMATTER),
                flight.getArrivalTime().format(DataManager.FLIGHT_DATE_TIME_FORMATTER),
                flight.getStatus());
    }

    @Override
    public void saveFlight(Flight flight) {
        DataManager.getInstance().getFlights().add(flight);
    }

    @Override
    public Flight findByFlightId(String flightId) {
        return DataManager.getInstance().getFlights().stream()
                .filter(f -> f.getFlightId().equals(flightId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Flight> findFlightsByRouteAndDate(String routeId, LocalDate date) {
        return DataManager.getInstance().getFlights().stream()
                .filter(f -> f.getRouteId().equals(routeId) && f.getDepartureTime().toLocalDate().equals(date))
                .collect(Collectors.toList());
    }

    @Override
    public List<Flight> findAll() {
        return DataManager.getInstance().getFlights();
    }

    @Override
    public void updateFlight(Flight flight) {
        List<Flight> flights = DataManager.getInstance().getFlights();
        for (int i = 0; i < flights.size(); i++) {
            if (flights.get(i).getFlightId().equals(flight.getFlightId())) {
                flights.set(i, flight);
                return;
            }
        }
    }

    @Override
    public void deleteFlight(String flightId) {
        List<Flight> flights = DataManager.getInstance().getFlights();
        flights.removeIf(f -> f.getFlightId().equals(flightId));
    }

    @Override
    public List<Flight> findFlightsByRoute(String routeId) {
        return DataManager.getInstance().getFlights().stream()
                .filter(f -> f.getRouteId().equals(routeId))
                .collect(Collectors.toList());
    }
}
