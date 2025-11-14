package com.team1.airline.dao;

import com.team1.airline.entity.Airport;
import java.util.List;

public interface AirportDAO {
    void saveAirport(Airport airport);
    Airport findById(String airportCode);
    List<Airport> findAll();
    void updateAirport(Airport airport);
    void deleteAirport(String airportCode);
    List<Airport> findByName(String name);
}
