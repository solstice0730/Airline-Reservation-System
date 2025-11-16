package GUI;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel; 
import java.awt.*;

public class FlightListPanel extends JPanel {

    private MainApp mainApp;
    
    private Font titleFont = new Font("SansSerif", Font.BOLD, 20);
    private Font labelFont = new Font("SansSerif", Font.BOLD, 16);
    private Font infoFont = new Font("SansSerif", Font.PLAIN, 14);

    private JTable table;
    private DefaultTableModel model;
    
    private JLabel westRouteLabel; 
    private JLabel westDepDateLabel;  
    private JLabel westRetDateLabel;  
    
    private JLabel eastAirlineLabel; 
    private JLabel eastRouteTimeLabel; 
    private JLabel eastPriceLabel;     
    
    private String currentRoute = "";
    private String currentDepartureDate = ""; // *** 가는 날 저장 필드 이름 변경 ***
    private String currentReturnDate = "";    // *** 오는 날 저장 필드 추가 ***

    public FlightListPanel(MainApp mainApp) {
        this.mainApp = mainApp;
        setLayout(new BorderLayout(10, 10)); 
        setBackground(Color.WHITE);

        add(createTitlePanel(), BorderLayout.NORTH);
        add(createWestPanel(), BorderLayout.WEST);
        add(createCenterTable(), BorderLayout.CENTER); 
        add(createEastPanel(), BorderLayout.EAST);
        
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                
                String airline = model.getValueAt(row, 0).toString();
                String depTime = model.getValueAt(row, 1).toString();
                String arrTime = model.getValueAt(row, 2).toString();
                String price = model.getValueAt(row, 4).toString(); 
                
                String depAirport = currentRoute.split(" -> ")[0];
                String arrAirport = currentRoute.split(" -> ")[1];

                eastAirlineLabel.setText("항공사: " + airline);
                eastRouteTimeLabel.setText(depAirport + " " + depTime + " ~ " + arrAirport + " " + arrTime);
                eastPriceLabel.setText(price);
            }
        });
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0, 122, 255)); 
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));
        JLabel title = new JLabel("검색 결과");
        title.setFont(titleFont);
        title.setForeground(Color.WHITE);
        panel.add(title, BorderLayout.CENTER);
        JButton closeButton = new JButton("X");
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(new Color(0, 122, 255));
        closeButton.setBorder(null);
        closeButton.setFont(labelFont);
        closeButton.addActionListener(e -> mainApp.showPanel("SEARCH"));
        panel.add(closeButton, BorderLayout.EAST);
        return panel;
    }

    private JPanel createWestPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); 
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(10, 10, 10, 10), 
                BorderFactory.createLineBorder(Color.LIGHT_GRAY)
        ));
        
        JLabel title = new JLabel("검색 조건");
        title.setFont(labelFont);
        title.setBorder(new EmptyBorder(5, 5, 10, 5));
        panel.add(title);

        westRouteLabel = new JLabel("미정");
        westDepDateLabel = new JLabel("미정");  
        westRetDateLabel = new JLabel("미정");  
        
        panel.add(westRouteLabel);
        panel.add(new JLabel(" ")); 
        panel.add(westDepDateLabel);
        panel.add(new JLabel(" ")); 
        panel.add(westRetDateLabel); 
        panel.add(new JLabel(" ")); 
        panel.add(new JLabel("성인 1명")); 
        
        for (Component c : panel.getComponents()) {
            if (c instanceof JLabel) {
                ((JLabel) c).setFont(infoFont);
                ((JLabel) c).setAlignmentX(Component.LEFT_ALIGNMENT);
            }
        }
        title.setFont(labelFont); 
        
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JScrollPane createCenterTable() {
        String[] columnNames = {"항공사", "출발 시간", "도착 시간", "소요 시간", "가격"};
        
        Object[][] data = {};

        model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model); 
        
        table.setRowHeight(30);
        table.setFont(infoFont);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.getTableHeader().setBackground(Color.LIGHT_GRAY);
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(10, 0, 10, 0));
        return scrollPane;
    }

    private JPanel createEastPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); 
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(10, 10, 10, 10), 
                BorderFactory.createLineBorder(Color.LIGHT_GRAY)
        ));

        JLabel title = new JLabel("선택 항공권");
        title.setFont(labelFont);
        title.setBorder(new EmptyBorder(5, 5, 10, 5));
        panel.add(title);

        eastAirlineLabel = new JLabel("항공사: (선택 대기)");
        eastRouteTimeLabel = new JLabel("(항공편을 선택하세요)");
        eastPriceLabel = new JLabel(" ");
        
        eastPriceLabel.setFont(titleFont);
        eastPriceLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        panel.add(eastAirlineLabel);
        panel.add(eastRouteTimeLabel);
        panel.add(eastPriceLabel);

        JButton selectButton = new JButton("선택");
        selectButton.setBackground(new Color(0, 122, 255));
        selectButton.setForeground(Color.WHITE);
        selectButton.setFont(labelFont);
        selectButton.setOpaque(true);
        selectButton.setBorderPainted(false);
        
        selectButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(mainApp, "항공편을 먼저 선택해주세요.");
                return;
            }
            
            String airline = model.getValueAt(selectedRow, 0).toString();
            String depTime = model.getValueAt(selectedRow, 1).toString();
            String arrTime = model.getValueAt(selectedRow, 2).toString();
            String price = model.getValueAt(selectedRow, 4).toString();
            
            String time = depTime + " ~ " + arrTime;
            String person = "성인 1명"; 
            
            // MainApp의 중개 메소드 호출 (returnDate도 함께 전달)
            mainApp.confirmFlight(currentRoute, currentDepartureDate, currentReturnDate, time, person, price);
        });
        
        panel.add(selectButton);

        for (Component c : panel.getComponents()) {
            if (c instanceof JLabel) {
                ((JLabel) c).setFont(infoFont);
                ((JLabel) c).setAlignmentX(Component.LEFT_ALIGNMENT);
            }
        }
        title.setFont(labelFont);
        eastPriceLabel.setFont(titleFont);
        
        panel.add(Box.createVerticalGlue()); 
        return panel;
    }
    
    public void updateSearchCriteria(String departure, String arrival, String departureDate, String returnDate) {
        this.currentRoute = departure + " -> " + arrival;
        this.currentDepartureDate = departureDate; // 가는 날 저장
        this.currentReturnDate = returnDate;       // *** 오는 날 저장 ***
        
        westRouteLabel.setText(currentRoute);
        westDepDateLabel.setText("가는 날: " + departureDate); 
        westRetDateLabel.setText("오는 날: " + returnDate);   
    }
    
    public void populateTable(Object[][] data) {
        model.setRowCount(0); 
        
        if (data != null) {
            for (Object[] row : data) {
                model.addRow(row);
            }
        }
    }
}