package com.team1.airline.gui;

import com.team1.airline.entity.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * [항공권 검색 화면]
 * - 출발지, 도착지, 날짜, 인원 수를 입력받습니다.
 * - 입력된 정보는 하단의 요약 패널과 상단의 검색 박스에 실시간으로 반영(동기화)됩니다.
 */
public class SearchPanel extends JPanel {

    private final MainApp mainApp;
    private JTextField departureField, arrivalField, departureDateField, returnDateField;
    private JLabel userLabel;
    
    // 상단 검색 박스 내의 인원 표시 라벨 (JTextField로 변경하여 크기 고정됨)
    private JTextField seatSummaryLabel; 
    
    // 하단 요약 정보 라벨들
    private JLabel routeSummaryLabel, dateSummaryLabel, bottomSeatLabel; 
    
    // 선택된 인원 수 상태 저장
    private int economySeats = 1, businessSeats = 0;

    public SearchPanel(MainApp mainApp) {
        this.mainApp = mainApp;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_COLOR);
        setPreferredSize(new Dimension(850, 500));

        // 공통 타이틀 적용
        add(UITheme.createTitlePanel(mainApp, "항공권 검색", "MAIN"), BorderLayout.NORTH);
        add(createFormPanel(),  BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel createFormPanel() {
        UITheme.RoundedPanel root = new UITheme.RoundedPanel(30, Color.WHITE);
        root.setLayout(new BorderLayout());
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(20, 40, 20, 40));
        
        root.setBorder(new EmptyBorder(30, 30, 30, 30));
        root.add(createUserPanel(), BorderLayout.NORTH);

        JPanel searchBarPanel = createSearchBarPanel();
        searchBarPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JPanel summaryPanel = createSummaryPanel();

        JPanel centerStack = new JPanel();
        centerStack.setLayout(new BoxLayout(centerStack, BoxLayout.Y_AXIS));
        centerStack.setOpaque(false);
        centerStack.add(searchBarPanel);
        centerStack.add(summaryPanel);

        root.add(centerStack, BorderLayout.CENTER);
        
        wrapper.add(root);
        return wrapper;
    }

    /**
     * 상단 사용자 환영 메시지 패널
     */
    private JPanel createUserPanel() {
        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setOpaque(false);
        userLabel = new JLabel("사용자님");
        userLabel.setFont(UITheme.FONT_SUBTITLE);
        userLabel.setForeground(UITheme.PRIMARY_BLUE);
        updateUserName(); 
        userPanel.add(userLabel, BorderLayout.WEST);
        return userPanel;
    }
    
    public void updateUserName() {
        if (mainApp.getUserController() != null && mainApp.getUserController().isLoggedIn()) {
            User currentUser = mainApp.getUserController().getCurrentUser();
            userLabel.setText("반갑습니다, " + currentUser.getUserName() + "님");
        } else {
            userLabel.setText("비회원님");
        }
    }

    /**
     * 5개의 검색 조건 박스(출발, 도착, 가는날, 오는날, 인원)를 배치하는 패널
     */
    private JPanel createSearchBarPanel() {
        JPanel searchBarPanel = new JPanel();
        searchBarPanel.setOpaque(false);
        searchBarPanel.setLayout(new GridLayout(1, 5, 10, 0)); 

        searchBarPanel.add(createBox("출발지", true, false));
        searchBarPanel.add(createBox("도착지", false, false));
        searchBarPanel.add(createBox("가는 날", true, true));
        searchBarPanel.add(createBox("오는 날", false, true));
        searchBarPanel.add(createSeatBox());

        return searchBarPanel;
    }
    
    /**
     * 일반 검색 박스 생성 (공항 또는 날짜 선택)
     */
    private JPanel createBox(String title, boolean isFirst, boolean isDate) {
        UITheme.RoundedPanel box = new UITheme.RoundedPanel(15, Color.WHITE);
        box.setLayout(new BorderLayout());
        box.setDrawBorder(true);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UITheme.FONT_BOLD);
        titleLabel.setForeground(Color.GRAY);
        titleLabel.setBorder(new EmptyBorder(10, 10, 0, 5));
        box.add(titleLabel, BorderLayout.NORTH);
        
        JTextField tf = new JTextField(isDate ? "날짜 선택" : (isFirst ? "출발지" : "도착지"));
        tf.setEditable(false);
        tf.setFont(UITheme.FONT_PLAIN);
        tf.setBorder(null);
        tf.setOpaque(false);
        tf.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // [중요] 텍스트가 길어져도 박스 크기가 늘어나지 않도록 컬럼 수 고정
        tf.setColumns(8); 

        if (isDate) { if (isFirst) departureDateField = tf; else returnDateField = tf; } 
        else { if (isFirst) departureField = tf; else arrivalField = tf; }

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setOpaque(false);
        inputPanel.setBorder(new EmptyBorder(0, 10, 10, 10));
        
        if (isDate) inputPanel.add(new JLabel("📅 "), BorderLayout.WEST);
        inputPanel.add(tf, BorderLayout.CENTER);
        
        box.add(inputPanel, BorderLayout.CENTER);
        
        // 클릭 이벤트 연결
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

    /**
     * 인원 선택 박스 생성
     */
    private JPanel createSeatBox() {
        UITheme.RoundedPanel box = new UITheme.RoundedPanel(15, Color.WHITE);
        box.setLayout(new BorderLayout());
        box.setDrawBorder(true);
        
        JLabel t = new JLabel("인원");
        t.setFont(UITheme.FONT_BOLD);
        t.setForeground(Color.GRAY);
        t.setBorder(new EmptyBorder(10, 10, 0, 5));
        box.add(t, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(0, 10, 10, 10));
        contentPanel.add(new JLabel("👤 "), BorderLayout.WEST);
        
        // JTextField를 사용하여 내용이 길어져도 UI가 깨지지 않게 함
        seatSummaryLabel = new JTextField(buildSeatSummaryText(economySeats, businessSeats));
        seatSummaryLabel.setFont(UITheme.FONT_PLAIN);
        seatSummaryLabel.setEditable(false);
        seatSummaryLabel.setBorder(null);
        seatSummaryLabel.setOpaque(false);
        seatSummaryLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // 텍스트가 길어져도 박스 크기가 늘어나지 않도록 컬럼 수 고정
        seatSummaryLabel.setColumns(8);

        contentPanel.add(seatSummaryLabel, BorderLayout.CENTER);
        
        box.add(contentPanel, BorderLayout.CENTER);
        
        MouseAdapter clickListener = new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) { openSeatSelectionDialog(); }
        };
        addClickListenerToAll(box, clickListener);
        return box;
    }

    /**
     * 하단 요약 패널 (선택된 조건들을 텍스트로 보여줌)
     */
    private JPanel createSummaryPanel() {
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setOpaque(false);
        summaryPanel.setBorder(new EmptyBorder(30, 20, 0, 20));

        routeSummaryLabel = new JLabel(); routeSummaryLabel.setFont(UITheme.FONT_BOLD);
        dateSummaryLabel = new JLabel();  dateSummaryLabel.setFont(UITheme.FONT_BOLD);
        bottomSeatLabel = new JLabel();   bottomSeatLabel.setFont(UITheme.FONT_BOLD);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);
        
        list.add(createSummaryRow("✈️", routeSummaryLabel));
        list.add(createSummaryRow("📆 ", dateSummaryLabel));
        list.add(createSummaryRow("👤", bottomSeatLabel));

        summaryPanel.add(list, BorderLayout.WEST);
        updateSummary();
        return summaryPanel;
    }

    private JPanel createSummaryRow(String icon, JLabel label) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row.setOpaque(false);
        row.add(new JLabel(icon));
        row.add(label);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        return row;
    }
    
    /**
     * [UI 업데이트] 모든 입력 필드의 변경 사항을 감지하여 UI를 동기화합니다.
     * 상단 검색 박스와 하단 요약 패널을 모두 업데이트합니다.
     */
    public void updateSummary() {
        if (routeSummaryLabel == null) return;
        
        String dep = getSafeText(departureField);
        String arr = getSafeText(arrivalField);
        routeSummaryLabel.setText(dep + " -> " + arr);
        
        String dDate = getSafeDateText(departureDateField);
        String rDate = getSafeDateText(returnDateField);
        dateSummaryLabel.setText(dDate + " ~ " + rDate);
        
        String seatText = buildSeatSummaryText(economySeats, businessSeats);
        bottomSeatLabel.setText(seatText); // 하단 요약 업데이트
        
        if (seatSummaryLabel != null) {
            seatSummaryLabel.setText(seatText); // 상단 검색 박스 업데이트
        }
    }

    public String getSeatSummaryForResult() { return buildSeatSummaryText(economySeats, businessSeats); }
    
    // 인원 수 텍스트 생성
    private String buildSeatSummaryText(int econ, int biz) {
        StringBuilder sb = new StringBuilder();
        if (econ > 0) sb.append("이코노미 ").append(econ).append("석");
        if (biz > 0) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("비즈니스 ").append(biz).append("석");
        }
        return sb.length() == 0 ? "선택 안 함" : sb.toString();
    }
    
    // 텍스트 필드 값 안전 조회 (null 체크)
    private String getSafeText(JTextField tf) {
        if (tf == null || tf.getText().isBlank() || tf.getText().contains("선택")) return "-";
        return tf.getText();
    }
    
    private String getSafeDateText(JTextField tf) {
        if (tf == null || tf.getText().isBlank() || tf.getText().contains("날짜")) return "-";
        return tf.getText();
    }
    
    // 컴포넌트와 자식들까지 클릭 리스너 재귀 등록
    private void addClickListenerToAll(Component component, MouseAdapter listener) {
        component.addMouseListener(listener);
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                addClickListenerToAll(child, listener);
            }
        }
    }
    
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(UITheme.BG_COLOR);
        bottomPanel.setBorder(new EmptyBorder(10, 0, 30, 0));

        UITheme.RoundedButton searchButton = new UITheme.RoundedButton("항공권 검색");
        searchButton.setPreferredSize(new Dimension(300, 55));
        searchButton.setFont(UITheme.FONT_SUBTITLE);
        
        // 검색 버튼 클릭 시 메인 앱으로 검색 요청
        searchButton.addActionListener(e -> {
            String depDate = departureDateField.getText().contains("날짜") ? "" : departureDateField.getText();
            String retDate = returnDateField.getText().contains("날짜") ? "" : returnDateField.getText();
            mainApp.searchFlights(departureField.getText(), arrivalField.getText(), depDate, retDate, economySeats, businessSeats);
        });

        bottomPanel.add(searchButton);
        return bottomPanel;
    }

    // --- 각종 다이얼로그 (공항 선택, 인원 선택, 날짜 선택) ---

    private void openAirportSelectionDialog(JTextField targetField) {
        java.util.List<String> airportNames = mainApp.getAllAirportNames();
        if (airportNames == null || airportNames.isEmpty()) { JOptionPane.showMessageDialog(this, "공항 데이터 없음"); return; }
        JComboBox<String> combo = new JComboBox<>(airportNames.toArray(new String[0]));
        if (JOptionPane.showConfirmDialog(this, combo, "공항 선택", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            targetField.setText((String) combo.getSelectedItem());
            updateSummary();
        }
    }
    
    private void openSeatSelectionDialog() {
        final int[] counts = { economySeats, businessSeats }; 
        JPanel panel = new JPanel(); panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(createCounterPanel("이코노미", counts, 0));
        panel.add(createCounterPanel("비즈니스", counts, 1));
        if (JOptionPane.showConfirmDialog(this, panel, "인원 선택", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            if (counts[0] + counts[1] <= 0) { JOptionPane.showMessageDialog(this, "최소 1석 이상 선택해야 합니다."); return; }
            economySeats = counts[0]; businessSeats = counts[1]; 
            updateSummary(); 
        }
    }
    
    private JPanel createCounterPanel(String label, int[] counts, int idx) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel countLbl = new JLabel(String.valueOf(counts[idx]));
        JButton minus = new JButton("-"); JButton plus = new JButton("+");
        minus.addActionListener(e -> { if (counts[idx] > 0) { counts[idx]--; countLbl.setText(String.valueOf(counts[idx])); } });
        plus.addActionListener(e -> { counts[idx]++; countLbl.setText(String.valueOf(counts[idx])); });
        p.add(new JLabel(label)); p.add(minus); p.add(countLbl); p.add(plus); return p;
    }
    
    private void openDatePickerDialog(JTextField targetField) {
        Window window = SwingUtilities.getWindowAncestor(this);
        LocalDate initDate = LocalDate.now();
        try {
            String txt = targetField.getText();
            if (!txt.contains("날짜") && !txt.isBlank()) initDate = LocalDate.parse(txt, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception ignored) {}
        DatePickerDialog dialog = new DatePickerDialog(window, initDate);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        if (dialog.isDateCleared()) targetField.setText("날짜를 선택하세요");
        else if (dialog.getSelectedDate() != null) targetField.setText(dialog.getSelectedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        updateSummary();
    }
    
    /** 내부 클래스: 간단한 날짜 선택 달력 다이얼로그 */
    private static class DatePickerDialog extends JDialog {
        private LocalDate selectedDate; private boolean dateCleared = false; private YearMonth currentYearMonth;
        private JPanel calendarPanel; private JComboBox<Integer> yearCombo; private JComboBox<Integer> monthCombo;
        DatePickerDialog(Window owner, LocalDate initialDate) {
            super(owner, "날짜 선택", Dialog.ModalityType.APPLICATION_MODAL);
            if (initialDate == null) initialDate = LocalDate.now();
            this.currentYearMonth = YearMonth.from(initialDate);
            setLayout(new BorderLayout(10, 10));
            JPanel top = new JPanel(new FlowLayout());
            yearCombo = new JComboBox<>(); for (int y = initialDate.getYear() - 1; y <= initialDate.getYear() + 1; y++) yearCombo.addItem(y);
            yearCombo.setSelectedItem(initialDate.getYear());
            monthCombo = new JComboBox<>(); for (int m = 1; m <= 12; m++) monthCombo.addItem(m);
            monthCombo.setSelectedItem(initialDate.getMonthValue());
            top.add(new JLabel("연도:")); top.add(yearCombo); top.add(new JLabel("월:")); top.add(monthCombo);
            add(top, BorderLayout.NORTH);
            calendarPanel = new JPanel(new GridLayout(0, 7, 5, 5)); add(calendarPanel, BorderLayout.CENTER);
            JPanel bottom = new JPanel(new FlowLayout());
            JButton okBtn = new JButton("확인"); JButton clearBtn = new JButton("초기화"); JButton cancelBtn = new JButton("취소");
            bottom.add(okBtn); bottom.add(clearBtn); bottom.add(cancelBtn); add(bottom, BorderLayout.SOUTH);
            yearCombo.addActionListener(e -> updateCalendar()); monthCombo.addActionListener(e -> updateCalendar());
            okBtn.addActionListener(e -> dispose()); clearBtn.addActionListener(e -> { selectedDate = null; dateCleared = true; dispose(); });
            cancelBtn.addActionListener(e -> { selectedDate = null; dateCleared = false; dispose(); });
            rebuildCalendar(); pack();
        }
        private void updateCalendar() { currentYearMonth = YearMonth.of((Integer) yearCombo.getSelectedItem(), (Integer) monthCombo.getSelectedItem()); rebuildCalendar(); }
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
                btn.addActionListener(e -> { selectedDate = currentYearMonth.atDay(day); dispose(); });
                calendarPanel.add(btn);
            }
            calendarPanel.revalidate(); calendarPanel.repaint(); pack();
        }
        public LocalDate getSelectedDate() { return selectedDate; }
        public boolean isDateCleared() { return dateCleared; }
    }
}