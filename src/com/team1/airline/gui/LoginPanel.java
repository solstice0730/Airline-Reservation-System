package com.team1.airline.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 로그인 화면
 * - 아이디/비밀번호 입력 및 회원가입 화면 이동 기능
 */
public class LoginPanel extends JPanel {

    private final MainApp mainApp;
    
    // 스타일 상수
    private final Color PRIMARY_BLUE = new Color(0, 122, 255);
    private final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, 14);
    private final Font INPUT_FONT = new Font("SansSerif", Font.PLAIN, 14);

    private JTextField idField;
    private JPasswordField pwField;

    public LoginPanel(MainApp mainApp) {
        this.mainApp = mainApp;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 화면 중앙 배치를 위한 컨테이너
        JPanel centerContainer = new JPanel(new GridBagLayout());
        centerContainer.setBackground(Color.WHITE);
        
        // 로그인 폼 패널 생성
        JPanel formPanel = createFormPanel();
        
        centerContainer.add(formPanel);
        add(centerContainer, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        formPanel.setPreferredSize(new Dimension(400, 450));

        // 1. 헤더
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_BLUE);
        headerPanel.setBorder(new EmptyBorder(15, 0, 15, 0));
        
        JLabel titleLabel = new JLabel("로그인", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        formPanel.add(headerPanel, BorderLayout.NORTH);

        // 2. 입력 필드 영역
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        contentPanel.add(createLabel("아이디"));
        contentPanel.add(Box.createVerticalStrut(5));
        idField = createTextField();
        contentPanel.add(idField);
        
        contentPanel.add(Box.createVerticalStrut(20));

        contentPanel.add(createLabel("비밀번호"));
        contentPanel.add(Box.createVerticalStrut(5));
        pwField = createPasswordField();
        contentPanel.add(pwField);

        contentPanel.add(Box.createVerticalStrut(20));

        // 회원가입 링크
        JLabel signUpLink = new JLabel("계정이 없으신가요? 가입하기");
        signUpLink.setFont(new Font("SansSerif", Font.PLAIN, 13));
        signUpLink.setForeground(Color.DARK_GRAY);
        signUpLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signUpLink.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        signUpLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainApp.showPanel("SIGNUP");
            }
        });
        contentPanel.add(signUpLink);

        formPanel.add(contentPanel, BorderLayout.CENTER);

        // 3. 하단 로그인 버튼
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(0, 0, 30, 0));

        JButton loginButton = new JButton("로그인");
        loginButton.setPreferredSize(new Dimension(320, 45));
        loginButton.setBackground(PRIMARY_BLUE);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        loginButton.setFocusPainted(false);
        loginButton.setBorder(null);

        loginButton.addActionListener(e -> {
            String userId = idField.getText();
            String password = new String(pwField.getPassword());

            if (userId.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "아이디와 비밀번호를 모두 입력해주세요.", "로그인 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean loggedIn = mainApp.getUserController().login(userId, password);

            if (loggedIn) {
                mainApp.showPanel("MAIN");
            } else {
                JOptionPane.showMessageDialog(this, "아이디 또는 비밀번호가 일치하지 않습니다.", "로그인 실패", JOptionPane.ERROR_MESSAGE);
                pwField.setText(""); // 비밀번호 필드 초기화
            }
        });

        bottomPanel.add(loginButton);
        formPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        return formPanel;
    }

    // --- Helper Methods ---
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(INPUT_FONT);
        field.setPreferredSize(new Dimension(100, 35));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(INPUT_FONT);
        field.setPreferredSize(new Dimension(100, 35));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }
}