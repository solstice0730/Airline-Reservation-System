package com.team1.airline.dao.impl;

import com.team1.airline.dao.AirportDAO;
import com.team1.airline.entity.Airport;
import com.team1.airline.dao.impl.DataManager;

import java.util.List;
import java.util.stream.Collectors;

public class AirportDAOImpl implements AirportDAO {

    private String airportToLine(Airport airport) {
        return String.join(" ",
                airport.getAirportCode(),
                airport.getAirportName(),
                airport.getCity(),
                airport.getCountry());
    }

    @Override
    public void saveAirport(Airport airport) {
        DataManager.getInstance().getAirports().add(airport);
    }

    @Override
    public Airport findByAirportCode(String airportCode) {
        return DataManager.getInstance().getAirports().stream()
                .filter(a -> a.getAirportCode().equals(airportCode))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Airport> findAll() {
        return DataManager.getInstance().getAirports();
    }

    @Override
    public void updateAirport(Airport airport) {
        List<Airport> airports = DataManager.getInstance().getAirports();
        for (int i = 0; i < airports.size(); i++) {
            if (airports.get(i).getAirportCode().equals(airport.getAirportCode())) {
                airports.set(i, airport);
                return;
            }
        }
    }

    @Override
    public void deleteAirport(String airportCode) {
        List<Airport> airports = DataManager.getInstance().getAirports();
        airports.removeIf(a -> a.getAirportCode().equals(airportCode));
    }
}
