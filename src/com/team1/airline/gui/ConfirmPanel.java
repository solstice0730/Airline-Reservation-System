package com.team1.airline.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * 예약 확인 및 결제 화면
 */
public class ConfirmPanel extends JPanel {

    private MainApp mainApp;
    private final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 22);
    private final Font INFO_FONT = new Font("SansSerif", Font.PLAIN, 16);
    private final Color PRIMARY_BLUE = new Color(0, 122, 255);

    private JTextArea routeArea;
    private JLabel timeLabel;
    private JLabel personLabel;
    
    private JLabel confirmRouteLabel;
    private JLabel confirmPriceLabel;

    public ConfirmPanel(MainApp mainApp) {
        this.mainApp = mainApp;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(1000, 600));

        add(createTitlePanel(), BorderLayout.NORTH);
        add(createContentPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_BLUE);
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel title = new JLabel("예약 확인", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        JButton backButton = new JButton("X");
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(PRIMARY_BLUE);
        backButton.setBorder(null);
        backButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        backButton.addActionListener(e -> mainApp.showPanel("LIST")); 
        
        panel.add(title, BorderLayout.CENTER);
        panel.add(backButton, BorderLayout.EAST);
        return panel;
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); 
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 40, 30));

        // 상단: 선택 항공권 정보
        panel.add(createSectionTitle("선택 항공권"));
        panel.add(createFlightInfoBox());
        panel.add(Box.createVerticalStrut(40));

        // 하단: 최종 확인 정보
        panel.add(createSectionTitle("예약 확인"));
        panel.add(createConfirmDetailBox()); 
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }

    private JPanel createFlightInfoBox() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        routeArea = new JTextArea();
        routeArea.setFont(INFO_FONT);
        routeArea.setEditable(false);
        routeArea.setOpaque(false); 
        
        timeLabel = new JLabel();
        timeLabel.setFont(INFO_FONT);
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timeLabel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, Color.LIGHT_GRAY));

        personLabel = new JLabel();
        personLabel.setFont(INFO_FONT);
        personLabel.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(routeArea);
        panel.add(timeLabel);
        panel.add(personLabel);
        return panel;
    }

    private JPanel createConfirmDetailBox() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        
        confirmRouteLabel = new JLabel("선택한 항공권: ");
        confirmRouteLabel.setFont(INFO_FONT);
        
        JLabel userLabel = new JLabel("승객 성명: 홍길동"); // 로그인 세션 연동 필요
        userLabel.setFont(INFO_FONT);

        confirmPriceLabel = new JLabel("₩0");
        confirmPriceLabel.setFont(new Font("SansSerif", Font.BOLD, 24));

        panel.add(confirmRouteLabel);
        panel.add(userLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(new JLabel("총 결제 금액"));
        panel.add(confirmPriceLabel);
        
        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(0, 30, 40, 30));

        JButton payButton = new JButton("결제");
        payButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        payButton.setBackground(PRIMARY_BLUE);
        payButton.setForeground(Color.WHITE);
        payButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));
        
        payButton.addActionListener(e -> {
            // TODO: [개발팀] 결제 처리 로직 (DB 저장 등)
            JOptionPane.showMessageDialog(mainApp, "예약이 완료되었습니다.");
            mainApp.showPanel("SEARCH");
        });
        
        panel.add(payButton, BorderLayout.CENTER);
        return panel;
    }

    private JLabel createSectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(TITLE_FONT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        return label;
    }

    /**
     * MainApp에서 호출하여 화면에 데이터를 세팅하는 메서드
     */
    public void setFlightDetails(String routeShort, String routeLong, String depDate, String retDate, String time, String person, String price) {
        String dateRange = depDate + (retDate != null && !retDate.isEmpty() ? " ~ " + retDate : "");
        
        routeArea.setText(routeShort + "\n" + dateRange);
        timeLabel.setText(time);
        personLabel.setText(person);
        confirmRouteLabel.setText("선택한 항공권: " + routeLong);
        confirmPriceLabel.setText(price);
    }
}