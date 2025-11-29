package com.team1.airline.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * [UI 테마 관리 클래스]
 * - 애플리케이션 전반에서 사용되는 색상, 폰트 상수를 정의합니다.
 * - 반복적으로 사용되는 UI 컴포넌트(타이틀 바, 둥근 버튼, 스타일링된 테이블 등)를 생성하는 정적 메서드를 제공합니다.
 */
public class UITheme {

    // --- 색상 팔레트 ---
    public static final Color PRIMARY_BLUE = new Color(0, 122, 255);
    public static final Color HOVER_BLUE = new Color(0, 100, 230);
    public static final Color BG_COLOR = new Color(245, 248, 250);
    public static final Color TEXT_COLOR = new Color(50, 50, 50);
    public static final Color GRAY_BORDER = new Color(220, 220, 220);

    // --- 폰트 정의 ---
    public static final Font FONT_TITLE = new Font("맑은 고딕", Font.BOLD, 24);
    public static final Font FONT_SUBTITLE = new Font("맑은 고딕", Font.BOLD, 18);
    public static final Font FONT_BOLD = new Font("맑은 고딕", Font.BOLD, 14);
    public static final Font FONT_PLAIN = new Font("맑은 고딕", Font.PLAIN, 14);

    /**
     * [공통] 상단 타이틀 패널 생성 메서드
     * @param mainApp 화면 전환을 위한 MainApp 참조
     * @param titleText 제목 텍스트
     * @param backPanelName 뒤로가기/닫기 버튼 클릭 시 이동할 패널 이름
     * @return 생성된 JPanel
     */
    public static JPanel createTitlePanel(MainApp mainApp, String titleText, String backPanelName) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_BLUE);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel(titleText, SwingConstants.CENTER);
        title.setFont(FONT_TITLE);
        title.setForeground(Color.WHITE);

        JButton closeButton = new JButton("✕");
        closeButton.setForeground(Color.WHITE);
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        
        // 버튼 클릭 시 지정된 패널로 이동
        closeButton.addActionListener(e -> mainApp.showPanel(backPanelName));

        panel.add(title, BorderLayout.CENTER);
        panel.add(closeButton, BorderLayout.EAST);
        return panel;
    }
    
    /**
     * [공통] 섹션 소제목 생성 (파란색 굵은 글씨)
     * 예: "예약 정보", "검색 조건" 등
     */
    public static JLabel createSectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_SUBTITLE);
        label.setForeground(PRIMARY_BLUE);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    /**
     * [공통] 입력 폼 필드 제목 생성 (진한 회색 굵은 글씨)
     * 예: "아이디", "비밀번호" 등
     */
    public static JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_BOLD);
        label.setForeground(TEXT_COLOR);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    /**
     * [공통] JTable 스타일 일괄 적용
     * - 헤더 스타일, 행 높이, 그리드 라인 제거 등을 설정합니다.
     */
    public static void applyTableTheme(JTable table) {
        table.setRowHeight(40);
        table.setFont(FONT_PLAIN);
        
        // 헤더 스타일
        table.getTableHeader().setFont(FONT_BOLD);
        table.getTableHeader().setBackground(new Color(245, 245, 245));
        table.getTableHeader().setForeground(TEXT_COLOR);
        table.getTableHeader().setReorderingAllowed(false);
        
        // 라인 및 선택 스타일
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // 스크롤페인에 넣었을 때 배경색 처리를 위해 부모가 뷰포트라면 배경색 지정 (선택사항)
        if (table.getParent() instanceof JViewport) {
            table.getParent().setBackground(Color.WHITE);
        }
    }

    // --- 커스텀 컴포넌트: 둥근 버튼 ---
    public static class RoundedButton extends JButton {
        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setForeground(Color.WHITE);
            setBackground(PRIMARY_BLUE);
            setFont(FONT_BOLD);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // 버튼 상태에 따른 색상 변경
            if (getModel().isPressed()) {
                g2.setColor(getBackground().darker());
            } else if (getModel().isRollover()) {
                g2.setColor(HOVER_BLUE);
            } else {
                g2.setColor(getBackground());
            }
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            super.paintComponent(g);
            g2.dispose();
        }
    }

    // --- 커스텀 컴포넌트: 둥근 패널 ---
    public static class RoundedPanel extends JPanel {
        private int arc;
        private Color backgroundColor;
        private boolean drawBorder = true;

        public RoundedPanel() { this(20, Color.WHITE); }
        public RoundedPanel(int arc, Color bg) {
            this.arc = arc;
            this.backgroundColor = bg;
            setOpaque(false);
        }
        public void setDrawBorder(boolean draw) { this.drawBorder = draw; }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            if (drawBorder) {
                g2.setColor(GRAY_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            }
            super.paintComponent(g);
            g2.dispose();
        }
    }

    // --- 커스텀 컴포넌트: 둥근 텍스트/비밀번호 필드 ---
    public static class RoundedTextField extends JTextField {
        public RoundedTextField() {
            setOpaque(false);
            setBorder(new EmptyBorder(5, 10, 5, 10));
            setFont(FONT_PLAIN);
        }
        @Override
        protected void paintComponent(Graphics g) {
            drawRoundedField(g, getWidth(), getHeight());
            super.paintComponent(g);
        }
    }

    public static class RoundedPasswordField extends JPasswordField {
        public RoundedPasswordField() {
            setOpaque(false);
            setBorder(new EmptyBorder(5, 10, 5, 10));
            setFont(FONT_PLAIN);
        }
        @Override
        protected void paintComponent(Graphics g) {
            drawRoundedField(g, getWidth(), getHeight());
            super.paintComponent(g);
        }
    }

    // 텍스트 필드 그리기 공통 메서드
    private static void drawRoundedField(Graphics g, int w, int h) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, w - 1, h - 1, 15, 15);
        g2.setColor(GRAY_BORDER);
        g2.drawRoundRect(0, 0, w - 1, h - 1, 15, 15);
        g2.dispose();
    }
}