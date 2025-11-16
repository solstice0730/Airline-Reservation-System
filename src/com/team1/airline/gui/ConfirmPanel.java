package com.team1.airline.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ConfirmPanel extends JPanel {

    private MainApp mainApp;
    private Font titleFont = new Font("SansSerif", Font.BOLD, 22);
    private Font infoFont = new Font("SansSerif", Font.PLAIN, 16);

    private JTextArea routeArea;
    private JLabel timeLabel;
    private JLabel personLabel;
    
    private JLabel confirmRouteLabel;
    private JLabel confirmPriceLabel;

    public ConfirmPanel(MainApp mainApp) {
        this.mainApp = mainApp;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        add(createTitlePanel(), BorderLayout.NORTH);
        add(createContentPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0, 122, 255));
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));
        JButton backButton = new JButton("X");
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(0, 122, 255));
        backButton.setBorder(null);
        backButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        backButton.addActionListener(e -> mainApp.showPanel("LIST")); 
        panel.add(backButton, BorderLayout.EAST);
        JLabel title = new JLabel("예약 확인", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        panel.add(title, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); 
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 40, 30));

        panel.add(createTitleLabel("선택 항공권"));
        
        JPanel flightInfoPanel = createFlightInfoPanel(); 
        flightInfoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100)); 
        flightInfoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(flightInfoPanel);

        panel.add(Box.createVerticalStrut(40));

        panel.add(createTitleLabel("예약 확인"));
        panel.add(createConfirmationPanel()); 
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(0, 30, 40, 30));
        JButton payButton = new JButton("결제");
        payButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        payButton.setBackground(new Color(0, 122, 255));
        payButton.setForeground(Color.WHITE);
        payButton.setOpaque(true);
        payButton.setBorderPainted(false);
        payButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));
        payButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(mainApp, "예약이 완료되었습니다.");
            mainApp.showPanel("SEARCH");
        });
        panel.add(payButton, BorderLayout.CENTER);
        return panel;
    }

    private JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(titleFont);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        return label;
    }
    
    private JPanel createFlightInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        routeArea = new JTextArea("노선 정보\n날짜 정보");
        routeArea.setFont(infoFont);
        routeArea.setEditable(false);
        routeArea.setOpaque(false); 
        panel.add(routeArea);
        
        timeLabel = new JLabel("시간 정보");
        timeLabel.setFont(infoFont);
        timeLabel.setVerticalAlignment(SwingConstants.CENTER);
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timeLabel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, Color.LIGHT_GRAY));
        panel.add(timeLabel);

        personLabel = new JLabel("인원 정보");
        personLabel.setFont(infoFont);
        personLabel.setVerticalAlignment(SwingConstants.CENTER);
        personLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(personLabel);
        
        return panel;
    }
    
    private JPanel createConfirmationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        
        confirmRouteLabel = new JLabel("선택한 항공권: ");
        panel.add(confirmRouteLabel);
        panel.add(new JLabel("승객 성명: 홍길동")); 
        panel.add(Box.createVerticalStrut(20));
        
        JLabel total = new JLabel("총 결제 금액");
        total.setFont(infoFont);
        panel.add(total);
        
        confirmPriceLabel = new JLabel("₩0");
        confirmPriceLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        panel.add(confirmPriceLabel);
        
        for(Component c : panel.getComponents()) {
            if (c instanceof JLabel) ((JLabel) c).setFont(infoFont);
        }
        confirmPriceLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        
        return panel;
    }
    
    public void setFlightDetails(String route, String departureDate, String returnDate, String time, String person, String price) {
        
        // "선택 항공권" 패널
        // 가는 날과 오는 날을 함께 표시 (예: 2025/11/16 ~ 2025/11/20)
        String dateRange = departureDate;
        if (returnDate != null && !returnDate.isEmpty()) {
             dateRange += " ~ " + returnDate;
        }
        routeArea.setText(route + "\n" + dateRange); 
        
        timeLabel.setText(time);                
        personLabel.setText(person);            
        
        // "예약 확인" 패널
        confirmRouteLabel.setText("선택한 항공권: " + route);
        confirmPriceLabel.setText(price);
    }
}