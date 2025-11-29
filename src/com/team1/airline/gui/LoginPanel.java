package com.team1.airline.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * [로그인 화면 패널]
 * - 애플리케이션 시작 시 가장 먼저 표시되는 화면입니다.
 * - 아이디와 비밀번호를 입력받아 UserController를 통해 인증을 수행합니다.
 * - 회원가입 화면으로 이동하는 링크를 제공합니다.
 */
public class LoginPanel extends JPanel {

    private final MainApp mainApp;
    private UITheme.RoundedTextField idField;
    private UITheme.RoundedPasswordField pwField;

    public LoginPanel(MainApp mainApp) {
        this.mainApp = mainApp;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_COLOR);

        // 중앙 정렬을 위한 GridBagLayout 컨테이너
        JPanel centerContainer = new JPanel(new GridBagLayout());
        centerContainer.setOpaque(false);
        centerContainer.add(createFormPanel());
        add(centerContainer, BorderLayout.CENTER);
    }

    /**
     * 로그인 폼 UI 생성
     * - 둥근 카드 형태의 패널 안에 입력 필드와 버튼을 배치합니다.
     */
    private JPanel createFormPanel() {
        // 둥근 카드 형태 패널 사용
        UITheme.RoundedPanel formPanel = new UITheme.RoundedPanel(30, Color.WHITE);
        formPanel.setLayout(new BorderLayout());
        formPanel.setPreferredSize(new Dimension(400, 480));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. 헤더 (타이틀)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("로그인", SwingConstants.CENTER);
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.PRIMARY_BLUE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        formPanel.add(headerPanel, BorderLayout.NORTH);

        // 2. 입력 영역
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(10, 30, 10, 30));

        // 아이디 입력 필드
        contentPanel.add(UITheme.createFieldLabel("아이디"));
        contentPanel.add(Box.createVerticalStrut(5));
        
        idField = new UITheme.RoundedTextField();
        idField.setPreferredSize(new Dimension(100, 40));
        idField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        idField.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(idField);
        
        contentPanel.add(Box.createVerticalStrut(20));

        // 비밀번호 입력 필드
        contentPanel.add(UITheme.createFieldLabel("비밀번호"));
        contentPanel.add(Box.createVerticalStrut(5));
        
        pwField = new UITheme.RoundedPasswordField();
        pwField.setPreferredSize(new Dimension(100, 40));
        pwField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        pwField.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(pwField);

        contentPanel.add(Box.createVerticalStrut(20));

        // 회원가입 링크
        JLabel signUpLink = new JLabel("계정이 없으신가요? 가입하기");
        signUpLink.setFont(UITheme.FONT_PLAIN);
        signUpLink.setForeground(Color.GRAY);
        signUpLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signUpLink.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        signUpLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainApp.showPanel("SIGNUP"); // 회원가입 화면으로 전환
            }
        });
        contentPanel.add(signUpLink);

        formPanel.add(contentPanel, BorderLayout.CENTER);

        // 3. 버튼 영역
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(10, 0, 20, 0));

        UITheme.RoundedButton loginButton = new UITheme.RoundedButton("로그인");
        loginButton.setPreferredSize(new Dimension(280, 50));
        loginButton.addActionListener(e -> performLogin());

        bottomPanel.add(loginButton);
        formPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        return formPanel;
    }

    /**
     * [로그인 로직]
     * - 입력값을 가져와 UserController를 통해 인증 시도
     * - 성공 시 메인 메뉴로 이동, 실패 시 에러 메시지 표시
     */
    private void performLogin() {
        String userId = idField.getText();
        String password = new String(pwField.getPassword());
        boolean isLoginSuccess = mainApp.getUserController().login(userId, password);

        if (isLoginSuccess) {
            idField.setText("");
            pwField.setText("");
            JOptionPane.showMessageDialog(this, userId + "님 환영합니다!");
            mainApp.showPanel("MAIN"); 
        } else {
            JOptionPane.showMessageDialog(this, "아이디 또는 비밀번호를 확인해주세요.", "로그인 실패", JOptionPane.ERROR_MESSAGE);
        }
    }
}