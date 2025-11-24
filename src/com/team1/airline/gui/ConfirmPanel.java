package com.team1.airline.gui;

import com.team1.airline.entity.Reservation;
import com.team1.airline.entity.User;

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
    private JLabel userLabel;
    private JLabel confirmPriceLabel;

    private String flightId;

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
        
        userLabel = new JLabel("승객 성명: ");
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
            User currentUser = mainApp.getUserController().getCurrentUser();
            if (currentUser == null) {
                JOptionPane.showMessageDialog(mainApp, "로그인이 필요합니다.", "오류", JOptionPane.ERROR_MESSAGE);
                mainApp.showPanel("LOGIN");
                return;
            }

            // A1, A2... 식으로 좌석 배정. 인원수만큼 반복해야 하지만, UI가 인원수만 받아 간단히 첫번째 좌석만 배정
            String seatNumber = "A1"; 

            Reservation reservation = mainApp.getReservationController().makeReservation(this.flightId, seatNumber);

            if (reservation != null) {
                JOptionPane.showMessageDialog(mainApp, "예약이 완료되었습니다. 예약번호: " + reservation.getReservationId(), "예약 성공", JOptionPane.INFORMATION_MESSAGE);
                mainApp.showPanel("MAIN");
            } else {
                JOptionPane.showMessageDialog(mainApp, "예약에 실패했습니다. (좌석 부족 또는 시스템 오류)", "예약 실패", JOptionPane.ERROR_MESSAGE);
            }
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
    public void setFlightDetails(String flightId, String routeShort, String routeLong, String depDate, String retDate, String time, String person, String price) {
        this.flightId = flightId;
        String dateRange = depDate + (retDate != null && !retDate.isEmpty() ? " ~ " + retDate : "");
        
        User currentUser = mainApp.getUserController().getCurrentUser();
        
        routeArea.setText(routeShort + "\n" + dateRange);
        timeLabel.setText(time);
        personLabel.setText(person);
        confirmRouteLabel.setText("선택한 항공권: " + routeLong);
        if (currentUser != null) {
            userLabel.setText("승객 성명: " + currentUser.getUserName());
        }
        confirmPriceLabel.setText(price);
    }
}