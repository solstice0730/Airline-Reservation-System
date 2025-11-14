package com.team1.airline.dao;

import com.team1.airline.entity.Flight;
import java.time.LocalDate;
import java.util.List;

public interface FlightDAO {
    void saveFlight(Flight flight);
    Flight findById(String flightId);
    List<Flight> findAll();
    void updateFlight(Flight flight);
    void deleteFlight(String flightId);
    List<Flight> findByOriginAndDestination(String originCode, String destinationCode);
    List<Flight> findByOriginAndDestinationAndDepartureDate(String originCode, String destinationCode, LocalDate departureDate);
    List<Flight> findByFlightNumber(String flightNumber);
}
