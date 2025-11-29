package com.team1.airline.gui;

import com.team1.airline.dao.UserDAO;
import com.team1.airline.dao.impl.UserDAOImpl;
import com.team1.airline.entity.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * [마이페이지 패널]
 * - 로그인한 사용자의 정보를 표시합니다.
 * - 비밀번호, 여권번호, 전화번호 변경 기능을 제공합니다.
 * - 회원 탈퇴 기능을 포함합니다.
 */
public class MyPagePanel extends JPanel {

    private final MainApp mainApp;
    private final UserDAO userDAO;
    private User currentUser;

    private JLabel nameIdLabel, passportValueLabel, phoneValueLabel, mileageLabel;

    public MyPagePanel(MainApp mainApp) {
        this.mainApp = mainApp;
        this.userDAO = new UserDAOImpl();
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_COLOR);
        add(UITheme.createTitlePanel(mainApp, "마이페이지", "MAIN"), BorderLayout.NORTH);
        add(createContentPanel(), BorderLayout.CENTER);
    }

    private JPanel createContentPanel() {
        JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(UITheme.BG_COLOR);

        UITheme.RoundedPanel card = new UITheme.RoundedPanel(30, Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(500, 550));
        card.setBorder(new EmptyBorder(30, 40, 30, 40));

        // 프로필 섹션
        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        profilePanel.setOpaque(false);
        profilePanel.add(createProfileIcon());
        
        nameIdLabel = new JLabel("사용자 정보 없음");
        nameIdLabel.setFont(UITheme.FONT_SUBTITLE);
        profilePanel.add(nameIdLabel);
        
        card.add(profilePanel);
        card.add(Box.createVerticalStrut(30));

        // 정보 행 (비밀번호, 여권번호, 전화번호)
        card.add(createRow("비밀번호", "****", e -> openChangePasswordDialog()));
        card.add(Box.createVerticalStrut(10));
        
        passportValueLabel = new JLabel("-"); 
        passportValueLabel.setFont(UITheme.FONT_PLAIN);
        card.add(createRowLabel("여권번호", passportValueLabel, e -> openChangePassportDialog()));
        card.add(Box.createVerticalStrut(10));
        
        phoneValueLabel = new JLabel("-"); 
        phoneValueLabel.setFont(UITheme.FONT_PLAIN);
        card.add(createRowLabel("전화번호", phoneValueLabel, e -> openChangePhoneDialog()));
        
        card.add(Box.createVerticalStrut(20));
        
        // 마일리지
        mileageLabel = new JLabel("0 P");
        mileageLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        mileageLabel.setForeground(new Color(0, 150, 0));
        JPanel mileRow = createRowLabel("마일리지", mileageLabel, null);
        card.add(mileRow);
        
        card.add(Box.createVerticalGlue());

        // 하단 저장 버튼
        UITheme.RoundedButton saveButton = new UITheme.RoundedButton("저장");
        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        saveButton.addActionListener(e -> saveChangesToDB());
        card.add(saveButton);
        
        card.add(Box.createVerticalStrut(10));

        // 회원탈퇴 버튼
        JButton delButton = new JButton("회원탈퇴");
        delButton.setFont(UITheme.FONT_PLAIN);
        delButton.setForeground(Color.GRAY);
        delButton.setContentAreaFilled(false);
        delButton.setBorderPainted(false);
        delButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        delButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        delButton.addActionListener(e -> deleteAccount());
        card.add(delButton);

        container.add(card);
        return container;
    }

    // [디자인 개선] 버튼을 예쁜 스타일로 생성하는 헬퍼 메서드
    private JButton createSmallStyledButton(String text, ActionListener action) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // 마우스 오버 시 연한 하늘색 배경, 평소엔 흰색
                if (getModel().isRollover()) {
                    g2.setColor(new Color(235, 245, 255)); 
                } else {
                    g2.setColor(Color.WHITE);
                }
                // 배경 채우기
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                
                // 테두리 그리기 (Primary Blue)
                g2.setColor(UITheme.PRIMARY_BLUE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                
                super.paintComponent(g);
                g2.dispose();
            }
        };

        btn.setPreferredSize(new Dimension(60, 28)); // 크기 고정
        btn.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        btn.setForeground(UITheme.PRIMARY_BLUE); // 글자색 파란색
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(action);
        
        return btn;
    }

    private JPanel createRowLabel(String title, JLabel valLabel, ActionListener action) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.GRAY_BORDER));
        panel.setPreferredSize(new Dimension(400, 50));
        
        JLabel t = new JLabel(title); 
        t.setFont(UITheme.FONT_BOLD); 
        t.setPreferredSize(new Dimension(80, 0));
        
        panel.add(t, BorderLayout.WEST);
        panel.add(valLabel, BorderLayout.CENTER);
        
        if (action != null) {
            // 개선된 버튼 메서드 호출
            JButton btn = createSmallStyledButton("변경", action);
            
            // 버튼을 오른쪽 정렬하기 위해 패널로 감싸기
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
            btnPanel.setOpaque(false);
            btnPanel.add(btn);
            
            panel.add(btnPanel, BorderLayout.EAST);
        }
        return panel;
    }
    
    private JPanel createRow(String title, String val, ActionListener action) {
        JLabel l = new JLabel(val); 
        l.setFont(UITheme.FONT_PLAIN);
        return createRowLabel(title, l, action);
    }

    private JPanel createProfileIcon() {
        JPanel icon = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(220, 230, 250));
                g2.fillOval(0, 0, 60, 60);
                
                // 아이콘 내부에 사람 모양 텍스트 추가
                g2.setColor(UITheme.PRIMARY_BLUE);
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
                FontMetrics fm = g2.getFontMetrics();
                String emoji = "👤";
                int x = (getWidth() - fm.stringWidth(emoji)) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 4;
                g2.drawString(emoji, x, y);
            }
        };
        icon.setPreferredSize(new Dimension(60, 60));
        icon.setOpaque(false);
        return icon;
    }

    public void setUserInfo(User user) {
        this.currentUser = user;
        if (user != null) {
            nameIdLabel.setText(user.getUserName() + " (" + user.getUserId() + ")");
            passportValueLabel.setText(user.getPassportNumber());
            phoneValueLabel.setText(user.getPhone());
            mileageLabel.setText(String.format("%,d P", user.getMileage()));
        }
    }
    
    // --- 다이얼로그 로직 ---
    private void openChangePasswordDialog() {
        if(currentUser==null)return;
        JPasswordField pf = new JPasswordField();
        if(JOptionPane.showConfirmDialog(this, pf, "새 비밀번호 입력", JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION){
            String p = new String(pf.getPassword());
            if(!p.isBlank()){ currentUser.setPassword(p); JOptionPane.showMessageDialog(this, "비밀번호가 변경되었습니다. (저장 버튼을 눌러 확정하세요)"); }
        }
    }
    private void openChangePassportDialog() {
        if(currentUser==null)return;
        String s = JOptionPane.showInputDialog(this, "새 여권번호 입력", currentUser.getPassportNumber());
        if(s!=null && !s.isBlank()){ currentUser.setPassportNumber(s); passportValueLabel.setText(s); }
    }
    private void openChangePhoneDialog() {
        if(currentUser==null)return;
        String s = JOptionPane.showInputDialog(this, "새 전화번호 입력", currentUser.getPhone());
        if(s!=null && !s.isBlank()){ currentUser.setPhone(s); phoneValueLabel.setText(s); }
    }
    private void saveChangesToDB() {
        if(currentUser!=null){ userDAO.updateUser(currentUser); JOptionPane.showMessageDialog(this, "정보가 저장되었습니다."); }
    }
    private void deleteAccount() {
        if(currentUser!=null && JOptionPane.showConfirmDialog(this, "정말로 탈퇴하시겠습니까?", "회원탈퇴", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
            userDAO.deleteUser(currentUser.getUserId());
            JOptionPane.showMessageDialog(this, "탈퇴되었습니다.");
            mainApp.getUserController().logout();
            mainApp.showPanel("LOGIN");
        }
    }
}