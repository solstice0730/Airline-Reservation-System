package com.team1.airline.gui;

import com.team1.airline.dao.UserDAO;
import com.team1.airline.dao.impl.UserDAOImpl;
import com.team1.airline.entity.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * [회원가입 패널]
 * 신규 사용자 등록 및 아이디 중복 검사
 */
public class SignUpPanel extends JPanel {

    private final MainApp mainApp;
    private final UserDAO userDAO; 
    
    private final Color PRIMARY_BLUE = new Color(0, 122, 255);
    private final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, 14);
    private final Font INPUT_FONT = new Font("SansSerif", Font.PLAIN, 14);

    private JTextField idField;
    private JPasswordField pwField;
    private JPasswordField pwConfirmField;
    private JTextField nameField;
    private JTextField passportField;
    private JTextField phoneField;
    
    private boolean isIdChecked = false; // 중복 확인 여부

    public SignUpPanel(MainApp mainApp) {
        this.mainApp = mainApp;
        this.userDAO = new UserDAOImpl();
        
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel centerContainer = new JPanel(new GridBagLayout());
        centerContainer.setBackground(Color.WHITE);
        centerContainer.add(createFormPanel());
        
        add(centerContainer, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        formPanel.setPreferredSize(new Dimension(400, 680));

        // 헤더
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_BLUE);
        headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("회원가입", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        JButton closeButton = new JButton("X");
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(PRIMARY_BLUE);
        closeButton.setBorder(null);
        closeButton.setFocusPainted(false);
        closeButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        closeButton.addActionListener(e -> mainApp.showPanel("LOGIN"));

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(closeButton, BorderLayout.EAST);
        formPanel.add(headerPanel, BorderLayout.NORTH);

        // 입력 필드
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        contentPanel.add(createLabel("아이디"));
        contentPanel.add(Box.createVerticalStrut(5));
        
        JPanel idPanel = new JPanel(new BorderLayout(5, 0));
        idPanel.setBackground(Color.WHITE);
        idPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        idPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        idField = new JTextField();
        idField.setFont(INPUT_FONT);
        
        JButton checkButton = new JButton("중복 확인");
        checkButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        checkButton.setBackground(Color.DARK_GRAY);
        checkButton.setForeground(Color.WHITE);
        checkButton.setFocusPainted(false);
        checkButton.addActionListener(e -> handleCheckDuplicate());
        
        idPanel.add(idField, BorderLayout.CENTER);
        idPanel.add(checkButton, BorderLayout.EAST);
        contentPanel.add(idPanel);
        
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(createLabel("비밀번호/확인"));
        contentPanel.add(Box.createVerticalStrut(5));
        pwField = createPasswordField();
        contentPanel.add(pwField);
        contentPanel.add(Box.createVerticalStrut(5));
        pwConfirmField = createPasswordField();
        contentPanel.add(pwConfirmField);
        
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(createLabel("이름"));
        contentPanel.add(Box.createVerticalStrut(5));
        nameField = createTextField();
        contentPanel.add(nameField);

        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(createLabel("여권번호"));
        contentPanel.add(Box.createVerticalStrut(5));
        passportField = createTextField();
        contentPanel.add(passportField);

        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(createLabel("휴대전화"));
        contentPanel.add(Box.createVerticalStrut(5));
        phoneField = createTextField();
        contentPanel.add(phoneField);

        formPanel.add(contentPanel, BorderLayout.CENTER);

        // 가입 버튼
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(0, 0, 30, 0));

        JButton signUpButton = new JButton("회원가입");
        signUpButton.setPreferredSize(new Dimension(320, 45));
        signUpButton.setBackground(PRIMARY_BLUE);
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        signUpButton.setFocusPainted(false);
        signUpButton.setBorder(null);
        signUpButton.addActionListener(e -> handleSignUp());

        bottomPanel.add(signUpButton);
        formPanel.add(bottomPanel, BorderLayout.SOUTH);

        return formPanel;
    }

    private void handleCheckDuplicate() {
        String userId = idField.getText().trim();
        if (userId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "아이디를 입력해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User existingUser = userDAO.findByUserId(userId);
        if (existingUser != null) {
            JOptionPane.showMessageDialog(this, "이미 사용 중인 아이디입니다.", "중복 확인", JOptionPane.ERROR_MESSAGE);
            isIdChecked = false;
        } else {
            int choice = JOptionPane.showConfirmDialog(this, "사용 가능한 아이디입니다. 사용하시겠습니까?", "중복 확인", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                isIdChecked = true;
                idField.setEditable(false);
                idField.setBackground(new Color(240, 240, 240));
            }
        }
    }

    private void handleSignUp() {
        if (idField.getText().isBlank() || new String(pwField.getPassword()).isBlank() ||
            nameField.getText().isBlank() || passportField.getText().isBlank() || phoneField.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "모든 정보를 입력해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!new String(pwField.getPassword()).equals(new String(pwConfirmField.getPassword()))) {
            JOptionPane.showMessageDialog(this, "비밀번호가 일치하지 않습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!isIdChecked) {
            JOptionPane.showMessageDialog(this, "아이디 중복 확인을 해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }

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
            JOptionPane.showMessageDialog(this, "회원가입 실패. 다시 시도해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
            idField.setEditable(true);
            idField.setBackground(Color.WHITE);
            isIdChecked = false;
        }
    }
    
    private void clearFields() {
        idField.setText("");
        idField.setEditable(true);
        idField.setBackground(Color.WHITE);
        isIdChecked = false;
        pwField.setText("");
        pwConfirmField.setText("");
        nameField.setText("");
        passportField.setText("");
        phoneField.setText("");
    }

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