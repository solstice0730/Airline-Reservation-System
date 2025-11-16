package GUI;

import javax.swing.*;
import java.awt.*;
import java.util.List;

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

    public MainApp() {
        setTitle("항공권 예약 시스템 (프로토타입)");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

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

    public void searchFlights(String departure, String arrival, String departureDate, String returnDate) {
        // [1] 이 곳에서 개발부의 백엔드 로직을 호출합니다.
        
        // 가상 검색 결과 데이터 생성 (JTable에 표시될 내용)
        Object[][] fakeData = {
                {"대한항공", "7:10", "8:25", "1시간 15분", "150,000원"},
                {"아시아나", "6:00", "7:15", "1시간 15분", "145,000원"},
                {"진에어", "8:30", "9:45", "1시간 15분", "120,000원"}
        };
        
        // [2] FlightListPanel에 검색 조건 전달
        flightListPanel.updateSearchCriteria(departure, arrival, departureDate, returnDate);
        
        // [3] FlightListPanel의 테이블에 검색 결과 채우기
        flightListPanel.populateTable(fakeData);

        // [4] 화면 전환
        showPanel("LIST");
    }
    
    // FlightListPanel에서 선택된 항공편 정보와 함께 '가는 날', '오는 날' 정보를 받음
    public void confirmFlight(String route, String departureDate, String returnDate, String time, String person, String price) {
        // [1] ConfirmPanel에 선택된 항공권 정보 전달
        confirmPanel.setFlightDetails(route, departureDate, returnDate, time, person, price); // *** returnDate 추가 ***

        // [2] 화면 전환
        showPanel("CONFIRM");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainApp app = new MainApp();
            app.setVisible(true);
        });
    }
}