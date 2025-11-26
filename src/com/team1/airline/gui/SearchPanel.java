package com.team1.airline.gui;

import com.team1.airline.entity.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * [검색 화면 패널]
 * 항공편 검색 조건(출발/도착지, 날짜, 인원)을 입력받는 화면.
 */
public class SearchPanel extends JPanel {

    private final MainApp mainApp;

    private static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 28);
    private static final Font FONT_LABEL = new Font("SansSerif", Font.BOLD, 16);
    private static final Font FONT_INFO  = new Font("SansSerif", Font.PLAIN, 14);
    private static final Color COLOR_BG_GRAY = new Color(245, 245, 245);
    private static final Color COLOR_PRIMARY = new Color(0, 122, 255);

    private JTextField departureField;
    private JTextField arrivalField;
    private JTextField departureDateField;
    private JTextField returnDateField;
    private JLabel userLabel; // 사용자 이름 표시

    private int economySeats  = 1;
    private int businessSeats = 0;
    
    private JLabel seatSummaryLabel;
    private JLabel routeSummaryLabel;
    private JLabel dateSummaryLabel;
    private JLabel bottomSeatLabel;

    public SearchPanel(MainApp mainApp) {
        this.mainApp = mainApp;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(850, 400));

        add(createTitlePanel(), BorderLayout.NORTH);
        add(createFormPanel(),  BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    // --- UI 구성 메서드들 ---

    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(COLOR_PRIMARY);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("항공권 검색", SwingConstants.CENTER);
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        JButton closeButton = new JButton("X");
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(COLOR_PRIMARY);
        closeButton.setBorder(null);
        closeButton.setFocusPainted(false);
        closeButton.setFont(FONT_LABEL);
        closeButton.addActionListener(e -> mainApp.showPanel("MAIN"));
        titlePanel.add(closeButton, BorderLayout.EAST);

        return titlePanel;
    }

    private JPanel createFormPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        root.add(createUserPanel(), BorderLayout.NORTH);

        JPanel searchBarPanel = createSearchBarPanel();
        searchBarPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JPanel summaryPanel = createSummaryPanel();

        JPanel centerStack = new JPanel();
        centerStack.setLayout(new BoxLayout(centerStack, BoxLayout.Y_AXIS));
        centerStack.setOpaque(false);
        centerStack.add(searchBarPanel);
        centerStack.add(summaryPanel);

        root.add(centerStack, BorderLayout.CENTER);
        return root;
    }

    private JPanel createUserPanel() {
        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setOpaque(false);
        
        userLabel = new JLabel("사용자님");
        userLabel.setFont(FONT_LABEL);
        updateUserName(); // 초기화 시 이름 설정

        userPanel.add(userLabel, BorderLayout.WEST);
        return userPanel;
    }
    
    /**
     * MainApp에서 화면 전환 시 호출하여 사용자 이름을 최신 상태로 갱신
     */
    public void updateUserName() {
        if (mainApp.getUserController() != null && mainApp.getUserController().isLoggedIn()) {
            User currentUser = mainApp.getUserController().getCurrentUser();
            userLabel.setText(currentUser.getUserName() + "님");
        } else {
            userLabel.setText("비회원님");
        }
    }

    private JPanel createSearchBarPanel() {
        JPanel searchBarPanel = new JPanel();
        searchBarPanel.setOpaque(false);
        searchBarPanel.setLayout(new GridLayout(1, 5, 0, 0));

        searchBarPanel.add(createBox("출발지", true, false));
        searchBarPanel.add(createBox("도착지", false, false));
        searchBarPanel.add(createBox("가는 날", true, true));
        searchBarPanel.add(createBox("오는 날", false, true));
        searchBarPanel.add(createSeatBox());

        return searchBarPanel;
    }
    
    /**
     * 반복되는 입력 박스 생성 헬퍼 메서드
     */
    private JPanel createBox(String title, boolean isFirst, boolean isDate) {
        JPanel box = new JPanel(new BorderLayout());
        box.setBackground(Color.WHITE);
        box.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
        box.add(titleLabel, BorderLayout.NORTH);
        
        JTextField tf = new JTextField(isDate ? "날짜를 선택하세요" : (isFirst ? "출발지를 선택하세요" : "도착지를 선택하세요"));
        tf.setEditable(false);
        tf.setFont(FONT_INFO);
        tf.setBorder(null);
        tf.setOpaque(false);
        tf.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // 필드 참조 저장
        if (isDate) {
            if (isFirst) departureDateField = tf; else returnDateField = tf;
        } else {
            if (isFirst) departureField = tf; else arrivalField = tf;
        }

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setOpaque(false);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        
        if (isDate) inputPanel.add(new JLabel("📅"), BorderLayout.WEST);
        inputPanel.add(tf, BorderLayout.CENTER);
        if (!isDate) inputPanel.add(new JLabel("▼"), BorderLayout.EAST); // 공항 선택 화살표
        
        box.add(inputPanel, BorderLayout.CENTER);
        
        // 클릭 리스너 연결
        MouseAdapter clickListener = new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (isDate) openDatePickerDialog(tf);
                else openAirportSelectionDialog(tf);
            }
        };
        addClickListenerToAll(box, clickListener);
        
        return box;
    }

    private JPanel createSeatBox() {
        JPanel box = new JPanel(new BorderLayout());
        box.setBackground(Color.WHITE);
        box.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        
        box.add(new JLabel(" 인원"), BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        contentPanel.add(new JLabel("👤"), BorderLayout.WEST);
        
        seatSummaryLabel = new JLabel(buildSeatSummaryText(economySeats, businessSeats));
        seatSummaryLabel.setFont(FONT_INFO);
        contentPanel.add(seatSummaryLabel, BorderLayout.CENTER);
        
        box.add(contentPanel, BorderLayout.CENTER);
        
        MouseAdapter clickListener = new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) { openSeatSelectionDialog(); }
        };
        addClickListenerToAll(box, clickListener);
        return box;
    }

    private JPanel createSummaryPanel() {
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setOpaque(false);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 0, 40));

        routeSummaryLabel = new JLabel(); routeSummaryLabel.setFont(FONT_INFO);
        dateSummaryLabel = new JLabel();  dateSummaryLabel.setFont(FONT_INFO);
        bottomSeatLabel = new JLabel();   bottomSeatLabel.setFont(FONT_INFO);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);
        
        list.add(createSummaryRow("✈️", routeSummaryLabel));
        list.add(createSummaryRow("📆  ", dateSummaryLabel));
        list.add(createSummaryRow("👤", bottomSeatLabel));

        summaryPanel.add(list, BorderLayout.WEST);
        updateSummary();
        return summaryPanel;
    }

    private JPanel createSummaryRow(String icon, JLabel label) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setOpaque(false);
        row.add(new JLabel(icon));
        row.add(label);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0));
        return row;
    }

    public void updateSummary() {
        if (routeSummaryLabel == null) return;
        String dep = getSafeText(departureField);
        String arr = getSafeText(arrivalField);
        routeSummaryLabel.setText(dep + " -> " + arr);
        
        String dDate = getSafeDateText(departureDateField);
        String rDate = getSafeDateText(returnDateField);
        dateSummaryLabel.setText(dDate + " ~ " + rDate);
        
        bottomSeatLabel.setText(buildSeatSummaryText(economySeats, businessSeats));
    }

    public String getSeatSummaryForResult() {
        return buildSeatSummaryText(economySeats, businessSeats);
    }

    private String buildSeatSummaryText(int econ, int biz) {
        StringBuilder sb = new StringBuilder();
        if (econ > 0) sb.append("이코노미 ").append(econ).append("석");
        if (biz > 0) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("비즈니스 ").append(biz).append("석");
        }
        return sb.length() == 0 ? "선택 안 함" : sb.toString();
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(COLOR_BG_GRAY);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JButton searchButton = new JButton("항공권 검색");
        searchButton.setFont(FONT_LABEL);
        searchButton.setForeground(Color.WHITE);
        searchButton.setBackground(COLOR_PRIMARY);
        searchButton.setFocusPainted(false);
        searchButton.setPreferredSize(new Dimension(300, 50));

        searchButton.addActionListener(e -> {
            String depDate = departureDateField.getText().contains("날짜") ? "" : departureDateField.getText();
            String retDate = returnDateField.getText().contains("날짜") ? "" : returnDateField.getText();
            mainApp.searchFlights(departureField.getText(), arrivalField.getText(), depDate, retDate, economySeats, businessSeats);
        });

        bottomPanel.add(searchButton);
        return bottomPanel;
    }

    // --- Utility Methods ---
    
    private String getSafeText(JTextField tf) {
        if (tf == null || tf.getText().isBlank() || tf.getText().contains("선택하세요")) return "-";
        return tf.getText();
    }
    
    private String getSafeDateText(JTextField tf) {
        if (tf == null || tf.getText().isBlank() || tf.getText().contains("날짜")) return "-";
        return tf.getText();
    }

    private void addClickListenerToAll(Component component, MouseAdapter listener) {
        component.addMouseListener(listener);
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                addClickListenerToAll(child, listener);
            }
        }
    }

    // --- Dialog Openers ---

    private void openAirportSelectionDialog(JTextField targetField) {
        java.util.List<String> airportNames = mainApp.getAllAirportNames();
        if (airportNames == null || airportNames.isEmpty()) {
            JOptionPane.showMessageDialog(this, "공항 데이터 없음", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JComboBox<String> combo = new JComboBox<>(airportNames.toArray(new String[0]));
        if (JOptionPane.showConfirmDialog(this, combo, "공항 선택", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            targetField.setText((String) combo.getSelectedItem());
            updateSummary();
        }
    }

    private void openSeatSelectionDialog() {
        final int[] counts = { economySeats, businessSeats }; 
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(createCounterPanel("이코노미", counts, 0));
        panel.add(createCounterPanel("비즈니스", counts, 1));
        
        if (JOptionPane.showConfirmDialog(this, panel, "인원 선택", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            if (counts[0] + counts[1] <= 0) {
                JOptionPane.showMessageDialog(this, "최소 1석 이상 선택해야 합니다.");
                return;
            }
            economySeats = counts[0];
            businessSeats = counts[1];
            updateSummary();
        }
    }
    
    private JPanel createCounterPanel(String label, int[] counts, int idx) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel countLbl = new JLabel(String.valueOf(counts[idx]));
        JButton minus = new JButton("-");
        JButton plus = new JButton("+");
        minus.addActionListener(e -> { 
            if (counts[idx] > 0) { counts[idx]--; countLbl.setText(String.valueOf(counts[idx])); } 
        });
        plus.addActionListener(e -> { 
            counts[idx]++; countLbl.setText(String.valueOf(counts[idx])); 
        });
        p.add(new JLabel(label));
        p.add(minus);
        p.add(countLbl);
        p.add(plus);
        return p;
    }

    private void openDatePickerDialog(JTextField targetField) {
        Window window = SwingUtilities.getWindowAncestor(this);
        LocalDate initDate = LocalDate.now();
        try {
            String txt = targetField.getText();
            if (!txt.contains("날짜") && !txt.isBlank()) {
                initDate = LocalDate.parse(txt, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
        } catch (Exception ignored) {}
        
        DatePickerDialog dialog = new DatePickerDialog(window, initDate);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        
        if (dialog.isDateCleared()) {
            targetField.setText("날짜를 선택하세요");
        } else if (dialog.getSelectedDate() != null) {
            targetField.setText(dialog.getSelectedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        updateSummary();
    }

    // 내부 클래스: 날짜 선택 다이얼로그
    private static class DatePickerDialog extends JDialog {
        private LocalDate selectedDate;
        private boolean dateCleared = false;
        private YearMonth currentYearMonth;
        private JPanel calendarPanel;
        private JComboBox<Integer> yearCombo;
        private JComboBox<Integer> monthCombo;

        DatePickerDialog(Window owner, LocalDate initialDate) {
            super(owner, "날짜 선택", Dialog.ModalityType.APPLICATION_MODAL);
            if (initialDate == null) initialDate = LocalDate.now();
            this.currentYearMonth = YearMonth.from(initialDate);
            setLayout(new BorderLayout(10, 10));

            JPanel top = new JPanel(new FlowLayout());
            yearCombo = new JComboBox<>();
            for (int y = initialDate.getYear() - 1; y <= initialDate.getYear() + 1; y++) yearCombo.addItem(y);
            yearCombo.setSelectedItem(initialDate.getYear());
            monthCombo = new JComboBox<>();
            for (int m = 1; m <= 12; m++) monthCombo.addItem(m);
            monthCombo.setSelectedItem(initialDate.getMonthValue());
            top.add(new JLabel("연도:")); top.add(yearCombo);
            top.add(new JLabel("월:")); top.add(monthCombo);
            add(top, BorderLayout.NORTH);

            calendarPanel = new JPanel(new GridLayout(0, 7, 5, 5));
            add(calendarPanel, BorderLayout.CENTER);

            JPanel bottom = new JPanel(new FlowLayout());
            JButton okBtn = new JButton("확인");
            JButton clearBtn = new JButton("초기화");
            JButton cancelBtn = new JButton("취소");
            bottom.add(okBtn); bottom.add(clearBtn); bottom.add(cancelBtn);
            add(bottom, BorderLayout.SOUTH);

            yearCombo.addActionListener(e -> updateCalendar());
            monthCombo.addActionListener(e -> updateCalendar());
            okBtn.addActionListener(e -> dispose());
            clearBtn.addActionListener(e -> { selectedDate = null; dateCleared = true; dispose(); });
            cancelBtn.addActionListener(e -> { selectedDate = null; dateCleared = false; dispose(); });

            rebuildCalendar();
            pack();
        }
        private void updateCalendar() {
            currentYearMonth = YearMonth.of((Integer) yearCombo.getSelectedItem(), (Integer) monthCombo.getSelectedItem());
            rebuildCalendar();
        }
        private void rebuildCalendar() {
            calendarPanel.removeAll();
            LocalDate firstDay = currentYearMonth.atDay(1);
            int firstDow = firstDay.getDayOfWeek().getValue(); 
            for (int i = 1; i < firstDow; i++) calendarPanel.add(new JLabel(" "));
            int length = currentYearMonth.lengthOfMonth();
            for (int d = 1; d <= length; d++) {
                final int day = d;
                JButton btn = new JButton(String.valueOf(day));
                btn.setMargin(new Insets(2, 2, 2, 2));
                btn.addActionListener(e -> {
                    selectedDate = currentYearMonth.atDay(day);
                    dispose();
                });
                calendarPanel.add(btn);
            }
            calendarPanel.revalidate();
            calendarPanel.repaint();
            pack();
        }
        public LocalDate getSelectedDate() { return selectedDate; }
        public boolean isDateCleared() { return dateCleared; }
    }
}