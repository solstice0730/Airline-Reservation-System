package com.team1.airline.gui;

import com.team1.airline.entity.Reservation;
import com.team1.airline.entity.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import com.team1.airline.entity.Reservation;
import com.team1.airline.entity.User;
import java.awt.*;

/**
 * [예약 확인 및 결제 패널]
 * 최종 금액 확인, 마일리지 사용, 결제 실행
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
    private JLabel confirmSeatLabel; 
    private JLabel confirmPriceLabel;
    private JLabel confirmUserLabel;
    
    private JLabel availableMileageLabel; 
    private JButton useMileageButton;
    private JLabel discountLabel; 

    private String currentFlightId;
    private String currentSeatNumber;
    
    private int originalPrice = 0;
    private int usedMileage = 0;

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

        panel.add(createSectionTitle("선택 항공권"));
        panel.add(createFlightInfoBox());
        panel.add(Box.createVerticalStrut(40));

        panel.add(createSectionTitle("예약 확인"));
        panel.add(createConfirmDetailBox());
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createFlightInfoBox() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
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

        confirmSeatLabel = new JLabel("선택한 좌석: -");
        confirmSeatLabel.setFont(INFO_FONT);

        confirmUserLabel = new JLabel("승객 성명: -"); 
        confirmUserLabel.setFont(INFO_FONT);
        
        confirmPriceLabel = new JLabel("₩0");
        confirmPriceLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        
        discountLabel = new JLabel("");
        discountLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        discountLabel.setForeground(Color.RED);

        panel.add(confirmRouteLabel);
        panel.add(confirmSeatLabel); 
        panel.add(confirmUserLabel);
        
        panel.add(Box.createVerticalStrut(20));
        
        panel.add(new JLabel("총 결제 금액"));
        panel.add(confirmPriceLabel);
        panel.add(discountLabel); 

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(0, 30, 40, 30));

        // 마일리지 패널
        JPanel mileagePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        mileagePanel.setBackground(Color.WHITE);
        
        availableMileageLabel = new JLabel("보유 마일리지: 0 P");
        availableMileageLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        availableMileageLabel.setForeground(new Color(0, 100, 0));
        
        useMileageButton = new JButton("사용하기");
        useMileageButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        useMileageButton.setBackground(Color.WHITE);
        useMileageButton.setFocusPainted(false);
        useMileageButton.addActionListener(e -> openMileageDialog());
        
        mileagePanel.add(availableMileageLabel);
        mileagePanel.add(useMileageButton);

        // 결제 버튼
        JButton payButton = new JButton("결제");
        payButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        payButton.setBackground(PRIMARY_BLUE);
        payButton.setForeground(Color.WHITE);
        payButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));
        payButton.addActionListener(e -> handlePayment());

        mainPanel.add(mileagePanel, BorderLayout.NORTH);
        mainPanel.add(payButton, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private void openMileageDialog() {
        if (!mainApp.getUserController().isLoggedIn()) return;
        
        User user = mainApp.getUserController().getCurrentUser();
        int maxMileage = user.getMileage();
        
        if (maxMileage <= 0) {
            JOptionPane.showMessageDialog(this, "사용 가능한 마일리지가 없습니다.");
            return;
        }

        String input = JOptionPane.showInputDialog(this, 
                "사용할 마일리지를 입력하세요 (보유: " + maxMileage + " P)\n(최대 사용 가능: " + originalPrice + "원)", 
                "0");
        
        if (input != null && !input.isBlank()) {
            try {
                int amount = Integer.parseInt(input);
                if (amount < 0) JOptionPane.showMessageDialog(this, "올바른 금액을 입력해주세요.");
                else if (amount > maxMileage) JOptionPane.showMessageDialog(this, "보유 마일리지를 초과했습니다.");
                else if (amount > originalPrice) JOptionPane.showMessageDialog(this, "결제 금액보다 많이 사용할 수 없습니다.");
                else {
                    this.usedMileage = amount;
                    updatePriceDisplay();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "숫자만 입력해주세요.");
            }
        }
    }
    
    private void updatePriceDisplay() {
        int finalPrice = originalPrice - usedMileage;
        confirmPriceLabel.setText(String.format("%,d원", finalPrice));
        
        if (usedMileage > 0) {
            discountLabel.setText(String.format("(- 마일리지 사용: %,d P)", usedMileage));
            useMileageButton.setText("사용 취소"); 
        } else {
            discountLabel.setText("");
            useMileageButton.setText("사용하기");
        }
    }

    private void handlePayment() {
        if (currentSeatNumber == null || currentSeatNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "좌석 정보가 누락되었습니다.");
            return;
        }

        String[] seats = currentSeatNumber.split(",\\s*");
        int successCount = 0;
        StringBuilder sb = new StringBuilder();

        for (String seat : seats) {
            Reservation reservation = mainApp.getReservationController().makeReservation(currentFlightId, seat);
            if (reservation != null) {
                successCount++;
                sb.append(reservation.getReservationId()).append(", ");
            }
        }

        if (successCount > 0) {
            int finalPaymentAmount = originalPrice - usedMileage;
            int mileageEarned = (int) (finalPaymentAmount * 0.05);
            
            if (usedMileage > 0) mainApp.addMileage(-usedMileage);
            mainApp.addMileage(mileageEarned);

            String reservationIds = sb.toString();
            if (reservationIds.length() > 2) reservationIds = reservationIds.substring(0, reservationIds.length() - 2);

            String msg = String.format("총 %d건의 예약이 완료되었습니다.\n(예약번호: %s)\n\n" +
                                     "[결제 상세]\n- 정상가: %,d원\n- 마일리지 사용: -%,d P\n- 최종 결제: %,d원\n\n" +
                                     "[적립]\n+ %,d Point",
                    successCount, reservationIds, originalPrice, usedMileage, finalPaymentAmount, mileageEarned);
            
            JOptionPane.showMessageDialog(mainApp, msg, "결제 완료", JOptionPane.INFORMATION_MESSAGE);
            mainApp.showPanel("SEARCH");
        } else {
            JOptionPane.showMessageDialog(this, "예약에 실패했습니다.\n(이미 예약된 좌석이거나 오류가 발생했습니다.)", "예약 실패", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel createSectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(TITLE_FONT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        return label;
    }

    public void setFlightDetails(String flightId, String seatNumber, String routeShort, String routeLong, String depDate, String retDate, String time, String person, String priceStr) {
        this.currentFlightId = flightId;
        this.currentSeatNumber = seatNumber;
        
        try {
            String numberOnly = priceStr.replaceAll("[^0-9]", "");
            this.originalPrice = Integer.parseInt(numberOnly);
        } catch (NumberFormatException e) {
            this.originalPrice = 0;
        }
        this.usedMileage = 0; 

        routeArea.setText(routeShort); 
        timeLabel.setText(time);
        personLabel.setText(person);
        confirmRouteLabel.setText("선택한 항공권: " + routeLong);
        confirmSeatLabel.setText("선택한 좌석: " + seatNumber);
        
        updatePriceDisplay();
        
        if (mainApp.getUserController().isLoggedIn()) {
            User currentUser = mainApp.getUserController().getCurrentUser();
            confirmUserLabel.setText("승객 성명: " + currentUser.getUserName());
            availableMileageLabel.setText(String.format("보유 마일리지: %,d P", currentUser.getMileage()));
            useMileageButton.setEnabled(true);
        } else {
            confirmUserLabel.setText("승객 성명: (로그인 정보 없음)");
            availableMileageLabel.setText("보유 마일리지: -");
            useMileageButton.setEnabled(false);
        }
    }
}