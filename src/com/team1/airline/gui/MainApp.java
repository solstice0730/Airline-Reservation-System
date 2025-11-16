package com.team1.airline.gui;

import com.team1.airline.controller.FlightController;
import com.team1.airline.dao.AircraftDAO;
import com.team1.airline.dao.AirportDAO; // Import AirportDAO
import com.team1.airline.dao.FlightDAO;
import com.team1.airline.dao.ReservationDAO;
import com.team1.airline.dao.RouteDAO;
import com.team1.airline.dao.impl.*;
import com.team1.airline.entity.Airport; // Import Airport entity
import com.team1.airline.entity.Flight;
import com.team1.airline.entity.Route;
import com.team1.airline.service.FlightManageable;
import com.team1.airline.service.impl.FlightManager;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional; // Import Optional

class FlightData {
    String airline, departureTime, arrivalTime, duration, price;
    public FlightData(String a, String dt, String at, String d, String p) {
        airline = a; departureTime = dt; arrivalTime = at; duration = d; price = p;
    }
}

public class MainApp extends JFrame {

    private CardLayout cardLayout;
    private JPanel cardPanel;

    private SearchPanel searchPanel;
    private FlightListPanel flightListPanel;
    private ConfirmPanel confirmPanel;

    // Backend instances
    private DataManager dataManager;
    private FlightDAO flightDAO;
    private RouteDAO routeDAO;
    private AircraftDAO aircraftDAO;
    private ReservationDAO reservationDAO;
    private AirportDAO airportDAO; // Declare AirportDAO
    private FlightManageable flightService;
    private FlightController flightController;

    public MainApp() {
        setTitle("항공권 예약 시스템 (프로토타입)");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize backend components
        dataManager = DataManager.getInstance();
        dataManager.loadAllData(); // Load data at startup

        flightDAO = new FlightDAOImpl();
        routeDAO = new RouteDAOImpl();
        aircraftDAO = new AircraftDAOImpl();
        reservationDAO = new ReservationDAOImpl();
        airportDAO = new AirportDAOImpl(); // Initialize AirportDAO
        flightService = new FlightManager(flightDAO, routeDAO, aircraftDAO, reservationDAO);
        flightController = new FlightController(flightService);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        searchPanel = new SearchPanel(this);
        flightListPanel = new FlightListPanel(this);
        confirmPanel = new ConfirmPanel(this);

        cardPanel.add(searchPanel, "SEARCH");
        cardPanel.add(flightListPanel, "LIST");
        cardPanel.add(confirmPanel, "CONFIRM");

        add(cardPanel);
        showPanel("SEARCH");
    }

    public void showPanel(String panelName) {
        cardLayout.show(cardPanel, panelName);
    }

    // Helper method to convert airport name to airport code
    private String getAirportCode(String airportName) {
        Optional<Airport> airport = airportDAO.findAll().stream()
                .filter(a -> a.getAirportName().equalsIgnoreCase(airportName))
                .findFirst();
        return airport.map(Airport::getAirportCode).orElse(null);
    }

    public void searchFlights(String departureName, String arrivalName, String departureDateStr, String returnDateStr) {
        System.out.println("MainApp: searchFlights called with: departureName=" + departureName + ", arrivalName=" + arrivalName + ", departureDateStr=" + departureDateStr);

        // Convert airport names to codes
        String departureCode = getAirportCode(departureName);
        String arrivalCode = getAirportCode(arrivalName);

        System.out.println("MainApp: Converted codes: departureCode=" + departureCode + ", arrivalCode=" + arrivalCode);

        if (departureCode == null) {
            JOptionPane.showMessageDialog(this, "출발지 '" + departureName + "'을(를) 찾을 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (arrivalCode == null) {
            JOptionPane.showMessageDialog(this, "목적지 '" + arrivalName + "'을(를) 찾을 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate departureDate = null;
        if (departureDateStr != null && !departureDateStr.trim().isEmpty()) {
            try {
                // Assuming departureDateStr is in "M/d" format (e.g., "1/15")
                // Prepend current year for full date
                int currentYear = LocalDate.now().getYear();
                String fullDepartureDateStr = currentYear + "/" + departureDateStr;
                departureDate = LocalDate.parse(fullDepartureDateStr, DateTimeFormatter.ofPattern("yyyy/M/d"));
                System.out.println("MainApp: Parsed departureDate=" + departureDate);
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "출발 날짜 형식이 올바르지 않습니다 (예: 1/15).", "날짜 형식 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            System.out.println("MainApp: Departure date is empty, searching without date constraint.");
        }

        List<Flight> flights = flightController.searchFlights(departureCode, arrivalCode, departureDate);
        System.out.println("MainApp: flightController.searchFlights returned " + (flights != null ? flights.size() : 0) + " flights.");

        Object[][] flightData = new Object[flights.size()][5]; // airline, departureTime, arrivalTime, duration, price

        for (int i = 0; i < flights.size(); i++) {
            Flight flight = flights.get(i);
            Route route = routeDAO.findByRouteId(flight.getRouteId()); // Get route to extract duration and price

            String airline = flight.getFlightId().substring(0, Math.min(flight.getFlightId().length(), 3)); // Placeholder for airline
            String depTime = flight.getDepartureTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            String arrTime = flight.getArrivalTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            String duration = (route != null ? route.getDuration() + "시간" : "N/A");
            String price = (route != null ? String.format(Locale.KOREA, "%,.0f원", route.getPrice()) : "N/A");

            flightData[i] = new Object[]{airline, depTime, arrTime, duration, price};
        }

        flightListPanel.updateSearchCriteria(departureName, arrivalName, departureDateStr, returnDateStr); // Pass names back to GUI
        flightListPanel.populateTable(flightData);
        showPanel("LIST");
    }

    // FlightListPanel에서 선택된 항공편 정보와 함께 '가는 날', '오는 날' 정보를 받음
    public void confirmFlight(String route, String departureDate, String returnDate, String time, String person, String price) {
        // [1] ConfirmPanel에 선택된 항공권 정보 전달
        confirmPanel.setFlightDetails(route, departureDate, returnDate, time, person, price); // *** returnDate 추가 ***

        // [2] 화면 전환
        showPanel("CONFIRM");
    }

    public List<String> getAllAirportNames() {
        return airportDAO.findAll().stream()
                .map(Airport::getAirportName)
                .collect(java.util.stream.Collectors.toList());
    }

}