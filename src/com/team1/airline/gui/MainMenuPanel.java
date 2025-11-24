package com.team1.airline.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 메인 메뉴 화면
 * - 디자인: 카드 형태의 큰 버튼
 * - 기능: 항공권 예매, 결제 내역 조회, 로그아웃
 */
public class MainMenuPanel extends JPanel {

    private final MainApp mainApp;

    private static final Color PRIMARY_BLUE = new Color(0, 122, 255);
    private static final Color BG_COLOR = new Color(245, 248, 250);
    private static final Font FONT_BTN_TITLE = new Font("SansSerif", Font.BOLD, 20);
    private static final Font FONT_BTN_DESC = new Font("SansSerif", Font.PLAIN, 14);

    public MainMenuPanel(MainApp mainApp) {
        this.mainApp = mainApp;
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);

        // 1. 상단 헤더
        add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. 중앙 메뉴 버튼 영역 (예매, 결제내역)
        add(createMenuGrid(), BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(20, 30, 20, 30));

        // 로고/타이틀
        JLabel titleLabel = new JLabel("Airline"); 
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        titleLabel.setForeground(PRIMARY_BLUE);

        // 로그아웃 버튼
        JButton logoutBtn = new JButton("로그아웃");
        logoutBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        logoutBtn.setForeground(Color.GRAY);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "로그아웃 하시겠습니까?", "로그아웃", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                mainApp.showPanel("LOGIN"); 
            }
        });

        header.add(titleLabel, BorderLayout.WEST);
        header.add(logoutBtn, BorderLayout.EAST);
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(header, BorderLayout.CENTER);
        JPanel line = new JPanel();
        line.setBackground(new Color(230, 230, 230));
        line.setPreferredSize(new Dimension(0, 1));
        wrapper.add(line, BorderLayout.SOUTH);

        return wrapper;
    }

    private JPanel createMenuGrid() {
    	JPanel gridPanel = new JPanel(new GridLayout(1, 3, 30, 0));
        gridPanel.setBackground(BG_COLOR);
        gridPanel.setBorder(new EmptyBorder(40, 40, 60, 40));

        // --- 메뉴 버튼 생성 ---
        
        // 1. 항공권 예매 버튼
        JPanel searchBtn = createMenuCard("🛫", "항공권 예매", "국내/해외 항공권을 검색하고 예약하세요.", e -> {
            mainApp.showPanel("SEARCH");
        });

        // 2. 결제 내역 버튼
        JPanel historyBtn = createMenuCard("💳", "결제 내역", "나의 예약 및 결제 내역을 확인하세요.", e -> {
            mainApp.showPanel("PAYMENT_HISTORY");
        });
        
        //3. 마이페이지 버튼
        JPanel myPageBtn = createMenuCard("👤", "마이페이지", "나의 개인정보를 확인하고 수정하세요.", e -> {
            //마이페이지 이동 추가
        });
        
        // 레이아웃 배치
        gridPanel.add(searchBtn);
        gridPanel.add(historyBtn);
        gridPanel.add(myPageBtn);

        return gridPanel;
    }

    private JPanel createMenuCard(String icon, String title, String desc, java.awt.event.ActionListener action) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true), 
                new EmptyBorder(30, 30, 30, 30)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 아이콘
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 60));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 제목
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_BTN_TITLE);
        titleLabel.setForeground(new Color(50, 50, 50));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 설명
        String[] lines = desc.split("\n");
               
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);
        textPanel.setOpaque(false); 
        textPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
     
        for (String line : lines) {
            JLabel lineLabel = new JLabel(line, SwingConstants.CENTER);
            lineLabel.setFont(FONT_BTN_DESC);
            lineLabel.setForeground(Color.GRAY);
            lineLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            textPanel.add(lineLabel);
            textPanel.add(Box.createVerticalStrut(3));
        }

        // 간격
        card.add(Box.createVerticalGlue());
        card.add(iconLabel);
        card.add(Box.createVerticalStrut(20));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(textPanel); 
        card.add(Box.createVerticalGlue());
        
        
        // 클릭 이벤트 연결
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                action.actionPerformed(null);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(240, 248, 255)); 
                card.setBorder(BorderFactory.createLineBorder(PRIMARY_BLUE, 2)); 
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
            }
        });

        return card;
    }
}