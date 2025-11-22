package com.team1.airline.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 메인 메뉴 화면
 * - 검색 및 결제 내역 이동 메뉴
 * - 우측 상단 로그아웃 기능
 */
public class MainMenuPanel extends JPanel {

    private final MainApp mainApp;

    public MainMenuPanel(MainApp mainApp) {
        this.mainApp = mainApp;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 400));

        // 1. 상단 헤더 (제목, 로그아웃)
        add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. 중앙 버튼 (검색, 결제내역)
        add(createCenterButtonPanel(), BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("메인 화면 (디자인 예정)", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));

        JLabel logoutLabel = new JLabel("로그아웃");
        logoutLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        logoutLabel.setForeground(Color.DARK_GRAY);
        logoutLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // 로그아웃 클릭 시 로그인 화면 이동
        logoutLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                        MainMenuPanel.this, "로그아웃 하시겠습니까?", "로그아웃", JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    mainApp.showPanel("LOGIN");
                }
            }
        });

        // 중앙 정렬 균형을 위한 더미 라벨
        JLabel dummyLabel = new JLabel("로그아웃");
        dummyLabel.setForeground(new Color(0,0,0,0)); // 투명

        headerPanel.add(dummyLabel, BorderLayout.WEST);
        headerPanel.add(title, BorderLayout.CENTER);
        headerPanel.add(logoutLabel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createCenterButtonPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setLayout(new GridLayout(1, 2, 40, 0));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(80, 120, 80, 120));

        JButton searchButton = new JButton("항공권 검색");
        searchButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(e -> mainApp.showPanel("SEARCH"));

        JButton payHistoryButton = new JButton("결제 내역");
        payHistoryButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        payHistoryButton.setFocusPainted(false);
        payHistoryButton.addActionListener(e -> mainApp.showPanel("PAYMENT_HISTORY"));

        centerPanel.add(searchButton);
        centerPanel.add(payHistoryButton);

        return centerPanel;
    }
}