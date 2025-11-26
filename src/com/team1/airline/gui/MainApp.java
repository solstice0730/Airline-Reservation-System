package com.team1.airline.gui;

import com.team1.airline.controller.FlightController;
import com.team1.airline.controller.ReservationController;
import com.team1.airline.controller.UserController;
import com.team1.airline.dao.*;
import com.team1.airline.dao.impl.*;
import com.team1.airline.entity.*;
import com.team1.airline.service.FlightManageable;
import com.team1.airline.service.impl.FlightManager;
import com.team1.airline.service.impl.ReservationManager;
import com.team1.airline.service.impl.UserManager;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class MainApp extends JFrame {

    private CardLayout cardLayout;
    private JPanel cardPanel;

    private LoginPanel loginPanel;
    private SignUpPanel signUpPanel;
    private SearchPanel searchPanel;
    private FlightListPanel flightListPanel;
    private ConfirmPanel confirmPanel;
    private MainMenuPanel mainMenuPanel;
    private PaymentHistoryPanel paymentHistoryPanel;
    private MyPagePanel myPagePanel;

    private UserDAO userDAO;
    private UserController userController;
    private DataManager dataManager;
    private FlightDAO flightDAO;
    private RouteDAO routeDAO;
    private AircraftDAO aircraftDAO;
    private ReservationDAO reservationDAO;
    private AirportDAO airportDAO;
    private FlightManageable flightService;
    private FlightController flightController;
    private ReservationController reservationController;
    
    // [추가] 검색 시 선택했던 좌석 수 저장용
    private int currentEconomySeats = 0;
    private int currentBusinessSeats = 0;

    public MainApp() {
        setTitle("항공권 예약 시스템 (프로토타입)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initBackend();
        initUI();

        showPanel("LOGIN");
    }

    private void initBackend() {
        dataManager = DataManager.getInstance();
        dataManager.loadAllData();

        userDAO = new UserDAOImpl();
        UserManager userManager = new UserManager(userDAO);
        userController = new UserController(userManager);

        flightDAO = new FlightDAOImpl();
        routeDAO = new RouteDAOImpl();
        aircraftDAO = new AircraftDAOImpl();
        reservationDAO = new ReservationDAOImpl();
        airportDAO = new AirportDAOImpl();

        flightService = new FlightManager(flightDAO, routeDAO, aircraftDAO, reservationDAO);
        flightController = new FlightController(flightService);
        
        ReservationManager reservationManager = new ReservationManager(reservationDAO, userDAO, flightDAO, routeDAO, flightService);
        reservationController = new ReservationController(reservationManager, userController);
    }

    public UserController getUserController() {
        return userController;
    }
    
    public ReservationController getReservationController() {
        return reservationController;
    }

    private void initUI() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        loginPanel = new LoginPanel(this);
        signUpPanel = new SignUpPanel(this);
        mainMenuPanel = new MainMenuPanel(this);
        searchPanel = new SearchPanel(this);
        flightListPanel = new FlightListPanel(this);
        confirmPanel = new ConfirmPanel(this);
        paymentHistoryPanel = new PaymentHistoryPanel(this);
        myPagePanel = new MyPagePanel(this);

        cardPanel.add(loginPanel, "LOGIN");
        cardPanel.add(signUpPanel, "SIGNUP");
        cardPanel.add(mainMenuPanel, "MAIN");
        cardPanel.add(searchPanel, "SEARCH");
        cardPanel.add(flightListPanel, "LIST");
        cardPanel.add(confirmPanel, "CONFIRM");
        cardPanel.add(paymentHistoryPanel, "PAYMENT_HISTORY");
        cardPanel.add(myPagePanel, "MYPAGE");

        add(cardPanel);
    }

    public void showPanel(String panelName) {
        cardLayout.show(cardPanel, panelName);
        JPanel current = null;

        switch (panelName) {
            case "LOGIN": current = loginPanel; break;
            case "SIGNUP": current = signUpPanel; break;
            case "MAIN": current = mainMenuPanel; break;
            case "SEARCH":
                searchPanel.updateUserName(); // [추가] 검색 화면으로 갈 때 이름 갱신
                current = searchPanel;
                break;
            case "LIST": current = flightListPanel; break;
            case "CONFIRM": current = confirmPanel; break;
            case "PAYMENT_HISTORY":
                if (userController.isLoggedIn()) {
                    updatePaymentHistoryData();
                }
                current = paymentHistoryPanel;
                break;
            case "MYPAGE":
                if (userController.isLoggedIn()) {
                    myPagePanel.setUserInfo(userController.getCurrentUser());
                }
                current = myPagePanel;
                break;
        }

        if (current != null) {
            cardPanel.setPreferredSize(current.getPreferredSize());
            pack();
            setLocationRelativeTo(null);
        }
    }
    
    private void updatePaymentHistoryData() {
        List<Reservation> myReservations = reservationController.getMyReservations();
        java.util.List<PaymentHistoryPanel.PaymentRow> rows = new java.util.ArrayList<>();

        for (Reservation r : myReservations) {
            if ("Cancelled".equalsIgnoreCase(r.getStatus())) {
                continue; 
            }

            Flight flight = flightDAO.findByFlightId(r.getFlightId());
            if (flight != null) {
                Route route = routeDAO.findByRouteId(flight.getRouteId());
                
                String airline = flight.getFlightId().substring(0, Math.min(flight.getFlightId().length(), 3));
                String flightNo = flight.getFlightId();
                String routeStr = (route != null) ? 
                        route.getDepartureAirportCode() + " -> " + route.getArrivalAirportCode() : "Unknown";
                
                String timeInfo = flight.getDepartureTime().format(DateTimeFormatter.ofPattern("MM-dd HH:mm")) + 
                                  " ~ " + 
                                  flight.getArrivalTime().format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));
                
                String priceStr = String.format(Locale.KOREA, "%,.0f원", r.getFinalPrice());

                rows.add(new PaymentHistoryPanel.PaymentRow(
                        r.getReservationId(), 
                        airline,
                        flightNo,
                        routeStr,
                        timeInfo,
                        r.getSeatNumber(),
                        priceStr
                ));
            }
        }
        paymentHistoryPanel.setPaymentRows(rows);
    }

    private String getAirportCode(String airportName) {
        Optional<Airport> airport = airportDAO.findAll().stream()
                .filter(a -> a.getAirportName().equalsIgnoreCase(airportName)).findFirst();
        return airport.map(Airport::getAirportCode).orElse(null);
    }

    public List<String> getAllAirportNames() {
        return airportDAO.findAll().stream()
                .map(Airport::getAirportName)
                .collect(java.util.stream.Collectors.toList());
    }

    public void searchFlights(String departureName, String arrivalName, String departureDateStr, String returnDateStr, int economySeats, int businessSeats) {
    	// [추가] 인원수 저장 (나중에 좌석 선택할 때 쓰임)
        this.currentEconomySeats = economySeats;
        this.currentBusinessSeats = businessSeats;
    	String departureCode = getAirportCode(departureName);
        String arrivalCode = getAirportCode(arrivalName);

        if (departureCode == null || arrivalCode == null) {
            JOptionPane.showMessageDialog(this, "출발지와 도착지를 모두 선택해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate departureDate = null;
        if (departureDateStr != null && !departureDateStr.trim().isEmpty() && !departureDateStr.contains("날짜")) {
            try {
                departureDate = LocalDate.parse(departureDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "날짜 형식이 올바르지 않습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        List<Flight> flights = flightController.searchFlights(departureCode, arrivalCode, departureDate);
        Object[][] flightData = new Object[flights.size()][7];

        for (int i = 0; i < flights.size(); i++) {
            Flight flight = flights.get(i);
            Route route = routeDAO.findByRouteId(flight.getRouteId());

            String flightCode = flight.getFlightId();
            String depTime = flight.getDepartureTime().format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));
            String arrTime = flight.getArrivalTime().format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));
            String duration = (route != null ? route.getDuration() + "분" : "N/A");
            
            String basePriceStr = "N/A";
            String totalPriceStr = "N/A";
            
            if (route != null) {
                double basePrice = route.getPrice();
                basePriceStr = String.format(Locale.KOREA, "%,.0f원", basePrice);
                double totalPrice = (basePrice * economySeats) + (basePrice * 2.0 * businessSeats);
                totalPriceStr = String.format(Locale.KOREA, "%,.0f원", totalPrice);
            }

            flightData[i] = new Object[] { flightCode, depTime, arrTime, duration, basePriceStr, flight.getRouteId(), totalPriceStr };
        }

        String seatSummary = searchPanel.getSeatSummaryForResult();
        flightListPanel.updateSearchCriteria(departureName, arrivalName, departureDateStr, returnDateStr, seatSummary);
        flightListPanel.populateTable(flightData);
        showPanel("LIST");
    }

    /**
     * [추가] 좌석 선택 다이얼로그 호출
     */
    public void openSeatSelection(String flightId, String routeShort, String routeLong, String departureDate, String returnDate, String time, String person, String price) {
        Flight flight = flightDAO.findByFlightId(flightId);
        if (flight == null) return;
        Aircraft aircraft = aircraftDAO.findByAircraftId(flight.getAircraftId());
        
     // [수정] 항공기 정보가 없을 경우 에러 메시지를 띄우고 중단 (NullPointerException 방지)
        if (aircraft == null) {
            JOptionPane.showMessageDialog(this, 
                "항공기 정보(" + flight.getAircraftId() + ")를 찾을 수 없습니다.\n관리자에게 문의하세요.", 
                "데이터 오류", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        List<Reservation> reservations = reservationDAO.findReservationsByFlightId(flightId);
        java.util.List<String> occupiedSeats = new java.util.ArrayList<>();
        for(Reservation r : reservations) {
            if("Confirmed".equals(r.getStatus()) || "Paid".equals(r.getStatus())) {
                occupiedSeats.add(r.getSeatNumber());
            }
        }

        // [수정] 다이얼로그 생성 시 인원수(currentBusinessSeats, currentEconomySeats) 전달
        SeatSelectionDialog dialog = new SeatSelectionDialog(
                this, aircraft, occupiedSeats, currentBusinessSeats, currentEconomySeats);
        
        dialog.setVisible(true);

        // 선택된 좌석들 (예: "1A, 1B")
        String selectedSeats = dialog.getSelectedSeats();
        
        if (selectedSeats != null && !selectedSeats.isEmpty()) {
            confirmFlight(flightId, selectedSeats, routeShort, routeLong, departureDate, returnDate, time, person, price);
        }
    }

    /**
     * [수정] 좌석 번호(seatNumber) 매개변수 추가
     */
    public void confirmFlight(String flightId, String seatNumber, String routeShort, String routeLong, String departureDate, String returnDate, String time, String person, String price) {
        confirmPanel.setFlightDetails(flightId, seatNumber, routeShort, routeLong, departureDate, returnDate, time, person, price);
        showPanel("CONFIRM");
    }

    public void addMileage(int amount) {
        User currentUser = userController.getCurrentUser();
        if (currentUser != null) {
            int currentMileage = currentUser.getMileage();
            currentUser.setMileage(currentMileage + amount);
            userDAO.updateUser(currentUser);
            System.out.println("[MainApp] 마일리지 적립 완료: +" + amount + " (현재 총: " + currentUser.getMileage() + ")");
        } else {
            System.out.println("[MainApp] 오류: 로그인된 사용자가 없어 마일리지를 적립할 수 없습니다.");
        }
    }
}