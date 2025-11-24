package com.team1.airline.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 항공권 검색 메인 화면
 * * [기능 요약]
 * 1. 출발지/도착지 선택 (다이얼로그)
 * 2. 가는 날/오는 날 선택 (커스텀 달력 DatePicker)
 * 3. 인원 선택 (이코노미/비즈니스 카운터)
 * 4. 하단 검색 버튼 클릭 시 MainApp.searchFlights() 호출
 */
public class SearchPanel extends JPanel {

    private final MainApp mainApp;

    // --- UI Constants (스타일 통일) ---
    private static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 28);
    private static final Font FONT_LABEL = new Font("SansSerif", Font.BOLD, 16);
    private static final Font FONT_INFO  = new Font("SansSerif", Font.PLAIN, 14);
    private static final Color COLOR_BG_GRAY = new Color(245, 245, 245);
    private static final Color COLOR_PRIMARY = new Color(0, 122, 255);

    // --- Input Fields ---
    private JTextField departureField;
    private JTextField arrivalField;
    private JTextField departureDateField;
    private JTextField returnDateField;

    // --- State Data ---
    private int economySeats  = 1;
    private int businessSeats = 0;

    // --- Labels ---
    private JLabel seatSummaryLabel;
    private JLabel routeSummaryLabel;
    private JLabel dateSummaryLabel;
    private JLabel bottomSeatLabel;

    public SearchPanel(MainApp mainApp) {
        this.mainApp = mainApp;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(850, 400));

        // UI 구성 요소 배치
        add(createTitlePanel(), BorderLayout.NORTH);
        add(createFormPanel(),  BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    // =================================================================================
    // 1. Top Title Panel
    // =================================================================================

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
        
        // 닫기 버튼 동작
        closeButton.addActionListener(e -> mainApp.showPanel("MAIN"));
        titlePanel.add(closeButton, BorderLayout.EAST);

        return titlePanel;
    }

    // =================================================================================
    // 2. Central Form Panel (Search Options + Summary)
    // =================================================================================

    private JPanel createFormPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1) 사용자 정보 (상단)
        root.add(createUserPanel(), BorderLayout.NORTH);

        // 2) 검색 옵션 박스 (중단)
        JPanel searchBarPanel = createSearchBarPanel();
        searchBarPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        // 3) 요약 정보 (하단)
        JPanel summaryPanel = createSummaryPanel();

        // 중앙 정렬용 스택 패널
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
        
        // [추후 개발] 로그인 세션 정보와 연동 필요
        JLabel userLabel = new JLabel("홍길동님");
        userLabel.setFont(FONT_LABEL);

        userPanel.add(userLabel, BorderLayout.WEST);
        return userPanel;
    }

    private JPanel createSearchBarPanel() {
        JPanel searchBarPanel = new JPanel();
        searchBarPanel.setOpaque(false);
        searchBarPanel.setLayout(new GridLayout(1, 5, 0, 0));

        // 각 검색 박스 생성
        searchBarPanel.add(createAirportBox(true));   // 출발지
        searchBarPanel.add(createAirportBox(false));  // 도착지
        searchBarPanel.add(createDateBox(true));      // 가는 날
        searchBarPanel.add(createDateBox(false));     // 오는 날
        searchBarPanel.add(createSeatBox());          // 인원

        return searchBarPanel;
    }

    // --- Component Creator Helpers (UI 중복 제거) ---

    /** 공통 박스 스타일 (테두리, 제목) 생성 */
    private JPanel createBaseBox(String title) {
        JPanel box = new JPanel(new BorderLayout());
        box.setBackground(Color.WHITE);
        box.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
        box.add(titleLabel, BorderLayout.NORTH);
        
        // 마우스 커서 설정
        box.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return box;
    }

    /** 읽기 전용 텍스트 필드 생성 (클릭용) */
    private JTextField createBaseTextField(String placeholder) {
        JTextField tf = new JTextField(placeholder);
        tf.setEditable(false);
        tf.setFont(FONT_INFO);
        tf.setBorder(null);
        tf.setOpaque(false);
        tf.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return tf;
    }

    /** 패널 내 모든 컴포넌트에 클릭 리스너 추가 (UX 향상) */
    private void addClickListenerToAll(Component component, MouseAdapter listener) {
        component.addMouseListener(listener);
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                addClickListenerToAll(child, listener);
            }
        }
    }

    // --- Individual Box Creators ---

    private JPanel createAirportBox(boolean isDeparture) {
        JPanel box = createBaseBox(isDeparture ? "출발지" : "도착지");
        JTextField textField = createBaseTextField(isDeparture ? "출발지를 선택하세요" : "도착지를 선택하세요");
        
        // 필드 참조 저장
        if (isDeparture) departureField = textField;
        else arrivalField = textField;

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setOpaque(false);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        
        JLabel icon = new JLabel("▼");
        icon.setFont(FONT_INFO);
        
        inputPanel.add(textField, BorderLayout.CENTER);
        inputPanel.add(icon, BorderLayout.EAST);
        box.add(inputPanel, BorderLayout.CENTER);

        // 이벤트 연결
        MouseAdapter clickListener = new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                openAirportSelectionDialog(textField);
            }
        };
        addClickListenerToAll(box, clickListener);

        return box;
    }

    private JPanel createDateBox(boolean isDeparture) {
        JPanel box = createBaseBox(isDeparture ? "가는 날" : "오는 날");
        JTextField textField = createBaseTextField("날짜를 선택하세요");

        if (isDeparture) departureDateField = textField;
        else returnDateField = textField;

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setOpaque(false);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

        JLabel icon = new JLabel("📅");
        inputPanel.add(icon, BorderLayout.WEST);
        inputPanel.add(textField, BorderLayout.CENTER);
        box.add(inputPanel, BorderLayout.CENTER);

        MouseAdapter clickListener = new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                openDatePickerDialog(textField);
            }
        };
        addClickListenerToAll(box, clickListener);

        return box;
    }

    private JPanel createSeatBox() {
        JPanel box = createBaseBox("인원");
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

        JLabel icon = new JLabel("👤");
        seatSummaryLabel = new JLabel(buildSeatSummaryText(economySeats, businessSeats));
        seatSummaryLabel.setFont(FONT_INFO);

        contentPanel.add(icon, BorderLayout.WEST);
        contentPanel.add(seatSummaryLabel, BorderLayout.CENTER);
        box.add(contentPanel, BorderLayout.CENTER);

        MouseAdapter clickListener = new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                openSeatSelectionDialog();
            }
        };
        addClickListenerToAll(box, clickListener);

        return box;
    }

    // =================================================================================
    // 3. Summary Panel (Bottom of Form)
    // =================================================================================

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

    /** 입력값 변경 시 하단 요약 텍스트 갱신 */
    private void updateSummary() {
        if (routeSummaryLabel == null) return;

        String dep = getSafeText(departureField, "출발지");
        String arr = getSafeText(arrivalField, "도착지");
        routeSummaryLabel.setText(dep + " -> " + arr);

        String dDate = getSafeDateText(departureDateField);
        String rDate = getSafeDateText(returnDateField);
        dateSummaryLabel.setText(dDate + " ~ " + rDate);

        bottomSeatLabel.setText(buildSeatSummaryText(economySeats, businessSeats));
    }

    private String getSafeText(JTextField tf, String def) {
        if (tf == null || tf.getText().isBlank()) return def;
        return tf.getText();
    }

    private String getSafeDateText(JTextField tf) {
        if (tf == null || tf.getText().isBlank() || tf.getText().contains("날짜")) return "20XX-XX-XX";
        return tf.getText();
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

    // =================================================================================
    // 4. Bottom Action Panel
    // =================================================================================

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
            // 플레이스홀더 텍스트 처리 후 검색 요청
            String depDate = departureDateField.getText().contains("날짜") ? "" : departureDateField.getText();
            String retDate = returnDateField.getText().contains("날짜") ? "" : returnDateField.getText();
            
            mainApp.searchFlights(departureField.getText(), arrivalField.getText(), depDate, retDate);
        });

        bottomPanel.add(searchButton);
        return bottomPanel;
    }

    // =================================================================================
    // 5. Dialog Logics
    // =================================================================================

    private void openAirportSelectionDialog(JTextField targetField) {
        // [Backend] MainApp에서 공항 데이터 조회
        List<String> airportNames = mainApp.getAllAirportNames();
        if (airportNames == null || airportNames.isEmpty()) {
            JOptionPane.showMessageDialog(this, "출발지와 도착지를 모두 선택해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JComboBox<String> combo = new JComboBox<>(airportNames.toArray(new String[0]));
        if (JOptionPane.showConfirmDialog(this, combo, "공항 선택", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            targetField.setText((String) combo.getSelectedItem());
            updateSummary();
        }
    }

    private void openSeatSelectionDialog() {
        // 인원 선택 로직
        final int[] counts = { economySeats, businessSeats }; // 0:econ, 1:biz

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
            seatSummaryLabel.setText(buildSeatSummaryText(economySeats, businessSeats));
            updateSummary();
        }
    }
    
    // 인원 선택용 +/- 패널 생성 헬퍼
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
            updateSummary();
        } else if (dialog.getSelectedDate() != null) {
            targetField.setText(dialog.getSelectedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            updateSummary();
        }
    }

    // =================================================================================
    // 6. Custom Date Picker
    // =================================================================================

    private static class DatePickerDialog extends JDialog {
        private LocalDate selectedDate;
        private boolean dateCleared = false;
        private YearMonth currentYearMonth;
        
        private JPanel calendarPanel;
        private JComboBox<Integer> yearCombo;
        private JComboBox<Integer> monthCombo;

        DatePickerDialog(Window owner, LocalDate initialDate) {
            super(owner, "날짜 선택", ModalityType.APPLICATION_MODAL);
            if (initialDate == null) initialDate = LocalDate.now();
            this.currentYearMonth = YearMonth.from(initialDate);
            setLayout(new BorderLayout(10, 10));

            // 상단: 연/월 선택
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

            // 중앙: 달력 그리드
            calendarPanel = new JPanel(new GridLayout(0, 7, 5, 5));
            add(calendarPanel, BorderLayout.CENTER);

            // 하단: 버튼
            JPanel bottom = new JPanel(new FlowLayout());
            JButton okBtn = new JButton("확인");
            JButton clearBtn = new JButton("초기화");
            JButton cancelBtn = new JButton("취소");
            bottom.add(okBtn); bottom.add(clearBtn); bottom.add(cancelBtn);
            add(bottom, BorderLayout.SOUTH);

            // 리스너 로직
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