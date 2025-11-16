package com.team1.airline.gui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List; // Added missing import

/**
 * 1. 항공권 검색 (메인) 화면
 */
public class SearchPanel extends JPanel {

    private MainApp mainApp; 

    private Font titleFont = new Font("SansSerif", Font.BOLD, 28);
    private Font labelFont = new Font("SansSerif", Font.BOLD, 16);
    private Font infoFont = new Font("SansSerif", Font.PLAIN, 14);
    private Color lightGray = new Color(240, 240, 240);
    
    private JTextField departureField;
    private JTextField arrivalField;
    private JTextField departureDateField; 
    private JTextField returnDateField;    

    public SearchPanel(MainApp mainApp) {
        this.mainApp = mainApp;
        setLayout(new BorderLayout()); 

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(lightGray);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20)); 
        JLabel titleLabel = new JLabel("항공권 검색", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        titlePanel.add(titleLabel);
        
        JPanel formPanel = createFormPanel(); 
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel bottomPanel = new JPanel(); 
        bottomPanel.setBackground(lightGray);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20)); 
        
        JButton searchButton = new JButton("항공권 검색");
        searchButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        searchButton.setBackground(new Color(0, 122, 255));
        searchButton.setForeground(Color.WHITE);
        searchButton.setOpaque(true);
        searchButton.setBorderPainted(false);
        searchButton.setPreferredSize(new Dimension(300, 50)); 

        searchButton.addActionListener(e -> {
            String departure = departureField.getText();
            String arrival = arrivalField.getText();
            
            String departureDateStr = departureDateField.getText();
            String returnDateStr = returnDateField.getText(); //
            
            // MainApp의 중개 메소드 호출 (오는 날짜 정보 추가)
            mainApp.searchFlights(departure, arrival, departureDateStr, returnDateStr); 
        });
        
        bottomPanel.add(searchButton);

        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); 
        panel.setBackground(Color.WHITE); 
        panel.add(Box.createVerticalStrut(30)); 

        departureField = new JTextField();
        arrivalField = new JTextField();
        departureDateField = new JTextField();
        returnDateField = new JTextField();
        
        panel.add(createInputRow("출발지", departureField)); 
        panel.add(Box.createVerticalStrut(20));
        panel.add(createInputRow("목적지", arrivalField)); 
        panel.add(Box.createVerticalStrut(40));

        // 날짜/인원 선택 (가로 2열)
        JPanel rowPanel1 = new JPanel(new GridLayout(1, 2, 20, 0)); 
        rowPanel1.setBackground(Color.WHITE); 
        
        // *** 1. createDateInputBox 사용 (가는 날) ***
        rowPanel1.add(createDateInputBox("가는 날", departureDateField));
        
        rowPanel1.add(createBoxPanel("인원", "인원 수 입력\n성인: 1명\n소아: 0명\n유아: 0명"));
        panel.add(rowPanel1);
        panel.add(Box.createVerticalStrut(20));
        
        // 오는 날 (가로 2열)
        JPanel rowPanel2 = new JPanel(new GridLayout(1, 2, 20, 0));
        rowPanel2.setBackground(Color.WHITE); 
        
        // *** 2. createDateInputBox 사용 (오는 날) ***
        rowPanel2.add(createDateInputBox("오는 날", returnDateField)); 
        
        JPanel emptyPanel = new JPanel(); 
        emptyPanel.setBackground(Color.WHITE); 
        rowPanel2.add(emptyPanel);
        panel.add(rowPanel2);

        panel.add(Box.createVerticalGlue()); 
        return panel;
    }

    // "출발지/목적지" 입력 행 생성 (동일)
    private JPanel createInputRow(String labelText, JTextField textField) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(Color.WHITE); 
        
        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);
        row.add(label, BorderLayout.WEST);

        textField.setFont(infoFont); 
        // 텍스트 필드에 기본 텍스트 추가 (예시)
        if (labelText.equals("출발지")) textField.setText("인천");
        if (labelText.equals("목적지")) textField.setText("제주");

        row.add(textField, BorderLayout.CENTER);

        JButton plusButton = new JButton("+");
        plusButton.addActionListener(e -> {
            List<String> airportNames = mainApp.getAllAirportNames();
            System.out.println("SearchPanel: Fetched " + airportNames.size() + " airport names."); // Debugging line
            if (airportNames.isEmpty()) {
                JOptionPane.showMessageDialog(this, "공항 정보를 불러올 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Use JComboBox with JOptionPane.showInputDialog for simpler selection
            JComboBox<String> airportComboBox = new JComboBox<>(airportNames.toArray(new String[0]));
            airportComboBox.setEditable(false); // Make it non-editable

            int option = JOptionPane.showConfirmDialog(
                    this,
                    airportComboBox,
                    "공항 선택",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (option == JOptionPane.OK_OPTION) {
                String selectedAirport = (String) airportComboBox.getSelectedItem();
                if (selectedAirport != null) {
                    textField.setText(selectedAirport);
                }
            }
        });
        row.add(plusButton, BorderLayout.EAST);
        return row;
    }

    // *** 3. 날짜 입력 전용 박스 패널 생성 (새로 추가) ***
    private JPanel createDateInputBox(String title, JTextField textField) {
        JPanel box = new JPanel(new BorderLayout(10, 10)); // 내부 여백
        box.setBackground(Color.WHITE);
        box.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1)); 
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5)); // 상단 여백
        box.add(titleLabel, BorderLayout.NORTH);

        // *** 4. 아이콘과 텍스트 필드를 담을 내부 패널 ***
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0)); // 아이콘과 텍스트 필드 간 간격
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5)); // 내부 패딩
        
        // 달력 아이콘
        JLabel iconLabel = new JLabel("📅"); // 유니코드 달력 이모지
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        inputPanel.add(iconLabel, BorderLayout.WEST);
        
        textField.setFont(infoFont);
        textField.setBorder(BorderFactory.createEmptyBorder()); // 텍스트 필드 자체 테두리 제거
        // 날짜 기본값 (예시)
        if (title.equals("가는 날")) textField.setText("1/15");
        if (title.equals("오는 날")) textField.setText("1/20");
        inputPanel.add(textField, BorderLayout.CENTER);
        
        box.add(inputPanel, BorderLayout.CENTER); // inputPanel을 박스의 중앙에 추가
        
        return box;
    }

    // "인원" 등의 박스 패널 생성 (동일)
    private JPanel createBoxPanel(String title, String content) {
        JPanel box = new JPanel(new BorderLayout(10, 10));
        box.setBackground(Color.WHITE); 
        box.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1)); 
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
        box.add(titleLabel, BorderLayout.NORTH);

        JTextArea contentArea = new JTextArea(content);
        contentArea.setFont(infoFont);
        contentArea.setEditable(false);
        contentArea.setOpaque(false); 
        contentArea.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        
        box.add(contentArea, BorderLayout.CENTER);
        return box;
    }
}