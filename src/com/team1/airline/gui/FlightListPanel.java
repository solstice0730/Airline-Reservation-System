package com.team1.airline.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 항공권 검색 결과 리스트 패널
 * * [기능 요약]
 * 1. 검색 조건 표시 (좌측 WestPanel)
 * 2. 항공권 리스트 테이블 표시 (중앙 CenterPanel)
 * - 가격, 시간, 소요시간에 따른 정렬 기능 (TableRowSorter)
 * - 'RouteId'는 숨겨진 컬럼으로 관리하여 선택 시 식별자로 사용
 * 3. 선택된 항공권 상세 표시 및 예약 진행 (우측 EastPanel)
 */
public class FlightListPanel extends JPanel {

    private final MainApp mainApp;

    // --- UI Constants (스타일 통일) ---
    private static final Color PRIMARY_BLUE = new Color(0, 122, 255);
    private static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 20);
    private static final Font FONT_LABEL = new Font("SansSerif", Font.BOLD, 16);
    private static final Font FONT_INFO  = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font FONT_HEADER = new Font("SansSerif", Font.BOLD, 14);

    // --- Components ---
    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;

    // 좌측: 검색 조건 라벨
    private JLabel westRouteLabel, westDepDateLabel, westRetDateLabel, westSeatLabel;

    // 우측: 선택 정보 라벨
    private JLabel eastAirlineLabel, eastRouteTimeLabel, eastPriceLabel;

    // 정렬 체크박스
    private JCheckBox priceCheckBox, durationCheckBox, depTimeCheckBox, arrTimeCheckBox;

    // --- Data State ---
    private String currentRoute          = "";
    private String currentDepartureDate  = "";
    private String currentReturnDate     = "";
    private String currentSeatSummary    = "";

    public FlightListPanel(MainApp mainApp) {
        this.mainApp = mainApp;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(1000, 600));

        // UI 구성
        add(createTitlePanel(),  BorderLayout.NORTH);
        add(createWestPanel(),   BorderLayout.WEST);
        add(createCenterTable(), BorderLayout.CENTER);
        add(createEastPanel(),   BorderLayout.EAST);
        
        // 기능 초기화
        initSortCheckBoxActions(); // 정렬 로직
        initTableSelectionListener(); // 테이블 선택 리스너
    }

    // =================================================================================
    // 1. UI Layout Methods
    // =================================================================================

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_BLUE);
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel title = new JLabel("검색 결과", SwingConstants.CENTER);
        title.setFont(FONT_TITLE);
        title.setForeground(Color.WHITE);
        panel.add(title, BorderLayout.CENTER);

        JButton closeButton = new JButton("X");
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(PRIMARY_BLUE);
        closeButton.setBorder(null);
        closeButton.setFont(FONT_LABEL);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> mainApp.showPanel("SEARCH"));
        
        panel.add(closeButton, BorderLayout.EAST);
        return panel;
    }

    private JPanel createWestPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(10, 10, 10, 10),
                BorderFactory.createLineBorder(Color.LIGHT_GRAY)
        ));
        panel.setPreferredSize(new Dimension(230, 600));

        // 1) 검색 조건 섹션
        panel.add(createStyledLabel("검색 조건", FONT_LABEL));
        panel.add(Box.createVerticalStrut(5));

        westRouteLabel   = createStyledLabel("미정", FONT_INFO);
        westDepDateLabel = createStyledLabel("가는 날: 선택 안함", FONT_INFO);
        westRetDateLabel = createStyledLabel("오는 날: 선택 안함", FONT_INFO);
        westSeatLabel    = createStyledLabel("좌석 선택 안함", FONT_INFO);

        panel.add(westRouteLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(westDepDateLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(westRetDateLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(westSeatLabel);

        // 2) 정렬 기준 섹션
        panel.add(Box.createVerticalStrut(20));
        JLabel sortTitle = createStyledLabel("정렬 기준", FONT_LABEL);
        sortTitle.setBorder(new EmptyBorder(10, 0, 5, 0));
        panel.add(sortTitle);

        priceCheckBox    = createStyledCheckBox("가격");
        durationCheckBox = createStyledCheckBox("최소시간");
        depTimeCheckBox  = createStyledCheckBox("출발시간(빠른순)");
        arrTimeCheckBox  = createStyledCheckBox("도착시간(빠른순)");

        panel.add(priceCheckBox);
        panel.add(durationCheckBox);
        panel.add(depTimeCheckBox);
        panel.add(arrTimeCheckBox);

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JScrollPane createCenterTable() {
        // 컬럼 정의: 마지막 "RouteId"는 화면에 보이지 않는 식별자용 컬럼
        String[] columnNames = { "항공사", "출발 시간", "도착 시간", "소요 시간", "가격", "RouteId" };
        
        model = new DefaultTableModel(new Object[][]{}, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 편집 불가
            }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(FONT_INFO);
        table.getTableHeader().setFont(FONT_HEADER);
        table.getTableHeader().setBackground(Color.LIGHT_GRAY);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // [중요] 5번째 컬럼(RouteId) 숨김 처리
        table.getColumnModel().getColumn(5).setMinWidth(0);
        table.getColumnModel().getColumn(5).setMaxWidth(0);
        table.getColumnModel().getColumn(5).setWidth(0);

        // [중요] 정렬기(Sorter) 설정 - 텍스트가 아닌 값(숫자, 시간) 기준으로 정렬하기 위함
        sorter = new TableRowSorter<>(model);
        sorter.setComparator(4, (o1, o2) -> Integer.compare(parsePrice(o1), parsePrice(o2))); // 가격 정렬
        sorter.setComparator(1, (o1, o2) -> parseTime(o1).compareTo(parseTime(o2)));          // 출발 시간 정렬
        sorter.setComparator(2, (o1, o2) -> parseTime(o1).compareTo(parseTime(o2)));          // 도착 시간 정렬
        sorter.setComparator(3, (o1, o2) -> Integer.compare(parseDurationMinutes(o1), parseDurationMinutes(o2))); // 소요 시간 정렬
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(10, 0, 10, 0));
        return scrollPane;
    }

    private JPanel createEastPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(10, 10, 10, 10),
                BorderFactory.createLineBorder(Color.LIGHT_GRAY)
        ));
        panel.setPreferredSize(new Dimension(230, 600));

        panel.add(createStyledLabel("선택 항공권", FONT_LABEL));
        panel.add(Box.createVerticalStrut(10));

        eastAirlineLabel   = createStyledLabel("항공사: (선택 대기)", FONT_INFO);
        eastRouteTimeLabel = createStyledLabel("(항공편을 선택하세요)", FONT_INFO);
        eastPriceLabel     = createStyledLabel(" ", FONT_TITLE); // 가격은 크게

        eastPriceLabel.setBorder(new EmptyBorder(10, 0, 10, 0));

        panel.add(eastAirlineLabel);
        panel.add(eastRouteTimeLabel);
        panel.add(eastPriceLabel);

        // 선택 버튼
        JButton selectButton = new JButton("선택");
        selectButton.setBackground(PRIMARY_BLUE);
        selectButton.setForeground(Color.WHITE);
        selectButton.setFont(FONT_LABEL);
        selectButton.setFocusPainted(false);
        selectButton.setBorderPainted(false); // Flat 스타일
        selectButton.setOpaque(true);

        selectButton.addActionListener(e -> handleFlightSelection());

        panel.add(selectButton);
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    // =================================================================================
    // 2. Logic & Event Handling
    // =================================================================================

    /**
     * 테이블 행 선택 시 우측(EastPanel) 정보를 갱신하는 리스너 등록
     */
    private void initTableSelectionListener() {
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();

                String airline = getValueAt(row, 0);
                String depTime = getValueAt(row, 1);
                String arrTime = getValueAt(row, 2);
                String price   = getValueAt(row, 4);

                eastAirlineLabel.setText("항공사: " + airline);
                eastRouteTimeLabel.setText("<html>" + currentRoute + "<br>" + depTime + " ~ " + arrTime + "</html>");
                eastPriceLabel.setText(price);
            } 
        });
    }

    /**
     * '선택' 버튼 클릭 시 MainApp을 통해 예약 확인 화면으로 이동
     */
    private void handleFlightSelection() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(mainApp, "항공편을 먼저 선택해주세요.");
            return;
        }

        // 테이블에서 데이터 추출
        String depTime = getValueAt(selectedRow, 1);
        String arrTime = getValueAt(selectedRow, 2);
        String price   = getValueAt(selectedRow, 4);
        String routeShort = getValueAt(selectedRow, 5); // 숨겨진 RouteId (ex: ICN-GMP)

        // MainApp으로 데이터 전달
        mainApp.confirmFlight(
                routeShort,            // 짧은 경로 (상단 표시용)
                currentRoute,          // 긴 경로 (하단 상세용)
                currentDepartureDate,
                currentReturnDate,
                depTime + " ~ " + arrTime,
                currentSeatSummary,
                price
        );
    }

    /**
     * 체크박스 정렬 로직 초기화 (상호 배타적 선택 보장)
     */
    private void initSortCheckBoxActions() {
        if (sorter == null) return;

        java.awt.event.ItemListener listener = e -> {
            JCheckBox src = (JCheckBox) e.getItemSelectable();
            
            if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                // 1. 다른 체크박스 모두 해제 (하나만 선택 가능)
                if (src != priceCheckBox)    priceCheckBox.setSelected(false);
                if (src != durationCheckBox) durationCheckBox.setSelected(false);
                if (src != depTimeCheckBox)  depTimeCheckBox.setSelected(false);
                if (src != arrTimeCheckBox)  arrTimeCheckBox.setSelected(false);

                // 2. 선택된 항목에 따라 정렬 적용
                if (src == priceCheckBox)         applySort(4); // 가격 컬럼
                else if (src == durationCheckBox) applySort(3); // 소요시간 컬럼
                else if (src == depTimeCheckBox)  applySort(1); // 출발시간 컬럼
                else if (src == arrTimeCheckBox)  applySort(2); // 도착시간 컬럼
                
            } else {
                // 3. 모두 선택 해제된 경우 정렬 초기화
                if (!priceCheckBox.isSelected() && !durationCheckBox.isSelected() &&
                    !depTimeCheckBox.isSelected() && !arrTimeCheckBox.isSelected()) {
                    sorter.setSortKeys(null);
                }
            }
        };

        priceCheckBox.addItemListener(listener);
        durationCheckBox.addItemListener(listener);
        depTimeCheckBox.addItemListener(listener);
        arrTimeCheckBox.addItemListener(listener);
    }

    private void applySort(int columnIndex) {
        List<RowSorter.SortKey> keys = List.of(
                new RowSorter.SortKey(columnIndex, SortOrder.ASCENDING)
        );
        sorter.setSortKeys(keys);
    }

    // =================================================================================
    // 3. Public API (Called by MainApp)
    // =================================================================================

    public void updateSearchCriteria(String depName, String arrName, String depDate, String retDate, String seat) {
        currentRoute = depName + " -> " + arrName;
        westRouteLabel.setText("<html>" + currentRoute + "</html>");

        currentDepartureDate = formatInfoText(depDate, "선택 안함");
        westDepDateLabel.setText("가는 날: " + currentDepartureDate);

        currentReturnDate = formatInfoText(retDate, "선택 안함");
        westRetDateLabel.setText("오는 날: " + currentReturnDate);

        currentSeatSummary = (seat != null && !seat.isBlank()) ? seat : "좌석 선택 안함";
        westSeatLabel.setText(currentSeatSummary);
    }

    public void populateTable(Object[][] data) {
        model.setRowCount(0);
        if (data != null) {
            for (Object[] row : data) {
                model.addRow(row);
            }
        }
    }

    // =================================================================================
    // 4. Helper Methods & Parsers
    // =================================================================================

    private JLabel createStyledLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JCheckBox createStyledCheckBox(String text) {
        JCheckBox cb = new JCheckBox(text);
        cb.setFont(FONT_INFO);
        cb.setOpaque(false);
        cb.setAlignmentX(Component.LEFT_ALIGNMENT);
        return cb;
    }

    private String getValueAt(int row, int col) {
        Object val = model.getValueAt(row, col);
        return val != null ? val.toString() : "";
    }

    private String formatInfoText(String text, String defaultText) {
        if (text == null || text.trim().isEmpty() || text.contains("날짜") || text.contains("선택")) {
            return defaultText;
        }
        return text;
    }

    // --- Parsers for Sorting (문자열 데이터를 비교 가능한 숫자로 변환) ---

    // "70,000원" -> 70000
    private int parsePrice(Object value) {
        if (value == null) return 0;
        String s = value.toString().replaceAll("[^0-9]", "");
        return s.isEmpty() ? 0 : Integer.parseInt(s);
    }

    // "09:00" -> LocalTime 객체
    private LocalTime parseTime(Object value) {
        if (value == null) return LocalTime.MIDNIGHT;
        try {
            return LocalTime.parse(value.toString().trim(), DateTimeFormatter.ofPattern("HH:mm"));
        } catch (Exception e) {
            return LocalTime.MIDNIGHT;
        }
    }

    // "1시간 15분" -> 75 (분 단위 정수)
    private int parseDurationMinutes(Object value) {
        if (value == null) return 0;
        String s = value.toString();
        int hours = 0;
        int mins  = 0;
        Matcher mHour = Pattern.compile("(\\d+)시간").matcher(s);
        Matcher mMin  = Pattern.compile("(\\d+)분").matcher(s);
        if (mHour.find()) hours = Integer.parseInt(mHour.group(1));
        if (mMin.find()) mins = Integer.parseInt(mMin.group(1));
        return hours * 60 + mins;
    }
}