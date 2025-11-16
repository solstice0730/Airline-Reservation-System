package com.team1.airline.service.impl;

// 필요한 DAO와 Entity, Service 인터페이스들을 임포트
import com.team1.airline.dao.*;
import com.team1.airline.entity.*;
import com.team1.airline.service.FlightManageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FlightManager implements FlightManageable {

    // 이 매니저는 항공편 검색을 위해 여러 DAO 부품이 필요합니다.
    private FlightDAO flightDAO;
    private RouteDAO routeDAO;
    private AircraftDAO aircraftDAO;
    private ReservationDAO reservationDAO;
    // (공항 이름 표시 등을 위해 AirportDAO도 주입받을 수 있습니다)

    /**
     * 생성자 (Constructor) - 부품들을 주입받습니다.
     */
    public FlightManager(FlightDAO flightDAO, RouteDAO routeDAO, AircraftDAO aircraftDAO, ReservationDAO reservationDAO) {
        this.flightDAO = flightDAO;
        this.routeDAO = routeDAO;
        this.aircraftDAO = aircraftDAO;
        this.reservationDAO = reservationDAO;
    }

    /**
     * R (Read) - 항공편 검색 로직 구현
     */
    @Override
    public List<Flight> searchFlights(String departureCode, String arrivalCode, LocalDate date) {
        System.out.println("FlightManager: searchFlights called with: departureCode=" + departureCode + ", arrivalCode=" + arrivalCode + ", date=" + date);

        // ★★★ 항공편 검색 '비즈니스 로직' ★★★

        // 1. DAO를 통해 '노선'을 먼저 찾습니다. (예: 인천 -> 뉴욕)
        List<Route> routes = routeDAO.findRoutesByAirports(departureCode, arrivalCode);
        System.out.println("FlightManager: Found " + (routes != null ? routes.size() : 0) + " routes for " + departureCode + " -> " + arrivalCode);

        // 2. 비즈니스 규칙: 노선이 아예 없으면 빈 리스트 반환
        if (routes == null || routes.isEmpty()) {
            System.out.println("FlightManager: 해당 노선이 없습니다.");
            return new ArrayList<>(); // 빈 리스트
        }

        // 3. 찾은 노선들(routeId)과 날짜(date)를 기준으로 '실제 항공편'을 검색합니다.
        List<Flight> availableFlights = new ArrayList<>();
        for (Route route : routes) {
            List<Flight> flightsOnRoute;
            if (date == null) {
                flightsOnRoute = flightDAO.findFlightsByRoute(route.getRouteId());
                System.out.println("FlightManager: Found " + (flightsOnRoute != null ? flightsOnRoute.size() : 0) + " flights on route " + route.getRouteId() + " for ALL dates (date was null).");
            } else {
                flightsOnRoute = flightDAO.findFlightsByRouteAndDate(route.getRouteId(), date);
                System.out.println("FlightManager: Found " + (flightsOnRoute != null ? flightsOnRoute.size() : 0) + " flights on route " + route.getRouteId() + " for date " + date);
            }
            if (flightsOnRoute != null) {
                availableFlights.addAll(flightsOnRoute);
            }
        }
        System.out.println("FlightManager: Total available flights before filtering status: " + availableFlights.size());

        // 4. 비즈니스 규칙: "Scheduled" (예약 가능) 상태인 항공편만 필터링
        List<Flight> finalFlights = availableFlights.stream()
                .filter(flight -> "예약 가능".equals(flight.getStatus())) // Changed from "Scheduled" to "예약 가능"
                .collect(Collectors.toList());
        System.out.println("FlightManager: Final flights after status filtering: " + finalFlights.size());
        return finalFlights;
    }

    @Override
    public Flight getFlightById(String flightId) {
        // DAO에게 단순 전달
        return flightDAO.findByFlightId(flightId);
    }

    /**
     * R (Read) - 좌석 현황 로직 구현
     */
    @Override
    public Map<String, Integer> getSeatAvailability(String flightId) {
        Map<String, Integer> seatInfo = new HashMap<>();

        // 1. 항공편 정보를 가져옵니다.
        Flight flight = flightDAO.findByFlightId(flightId);
        if (flight == null) return seatInfo; // 빈 맵

        // 2. 항공편에 연결된 '항공기' 정보를 가져옵니다. (총 좌석 수 확인)
        Aircraft aircraft = aircraftDAO.findByAircraftId(flight.getAircraftId());
        if (aircraft == null) return seatInfo;

        // ★★★ 좌석 '비즈니스 로직' ★★★
        int totalSeats = aircraft.getTotalSeats();
        
        // 3. 이 항공편에 '예약된' 내역을 모두 가져옵니다.
        List<Reservation> reservations = reservationDAO.findReservationsByFlightId(flightId);
        
        // 4. '확정' 또는 '결제완료' 상태인 예약만 카운트합니다.
        int reservedSeats = (int) reservations.stream()
                .filter(r -> "Confirmed".equals(r.getStatus()) || "Paid".equals(r.getStatus()))
                .count();

        int availableSeats = totalSeats - reservedSeats;

        seatInfo.put("total", totalSeats);
        seatInfo.put("reserved", reservedSeats);
        seatInfo.put("available", availableSeats);

        return seatInfo;
    }
}