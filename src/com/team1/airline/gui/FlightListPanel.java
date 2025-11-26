package com.team1.airline.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import java.awt.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * [검색 결과 리스트 패널]
 * 검색된 항공편 목록을 테이블로 보여주고, 정렬 및 선택 기능을 제공.
 */
public class FlightListPanel extends JPanel {

    private final MainApp mainApp;

    private static final Color PRIMARY_BLUE = new Color(0, 122, 255);
    private static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 20);
    private static final Font FONT_LABEL = new Font("SansSerif", Font.BOLD, 16);
    private static final Font FONT_INFO  = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font FONT_HEADER = new Font("SansSerif", Font.BOLD, 14);

    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;

    private JLabel westRouteLabel, westDepDateLabel, westRetDateLabel, westSeatLabel;
    private JLabel eastAirlineLabel, eastRouteTimeLabel, eastPriceLabel;
    private JCheckBox priceCheckBox, durationCheckBox, depTimeCheckBox, arrTimeCheckBox;

    private String currentRoute          = "";
    private String currentDepartureDate  = "";
    private String currentReturnDate     = "";
    private String currentSeatSummary    = "";

    public FlightListPanel(MainApp mainApp) {
        this.mainApp = mainApp;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(1000, 600));

        add(createTitlePanel(),  BorderLayout.NORTH);
        add(createWestPanel(),   BorderLayout.WEST);
        add(createCenterTable(), BorderLayout.CENTER);
        add(createEastPanel(),   BorderLayout.EAST);
        
        initSortCheckBoxActions();
        initTableSelectionListener();
    }

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
        String[] columnNames = { "항공편", "출발 시간", "도착 시간", "소요 시간", "가격", "RouteId", "TotalPrice" };
        
        model = new DefaultTableModel(new Object[][]{}, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(FONT_INFO);
        table.getTableHeader().setFont(FONT_HEADER);
        table.getTableHeader().setBackground(Color.LIGHT_GRAY);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 숨김 컬럼 (RouteId, TotalPrice)
        table.getColumnModel().getColumn(5).setMinWidth(0);
        table.getColumnModel().getColumn(5).setMaxWidth(0);
        table.getColumnModel().getColumn(5).setWidth(0);

        table.getColumnModel().getColumn(6).setMinWidth(0);
        table.getColumnModel().getColumn(6).setMaxWidth(0);
        table.getColumnModel().getColumn(6).setWidth(0);

        sorter = new TableRowSorter<>(model);
        // 정렬기 설정: 가격(숫자), 시간(문자열), 소요시간(숫자 파싱)
        sorter.setComparator(4, (o1, o2) -> Integer.compare(parsePrice(o1), parsePrice(o2))); 
        sorter.setComparator(1, (o1, o2) -> o1.toString().compareTo(o2.toString())); 
        sorter.setComparator(2, (o1, o2) -> o1.toString().compareTo(o2.toString())); 
        sorter.setComparator(3, (o1, o2) -> Integer.compare(parseDurationMinutes(o1), parseDurationMinutes(o2)));
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

        eastAirlineLabel   = createStyledLabel("항공편: (선택 대기)", FONT_INFO);
        eastRouteTimeLabel = createStyledLabel("(항공편을 선택하세요)", FONT_INFO);
        eastPriceLabel     = createStyledLabel(" ", FONT_TITLE); 

        eastPriceLabel.setBorder(new EmptyBorder(10, 0, 10, 0));

        panel.add(eastAirlineLabel);
        panel.add(eastRouteTimeLabel);
        panel.add(eastPriceLabel);

        JButton selectButton = new JButton("선택");
        selectButton.setBackground(PRIMARY_BLUE);
        selectButton.setForeground(Color.WHITE);
        selectButton.setFont(FONT_LABEL);
        selectButton.setFocusPainted(false);
        selectButton.setBorderPainted(false); 
        selectButton.setOpaque(true);

        selectButton.addActionListener(e -> handleFlightSelection());

        panel.add(selectButton);
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private void initTableSelectionListener() {
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();

                String flightCode = getValueAt(row, 0);
                String depTime = getValueAt(row, 1);
                String arrTime = getValueAt(row, 2);
                // String price   = getValueAt(row, 4); // 1인 정가
                String totalPrice = getValueAt(row, 6); // 총액

                eastAirlineLabel.setText("항공편: " + flightCode);
                eastRouteTimeLabel.setText("<html>" + currentRoute + "<br>" + depTime + " ~ " + arrTime + "</html>");
                eastPriceLabel.setText(totalPrice);
            } 
        });
    }

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

        String realDepDate = depTimeFull.split(" ")[0]; 
        String realRetDate = currentReturnDate;
        if (realRetDate == null || realRetDate.contains("선택") || realRetDate.isBlank()) {
            realRetDate = "";
        }

        // 좌석 선택 다이얼로그 호출
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

    private void initSortCheckBoxActions() {
        if (sorter == null) return;

        java.awt.event.ItemListener listener = e -> {
            JCheckBox src = (JCheckBox) e.getItemSelectable();
            if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                if (src != priceCheckBox)    priceCheckBox.setSelected(false);
                if (src != durationCheckBox) durationCheckBox.setSelected(false);
                if (src != depTimeCheckBox)  depTimeCheckBox.setSelected(false);
                if (src != arrTimeCheckBox)  arrTimeCheckBox.setSelected(false);

                if (src == priceCheckBox)         applySort(4); 
                else if (src == durationCheckBox) applySort(3); 
                else if (src == depTimeCheckBox)  applySort(1); 
                else if (src == arrTimeCheckBox)  applySort(2); 
            } else {
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
            for (Object[] row : data) model.addRow(row);
        }
    }

    // --- Helpers ---
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

    private int parsePrice(Object value) {
        if (value == null) return 0;
        String s = value.toString().replaceAll("[^0-9]", "");
        return s.isEmpty() ? 0 : Integer.parseInt(s);
    }

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