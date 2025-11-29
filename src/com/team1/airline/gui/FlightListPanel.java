package com.team1.airline.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * [검색 결과 리스트 패널]
 * - 검색된 항공편 목록을 JTable로 표시합니다.
 * - TableRowSorter를 이용하여 가격, 시간순 정렬 기능을 제공합니다.
 * - 항공편 선택 후 '좌석 선택' 버튼 클릭 시 다음 단계로 진행합니다.
 */
public class FlightListPanel extends JPanel {

    private final MainApp mainApp;

    // 컴포넌트
    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;

    // 좌측 검색 정보 라벨
    private JLabel westRouteLabel, westDepDateLabel, westRetDateLabel, westSeatLabel;
    
    // 우측 선택 정보 라벨
    private JLabel eastFlightNoLabel, eastRouteTimeLabel, eastPriceLabel;
    
    // 정렬 체크박스
    private JCheckBox priceCheckBox, durationCheckBox, depTimeCheckBox, arrTimeCheckBox;

    // 데이터 상태 저장
    private String currentRoute = "";
    private String currentDepartureDate = "";
    private String currentReturnDate = "";
    private String currentSeatSummary = "";

    public FlightListPanel(MainApp mainApp) {
        this.mainApp = mainApp;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_COLOR);
        setPreferredSize(new Dimension(1000, 600));

        add(UITheme.createTitlePanel(mainApp, "검색 결과", "SEARCH"), BorderLayout.NORTH);
        
        // 전체 레이아웃 (좌측 필터 + 중앙 테이블 + 우측 상세)
        JPanel contentPanel = new JPanel(new BorderLayout(15, 0));
        contentPanel.setBackground(UITheme.BG_COLOR);
        contentPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        
        contentPanel.add(createWestPanel(), BorderLayout.WEST);
        contentPanel.add(createCenterTable(), BorderLayout.CENTER);
        contentPanel.add(createEastPanel(), BorderLayout.EAST);
        
        add(contentPanel, BorderLayout.CENTER);
        
        initSortCheckBoxActions(); // 정렬 체크박스 이벤트 설정
        initTableSelectionListener(); // 테이블 행 선택 이벤트 설정
    }

    // 2. 좌측 검색 조건 및 필터 패널
    private JPanel createWestPanel() {
        UITheme.RoundedPanel panel = new UITheme.RoundedPanel(20, Color.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setPreferredSize(new Dimension(220, 0));

        // 검색 조건 섹션
        panel.add(UITheme.createSectionTitle("검색 조건"));
        panel.add(Box.createVerticalStrut(15));

        westRouteLabel = createStyledLabel("미정", UITheme.FONT_BOLD);
        westDepDateLabel = createStyledLabel("-", UITheme.FONT_PLAIN);
        westRetDateLabel = createStyledLabel("-", UITheme.FONT_PLAIN);
        westSeatLabel = createStyledLabel("-", UITheme.FONT_PLAIN);

        panel.add(westRouteLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(westDepDateLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(westRetDateLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(westSeatLabel);

        panel.add(Box.createVerticalStrut(30));

        // 정렬 기준 섹션
        panel.add(UITheme.createSectionTitle("정렬 기준"));
        panel.add(Box.createVerticalStrut(10));

        priceCheckBox    = createStyledCheckBox("가격순");
        durationCheckBox = createStyledCheckBox("소요시간순");
        depTimeCheckBox  = createStyledCheckBox("출발시간순");
        arrTimeCheckBox  = createStyledCheckBox("도착시간순");

        panel.add(priceCheckBox);
        panel.add(durationCheckBox);
        panel.add(depTimeCheckBox);
        panel.add(arrTimeCheckBox);

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    // 3. 중앙 테이블 패널
    private JScrollPane createCenterTable() {
        // 컬럼: [0:항공편, 1:출발, 2:도착, 3:소요시간, 4:가격(표시용), 5:RouteId(숨김), 6:TotalPrice(숨김)]
        String[] columnNames = { "항공편", "출발 시간", "도착 시간", "소요 시간", "1인 정가", "RouteId", "TotalPrice" };
        
        model = new DefaultTableModel(new Object[][]{}, columnNames) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(model);
        
        UITheme.applyTableTheme(table); // 공통 테이블 테마 적용

        // 셀 가운데 정렬
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0; i<5; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // 내부 로직용 컬럼(RouteId, TotalPrice) 숨김 처리
        hideColumn(5); 
        hideColumn(6); 

        // 커스텀 정렬기 설정 (문자열이 아닌 숫자/시간 기준 정렬을 위해)
        sorter = new TableRowSorter<>(model);
        sorter.setComparator(4, (o1, o2) -> Integer.compare(parsePrice(o1), parsePrice(o2))); // 가격
        sorter.setComparator(1, (o1, o2) -> o1.toString().compareTo(o2.toString()));          // 출발시간
        sorter.setComparator(2, (o1, o2) -> o1.toString().compareTo(o2.toString()));          // 도착시간
        sorter.setComparator(3, (o1, o2) -> Integer.compare(parseDurationMinutes(o1), parseDurationMinutes(o2))); // 소요시간
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); 
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        return scrollPane;
    }

    // 4. 우측 선택 상세 패널
    private JPanel createEastPanel() {
        UITheme.RoundedPanel panel = new UITheme.RoundedPanel(20, Color.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setPreferredSize(new Dimension(230, 0));

        JLabel title = new JLabel("선택 항공권");
        title.setFont(UITheme.FONT_SUBTITLE);
        title.setForeground(UITheme.PRIMARY_BLUE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        
        panel.add(Box.createVerticalStrut(15));
        panel.add(new JSeparator());
        panel.add(Box.createVerticalStrut(15));

        eastFlightNoLabel = createStyledLabel("항공편: -", UITheme.FONT_PLAIN);
        eastRouteTimeLabel = createStyledLabel("<html>(항공편을<br>선택하세요)</html>", UITheme.FONT_PLAIN);
        eastPriceLabel = new JLabel(" ");
        eastPriceLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        eastPriceLabel.setForeground(UITheme.TEXT_COLOR);
        eastPriceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(eastFlightNoLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(eastRouteTimeLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(eastPriceLabel);

        panel.add(Box.createVerticalGlue());

        UITheme.RoundedButton selectButton = new UITheme.RoundedButton("좌석 선택");
        selectButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        selectButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        selectButton.addActionListener(e -> handleFlightSelection());

        panel.add(selectButton);
        return panel;
    }

    // --- 로직 메서드 ---

    // 테이블 행 선택 시 우측 패널 정보 업데이트
    private void initTableSelectionListener() {
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                
                String flightCode = getValueAt(row, 0);
                String depTime = getValueAt(row, 1);
                String arrTime = getValueAt(row, 2);
                String totalPrice = getValueAt(row, 6); // 숨겨진 총액

                eastFlightNoLabel.setText("항공편: " + flightCode);
                eastRouteTimeLabel.setText("<html>" + currentRoute + "<br>" + depTime + " ~ " + arrTime + "</html>");
                eastPriceLabel.setText(totalPrice);
            } 
        });
    }

    /**
     * [항공권 선택 처리]
     * 선택된 항공편 정보를 바탕으로 좌석 선택 다이얼로그를 호출합니다.
     */
    private void handleFlightSelection() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(mainApp, "항공편을 먼저 선택해주세요.");
            return;
        }

        String flightId    = getValueAt(selectedRow, 0);
        String depTimeFull = getValueAt(selectedRow, 1);
        String arrTimeFull = getValueAt(selectedRow, 2);
        String routeShort  = getValueAt(selectedRow, 5);
        String totalPrice  = getValueAt(selectedRow, 6);

        String realDepDate = depTimeFull.split(" ")[0]; // "MM-dd" 추출
        String realRetDate = (currentReturnDate == null || currentReturnDate.contains("선택")) ? "" : currentReturnDate;

        // 좌석 선택 다이얼로그 호출 (MainApp 경유)
        mainApp.openSeatSelection(
                flightId,
                routeShort,            
                currentRoute,          
                realDepDate,           
                realRetDate,           
                depTimeFull + " ~ " + arrTimeFull, 
                currentSeatSummary,
                totalPrice
        );
    }

    // 정렬 체크박스 이벤트 처리 (단일 선택)
    private void initSortCheckBoxActions() {
        if (sorter == null) return;

        java.awt.event.ItemListener listener = e -> {
            JCheckBox src = (JCheckBox) e.getItemSelectable();
            if (e.getStateChange() == ItemEvent.SELECTED) {
                // 하나만 선택되도록 처리
                if (src != priceCheckBox)    priceCheckBox.setSelected(false);
                if (src != durationCheckBox) durationCheckBox.setSelected(false);
                if (src != depTimeCheckBox)  depTimeCheckBox.setSelected(false);
                if (src != arrTimeCheckBox)  arrTimeCheckBox.setSelected(false);

                if (src == priceCheckBox)         applySort(4); // 가격
                else if (src == durationCheckBox) applySort(3); // 소요시간
                else if (src == depTimeCheckBox)  applySort(1); // 출발
                else if (src == arrTimeCheckBox)  applySort(2); // 도착
            } else {
                // 모두 해제 시 정렬 초기화
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
        List<RowSorter.SortKey> keys = new ArrayList<>();
        keys.add(new RowSorter.SortKey(columnIndex, SortOrder.ASCENDING));
        sorter.setSortKeys(keys);
    }

    // 외부에서 검색 결과 정보를 받아와 패널 업데이트
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

    // 테이블 데이터 갱신
    public void populateTable(Object[][] data) {
        model.setRowCount(0);
        if (data != null) {
            for (Object[] row : data) {
                model.addRow(row);
            }
        }
    }

    // --- Helpers ---

    private void hideColumn(int index) {
        table.getColumnModel().getColumn(index).setMinWidth(0);
        table.getColumnModel().getColumn(index).setMaxWidth(0);
        table.getColumnModel().getColumn(index).setWidth(0);
    }

    private JLabel createStyledLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(UITheme.TEXT_COLOR);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JCheckBox createStyledCheckBox(String text) {
        JCheckBox cb = new JCheckBox(text);
        cb.setFont(UITheme.FONT_PLAIN);
        cb.setBackground(Color.WHITE);
        cb.setFocusPainted(false);
        cb.setAlignmentX(Component.LEFT_ALIGNMENT);
        return cb;
    }

    private String getValueAt(int row, int col) {
        Object val = table.getValueAt(row, col); // 뷰 인덱스 사용
        return val != null ? val.toString() : "";
    }

    private String formatInfoText(String text, String defaultText) {
        if (text == null || text.trim().isEmpty() || text.contains("날짜") || text.contains("선택")) {
            return defaultText;
        }
        return text;
    }

    // --- Parsers (정렬용) ---
    private int parsePrice(Object value) {
        if (value == null) return 0;
        String s = value.toString().replaceAll("[^0-9]", "");
        return s.isEmpty() ? 0 : Integer.parseInt(s);
    }

    private int parseDurationMinutes(Object value) {
        if (value == null) return 0;
        String s = value.toString();
        int hours = 0, mins = 0;
        Matcher mHour = Pattern.compile("(\\d+)시간").matcher(s);
        Matcher mMin  = Pattern.compile("(\\d+)분").matcher(s);
        if (mHour.find()) hours = Integer.parseInt(mHour.group(1));
        if (mMin.find()) mins = Integer.parseInt(mMin.group(1));
        return hours * 60 + mins;
    }
}