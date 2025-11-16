package com.team1.airline.dao;

import com.team1.airline.entity.Flight;
import java.time.LocalDate;
import java.util.List;

public interface FlightDAO {
    void saveFlight(Flight flight);
    Flight findByFlightId(String flightId);
    List<Flight> findFlightsByRouteAndDate(String routeId, LocalDate date);
    List<Flight> findAll();
    void updateFlight(Flight flight);
    void deleteFlight(String flightId);
    List<Flight> findFlightsByRoute(String routeId);
}