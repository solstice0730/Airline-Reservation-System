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

/**
 * [메인 애플리케이션 클래스]
 * - JFrame을 상속받아 전체 윈도우 프레임을 구성합니다.
 * - CardLayout을 사용하여 로그인, 검색, 예약 등 여러 패널 간의 화면 전환을 관리합니다.
 * - 백엔드(Controller, Service, DAO) 객체들을 초기화하고 각 패널에 의존성을 주입합니다.
 */
public class MainApp extends JFrame {

    private CardLayout cardLayout;
    private JPanel cardPanel;

    // --- 화면 패널들 (View) ---
    private LoginPanel loginPanel;
    private SignUpPanel signUpPanel;
    private SearchPanel searchPanel;
    private FlightListPanel flightListPanel;
    private ConfirmPanel confirmPanel;
    private MainMenuPanel mainMenuPanel;
    private PaymentHistoryPanel paymentHistoryPanel;
    private MyPagePanel myPagePanel;

    // --- 백엔드 구성요소 (Controller & DAO) ---
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

    // 검색 시 선택한 인원 수 상태 저장 (좌석 선택 단계에서 필요)
    private int currentEconomySeats = 0;
    private int currentBusinessSeats = 0;

    public MainApp() {
        setTitle("항공권 예약 시스템");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // [UI 설정] 안티앨리어싱 활성화 (텍스트/그래픽 깨짐 방지)
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        // 전체 배경색 설정
        getContentPane().setBackground(UITheme.BG_COLOR);
        
        setLocationRelativeTo(null); // 화면 중앙에 창 띄우기

        initBackend(); // 백엔드 초기화
        initUI();      // UI 패널 초기화

        showPanel("LOGIN"); // 앱 시작 시 로그인 화면 표시
    }

    /**
     * 백엔드 시스템 초기화 메서드
     * - 데이터 로드, DAO 생성, Service 및 Controller 조립(Dependency Injection)을 수행합니다.
     */
    private void initBackend() {
        dataManager = DataManager.getInstance();
        dataManager.loadAllData(); // CSV 등 데이터 파일 로드

        // User 관련 컴포넌트 초기화
        userDAO = new UserDAOImpl();
        UserManager userManager = new UserManager(userDAO);
        userController = new UserController(userManager);

        // Flight 관련 컴포넌트 초기화
        flightDAO = new FlightDAOImpl();
        routeDAO = new RouteDAOImpl();
        aircraftDAO = new AircraftDAOImpl();
        reservationDAO = new ReservationDAOImpl();
        airportDAO = new AirportDAOImpl();

        flightService = new FlightManager(flightDAO, routeDAO, aircraftDAO, reservationDAO);
        flightController = new FlightController(flightService);
        
        // Reservation 관련 컴포넌트 초기화
        ReservationManager reservationManager = new ReservationManager(reservationDAO, userDAO, flightDAO, routeDAO, flightService);
        reservationController = new ReservationController(reservationManager, userController);
    }

    // 각 패널에서 백엔드 기능에 접근할 수 있도록 Getter 제공
    public UserController getUserController() { return userController; }
    public ReservationController getReservationController() { return reservationController; }

    /**
     * UI 컴포넌트 초기화 및 CardLayout 구성
     * 각 패널 생성 시 MainApp(this)를 넘겨주어 화면 전환 기능을 공유합니다.
     */
    private void initUI() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(UITheme.BG_COLOR);

        loginPanel = new LoginPanel(this);
        signUpPanel = new SignUpPanel(this);
        mainMenuPanel = new MainMenuPanel(this);
        searchPanel = new SearchPanel(this);
        flightListPanel = new FlightListPanel(this);
        confirmPanel = new ConfirmPanel(this);
        paymentHistoryPanel = new PaymentHistoryPanel(this);
        myPagePanel = new MyPagePanel(this);

        // CardLayout에 패널 등록 (Key, Value)
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

    /**
     * 화면 전환 메서드
     * @param panelName CardLayout에 등록된 패널의 키 이름 (예: "LOGIN", "MAIN")
     */
    public void showPanel(String panelName) {
        cardLayout.show(cardPanel, panelName);
        JPanel current = null;

        // 화면 전환 시 데이터 갱신이 필요한 경우 처리
        switch (panelName) {
            case "LOGIN": current = loginPanel; break;
            case "SIGNUP": current = signUpPanel; break;
            case "MAIN": current = mainMenuPanel; break;
            case "SEARCH":
                searchPanel.updateUserName(); // 상단 사용자 이름 갱신
                current = searchPanel;
                break;
            case "LIST": current = flightListPanel; break;
            case "CONFIRM": current = confirmPanel; break;
            case "PAYMENT_HISTORY":
                if (userController.isLoggedIn()) {
                    updatePaymentHistoryData(); // 결제 내역 최신화
                }
                current = paymentHistoryPanel;
                break;
            case "MYPAGE":
                if (userController.isLoggedIn()) {
                    myPagePanel.setUserInfo(userController.getCurrentUser()); // 회원 정보 갱신
                }
                current = myPagePanel;
                break;
        }

        // 현재 패널 크기에 맞춰 창 크기 조절 및 중앙 정렬
        if (current != null) {
            cardPanel.setPreferredSize(current.getPreferredSize());
            pack();
            setLocationRelativeTo(null);
        }
    }
    
    /**
     * 결제 내역 데이터를 갱신하여 패널에 전달합니다.
     */
    private void updatePaymentHistoryData() {
        List<Reservation> myReservations = reservationController.getMyReservations();
        java.util.List<PaymentHistoryPanel.PaymentRow> rows = new java.util.ArrayList<>();

        for (Reservation r : myReservations) {
            if ("Cancelled".equalsIgnoreCase(r.getStatus())) {
                continue; // 취소된 예약은 제외
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

                rows.add(new PaymentHistoryPanel.PaymentRow(
                        r.getReservationId(), 
                        "N/A",
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

    // 공항 이름으로 공항 코드를 찾는 헬퍼 메서드
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
     * [SearchPanel -> FlightListPanel]
     * 사용자가 입력한 조건으로 항공권을 검색하고 결과 화면으로 이동합니다.
     */
    public void searchFlights(String departureName, String arrivalName, String departureDateStr, String returnDateStr, int economySeats, int businessSeats) {
        this.currentEconomySeats = economySeats;
        this.currentBusinessSeats = businessSeats;
        
        String departureCode = getAirportCode(departureName);
        String arrivalCode = getAirportCode(arrivalName);

        // 유효성 검사
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

        // 컨트롤러를 통해 항공권 검색 실행
        List<Flight> flights = flightController.searchFlights(departureCode, arrivalCode, departureDate);
        
        // 검색 결과를 테이블 표시용 2차원 배열로 변환
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
            
            // 인원 수에 따른 총액 계산
            if (route != null) {
                double basePrice = route.getPrice();
                basePriceStr = String.format(Locale.KOREA, "%,.0f원", basePrice);
                double totalPrice = (basePrice * economySeats) + (basePrice * 2.0 * businessSeats);
                totalPriceStr = String.format(Locale.KOREA, "%,.0f원", totalPrice);
            }

            flightData[i] = new Object[] { flightCode, depTime, arrTime, duration, basePriceStr, flight.getRouteId(), totalPriceStr };
        }

        // 검색 패널의 좌석 정보를 가져와 리스트 패널 업데이트
        String seatSummary = searchPanel.getSeatSummaryForResult();
        flightListPanel.updateSearchCriteria(departureName, arrivalName, departureDateStr, returnDateStr, seatSummary);
        flightListPanel.populateTable(flightData);
        showPanel("LIST");
    }

    /**
     * [FlightListPanel -> SeatSelectionDialog]
     * 선택한 항공편에 대해 좌석 선택 다이얼로그를 띄웁니다.
     */
    public void openSeatSelection(String flightId, String routeShort, String routeLong, String departureDate, String returnDate, String time, String person, String price) {
        Flight flight = flightDAO.findByFlightId(flightId);
        if (flight == null) return;
        
        Aircraft aircraft = aircraftDAO.findByAircraftId(flight.getAircraftId());
        if (aircraft == null) {
            JOptionPane.showMessageDialog(this, "항공기 정보를 찾을 수 없습니다.", "데이터 오류", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // 이미 예약된 좌석 정보를 조회하여 비활성화 목록 생성
        List<Reservation> reservations = reservationDAO.findReservationsByFlightId(flightId);
        java.util.List<String> occupiedSeats = new java.util.ArrayList<>();
        for(Reservation r : reservations) {
            if("Confirmed".equals(r.getStatus()) || "Paid".equals(r.getStatus())) {
                occupiedSeats.add(r.getSeatNumber());
            }
        }

        // 모달 다이얼로그 생성 및 표시
        SeatSelectionDialog dialog = new SeatSelectionDialog(
                this, aircraft, occupiedSeats, currentBusinessSeats, currentEconomySeats);
        dialog.setVisible(true);

        // 선택 완료 후 처리
        String selectedSeats = dialog.getSelectedSeats();
        if (selectedSeats != null && !selectedSeats.isEmpty()) {
            confirmFlight(flightId, selectedSeats, routeShort, routeLong, departureDate, returnDate, time, person, price);
        }
    }

    /**
     * [SeatSelectionDialog -> ConfirmPanel]
     * 최종 예약 확인 화면으로 데이터를 전달하고 이동합니다.
     */
    public void confirmFlight(String flightId, String seatNumber, String routeShort, String routeLong, String departureDate, String returnDate, String time, String person, String price) {
        confirmPanel.setFlightDetails(flightId, seatNumber, routeShort, routeLong, departureDate, returnDate, time, person, price);
        showPanel("CONFIRM");
    }

    /**
     * 마일리지 적립/차감을 수행하고 DB를 업데이트하는 유틸리티 메서드
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