package com.team1.airline.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import com.team1.airline.entity.Reservation;
import com.team1.airline.entity.User;
import java.awt.*;

/**
 * [예약 확인 및 결제 패널]
 * - 선택한 항공권 정보와 가격을 최종 확인합니다.
 * - 마일리지를 조회하고 사용하여 결제 금액을 할인받을 수 있습니다.
 * - 결제 완료 시 마일리지 차감 및 예약 생성이 이루어집니다.
 */
public class ConfirmPanel extends JPanel {

    private MainApp mainApp;
    private JTextArea routeArea;
    private JLabel timeLabel, personLabel;
    private JLabel confirmRouteLabel, confirmSeatLabel, confirmPriceLabel, confirmUserLabel;
    private JLabel availableMileageLabel, discountLabel;
    private UITheme.RoundedButton useMileageButton;

    private String currentFlightId;
    private String currentSeatNumber;
    
    private int originalPrice = 0;
    private int usedMileage = 0;

    public ConfirmPanel(MainApp mainApp) {
        this.mainApp = mainApp;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_COLOR);
        setPreferredSize(new Dimension(1000, 600));

        add(UITheme.createTitlePanel(mainApp, "예약 확인", "LIST"), BorderLayout.NORTH);
        add(createContentPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 40, 20, 40));

        panel.add(createSectionTitle("선택 항공권"));
        panel.add(createFlightInfoBox());
        panel.add(Box.createVerticalStrut(30));

        panel.add(createSectionTitle("예약 정보"));
        panel.add(createConfirmDetailBox());
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createFlightInfoBox() {
        UITheme.RoundedPanel panel = new UITheme.RoundedPanel(20, Color.WHITE);
        panel.setLayout(new GridLayout(1, 3, 10, 0));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        routeArea = new JTextArea();
        routeArea.setFont(UITheme.FONT_BOLD);
        routeArea.setEditable(false);
        routeArea.setOpaque(false);

        timeLabel = new JLabel();
        timeLabel.setFont(UITheme.FONT_PLAIN);
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timeLabel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, UITheme.GRAY_BORDER));

        personLabel = new JLabel();
        personLabel.setFont(UITheme.FONT_PLAIN);
        personLabel.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(routeArea);
        panel.add(timeLabel);
        panel.add(personLabel);
        return panel;
    }

    private JPanel createConfirmDetailBox() {
        UITheme.RoundedPanel panel = new UITheme.RoundedPanel(20, Color.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        confirmRouteLabel = new JLabel("선택한 항공권: ");
        confirmRouteLabel.setFont(UITheme.FONT_PLAIN);

        confirmSeatLabel = new JLabel("선택한 좌석: -");
        confirmSeatLabel.setFont(UITheme.FONT_PLAIN);

        confirmUserLabel = new JLabel("승객 성명: -"); 
        confirmUserLabel.setFont(UITheme.FONT_PLAIN);
        
        confirmPriceLabel = new JLabel("₩0");
        confirmPriceLabel.setFont(new Font("맑은 고딕", Font.BOLD, 28));
        confirmPriceLabel.setForeground(UITheme.PRIMARY_BLUE);
        
        discountLabel = new JLabel("");
        discountLabel.setFont(UITheme.FONT_PLAIN);
        discountLabel.setForeground(Color.RED);

        panel.add(confirmRouteLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(confirmSeatLabel); 
        panel.add(Box.createVerticalStrut(5));
        panel.add(confirmUserLabel);
        
        panel.add(Box.createVerticalStrut(30));
        
        JLabel totalLabel = new JLabel("총 결제 금액");
        totalLabel.setFont(UITheme.FONT_BOLD);
        panel.add(totalLabel);
        panel.add(confirmPriceLabel);
        panel.add(discountLabel);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BG_COLOR);
        mainPanel.setBorder(new EmptyBorder(10, 30, 30, 30));

        // 마일리지 패널
        JPanel mileagePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        mileagePanel.setOpaque(false);
        
        availableMileageLabel = new JLabel("보유 마일리지: 0 P");
        availableMileageLabel.setFont(UITheme.FONT_BOLD);
        availableMileageLabel.setForeground(new Color(0, 100, 0));
        
        useMileageButton = new UITheme.RoundedButton("사용하기");
        useMileageButton.setBackground(Color.GRAY);
        useMileageButton.setPreferredSize(new Dimension(100, 35));
        useMileageButton.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        useMileageButton.addActionListener(e -> openMileageDialog());
        
        mileagePanel.add(availableMileageLabel);
        mileagePanel.add(useMileageButton);

        // 결제 버튼
        UITheme.RoundedButton payButton = new UITheme.RoundedButton("결제하기");
        payButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 60));
        payButton.setFont(UITheme.FONT_SUBTITLE);
        payButton.addActionListener(e -> handlePayment());

        mainPanel.add(mileagePanel, BorderLayout.NORTH);
        mainPanel.add(payButton, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    /**
     * 마일리지 사용 다이얼로그 호출
     * - 보유 마일리지 내에서 사용 금액을 입력받습니다.
     */
    private void openMileageDialog() {
        if (!mainApp.getUserController().isLoggedIn()) return;
        User user = mainApp.getUserController().getCurrentUser();
        int maxMileage = user.getMileage();
        if (maxMileage <= 0) {
            JOptionPane.showMessageDialog(this, "사용 가능한 마일리지가 없습니다.");
            return;
        }
        String input = JOptionPane.showInputDialog(this, "사용할 마일리지 (보유: " + maxMileage + " P)\n(최대 사용: " + originalPrice + "원)", "0");
        if (input != null && !input.isBlank()) {
            try {
                int amount = Integer.parseInt(input);
                if (amount < 0 || amount > maxMileage || amount > originalPrice) {
                    JOptionPane.showMessageDialog(this, "올바른 금액을 입력해주세요.");
                } else {
                    this.usedMileage = amount;
                    updatePriceDisplay();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "숫자만 입력해주세요.");
            }
        }
    }
    
    // 마일리지 적용 후 최종 금액 표시 업데이트
    private void updatePriceDisplay() {
        int finalPrice = originalPrice - usedMileage;
        confirmPriceLabel.setText(String.format("%,d원", finalPrice));
        if (usedMileage > 0) {
            discountLabel.setText(String.format("(- 마일리지 사용: %,d P)", usedMileage));
            useMileageButton.setText("취소");
            useMileageButton.setBackground(Color.DARK_GRAY);
        } else {
            discountLabel.setText("");
            useMileageButton.setText("사용하기");
            useMileageButton.setBackground(Color.GRAY);
        }
    }

    /**
     * [결제 처리 로직]
     * - 예약 생성 (ReservationController 호출)
     * - 마일리지 차감 처리
     * - 중복 적립 방지 로직 포함
     */
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
            
            // 사용한 마일리지 차감 (백엔드 로직이 이를 모를 수 있으므로 유지)
            if (usedMileage > 0) mainApp.addMileage(-usedMileage);
            
            // [버그 수정] 마일리지 이중 적립 방지를 위해 GUI에서의 적립 코드를 제거
            // makeReservation 호출 시 내부적으로 마일리지가 적립되므로 아래 코드는 중복입니다.
            // mainApp.addMileage(mileageEarned); 

            // 사용자에게 적립 예정 금액은 그대로 안내 (실제 적립은 백엔드에서 수행됨)
            if (usedMileage > 0) mainApp.addMileage(0); // 갱신 트리거용으로 0 호출 혹은 생략 가능

            String reservationIds = sb.toString();
            if (reservationIds.length() > 2) reservationIds = reservationIds.substring(0, reservationIds.length() - 2);

            String msg = String.format("예약 완료!\n(예약번호: %s)\n\n[결제] %,d원 (적립 +%,d P)",
                    reservationIds, finalPaymentAmount, mileageEarned);
            JOptionPane.showMessageDialog(mainApp, msg);
            mainApp.showPanel("SEARCH");
        } else {
            JOptionPane.showMessageDialog(this, "예약 실패 (이미 예약된 좌석 등)", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel createSectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.FONT_SUBTITLE);
        label.setForeground(UITheme.PRIMARY_BLUE);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(new EmptyBorder(10, 5, 5, 0));
        return label;
    }

    // 외부에서 예약 데이터를 주입받는 메서드
    public void setFlightDetails(String flightId, String seatNumber, String routeShort, String routeLong, String depDate, String retDate, String time, String person, String priceStr) {
        this.currentFlightId = flightId;
        this.currentSeatNumber = seatNumber;
        try {
            String numberOnly = priceStr.replaceAll("[^0-9]", "");
            this.originalPrice = Integer.parseInt(numberOnly);
        } catch (NumberFormatException e) { this.originalPrice = 0; }
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
            confirmUserLabel.setText("승객 성명: -");
            availableMileageLabel.setText("보유 마일리지: -");
            useMileageButton.setEnabled(false);
        }
    }
}