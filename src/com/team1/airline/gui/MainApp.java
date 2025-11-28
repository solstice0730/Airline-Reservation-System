package com.team1.airline.gui;

import com.team1.airline.controller.*;
import com.team1.airline.dao.*;
import com.team1.airline.dao.impl.*;
import com.team1.airline.entity.*;
import com.team1.airline.service.FlightManageable;
import com.team1.airline.service.impl.*;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * [메인 프레임]
 * 어플리케이션의 진입점이자, 화면(Panel) 간의 전환을 담당하는 컨테이너입니다.
 * 모든 DAO와 Controller를 초기화하고 각 패널에 의존성을 주입합니다.
 */
public class MainApp extends JFrame {

    private CardLayout cardLayout;
    private JPanel cardPanel;

    // --- 화면 패널들 ---
    private LoginPanel loginPanel;
    private SignUpPanel signUpPanel;
    private SearchPanel searchPanel;
    private FlightListPanel flightListPanel;
    private ConfirmPanel confirmPanel;
    private MainMenuPanel mainMenuPanel;
    private PaymentHistoryPanel paymentHistoryPanel;
    private MyPagePanel myPagePanel;

    // --- 백엔드 구성요소 (DAO, Service, Controller) ---
    private UserDAO userDAO;
    private UserController userController;
    private DataManager dataManager;
    
    private FlightDAO flightDAO;
    private RouteDAO routeDAO;
    private AircraftDAO aircraftDAO;
    private ReservationDAO reservationDAO;
    private AirportDAO airportDAO;
    
    private FlightManageable flightService;
    private UserManageable userService;
    private ReservationManageable reservationService;
    private FlightController flightController;
    private ReservationController reservationController;
    
    // 검색 상태 저장 (좌석 선택 시 사용)
    private int currentEconomySeats = 0;
    private int currentBusinessSeats = 0;

    public MainApp() {
        setTitle("항공권 예약 시스템");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 화면 중앙 시작

        // 1. 데이터 및 백엔드 로직 초기화
        initBackend();
        
        // 2. UI 패널 생성 및 배치
        initUI();

        // 3. 시작 화면 설정
        showPanel("LOGIN");
    }

    /**
     * 데이터 매니저 로드 및 MVC 패턴의 Controller/Service/DAO 조립
     */
    private void initBackend() {
        dataManager = DataManager.getInstance();
        dataManager.loadAllData();

        // User 관련
        userDAO = new UserDAOImpl();
        UserManager userManager = new UserManager(userDAO);
        userController = new UserController(userManager);

        // Flight 관련
        flightDAO = new FlightDAOImpl();
        routeDAO = new RouteDAOImpl();
        aircraftDAO = new AircraftDAOImpl();
        reservationDAO = new ReservationDAOImpl();
        airportDAO = new AirportDAOImpl();

        flightService = new FlightManager(flightDAO, routeDAO, aircraftDAO, reservationDAO);
        userService = new UserManager(userDAO);
        reservationService = new ReservationManager(reservationDAO, userDAO, flightDAO, routeDAO, airportDAO, flightService);

        // Controllers
        flightController = new FlightController(flightService);
        
        // Reservation 관련
        ReservationManager reservationManager = new ReservationManager(reservationDAO, userDAO, flightDAO, routeDAO, flightService);
        reservationController = new ReservationController(reservationManager, userController);
    }

    public UserController getUserController() { return userController; }
    public ReservationController getReservationController() { return reservationController; }

    /**
     * 모든 GUI 패널을 생성하고 CardLayout에 등록
     */
    private void initUI() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // 각 패널에 MainApp(this)을 넘겨주어 화면 전환 기능을 공유함
        loginPanel = new LoginPanel(this);
        signUpPanel = new SignUpPanel(this);
        mainMenuPanel = new MainMenuPanel(this);
        searchPanel = new SearchPanel(this);
        flightListPanel = new FlightListPanel(this);
        confirmPanel = new ConfirmPanel(this);
        paymentHistoryPanel = new PaymentHistoryPanel(this);
        myPagePanel = new MyPagePanel(this);

        // 패널 등록 (Key값으로 화면 전환)
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
    
    // --- Public Getters for Controllers ---
    public UserController getUserController() {
        return userController;
    }
    
    public ReservationController getReservationController() {
        return reservationController;
    }

    /**
     * 화면 전환 메서드
     * 특정 화면으로 이동할 때 필요한 데이터 갱신 로직을 수행합니다.
     */
    public void showPanel(String panelName) {
        cardLayout.show(cardPanel, panelName);
        JPanel current = null;

        switch (panelName) {
            case "LOGIN": current = loginPanel; break;
            case "SIGNUP": current = signUpPanel; break;
            case "MAIN": current = mainMenuPanel; break;
            
            case "SEARCH":
                // 검색 화면 진입 시 사용자 이름 갱신 (로그인 상태 반영)
                searchPanel.updateUserName(); 
                current = searchPanel;
                break;
                
            case "LIST": current = flightListPanel; break;
            case "CONFIRM": current = confirmPanel; break;
            
            case "PAYMENT_HISTORY":
                // 결제 내역 진입 시 최신 예약 데이터 로드
                if (userController.isLoggedIn()) {
                    updatePaymentHistoryData();
                }
                current = paymentHistoryPanel;
                break;
            
            case "MYPAGE":
                // 마이페이지 진입 시 최신 유저 정보 로드
                if (userController.isLoggedIn()) {
                    myPagePanel.setUserInfo(userController.getCurrentUser());
                }
                current = myPagePanel;
                break;
        }

        // 화면 크기 자동 조절 (pack)
        if (current != null) {
            cardPanel.setPreferredSize(current.getPreferredSize());
            pack();
            setLocationRelativeTo(null);
        }
    }
    
    /**
     * 예약 내역 데이터를 로드하여 PaymentHistoryPanel로 전달
     * (취소된 예약은 제외하고 필터링)
     */
    private void updatePaymentHistoryData() {
        List<Reservation> myReservations = reservationController.getMyReservations();
        java.util.List<PaymentHistoryPanel.PaymentRow> rows = new java.util.ArrayList<>();

        for (Reservation r : myReservations) {
            if ("Cancelled".equalsIgnoreCase(r.getStatus())) {
                continue; // 취소된 예약은 목록에서 제외
            }

            Flight flight = flightDAO.findByFlightId(r.getFlightId());
            if (flight != null) {
                Route route = routeDAO.findByRouteId(flight.getRouteId());
                
                String flightNo = flight.getFlightId();
                String routeStr = (route != null) ? 
                        route.getDepartureAirportCode() + " -> " + route.getArrivalAirportCode() : "Unknown";
                
                String timeInfo = flight.getDepartureTime().format(DateTimeFormatter.ofPattern("MM-dd HH:mm")) + 
                                  " ~ " + 
                                  flight.getArrivalTime().format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));
                
                String priceStr = String.format(Locale.KOREA, "%,.0f원", r.getFinalPrice());

                // [주의] PaymentHistoryPanel 컬럼 순서에 맞춰 데이터 생성 (예약번호, 항공편, 경로, 좌석 등)
                rows.add(new PaymentHistoryPanel.PaymentRow(
                        r.getReservationId(), 
                        "N/A", // 구 항공사 필드(사용안함)
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

    // --- Helper Methods (공항 코드, 이름 조회) ---
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

    /**
     * 항공권 검색 로직
     * SearchPanel -> Controller -> FlightListPanel 로 데이터 흐름 제어
     */
    public void searchFlights(String departureName, String arrivalName, String departureDateStr, String returnDateStr, int economySeats, int businessSeats) {
        // 인원수 저장 (추후 좌석 선택 시 사용)
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
        
        // 테이블 데이터 구성 (7개 컬럼: 표시용 + 숨김용)
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
                
                // 총액 계산: (이코노미 * 1) + (비즈니스 * 2)
                double totalPrice = (basePrice * economySeats) + (basePrice * 2.0 * businessSeats);
                totalPriceStr = String.format(Locale.KOREA, "%,.0f원", totalPrice);
            }

            // [데이터 구조] 항공편, 출발, 도착, 소요시간, 정가(표시용), RouteId(숨김), 총액(숨김)
            flightData[i] = new Object[] { flightCode, depTime, arrTime, duration, basePriceStr, flight.getRouteId(), totalPriceStr };
        }

        String seatSummary = searchPanel.getSeatSummaryForResult();
        flightListPanel.updateSearchCriteria(departureName, arrivalName, departureDateStr, returnDateStr, seatSummary);
        flightListPanel.populateTable(flightData);
        showPanel("LIST");
    }

    /**
     * 좌석 선택 다이얼로그 호출
     */
    public void openSeatSelection(String flightId, String routeShort, String routeLong, String departureDate, String returnDate, String time, String person, String price) {
        Flight flight = flightDAO.findByFlightId(flightId);
        if (flight == null) return;
        
        Aircraft aircraft = aircraftDAO.findByAircraftId(flight.getAircraftId());
        if (aircraft == null) {
            JOptionPane.showMessageDialog(this, "항공기 정보를 찾을 수 없습니다.", "데이터 오류", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // 이미 예약된 좌석 목록 조회
        List<Reservation> reservations = reservationDAO.findReservationsByFlightId(flightId);
        java.util.List<String> occupiedSeats = new java.util.ArrayList<>();
        for(Reservation r : reservations) {
            if("Confirmed".equals(r.getStatus()) || "Paid".equals(r.getStatus())) {
                occupiedSeats.add(r.getSeatNumber());
            }
        }

        // 좌석 선택 창 표시 (인원수 전달)
        SeatSelectionDialog dialog = new SeatSelectionDialog(
                this, aircraft, occupiedSeats, currentBusinessSeats, currentEconomySeats);
        dialog.setVisible(true);

        // 선택 완료 후 좌석 정보 받기
        String selectedSeats = dialog.getSelectedSeats();
        if (selectedSeats != null && !selectedSeats.isEmpty()) {
            confirmFlight(flightId, selectedSeats, routeShort, routeLong, departureDate, returnDate, time, person, price);
        }
    }

    /**
     * 예약 확인창으로 이동
     */
    public void confirmFlight(String flightId, String seatNumber, String routeShort, String routeLong, String departureDate, String returnDate, String time, String person, String price) {
        confirmPanel.setFlightDetails(flightId, seatNumber, routeShort, routeLong, departureDate, returnDate, time, person, price);
        showPanel("CONFIRM");
    }

    /**
     * 마일리지 적립 로직 (UserController를 통해 현재 유저 찾기)
     */
    public void addMileage(int amount) {
        User currentUser = userController.getCurrentUser();
        if (currentUser != null) {
            int currentMileage = currentUser.getMileage();
            currentUser.setMileage(currentMileage + amount);
            userDAO.updateUser(currentUser);
            System.out.println("[MainApp] 마일리지 업데이트: " + amount + " (총: " + currentUser.getMileage() + ")");
        }
    }
}