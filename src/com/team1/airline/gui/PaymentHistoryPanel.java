package com.team1.airline.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * [결제/예약 내역 패널]
 * - MainApp으로부터 전달받은 예약 목록을 JTable에 표시합니다.
 * - 목록에서 항목을 선택하면 우측에 상세 정보가 나타납니다.
 * - '예약 취소' 버튼을 통해 예약을 취소하고 마일리지를 회수할 수 있습니다.
 */
public class PaymentHistoryPanel extends JPanel {

    private final MainApp mainApp;
    private JLabel countLabel;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel eastFlightNoLabel, eastRouteTimeLabel, eastSeatLabel, eastPriceLabel;
    private List<PaymentRow> paymentRows = new ArrayList<>();

    // 테이블 표시를 위한 DTO 클래스
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
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_COLOR);
        setPreferredSize(new Dimension(900, 500));

        add(UITheme.createTitlePanel(mainApp, "결제 내역", "MAIN"), BorderLayout.NORTH);
        
        JPanel content = new JPanel(new BorderLayout(15, 0));
        content.setBackground(UITheme.BG_COLOR);
        content.setBorder(new EmptyBorder(10, 20, 20, 20));
        
        content.add(createCenterPanel(), BorderLayout.CENTER);
        content.add(createEastPanel(), BorderLayout.EAST);
        
        add(content, BorderLayout.CENTER);

        initSelectionListener();
    }

    // 중앙 테이블 패널 생성
    private JPanel createCenterPanel() {
        UITheme.RoundedPanel panel = new UITheme.RoundedPanel(20, Color.WHITE);
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        countLabel = new JLabel("0개의 결제내역");
        countLabel.setFont(UITheme.FONT_BOLD);
        countLabel.setBorder(new EmptyBorder(0, 5, 10, 0));
        panel.add(countLabel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[] { "예약번호", "항공편", "경로", "좌석" }, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.applyTableTheme(table);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    // 우측 상세 정보 패널 생성
    private JPanel createEastPanel() {
        UITheme.RoundedPanel panel = new UITheme.RoundedPanel(20, Color.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setPreferredSize(new Dimension(280, 0));

        panel.add(UITheme.createSectionTitle("선택 상세"));
        
        eastFlightNoLabel = createLabel("항공편: -"); 
        eastRouteTimeLabel = createLabel("-");
        eastSeatLabel = createLabel("좌석: -");
        
        eastPriceLabel = new JLabel("₩0");
        eastPriceLabel.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        eastPriceLabel.setForeground(UITheme.TEXT_COLOR);
        eastPriceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(Box.createVerticalStrut(15));
        panel.add(new JSeparator());
        panel.add(Box.createVerticalStrut(15));
        panel.add(eastFlightNoLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(eastRouteTimeLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(eastSeatLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(eastPriceLabel);
        panel.add(Box.createVerticalGlue());

        UITheme.RoundedButton cancelButton = new UITheme.RoundedButton("예약 취소");
        cancelButton.setBackground(Color.RED);
        cancelButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        cancelButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        
        cancelButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) { JOptionPane.showMessageDialog(mainApp, "취소할 내역 선택 필요"); return; }
            String reservationNo = paymentRows.get(selectedRow).getReservationNo();
            int confirm = JOptionPane.showConfirmDialog(this, "정말로 예약을 취소하시겠습니까?\n(마일리지 회수됨)", "확인", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // 예약 취소 요청
                boolean success = mainApp.getReservationController().cancelReservation(reservationNo);
                if (success) {
                    paymentRows.remove(selectedRow);
                    tableModel.removeRow(selectedRow);
                    countLabel.setText(paymentRows.size() + "개의 결제내역");
                    eastFlightNoLabel.setText("항공편: -"); eastRouteTimeLabel.setText("-"); eastSeatLabel.setText("-"); eastPriceLabel.setText("₩0");
                    JOptionPane.showMessageDialog(this, "취소되었습니다.");
                } else {
                    JOptionPane.showMessageDialog(this, "취소 실패.");
                }
            }
        });
        
        panel.add(cancelButton);
        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.FONT_PLAIN);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    // 테이블 선택 리스너: 우측 상세 패널 업데이트
    private void initSelectionListener() {
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0 && row < paymentRows.size()) updateDetail(paymentRows.get(row));
            }
        });
    }

    private void updateDetail(PaymentRow pr) {
        eastFlightNoLabel.setText("항공편: " + pr.getFlightNo());
        eastRouteTimeLabel.setText("<html>" + pr.getRoute() + "<br>" + pr.getTimeInfo() + "</html>");
        eastSeatLabel.setText("좌석: " + pr.getSeatInfo());
        eastPriceLabel.setText(pr.getPriceText());
    }

    // 데이터 갱신 메서드
    public void setPaymentRows(List<PaymentRow> rows) {
        this.paymentRows = (rows != null) ? rows : new ArrayList<>();
        tableModel.setRowCount(0);
        for (PaymentRow row : this.paymentRows) {
            tableModel.addRow(new Object[] { row.getReservationNo(), row.getFlightNo(), row.getRoute(), row.getSeatInfo() });
        }
        if(countLabel!=null) countLabel.setText(this.paymentRows.size() + "개의 결제내역");
    }
}