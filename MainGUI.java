package main.java.gui;

import main.java.INTERFACE;
import main.java.config.DatabaseConfig;
import main.java.dao.CMMSActivityDAO;
import main.java.entities.CMMSActivity;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

/**
 * Main GUI class for CMMS system.
 * 分两个区：输入区 (JTabbedPane, 3 tabs) 和输出区 (JTable for results).
 * 输入区：
 * - Tab 1: Empty for future regular search.
 * - Tab 2: SQL input with submit button and Enter support.
 * - Tab 3: Quick query with building name input and date pickers (inputs in one row, spinners enlarged for year/month/day separate adjustment).
 * 输出区: Displays query results as table (columns widened, font larger).
 * 输入区面积增大 (占 ~40%)，输出区相应调整；窗口分辨率增大到 1400x1000.
 * JOptionPane buttons set to English "OK" / "Confirm".
 * 全英文界面，中文注释.
 */
public class MainGUI extends JFrame {
    private JTabbedPane inputTabs;  // 输入区标签页
    private JTextArea sqlInput;     // SQL 输入文本区
    private JTable outputTable;     // 输出表格
    private DefaultTableModel tableModel;  // 表格模型
    private static Connection conn;  // 数据库连接

    public MainGUI() {
        // 设置英文 Locale 和 JOptionPane 按钮文本 (OK / Confirm)
        Locale.setDefault(Locale.ENGLISH);
        UIManager.put("OptionPane.okButtonText", "OK");
        UIManager.put("OptionPane.yesButtonText", "Yes");
        UIManager.put("OptionPane.noButtonText", "No");
        UIManager.put("OptionPane.cancelButtonText", "Cancel");

        // 初始化数据库连接
        try {
            conn = DatabaseConfig.getInstance().getConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        setTitle("Group 38 CMMS - Campus Maintenance System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1477, 900);  // 分辨率增大
        setLocationRelativeTo(null);  // 居中显示

        // 主面板：BorderLayout，北输入区 (增大面积)，南输出区
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 输入区：JTabbedPane，3 tabs (增大高度)
        inputTabs = new JTabbedPane();
        setupInputTabs();
        JPanel inputWrapper = new JPanel(new BorderLayout());
        inputWrapper.add(inputTabs, BorderLayout.CENTER);
        inputWrapper.setPreferredSize(new Dimension(1400, 300));  // 输入区固定高，占 ~30%
        mainPanel.add(inputWrapper, BorderLayout.NORTH);

        // 输出区：JScrollPane 包裹 JTable (剩余面积)
        setupOutputTable();
        mainPanel.add(new JScrollPane(outputTable), BorderLayout.CENTER);

        add(mainPanel);
    }

    /**
     * 设置输入区 3 个标签页.
     */
    private void setupInputTabs() {
        // Tab 1: Empty placeholder for future regular search
        JPanel tab1 = new JPanel(new BorderLayout());
        tab1.add(new JLabel("Placeholder for Regular Search (To be implemented)", SwingConstants.CENTER), BorderLayout.CENTER);
        inputTabs.addTab("Regular Search", tab1);

        // Tab 2: SQL input with submit button (Enter support)
        JPanel tab2 = new JPanel(new BorderLayout(5, 5));
        sqlInput = new JTextArea(8, 60);  // 增大文本区
        sqlInput.setBorder(BorderFactory.createTitledBorder("Enter SQL Query"));
        sqlInput.setFont(new Font("Monospaced", Font.PLAIN, 14));  // 字体稍大
        JScrollPane sqlScroll = new JScrollPane(sqlInput);

        JButton sqlSubmit = new JButton("Execute SQL");
        sqlSubmit.addActionListener(new SQLActionListener());

        // 支持 Enter 提交
        sqlInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sqlSubmit.doClick();
                }
            }
        });

        tab2.add(sqlScroll, BorderLayout.CENTER);
        tab2.add(sqlSubmit, BorderLayout.SOUTH);
        inputTabs.addTab("SQL Query", tab2);

        // Tab 3: Quick Query with building ID input and date pickers (all in one row, spinners enlarged for year/month/day)
        JPanel tab3 = new JPanel(new BorderLayout(5, 5));
        tab3.setBorder(BorderFactory.createTitledBorder("Quick Query for Cleaning Activities"));

        // 输入行：Building ID + Start Date + End Date 在同一行 (GridBagLayout 水平排列，组件放大)
        JPanel inputRow = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);  // 增大间距
        gbc.anchor = GridBagConstraints.WEST;

        // Building ID (放大字段)
        gbc.gridx = 0; gbc.gridy = 0;
        inputRow.add(new JLabel("Building ID (e.g., B001):"), gbc);
        gbc.gridx = 1;
        JTextField buildingField = new JTextField(20);  // 放大宽度
        buildingField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputRow.add(buildingField, gbc);

        // Start Date (Spinner 放大，支持年月日分开调整)
        gbc.gridx = 2; gbc.gridy = 0;
        inputRow.add(new JLabel("Start Date:"), gbc);
        gbc.gridx = 3;
        SpinnerDateModel startModel = new SpinnerDateModel();
        JSpinner startSpinner = new JSpinner(startModel);
        startSpinner.setEditor(new JSpinner.DateEditor(startSpinner, "yyyy-MM-dd"));
        startSpinner.setPreferredSize(new Dimension(140, 30));  // 放大 Spinner
        startSpinner.setFont(new Font("Arial", Font.PLAIN, 14));
        startSpinner.setValue(new Date());  // 默认今天
        inputRow.add(startSpinner, gbc);

        // End Date (同上)
        gbc.gridx = 4; gbc.gridy = 0;
        inputRow.add(new JLabel("End Date:"), gbc);
        gbc.gridx = 5;
        SpinnerDateModel endModel = new SpinnerDateModel();
        JSpinner endSpinner = new JSpinner(endModel);
        endSpinner.setEditor(new JSpinner.DateEditor(endSpinner, "yyyy-MM-dd"));
        endSpinner.setPreferredSize(new Dimension(140, 30));  // 放大
        endSpinner.setFont(new Font("Arial", Font.PLAIN, 14));
        endSpinner.setValue(new Date());
        inputRow.add(endSpinner, gbc);

        tab3.add(inputRow, BorderLayout.CENTER);

        // 查找按钮：底下居中
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton quickSubmit = new JButton("Search Cleaning Activities");
        quickSubmit.setFont(new Font("Arial", Font.PLAIN, 14));  // 按钮字体大
        quickSubmit.setPreferredSize(new Dimension(200, 40));  // 放大按钮
        quickSubmit.addActionListener(new QuickQueryActionListener(buildingField, startSpinner, endSpinner));
        buttonPanel.add(quickSubmit);
        tab3.add(buttonPanel, BorderLayout.SOUTH);

        inputTabs.addTab("Quick Query", tab3);

        // Tab 切换监听（可选：切换时清空输出）
        inputTabs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                clearTable();
            }
        });
    }

    /**
     * 设置输出表格 (列宽动态调整，用户拖拽支持；字体大，行高增).
     */
    private void setupOutputTable() {
        String[] columns = {"Column 1", "Column 2", "Column 3", "Column 4", "Column 5", "Column 6", "Chemical Used", "Chemical Name"};  // 默认列，支持动态调整
        tableModel = new DefaultTableModel(columns, 0);
        outputTable = new JTable(tableModel);
        outputTable.setFont(new Font("Arial", Font.PLAIN, 16));  // 字体大一些
        outputTable.setRowHeight(30);  // 行高增
        outputTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);  // 固定宽度，但允许手动拖拽

        // 默认延长每个列宽 (150-200)
        for (int i = 0; i < 8; i++) {  // 假设8列
            TableColumn col = outputTable.getColumnModel().getColumn(i);
            col.setPreferredWidth(180);  // 初始宽
            col.setMinWidth(100);  // 最小宽
            col.setMaxWidth(300);  // 最大宽
        }

        // 动态调整：添加 MouseListener 到表头，支持拖拽列宽
        outputTable.getTableHeader().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                outputTable.columnAtPoint(evt.getPoint());  // 启用拖拽
            }
        });
        outputTable.getTableHeader().setReorderingAllowed(true);  // 允许列拖拽排序 (可选)

        // 表格整体首选大小 (占剩余面积)
        outputTable.setPreferredScrollableViewportSize(new Dimension(1400, 600));
    }

    /**
     * 清空输出表格.
     */
    private void clearTable() {
        tableModel.setRowCount(0);
    }

    /**
     * SQL 执行监听器（Tab 2）.
     */
    private class SQLActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String sql = sqlInput.getText().trim();
            if (sql.isEmpty()) {
                JOptionPane.showMessageDialog(MainGUI.this, "Please enter SQL query.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            clearTable();
            try (Statement stmt = conn.createStatement()) {
                boolean hasResult = stmt.execute(sql);

                if (hasResult) {
                    // 查询：填充表格
                    ResultSet rs = stmt.getResultSet();
                    ResultSetMetaData meta = rs.getMetaData();
                    int colCount = meta.getColumnCount();

                    // 动态设置列名
                    Vector<String> columnVector = new Vector<>();
                    for (int i = 1; i <= colCount; i++) {
                        columnVector.add(meta.getColumnName(i));
                    }
                    tableModel.setColumnIdentifiers(columnVector);

                    // 填充行
                    while (rs.next()) {
                        Vector<Object> row = new Vector<>();
                        for (int i = 1; i <= colCount; i++) {
                            row.add(rs.getObject(i));
                        }
                        tableModel.addRow(row);
                    }

                    // 动态调整新列宽
                    for (int i = 0; i < colCount; i++) {
                        TableColumn col = outputTable.getColumnModel().getColumn(i);
                        col.setPreferredWidth(180);
                    }

                    JOptionPane.showMessageDialog(MainGUI.this, "Query executed successfully. Results in table.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // 更新：显示影响行数
                    int updateCount = stmt.getUpdateCount();
                    JOptionPane.showMessageDialog(MainGUI.this, "Update executed. Rows affected: " + updateCount, "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(MainGUI.this, "SQL Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Quick Query 执行监听器（Tab 3）.
     */
    private class QuickQueryActionListener implements ActionListener {
        private JTextField buildingField;
        private JSpinner startSpinner;
        private JSpinner endSpinner;

        public QuickQueryActionListener(JTextField buildingField, JSpinner startSpinner, JSpinner endSpinner) {
            this.buildingField = buildingField;
            this.startSpinner = startSpinner;
            this.endSpinner = endSpinner;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String buildingId = buildingField.getText().trim();
            Date startDate = (Date) startSpinner.getValue();
            Date endDate = (Date) endSpinner.getValue();

            if (buildingId.isEmpty()) {
                JOptionPane.showMessageDialog(MainGUI.this, "Please enter Building ID.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String startStr = sdf.format(startDate);
            String endStr = sdf.format(endDate);

            clearTable();

            // Quick Query SQL (匹配项目要求：cleaning activities in time period for building, with chemicals)
            String sql = "SELECT a.Activity_ID, a.type, a.Building_ID, a.LevelNumber, a.RoomNumber, c.CP_ID " +
                    "FROM cmms_activity a " +
                    "LEFT JOIN activity_uses_chemical uc ON a.Activity_ID = uc.Activity_ID " +
                    "LEFT JOIN chemical_product c ON uc.CP_ID = c.CP_ID " +
                    "WHERE a.Building_ID LIKE ? AND a.type = 'cleaning' " +
                    "AND a.End_Date >= ? AND a.Start_Date <= ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, "%" + buildingId + "%");
                pstmt.setString(2, startStr);  // End_Date >= startStr
                pstmt.setString(3, endStr);    // Start_Date <= endStr

                ResultSet rs = pstmt.executeQuery();
                ResultSetMetaData meta = rs.getMetaData();
                int cols = meta.getColumnCount();

                // 动态列名
                Vector<String> columnVector = new Vector<>();
                for (int i = 1; i <= cols; i++) {
                    columnVector.add(meta.getColumnName(i));
                }
                columnVector.add("Chemical Used");
                columnVector.add("Chemical Name");
                tableModel.setColumnIdentifiers(columnVector);

                // 填充行 + 化学品逻辑
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    for (int i = 1; i <= cols; i++) {
                        row.add(rs.getObject(i));
                    }

                    String cpId = rs.getString("CP_ID");
                    if (cpId == null || cpId.isEmpty()) {
                        row.add("NO");
                        row.add("N/A");
                    } else {
                        // 二次查询名称
                        String tempSql = "SELECT Name FROM chemical_product WHERE CP_ID = ?";
                        try (PreparedStatement tempPstmt = conn.prepareStatement(tempSql)) {
                            tempPstmt.setString(1, cpId);
                            ResultSet tempRs = tempPstmt.executeQuery();
                            String name = tempRs.next() ? tempRs.getString("Name") : "Unknown";
                            row.add("YES");
                            row.add(name);
                        } catch (SQLException ex) {
                            row.add("YES");
                            row.add("Unknown");
                        }
                    }

                    tableModel.addRow(row);
                }

                // 动态调整列宽
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    TableColumn col = outputTable.getColumnModel().getColumn(i);
                    col.setPreferredWidth(180);
                }

                if (tableModel.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(MainGUI.this, "No matching cleaning activities found.", "No Results", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(MainGUI.this, "Quick query completed. Results in table.", "Success", JOptionPane.INFORMATION_MESSAGE);
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(MainGUI.this, "Quick Query Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 主方法，启动 GUI.
     */
    /*
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainGUI().setVisible(true));
    }

     */
}