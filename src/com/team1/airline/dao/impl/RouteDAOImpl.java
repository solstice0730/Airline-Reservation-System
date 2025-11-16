package com.team1.airline.dao.impl;

import com.team1.airline.dao.RouteDAO;
import com.team1.airline.entity.Route;
import com.team1.airline.dao.impl.DataManager;

import java.util.List;
import java.util.stream.Collectors;

public class RouteDAOImpl implements RouteDAO {

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
        DataManager.getInstance().getRoutes().add(route);
    }

    @Override
    public Route findByRouteId(String routeId) {
        return DataManager.getInstance().getRoutes().stream()
                .filter(r -> r.getRouteId().equals(routeId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Route> findRoutesByAirports(String departureAirportCode, String arrivalAirportCode) {
        return DataManager.getInstance().getRoutes().stream()
                .filter(r -> r.getDepartureAirportCode().equalsIgnoreCase(departureAirportCode)
                        && r.getArrivalAirportCode().equalsIgnoreCase(arrivalAirportCode))
                .collect(Collectors.toList());
    }

    @Override
    public List<Route> findAll() {
        return DataManager.getInstance().getRoutes();
    }

    @Override
    public void updateRoute(Route route) {
        List<Route> routes = DataManager.getInstance().getRoutes();
        for (int i = 0; i < routes.size(); i++) {
            if (routes.get(i).getRouteId().equals(route.getRouteId())) {
                routes.set(i, route);
                return;
            }
        }
    }

    @Override
    public void deleteRoute(String routeId) {
        List<Route> routes = DataManager.getInstance().getRoutes();
        routes.removeIf(r -> r.getRouteId().equals(routeId));
    }
}
