package com.team1.airline.gui;

import com.team1.airline.controller.FlightController;
import com.team1.airline.dao.*;
import com.team1.airline.dao.impl.*;
import com.team1.airline.entity.Airport;
import com.team1.airline.entity.Flight;
import com.team1.airline.entity.Route;
import com.team1.airline.service.FlightManageable;
import com.team1.airline.service.impl.FlightManager;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * MainApp
 * - 어플리케이션의 메인 프레임(JFrame)입니다.
 * - CardLayout을 사용하여 패널(화면) 간의 전환을 관리합니다.
 * - DAO, Controller 등 백엔드 객체를 초기화하고 각 패널에 전달합니다.
 */
public class MainApp extends JFrame {

    private CardLayout cardLayout;
    private JPanel cardPanel;

    // 화면 패널들
    private LoginPanel loginPanel;
    private SignUpPanel signUpPanel;
    private SearchPanel searchPanel;
    private FlightListPanel flightListPanel;
    private ConfirmPanel confirmPanel;
    private MainMenuPanel mainMenuPanel;
    private PaymentHistoryPanel paymentHistoryPanel;

    // 백엔드 구성요소
    private DataManager dataManager;
    private FlightDAO flightDAO;
    private RouteDAO routeDAO;
    private AircraftDAO aircraftDAO;
    private ReservationDAO reservationDAO;
    private AirportDAO airportDAO;
    private FlightManageable flightService;
    private FlightController flightController;

    public MainApp() {
        setTitle("항공권 예약 시스템 (프로토타입)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 1. 백엔드 초기화 (데이터 로드 및 의존성 주입)
        initBackend();

        // 2. UI 초기화 (레이아웃 및 패널 생성)
        initUI();

        // 3. 초기 화면 설정
        showPanel("LOGIN");
    }

    private void initBackend() {
        dataManager = DataManager.getInstance();
        dataManager.loadAllData(); 

        flightDAO = new FlightDAOImpl();
        routeDAO = new RouteDAOImpl();
        aircraftDAO = new AircraftDAOImpl();
        reservationDAO = new ReservationDAOImpl();
        airportDAO = new AirportDAOImpl();
        
        flightService = new FlightManager(flightDAO, routeDAO, aircraftDAO, reservationDAO);
        flightController = new FlightController(flightService);
    }

    private void initUI() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // 각 패널 생성 시 this(MainApp)를 넘겨 화면 전환이 가능하게 함
        loginPanel = new LoginPanel(this);
        signUpPanel = new SignUpPanel(this);
        mainMenuPanel = new MainMenuPanel(this);
        searchPanel = new SearchPanel(this);
        flightListPanel = new FlightListPanel(this);
        confirmPanel = new ConfirmPanel(this);
        paymentHistoryPanel = new PaymentHistoryPanel(this);

        // 패널 등록 (Key값으로 호출)
        cardPanel.add(loginPanel, "LOGIN");
        cardPanel.add(signUpPanel, "SIGNUP");
        cardPanel.add(mainMenuPanel, "MAIN");
        cardPanel.add(searchPanel, "SEARCH");
        cardPanel.add(flightListPanel, "LIST");
        cardPanel.add(confirmPanel, "CONFIRM");
        cardPanel.add(paymentHistoryPanel, "PAYMENT_HISTORY");

        add(cardPanel);
    }

    /**
     * 화면 전환 메서드
     * @param panelName 전환할 패널의 Key 이름
     */
    public void showPanel(String panelName) {
        cardLayout.show(cardPanel, panelName);
        JPanel current = null;

        // 현재 패널 객체를 찾아 창 크기를 재조정(pack)
        switch (panelName) {
            case "LOGIN": current = loginPanel; break;
            case "SIGNUP": current = signUpPanel; break;
            case "MAIN": current = mainMenuPanel; break;
            case "SEARCH": current = searchPanel; break;
            case "LIST": current = flightListPanel; break;
            case "CONFIRM": current = confirmPanel; break;
            case "PAYMENT_HISTORY": current = paymentHistoryPanel; break;
        }

        if (current != null) {
            cardPanel.setPreferredSize(current.getPreferredSize());
            pack();
            setLocationRelativeTo(null); // 화면 중앙 배치
        }
    }

    /**
     * 공항 이름으로 공항 코드 조회 (SearchPanel -> FlightController 연결용)
     */
    private String getAirportCode(String airportName) {
        Optional<Airport> airport = airportDAO.findAll().stream()
                .filter(a -> a.getAirportName().equalsIgnoreCase(airportName)).findFirst();
        return airport.map(Airport::getAirportCode).orElse(null);
    }
    
    /**
     * 전체 공항 이름 목록 조회 (SearchPanel 콤보박스용)
     */
    public List<String> getAllAirportNames() {
        return airportDAO.findAll().stream()
                .map(Airport::getAirportName)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 항공권 검색 로직
     * SearchPanel에서 호출되며, 결과를 FlightListPanel로 전달합니다.
     */
    public void searchFlights(String departureName, String arrivalName, String departureDateStr, String returnDateStr) {
        // 1. 공항 이름 -> 코드 변환
        String departureCode = getAirportCode(departureName);
        String arrivalCode = getAirportCode(arrivalName);

        if (departureCode == null || arrivalCode == null) {
            JOptionPane.showMessageDialog(this, "출발지와 도착지를 모두 선택해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. 날짜 파싱
        LocalDate departureDate = null;
        if (departureDateStr != null && !departureDateStr.trim().isEmpty() && !departureDateStr.contains("날짜")) {
            try {
                departureDate = LocalDate.parse(departureDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "날짜 형식이 올바르지 않습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // 3. 컨트롤러를 통해 검색 수행
        List<Flight> flights = flightController.searchFlights(departureCode, arrivalCode, departureDate);

        // 4. 검색 결과를 테이블 데이터(Object[][])로 변환
        // 컬럼: 항공사, 출발시간, 도착시간, 소요시간, 가격, [RouteId(숨김)]
        Object[][] flightData = new Object[flights.size()][6];

        for (int i = 0; i < flights.size(); i++) {
            Flight flight = flights.get(i);
            Route route = routeDAO.findByRouteId(flight.getRouteId());

            String airline = flight.getFlightId().substring(0, Math.min(flight.getFlightId().length(), 3));
            String depTime = flight.getDepartureTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            String arrTime = flight.getArrivalTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            String duration = (route != null ? route.getDuration() + "분" : "N/A");
            String price = (route != null ? String.format(Locale.KOREA, "%,.0f원", route.getPrice()) : "N/A");

            // 마지막 인덱스에 RouteId ("ICN-GMP") 저장
            flightData[i] = new Object[] { airline, depTime, arrTime, duration, price, flight.getRouteId() };
        }

        // 5. FlightListPanel UI 업데이트 및 화면 전환
        String seatSummary = searchPanel.getSeatSummaryForResult();
        flightListPanel.updateSearchCriteria(departureName, arrivalName, departureDateStr, returnDateStr, seatSummary);
        flightListPanel.populateTable(flightData);
        showPanel("LIST");
    }

    /**
     * 항공권 선택 후 예약 확인창(ConfirmPanel)으로 이동
     */
    public void confirmFlight(String routeShort, String routeLong, String departureDate, String returnDate, String time, String person, String price) {
        confirmPanel.setFlightDetails(routeShort, routeLong, departureDate, returnDate, time, person, price);
        showPanel("CONFIRM");
    }
}