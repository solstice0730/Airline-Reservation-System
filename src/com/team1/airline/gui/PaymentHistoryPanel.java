package com.team1.airline.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 결제 내역 확인 패널
 */
public class PaymentHistoryPanel extends JPanel {

    private final MainApp mainApp;
    private final Color PRIMARY_BLUE = new Color(0, 122, 255);

    // UI 컴포넌트
    private JLabel countLabel;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel eastAirlineLabel, eastRouteTimeLabel, eastSeatLabel, eastPriceLabel;

    // 데이터
    private List<PaymentRow> paymentRows = new ArrayList<>();

    // DTO 클래스
    public static class PaymentRow {
        private final String reservationNo, airline, flightNo, route, timeInfo, seatInfo, priceText;
        public PaymentRow(String rNo, String al, String fNo, String rt, String ti, String si, String pt) {
            this.reservationNo = rNo; this.airline = al; this.flightNo = fNo; 
            this.route = rt; this.timeInfo = ti; this.seatInfo = si; this.priceText = pt;
        }
        public String getReservationNo() { return reservationNo; }
        public String getAirline() { return airline; }
        public String getFlightNo() { return flightNo; }
        public String getRoute() { return route; }
        public String getTimeInfo() { return timeInfo; }
        public String getSeatInfo() { return seatInfo; }
        public String getPriceText() { return priceText; }
    }

    public PaymentHistoryPanel(MainApp mainApp) {
        this.mainApp = mainApp;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 400));

        add(createTitlePanel(), BorderLayout.NORTH);
        add(createWestPanel(), BorderLayout.WEST);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createEastPanel(), BorderLayout.EAST);

        initSelectionListener();
    }
    
    public void loadPaymentHistory() {
        if (mainApp.getUserController().isLoggedIn()) {
            List<PaymentRow> rows = mainApp.getReservationController().getMyReservationDetails();
            setPaymentRows(rows);
        } else {
            // Clear data if not logged in
            setPaymentRows(new ArrayList<>());
        }
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_BLUE);
        panel.setBorder(new EmptyBorder(5, 10, 5, 10));

        JLabel titleLabel = new JLabel("결제 내역", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));

        JButton closeButton = new JButton("X");
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(PRIMARY_BLUE);
        closeButton.setBorder(null);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> mainApp.showPanel("MAIN"));

        panel.add(titleLabel, BorderLayout.CENTER);
        panel.add(closeButton, BorderLayout.EAST);
        return panel;
    }

    private JPanel createWestPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
        panel.setPreferredSize(new Dimension(200, 0));

        JLabel title = new JLabel("결제 내역");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        countLabel = new JLabel("0개의 결제내역");
        
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));
        panel.add(countLabel);
        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        tableModel = new DefaultTableModel(new Object[] { "예약번호", "항공사", "항공편번호", "경로" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createEastPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 3, 10, 10));
        panel.setPreferredSize(new Dimension(230, 0));

        eastAirlineLabel = createLabel("-");
        eastRouteTimeLabel = createLabel("-");
        eastSeatLabel = createLabel("-");
        eastPriceLabel = createLabel("₩0");
        eastPriceLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        panel.add(createLabel("선택 항공권", new Font("SansSerif", Font.BOLD, 16)));
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JSeparator());
        panel.add(Box.createVerticalStrut(10));
        
        panel.add(eastAirlineLabel);
        panel.add(eastRouteTimeLabel);
        panel.add(eastSeatLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(eastPriceLabel);
        panel.add(Box.createVerticalStrut(15));

        JButton cancelButton = new JButton("예약 취소");
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBackground(PRIMARY_BLUE);
        
        cancelButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(mainApp, "취소할 예약 내역을 선택해주세요.");
                return;
            }

            PaymentRow rowData = paymentRows.get(table.convertRowIndexToModel(selectedRow));
            String reservationNo = rowData.getReservationNo();

            int confirm = JOptionPane.showConfirmDialog(this, 
                    "정말로 선택하신 예약을 취소하시겠습니까?\n예약번호: " + reservationNo,
                    "예약 취소 확인", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = mainApp.getReservationController().cancelReservation(reservationNo);

                if (success) {
                    JOptionPane.showMessageDialog(this, "예약이 정상적으로 취소되었습니다.");
                    loadPaymentHistory(); // Reload data from backend
                } else {
                    JOptionPane.showMessageDialog(this, "예약 취소에 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnWrapper.setBackground(Color.WHITE);
        btnWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnWrapper.add(cancelButton);
        
        panel.add(btnWrapper);
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JLabel createLabel(String text) {
        return createLabel(text, new Font("SansSerif", Font.PLAIN, 14));
    }
    
    private JLabel createLabel(String text, Font font) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void initSelectionListener() {
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    int modelRow = table.convertRowIndexToModel(selectedRow);
                    if (modelRow >= 0 && modelRow < paymentRows.size()) {
                        updateDetail(paymentRows.get(modelRow));
                    }
                }
            }
        });
    }

    private void updateDetail(PaymentRow pr) {
        eastAirlineLabel.setText("항공사: " + pr.getAirline());
        eastRouteTimeLabel.setText("<html>" + pr.getRoute() + "<br>" + pr.getTimeInfo() + "</html>");
        eastSeatLabel.setText("좌석번호: " + pr.getSeatInfo());
        eastPriceLabel.setText(pr.getPriceText());
    }

    public void setPaymentRows(List<PaymentRow> rows) {
        this.paymentRows = (rows != null) ? rows : new ArrayList<>();
        tableModel.setRowCount(0);
        for (PaymentRow row : this.paymentRows) {
            tableModel.addRow(new Object[] { row.getReservationNo(), row.getAirline(), row.getFlightNo(), row.getRoute() });
        }
        countLabel.setText(this.paymentRows.size() + "개의 결제내역이 있습니다.");
        
        // Clear details if list is empty
        if (this.paymentRows.isEmpty()) {
            eastAirlineLabel.setText("-");
            eastRouteTimeLabel.setText("-");
            eastSeatLabel.setText("-");
            eastPriceLabel.setText("₩0");
        }
    }
}