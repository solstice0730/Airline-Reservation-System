package com.team1.airline.gui;

import com.team1.airline.entity.Aircraft;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * [좌석 선택 다이얼로그]
 * - 비즈니스석과 이코노미석을 시각적으로 배치하여 제공합니다.
 * - 이미 예약된 좌석은 비활성화 처리합니다.
 * - GridBagLayout을 사용하여 통로(Aisle)와 좌석 배치를 구현합니다.
 */
public class SeatSelectionDialog extends JDialog {

    private Set<String> selectedSeats = new HashSet<>();
    private final Set<String> occupiedSeats; // 예약 완료된 좌석 목록
    
    private final int neededBiz; // 선택해야 할 비즈니스석 수
    private final int neededEco; // 선택해야 할 이코노미석 수
    private int currentBizCount = 0;
    private int currentEcoCount = 0;

    private final JPanel seatPanel;
    private JLabel statusLabel;

    // 디자인 상수
    private final Color COLOR_BUSINESS = new Color(63, 81, 181); // 인디고
    private final Color COLOR_ECONOMY = new Color(33, 150, 243); // 블루
    private final Color COLOR_TAKEN = new Color(224, 224, 224);  // 회색
    private final Color COLOR_SELECTED = new Color(76, 175, 80); // 그린

    public SeatSelectionDialog(Frame owner, Aircraft aircraft, List<String> reservedSeatList, int neededBiz, int neededEco) {
        super(owner, "좌석 선택", true);
        this.occupiedSeats = new HashSet<>(reservedSeatList);
        this.neededBiz = neededBiz;
        this.neededEco = neededEco;

        setLayout(new BorderLayout());
        setSize(550, 850);
        setLocationRelativeTo(owner);
        getContentPane().setBackground(Color.WHITE);

        // 1. 상단 정보 패널
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(new EmptyBorder(20, 0, 10, 0));
        
        topPanel.add(createLegendPanel(), BorderLayout.NORTH);
        
        statusLabel = new JLabel(getStatusText(), SwingConstants.CENTER);
        statusLabel.setFont(UITheme.FONT_BOLD);
        statusLabel.setForeground(UITheme.PRIMARY_BLUE);
        topPanel.add(statusLabel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);

        // 2. 좌석 영역 (스크롤 가능)
        seatPanel = new JPanel(new GridBagLayout());
        seatPanel.setBackground(Color.WHITE);
        generateSeats(aircraft); // 좌석 생성 로직 호출

        JScrollPane scrollPane = new JScrollPane(seatPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // 3. 하단 버튼
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(10, 0, 20, 0));

        UITheme.RoundedButton confirmBtn = new UITheme.RoundedButton("선택 완료");
        confirmBtn.setPreferredSize(new Dimension(300, 50));
        
        confirmBtn.addActionListener(e -> {
            // 필요 좌석 수를 모두 채워야 완료 가능
            if (currentBizCount != neededBiz || currentEcoCount != neededEco) {
                JOptionPane.showMessageDialog(this, 
                    String.format("좌석을 모두 선택해주세요.\n(비즈니스 %d석, 이코노미 %d석)", neededBiz, neededEco));
            } else {
                dispose();
            }
        });
        
        bottomPanel.add(confirmBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private String getStatusText() {
        return String.format("선택 현황: 비즈니스 %d/%d  |  이코노미 %d/%d", 
                currentBizCount, neededBiz, currentEcoCount, neededEco);
    }

    // 좌석 색상 범례 생성
    private JPanel createLegendPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(Color.WHITE);
        panel.add(createLegendItem(COLOR_BUSINESS, "비즈니스"));
        panel.add(createLegendItem(COLOR_ECONOMY, "이코노미"));
        panel.add(createLegendItem(COLOR_TAKEN, "예약됨"));
        panel.add(createLegendItem(COLOR_SELECTED, "선택됨"));
        return panel;
    }
    
    private JPanel createLegendItem(Color color, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        p.setOpaque(false);
        
        JPanel colorBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);
            }
        };
        colorBox.setPreferredSize(new Dimension(20, 20));
        
        JLabel label = new JLabel(text);
        label.setFont(UITheme.FONT_PLAIN);
        
        p.add(colorBox);
        p.add(label);
        return p;
    }

    /**
     * [핵심 로직] 항공기 정보를 바탕으로 좌석 버튼을 그리드에 배치합니다.
     * GridBagLayout을 사용하여 통로와 좌석 배치를 구현합니다.
     * - 총 컬럼: 7개 (0~6)
     * - 통로: 인덱스 3
     */
    private void generateSeats(Aircraft aircraft) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6); 

        int currentRow = 0;

        // 0. 통로(Aisle) 공간 확보 (3번 인덱스에 투명 컴포넌트 배치)
        gbc.gridx = 3; 
        gbc.gridy = 0;
        gbc.gridheight = 100; // 전체 높이에 걸쳐 통로 유지
        seatPanel.add(Box.createHorizontalStrut(40), gbc);
        gbc.gridheight = 1;

        // 1. 비즈니스석 배치 (4열: A, C | D, F)
        // [배치 전략] 양 끝(0, 6)을 비우고 안쪽으로 모음: [1] [2] - [통로] - [4] [5]
        int bizRows = (int) Math.ceil((double) aircraft.getBusiness() / 4);
        addHeader(gbc, "BUSINESS CLASS", ++currentRow);
        char[] bizCols = {'A', 'C', 'D', 'F'}; 
        
        // 좌표 매핑 변경 (1, 2, 4, 5)
        int[] bizGridX = {1, 2, 4, 5}; 
        
        for (int r = 0; r < bizRows; r++) {
            currentRow++;
            for (int c = 0; c < 4; c++) {
                String seatNum = currentRow + String.valueOf(bizCols[c]);
                gbc.gridx = bizGridX[c]; 
                gbc.gridy = currentRow;
                addSeatButton(seatNum, true, gbc);
            }
        }

        currentRow++; 
        
        // 2. 이코노미석 배치 (6열: A, B, C | D, E, F)
        // [배치 전략] 꽉 채움: [0] [1] [2] - [통로] - [4] [5] [6]
        int ecoRows = (int) Math.ceil((double) aircraft.getEconomy() / 6);
        addHeader(gbc, "ECONOMY CLASS", ++currentRow);
        char[] ecoCols = {'A', 'B', 'C', 'D', 'E', 'F'};
        
        int[] ecoGridX = {0, 1, 2, 4, 5, 6};

        for (int r = 0; r < ecoRows; r++) {
            currentRow++;
            for (int c = 0; c < 6; c++) {
                String seatNum = currentRow + String.valueOf(ecoCols[c]);
                gbc.gridx = ecoGridX[c];
                gbc.gridy = currentRow;
                addSeatButton(seatNum, false, gbc);
            }
        }
    }
    
    private void addHeader(GridBagConstraints gbc, String text, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 7; // 전체 폭(0~6)을 아우름
        
        JLabel label = new JLabel(text);
        label.setFont(UITheme.FONT_BOLD);
        label.setForeground(Color.GRAY);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(new EmptyBorder(20, 0, 10, 0));
        
        seatPanel.add(label, gbc);
        gbc.gridwidth = 1;
    }

    private void addSeatButton(String seatNum, boolean isBusiness, GridBagConstraints gbc) {
        JToggleButton btn = new JToggleButton(seatNum) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isSelected()) {
                    g2.setColor(COLOR_SELECTED);
                } else if (!isEnabled()) {
                    g2.setColor(COLOR_TAKEN);
                } else {
                    g2.setColor(getBackground());
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        
        btn.setPreferredSize(new Dimension(55, 45));
        btn.setMargin(new Insets(0,0,0,0));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFont(new Font("Arial", Font.PLAIN, 12));
        
        if (occupiedSeats.contains(seatNum)) {
            btn.setEnabled(false); // 예약된 좌석 비활성화
            btn.setForeground(Color.GRAY);
        } else {
            btn.setBackground(isBusiness ? COLOR_BUSINESS : COLOR_ECONOMY);
            btn.setForeground(Color.WHITE);
            
            // 클릭 이벤트: 선택 수 제한 확인 및 상태 업데이트
            btn.addActionListener(e -> {
                if (btn.isSelected()) {
                    if (isBusiness) {
                        if (currentBizCount < neededBiz) {
                            currentBizCount++;
                            selectedSeats.add(seatNum);
                        } else {
                            btn.setSelected(false);
                            JOptionPane.showMessageDialog(this, "비즈니스석은 " + neededBiz + "명만 선택 가능합니다.");
                        }
                    } else {
                        if (currentEcoCount < neededEco) {
                            currentEcoCount++;
                            selectedSeats.add(seatNum);
                        } else {
                            btn.setSelected(false);
                            JOptionPane.showMessageDialog(this, "이코노미석은 " + neededEco + "명만 선택 가능합니다.");
                        }
                    }
                } else {
                    if (isBusiness) currentBizCount--; else currentEcoCount--;
                    selectedSeats.remove(seatNum);
                }
                statusLabel.setText(getStatusText());
            });
        }
        seatPanel.add(btn, gbc);
    }

    public String getSelectedSeats() {
        if (currentBizCount == neededBiz && currentEcoCount == neededEco) {
            return selectedSeats.stream().sorted().collect(Collectors.joining(", "));
        }
        return null; 
    }
}