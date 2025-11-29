package com.team1.airline.gui;

import com.team1.airline.dao.UserDAO;
import com.team1.airline.dao.impl.UserDAOImpl;
import com.team1.airline.entity.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * [회원가입 화면 패널]
 * - 신규 사용자의 정보를 입력받아 DB에 저장합니다.
 * - 아이디 중복 확인, 비밀번호 일치 여부, 필수 입력값 확인 등 유효성 검사를 수행합니다.
 */
public class SignUpPanel extends JPanel {

    private final MainApp mainApp;
    private final UserDAO userDAO; 

    private UITheme.RoundedTextField idField;
    private UITheme.RoundedPasswordField pwField;
    private UITheme.RoundedPasswordField pwConfirmField;
    private UITheme.RoundedTextField nameField;
    private UITheme.RoundedTextField passportField;
    private UITheme.RoundedTextField phoneField;
    
    // 아이디 중복 확인 완료 여부 플래그
    private boolean isIdChecked = false;

    public SignUpPanel(MainApp mainApp) {
        this.mainApp = mainApp;
        this.userDAO = new UserDAOImpl(); // 아이디 중복 검사를 위해 DAO 직접 사용
        
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_COLOR);

        // 내용이 길어질 수 있으므로 스크롤 패널 사용
        JScrollPane scrollPane = new JScrollPane(createCenterContainer());
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createCenterContainer() {
        JPanel centerContainer = new JPanel(new GridBagLayout());
        centerContainer.setBackground(UITheme.BG_COLOR);
        centerContainer.add(createFormPanel());
        return centerContainer;
    }

    private JPanel createFormPanel() {
        UITheme.RoundedPanel formPanel = new UITheme.RoundedPanel(30, Color.WHITE);
        formPanel.setLayout(new BorderLayout());
        formPanel.setPreferredSize(new Dimension(450, 750)); // 높이 넉넉하게
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // 헤더 (타이틀 + 닫기 버튼)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(10, 0, 20, 0));

        JLabel titleLabel = new JLabel("회원가입", SwingConstants.CENTER);
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.PRIMARY_BLUE);
        
        JButton closeButton = new JButton("X");
        closeButton.setForeground(Color.GRAY);
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setFont(new Font("Arial", Font.BOLD, 18));
        closeButton.addActionListener(e -> mainApp.showPanel("LOGIN"));

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(closeButton, BorderLayout.EAST);
        formPanel.add(headerPanel, BorderLayout.NORTH);

        // 입력 필드 목록
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(10, 30, 10, 30));

        // 1. 아이디 필드 & 중복확인 버튼
        contentPanel.add(UITheme.createFieldLabel("아이디"));
        contentPanel.add(Box.createVerticalStrut(5));
        
        JPanel idPanel = new JPanel(new BorderLayout(5, 0));
        idPanel.setOpaque(false);
        idPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        idPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        idField = new UITheme.RoundedTextField();
        
        UITheme.RoundedButton checkButton = new UITheme.RoundedButton("중복");
        checkButton.setBackground(Color.DARK_GRAY);
        checkButton.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        checkButton.setPreferredSize(new Dimension(60, 40));
        checkButton.addActionListener(e -> handleCheckDuplicate());
        
        idPanel.add(idField, BorderLayout.CENTER);
        idPanel.add(checkButton, BorderLayout.EAST);
        contentPanel.add(idPanel);
        
        contentPanel.add(Box.createVerticalStrut(15));
        
        // 2. 비밀번호 & 확인 필드
        contentPanel.add(UITheme.createFieldLabel("비밀번호/확인"));
        contentPanel.add(Box.createVerticalStrut(5));
        pwField = new UITheme.RoundedPasswordField();
        setFieldSize(pwField);
        contentPanel.add(pwField);
        contentPanel.add(Box.createVerticalStrut(5));
        pwConfirmField = new UITheme.RoundedPasswordField();
        setFieldSize(pwConfirmField);
        contentPanel.add(pwConfirmField);
        
        contentPanel.add(Box.createVerticalStrut(15));
        
        // 3. 이름
        contentPanel.add(UITheme.createFieldLabel("이름"));
        contentPanel.add(Box.createVerticalStrut(5));
        nameField = new UITheme.RoundedTextField();
        setFieldSize(nameField);
        contentPanel.add(nameField);

        contentPanel.add(Box.createVerticalStrut(15));
        
        // 4. 여권번호
        contentPanel.add(UITheme.createFieldLabel("여권번호"));
        contentPanel.add(Box.createVerticalStrut(5));
        passportField = new UITheme.RoundedTextField();
        setFieldSize(passportField);
        contentPanel.add(passportField);

        contentPanel.add(Box.createVerticalStrut(15));
        
        // 5. 휴대전화
        contentPanel.add(UITheme.createFieldLabel("휴대전화"));
        contentPanel.add(Box.createVerticalStrut(5));
        phoneField = new UITheme.RoundedTextField();
        setFieldSize(phoneField);
        contentPanel.add(phoneField);

        formPanel.add(contentPanel, BorderLayout.CENTER);

        // 가입 완료 버튼
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        UITheme.RoundedButton signUpButton = new UITheme.RoundedButton("가입 완료");
        signUpButton.setPreferredSize(new Dimension(280, 50));
        signUpButton.addActionListener(e -> handleSignUp());

        bottomPanel.add(signUpButton);
        formPanel.add(bottomPanel, BorderLayout.SOUTH);

        return formPanel;
    }
    
    private void setFieldSize(JComponent c) {
        c.setPreferredSize(new Dimension(100, 40));
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    /**
     * [아이디 중복 확인 로직]
     * - DB에서 아이디 존재 여부를 확인합니다.
     * - 사용 가능한 경우 아이디 필드를 비활성화하여 수정을 막습니다.
     */
    private void handleCheckDuplicate() {
        String userId = idField.getText().trim();
        if (userId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "아이디를 입력해주세요.");
            return;
        }
        User existingUser = userDAO.findByUserId(userId);
        if (existingUser != null) {
            JOptionPane.showMessageDialog(this, "이미 사용 중인 아이디입니다.");
            isIdChecked = false;
        } else {
            int choice = JOptionPane.showConfirmDialog(this, "사용 가능합니다. 사용하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                isIdChecked = true;
                idField.setEditable(false);
                idField.setBackground(new Color(240, 240, 240));
            }
        }
    }

    /**
     * [회원가입 처리 로직]
     * - 모든 필드의 입력 여부와 비밀번호 일치 여부를 검사합니다.
     * - 아이디 중복 확인 여부를 체크합니다.
     * - 모든 조건 만족 시 사용자 등록을 수행합니다.
     */
    private void handleSignUp() {
        if (idField.getText().isBlank() || new String(pwField.getPassword()).isBlank() ||
            nameField.getText().isBlank() || passportField.getText().isBlank() || phoneField.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "모든 정보를 입력해주세요.");
            return;
        }
        if (!new String(pwField.getPassword()).equals(new String(pwConfirmField.getPassword()))) {
            JOptionPane.showMessageDialog(this, "비밀번호가 일치하지 않습니다.");
            return;
        }
        if (!isIdChecked) {
            JOptionPane.showMessageDialog(this, "아이디 중복 확인을 해주세요.");
            return;
        }
        
        // 회원가입 요청
        boolean success = mainApp.getUserController().register(
                idField.getText().trim(),
                new String(pwField.getPassword()),
                nameField.getText().trim(),
                passportField.getText().trim(),
                phoneField.getText().trim()
        );
        
        if (success) {
            JOptionPane.showMessageDialog(this, "회원가입이 완료되었습니다.");
            clearFields();
            mainApp.showPanel("LOGIN");
        } else {
            JOptionPane.showMessageDialog(this, "회원가입 실패.");
            idField.setEditable(true);
            isIdChecked = false;
        }
    }
    
    // 입력 필드 초기화
    private void clearFields() {
        idField.setText(""); idField.setEditable(true); isIdChecked = false;
        pwField.setText(""); pwConfirmField.setText("");
        nameField.setText(""); passportField.setText(""); phoneField.setText("");
    }
}