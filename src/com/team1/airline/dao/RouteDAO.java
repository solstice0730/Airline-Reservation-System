package com.team1.airline.dao;

import com.team1.airline.entity.Route;
import java.util.List;

public interface RouteDAO {
    void saveRoute(Route route);
    Route findById(String routeId);
    List<Route> findAll();
    void updateRoute(Route route);
    void deleteRoute(String routeId);
    List<Route> findByOriginAndDestination(String originAirportCode, String destinationAirportCode);
}
