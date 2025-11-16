package com.team1.airline.dao;

import com.team1.airline.entity.Route;
import java.util.List;

public interface RouteDAO {
    void saveRoute(Route route);
    Route findByRouteId(String routeId);
    List<Route> findRoutesByAirports(String departureAirportCode, String arrivalAirportCode);
    List<Route> findAll();
    void updateRoute(Route route);
    void deleteRoute(String routeId);
}
