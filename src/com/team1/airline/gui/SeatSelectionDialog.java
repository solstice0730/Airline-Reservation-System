package com.team1.airline.gui;

import com.team1.airline.entity.Aircraft;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SeatSelectionDialog extends JDialog {

    private Set<String> selectedSeats = new HashSet<>(); // 선택된 좌석 목록
    private final Set<String> occupiedSeats;
    
    private final int neededBiz;  // 선택해야 할 비즈니스 석 수
    private final int neededEco;  // 선택해야 할 이코노미 석 수
    private int currentBizCount = 0;
    private int currentEcoCount = 0;

    private final JPanel seatPanel;
    private JLabel statusLabel; // 현재 선택 현황 표시

    // 스타일 상수
    private final Color COLOR_BUSINESS = new Color(0, 51, 102); 
    private final Color COLOR_ECONOMY = new Color(100, 149, 237);
    private final Color COLOR_TAKEN = new Color(200, 200, 200);   
    private final Color COLOR_SELECTED = new Color(0, 180, 0);    

    public SeatSelectionDialog(Frame owner, Aircraft aircraft, List<String> reservedSeatList, int neededBiz, int neededEco) {
        super(owner, "좌석 선택", true);
        this.occupiedSeats = new HashSet<>(reservedSeatList);
        this.neededBiz = neededBiz;
        this.neededEco = neededEco;

        setLayout(new BorderLayout());
        setSize(500, 800);
        setLocationRelativeTo(owner);

        // 1. 상단 안내 (Legend + Status)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(createLegendPanel(), BorderLayout.NORTH);
        
        statusLabel = new JLabel(getStatusText(), SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        statusLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        statusLabel.setForeground(Color.DARK_GRAY);
        topPanel.add(statusLabel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);

        // 2. 좌석 배치 스크롤 패널
        seatPanel = new JPanel(new GridBagLayout());
        seatPanel.setBackground(Color.WHITE);
        generateSeats(aircraft);

        JScrollPane scrollPane = new JScrollPane(seatPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // 3. 하단 완료 버튼
        JButton confirmBtn = new JButton("선택 완료");
        confirmBtn.setBackground(new Color(0, 122, 255));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        confirmBtn.setPreferredSize(new Dimension(0, 50));
        
        confirmBtn.addActionListener(e -> {
            if (currentBizCount != neededBiz || currentEcoCount != neededEco) {
                JOptionPane.showMessageDialog(this, 
                    String.format("좌석을 모두 선택해주세요.\n(비즈니스 %d석, 이코노미 %d석)", neededBiz, neededEco));
            } else {
                dispose();
            }
        });
        
        add(confirmBtn, BorderLayout.SOUTH);
    }

    private String getStatusText() {
        return String.format("선택 현황: 비즈니스 %d/%d, 이코노미 %d/%d", 
                currentBizCount, neededBiz, currentEcoCount, neededEco);
    }

    private JPanel createLegendPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 5, 10));
        panel.add(createLegendItem(COLOR_BUSINESS, "비즈니스"));
        panel.add(createLegendItem(COLOR_ECONOMY, "이코노미"));
        panel.add(createLegendItem(COLOR_TAKEN, "예약됨"));
        panel.add(createLegendItem(COLOR_SELECTED, "선택됨"));
        return panel;
    }
    
    private JPanel createLegendItem(Color color, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        p.setOpaque(false);
        JPanel colorBox = new JPanel();
        colorBox.setBackground(color);
        colorBox.setPreferredSize(new Dimension(15, 15));
        p.add(colorBox);
        p.add(new JLabel(text));
        return p;
    }

    private void generateSeats(Aircraft aircraft) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4); 

        int currentRow = 1;

        // --- 비즈니스석 ---
        int bizRows = (int) Math.ceil((double) aircraft.getBusiness() / 4);
        addHeader(gbc, "BUSINESS CLASS", currentRow++);
        char[] bizCols = {'A', 'C', 'D', 'F'}; 
        
        for (int r = 0; r < bizRows; r++) {
            for (int c = 0; c < 4; c++) {
                String seatNum = currentRow + String.valueOf(bizCols[c]);
                gbc.gridx = (c >= 2) ? c + 1 : c; 
                gbc.gridy = currentRow;
                addSeatButton(seatNum, true, gbc);
            }
            currentRow++;
        }

        currentRow++; // 간격
        
        // --- 이코노미석 ---
        int ecoRows = (int) Math.ceil((double) aircraft.getEconomy() / 6);
        addHeader(gbc, "ECONOMY CLASS", currentRow++);
        char[] ecoCols = {'A', 'B', 'C', 'D', 'E', 'F'};

        for (int r = 0; r < ecoRows; r++) {
            for (int c = 0; c < 6; c++) {
                String seatNum = currentRow + String.valueOf(ecoCols[c]);
                gbc.gridx = (c >= 3) ? c + 1 : c; 
                gbc.gridy = currentRow;
                addSeatButton(seatNum, false, gbc);
            }
            currentRow++;
        }
    }
    
    private void addHeader(GridBagConstraints gbc, String text, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 7;
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(Color.GRAY);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(new EmptyBorder(10, 0, 5, 0));
        seatPanel.add(label, gbc);
        gbc.gridwidth = 1; 
    }

    private void addSeatButton(String seatNum, boolean isBusiness, GridBagConstraints gbc) {
        JToggleButton btn = new JToggleButton(seatNum);
        btn.setPreferredSize(new Dimension(50, 40));
        btn.setMargin(new Insets(0,0,0,0));
        btn.setFocusPainted(false);
        
        if (occupiedSeats.contains(seatNum)) {
            btn.setBackground(COLOR_TAKEN);
            btn.setEnabled(false);
            btn.setText("X");
        } else {
            btn.setBackground(isBusiness ? COLOR_BUSINESS : COLOR_ECONOMY);
            btn.setForeground(Color.WHITE);
            
            btn.addActionListener(e -> {
                if (btn.isSelected()) {
                    // 선택 시도
                    if (isBusiness) {
                        if (currentBizCount < neededBiz) {
                            currentBizCount++;
                            selectedSeats.add(seatNum);
                            btn.setBackground(COLOR_SELECTED);
                        } else {
                            btn.setSelected(false); // 선택 취소
                            JOptionPane.showMessageDialog(this, "비즈니스석은 " + neededBiz + "명만 선택 가능합니다.");
                        }
                    } else { // Economy
                        if (currentEcoCount < neededEco) {
                            currentEcoCount++;
                            selectedSeats.add(seatNum);
                            btn.setBackground(COLOR_SELECTED);
                        } else {
                            btn.setSelected(false);
                            JOptionPane.showMessageDialog(this, "이코노미석은 " + neededEco + "명만 선택 가능합니다.");
                        }
                    }
                } else {
                    // 선택 해제
                    if (isBusiness) currentBizCount--;
                    else currentEcoCount--;
                    
                    selectedSeats.remove(seatNum);
                    btn.setBackground(isBusiness ? COLOR_BUSINESS : COLOR_ECONOMY);
                }
                statusLabel.setText(getStatusText());
            });
        }
        
        seatPanel.add(btn, gbc);
    }

    /** * 선택된 좌석들을 콤마(,)로 구분된 문자열로 반환 
     * 예: "1A, 1C" 
     */
    public String getSelectedSeats() {
        if (currentBizCount == neededBiz && currentEcoCount == neededEco) {
            return selectedSeats.stream().sorted().collect(Collectors.joining(", "));
        }
        return null; // 선택 완료되지 않음
    }
}