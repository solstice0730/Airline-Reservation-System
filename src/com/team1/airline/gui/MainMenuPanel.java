package com.team1.airline.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * [메인 메뉴 패널]
 * - 로그인 후 처음으로 만나는 대시보드 화면입니다.
 * - '항공권 예매', '결제 내역', '마이페이지' 3개의 주요 기능으로 이동하는 카드형 버튼을 제공합니다.
 */
public class MainMenuPanel extends JPanel {

    private final MainApp mainApp;

    public MainMenuPanel(MainApp mainApp) {
        this.mainApp = mainApp;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_COLOR);

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMenuGrid(), BorderLayout.CENTER);
    }

    /**
     * 상단 헤더 생성 (로고 및 로그아웃 버튼)
     */
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("Airline System"); 
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        titleLabel.setForeground(UITheme.PRIMARY_BLUE);

        JButton logoutBtn = new JButton("로그아웃");
        logoutBtn.setFont(UITheme.FONT_PLAIN);
        logoutBtn.setForeground(Color.GRAY);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // 로그아웃 처리
        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "로그아웃 하시겠습니까?", "로그아웃", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                mainApp.getUserController().logout();
                mainApp.showPanel("LOGIN"); 
            }
        });

        header.add(titleLabel, BorderLayout.WEST);
        header.add(logoutBtn, BorderLayout.EAST);
        return header;
    }

    /**
     * 중앙 메뉴 그리드 생성 (3개의 카드 버튼 배치)
     */
    private JPanel createMenuGrid() {
        JPanel gridPanel = new JPanel(new GridLayout(1, 3, 30, 0));
        gridPanel.setBackground(UITheme.BG_COLOR);
        gridPanel.setBorder(new EmptyBorder(40, 40, 60, 40));

        JPanel searchBtn = createMenuCard("🛫", "항공권 예매", "국내/해외 항공권을 검색하고 예약하세요.", 
                e -> mainApp.showPanel("SEARCH"));

        JPanel historyBtn = createMenuCard("💳", "결제 내역", "나의 예약 및 결제 내역을 확인하세요.", 
                e -> mainApp.showPanel("PAYMENT_HISTORY"));
        
        JPanel myPageBtn = createMenuCard("👤", "마이페이지", "나의 개인정보를 확인하고 수정하세요.", 
                e -> mainApp.showPanel("MYPAGE"));
        
        gridPanel.add(searchBtn);
        gridPanel.add(historyBtn);
        gridPanel.add(myPageBtn);

        return gridPanel;
    }

    /**
     * [카드형 메뉴 버튼 생성 메서드]
     * - 아이콘, 제목, 설명, 클릭 이벤트를 받아 둥근 패널 형태의 버튼을 생성합니다.
     */
    private JPanel createMenuCard(String icon, String title, String desc, java.awt.event.ActionListener action) {
        UITheme.RoundedPanel card = new UITheme.RoundedPanel(30, Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(30, 30, 30, 30));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 60));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UITheme.FONT_SUBTITLE);
        titleLabel.setForeground(UITheme.TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false); 
        textPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
     
        // 설명 텍스트 줄바꿈 처리
        for (String line : desc.split("\n")) {
            JLabel lineLabel = new JLabel(line, SwingConstants.CENTER);
            lineLabel.setFont(UITheme.FONT_PLAIN);
            lineLabel.setForeground(Color.GRAY);
            lineLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            textPanel.add(lineLabel);
            textPanel.add(Box.createVerticalStrut(3));
        }

        card.add(Box.createVerticalGlue());
        card.add(iconLabel);
        card.add(Box.createVerticalStrut(20));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(textPanel); 
        card.add(Box.createVerticalGlue());
        
        // 클릭 리스너 등록
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { action.actionPerformed(null); }
        });
        return card;
    }
}