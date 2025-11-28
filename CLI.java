package main.java;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import main.java.config.DatabaseConfig;
import main.java.dao.EmployeeDAO;
import main.java.entities.BaseLevelWorker;
import main.java.entities.ExecutiveOfficer;
import main.java.entities.MidLevelManager;
import main.java.service.FacilityService;

public class CLI {
    private static Scanner scanner = new Scanner(System.in);
    private static EmployeeDAO employeeDAO = new EmployeeDAO();
    private static FacilityService facilityService = new FacilityService();
    private static Connection conn;
    private static Statement stmt;
    private static Statement checkStmt;
    private static PreparedStatement checkPstmt;
    private static PreparedStatement updateMidPstmt;
    private static PreparedStatement updateEOPstmt;
    private static ResultSet rs;

    public static void main(String[] args) throws SQLException {
        conn = DatabaseConfig.getInstance().getConnection();
        stmt = conn.createStatement();
        checkStmt = conn.createStatement();
        checkPstmt = null;
        updateMidPstmt = null;
        updateEOPstmt = null;
        rs = null;


            System.out.println("=== CMMS系统 ===");
            System.out.println("1. 员工管理");
            System.out.println("2. 设施管理");
            System.out.println("3. 活动管理"); // 新增活动管理选项
            System.out.println("4. 退出");
//        Scanner scanner; // 🔧 修复：声明在try外面，让finally能访问

            System.out.print("请选择功能模块: ");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    handleEmployeeManagement();
                    break;
                case 2:
                    handleFacilityManagement();
                    break;
//                case 3:
//                    handleActivityManagement(); // 新增活动管理处理
//                    break;
                case 4:
                    System.out.println("谢谢使用！");
                    return;
                default:
                    System.out.println("无效选择");
            }

    }


    private static void handleEmployeeManagement() throws SQLException {
        int choice1;
        do{
            System.out.println("1. 添加员工");      //在这里区分高管还是其他
            System.out.println("2. 查询员工");
            System.out.println("3. 更新员工信息");
            System.out.println("4. 删除员工");
            System.out.println("5. 返回上一级");

            System.out.print("请选择: ");
            choice1 = Integer.parseInt(scanner.nextLine());

            if (choice1 == 5) break;

            switch (choice1) {
                case 1:
                    addExecutiveOfficer();
                    break;
                case 2:
                    queryEmployees();
                    break;
                case 3:
                    updateEmployee();
                    break;
                case 4:
                    deleteEmployee();
                    break;
                default:
                    System.out.println("无效选择");
            }
        }while (choice1 !=4);
    }

    private static void addExecutiveOfficer() {
        System.out.println("\n=== 员工级别 ===");
        System.out.println("1. ExecutiveOfficers");      //在这里区分高管还是其他
        System.out.println("2. MidLevelManager");
        System.out.println("3. BaseLevelWorker");
        System.out.print("请选择: ");
        int choice2 = Integer.parseInt(scanner.nextLine());

        switch (choice2) {
            case 1:
                System.out.print("输入员工ID: ");
                String eoid = scanner.nextLine();
                System.out.print("输入姓名: ");
                String eoname = scanner.nextLine();
                System.out.print("输入联系方式: ");
                String eocontact = scanner.nextLine();
                System.out.print("输入邮箱: ");
                String eoemail = scanner.nextLine();
                employeeDAO.addExecutiveOfficer(new ExecutiveOfficer(eoid, eoname, eocontact, eoemail));
                System.out.println("add successful");
                break;
            case 2:
                System.out.print("输入员工ID: ");
                String midid = scanner.nextLine();
                System.out.print("输入姓名: ");
                String midname = scanner.nextLine();
                System.out.print("输入联系方式: ");
                String midcontact = scanner.nextLine();
                System.out.print("输入邮箱: ");
                String midemail = scanner.nextLine();
                System.out.print("输入上级ID: ");
                String eoid2String = scanner.nextLine();
                employeeDAO.addMidLevelManager(new MidLevelManager(midid, midname, midcontact, midemail, eoid2String));
                System.out.println("add successful");
                break;
            case 3:
                System.out.print("输入员工ID: ");
                String bidid = scanner.nextLine();
                System.out.print("输入姓名: ");
                String bidname = scanner.nextLine();
                System.out.print("输入联系方式: ");
                String bidcontact = scanner.nextLine();
                System.out.print("输入邮箱: ");
                String bidemail = scanner.nextLine();
                System.out.print("输入上级ID: ");
                String mid2String = scanner.nextLine();
                employeeDAO.addBaseLevelWorker(new BaseLevelWorker(bidid, bidname, bidcontact, bidemail, mid2String));
                System.out.println("add successful");
                break;
            default:
                System.out.println("无效选择");
        }


    }

    private static void queryEmployees() {
        System.out.println("\n=== 员工级别 ===");
        System.out.println("1. 查看高管");
        System.out.println("2. 查看经理");
        System.out.println("3. 查看员工");
        System.out.print("请选择: ");
        int choice = Integer.parseInt(scanner.nextLine());

        String employeeId;
        while (true) {
            System.out.print("请输入要updata的员工ID: ");
            employeeId = scanner.nextLine().trim();
            if (employeeId.isEmpty()) {
                System.out.println("员工ID不能为空！");
                return;
            }
            try {
                String checkSql = "";
                String tableName = "";
                String idColumn = "";

                switch (choice) {
                    case 1:
                        tableName = "executive_officer";
                        idColumn = "EO_ID";
                        checkSql = String.format("SELECT 1 FROM %s WHERE %s = '%s'", tableName, idColumn, employeeId);
                        break;
                    case 2:
                        tableName = "mid_level_manager";
                        idColumn = "MLM_ID";
                        checkSql = String.format("SELECT 1 FROM %s WHERE %s = '%s'", tableName, idColumn, employeeId);
                        break;
                    case 3:
                        tableName = "base_level_worker";
                        idColumn = "BLW_ID";
                        checkSql = String.format("SELECT 1 FROM %s WHERE %s = '%s'", tableName, idColumn, employeeId);
                        break;
                    default:
                        System.out.println("无效选择");
                        continue;
                }

                try (ResultSet rs = checkStmt.executeQuery(checkSql);) {
                    if (!rs.next()) {
                        System.err.println("错误：" + tableName + " 表中不存在ID为 [" + employeeId + "] 的员工！");
                    } else {
                        break;
                    }
                }
            } catch (SQLException e) {
                System.err.println("检查员工ID存在性时出错：" + e.getMessage());
                e.printStackTrace();
            }
        }
        switch (choice) {
            case 1:
                employeeDAO.getManagersByExecutive(employeeId).forEach(System.out::println);
                try {
                    boolean hasResultSet = stmt.execute(
                            "SELECT * FROM executive_officer WHERE EO_ID = '" + employeeId + "'"
                    );
                    System.out.println("add successful");
                    if (hasResultSet) {
                        ResultSet rs = stmt.getResultSet();
                        ResultSetMetaData meta = rs.getMetaData();
                        int cols = meta.getColumnCount();

                        // 步骤1: 收集所有行数据
                        List<List<String>> rows = new ArrayList<>();
                        while (rs.next()) {
                            List<String> row = new ArrayList<>();
                            for (int i = 1; i <= cols; i++) {
                                String val = rs.getString(i);
                                row.add(val != null ? val : "NULL");
                            }
                            rows.add(row);
                        }

                        // 步骤2: 计算每列最大宽度 + 4 padding（更宽松）
                        int[] colWidths = new int[cols];
                        for (int i = 1; i <= cols; i++) {
                            colWidths[i - 1] = meta.getColumnName(i).length() + 4;
                        }
                        for (List<String> row : rows) {
                            for (int i = 0; i < cols; i++) {
                                colWidths[i] = Math.max(colWidths[i], row.get(i).length() + 4);
                            }
                        }

                        // 步骤3: 打印表头（左对齐）
                        for (int i = 1; i <= cols; i++) {
                            System.out.print(String.format("%-" + colWidths[i - 1] + "s", meta.getColumnName(i)));
                        }
                        System.out.println();

                        // 打印分隔线（每个─段匹配宽度，每列间加1空格，整个左对齐）
                        for (int width : colWidths) {
                            System.out.print("─".repeat(width - 1) + " ");
                        }
                        System.out.println();

                        // 步骤4: 打印每一行（左对齐）
                        for (List<String> row : rows) {
                            for (int i = 0; i < cols; i++) {
                                System.out.print(String.format("%-" + colWidths[i] + "s", row.get(i)));
                            }
                            System.out.println();
                        }
                    } else {
                        // 是 INSERT/UPDATE/DELETE → 打印影响行数
                        System.out.println("Done! " + stmt.getUpdateCount() + " lines is revised!");
                    }
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case 2:
                employeeDAO.getManagersByExecutive(employeeId).forEach(System.out::println);
                try {
                    boolean hasResultSet = stmt.execute(
                            "SELECT * FROM mid_level_manager WHERE MLM_ID = '" + employeeId + "'"
                    );
                    System.out.println("add successful");
                    if (hasResultSet) {
                        ResultSet rs = stmt.getResultSet();
                        ResultSetMetaData meta = rs.getMetaData();
                        int cols = meta.getColumnCount();

                        // 步骤1: 收集所有行数据
                        List<List<String>> rows = new ArrayList<>();
                        while (rs.next()) {
                            List<String> row = new ArrayList<>();
                            for (int i = 1; i <= cols; i++) {
                                String val = rs.getString(i);
                                row.add(val != null ? val : "NULL");
                            }
                            rows.add(row);
                        }

                        // 步骤2: 计算每列最大宽度 + 4 padding（更宽松）
                        int[] colWidths = new int[cols];
                        for (int i = 1; i <= cols; i++) {
                            colWidths[i - 1] = meta.getColumnName(i).length() + 4;
                        }
                        for (List<String> row : rows) {
                            for (int i = 0; i < cols; i++) {
                                colWidths[i] = Math.max(colWidths[i], row.get(i).length() + 4);
                            }
                        }

                        // 步骤3: 打印表头（左对齐）
                        for (int i = 1; i <= cols; i++) {
                            System.out.print(String.format("%-" + colWidths[i - 1] + "s", meta.getColumnName(i)));
                        }
                        System.out.println();

                        // 打印分隔线（每个─段匹配宽度，每列间加1空格，整个左对齐）
                        for (int width : colWidths) {
                            System.out.print("─".repeat(width - 1) + " ");
                        }
                        System.out.println();

                        // 步骤4: 打印每一行（左对齐）
                        for (List<String> row : rows) {
                            for (int i = 0; i < cols; i++) {
                                System.out.print(String.format("%-" + colWidths[i] + "s", row.get(i)));
                            }
                            System.out.println();
                        }
                    } else {
                        // 是 INSERT/UPDATE/DELETE → 打印影响行数
                        System.out.println("Done! " + stmt.getUpdateCount() + " lines is revised!");
                    }
                    break;
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case 3:
                employeeDAO.getWorkersByManager(employeeId).forEach(System.out::println);
                try {
                    boolean hasResultSet = stmt.execute(
                            "SELECT * FROM base_level_worker WHERE BLM_ID = '" + employeeId + "'"
                    );
                    System.out.println("add successful");
                    if (hasResultSet) {
                        ResultSet rs = stmt.getResultSet();
                        ResultSetMetaData meta = rs.getMetaData();
                        int cols = meta.getColumnCount();

                        // 步骤1: 收集所有行数据
                        List<List<String>> rows = new ArrayList<>();
                        while (rs.next()) {
                            List<String> row = new ArrayList<>();
                            for (int i = 1; i <= cols; i++) {
                                String val = rs.getString(i);
                                row.add(val != null ? val : "NULL");
                            }
                            rows.add(row);
                        }

                        // 步骤2: 计算每列最大宽度 + 4 padding（更宽松）
                        int[] colWidths = new int[cols];
                        for (int i = 1; i <= cols; i++) {
                            colWidths[i - 1] = meta.getColumnName(i).length() + 4;
                        }
                        for (List<String> row : rows) {
                            for (int i = 0; i < cols; i++) {
                                colWidths[i] = Math.max(colWidths[i], row.get(i).length() + 4);
                            }
                        }

                        // 步骤3: 打印表头（左对齐）
                        for (int i = 1; i <= cols; i++) {
                            System.out.print(String.format("%-" + colWidths[i - 1] + "s", meta.getColumnName(i)));
                        }
                        System.out.println();

                        // 打印分隔线（每个─段匹配宽度，每列间加1空格，整个左对齐）
                        for (int width : colWidths) {
                            System.out.print("─".repeat(width - 1) + " ");
                        }
                        System.out.println();

                        // 步骤4: 打印每一行（左对齐）
                        for (List<String> row : rows) {
                            for (int i = 0; i < cols; i++) {
                                System.out.print(String.format("%-" + colWidths[i] + "s", row.get(i)));
                            }
                            System.out.println();
                        }
                    } else {
                        // 是 INSERT/UPDATE/DELETE → 打印影响行数
                        System.out.println("Done! " + stmt.getUpdateCount() + " lines is revised!");
                    }
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
                break;
            default:
                System.out.println("无效选择");
        }
    }

    private static void deleteEmployee() {

        try {
            //conn = DatabaseConfig.getInstance().getConnection();
            scanner = new Scanner(System.in);
            System.out.println("\n=== 员工级别 ===");
            System.out.println("1. ExecutiveOfficers");
            System.out.println("2. MidLevelManager");
            System.out.println("3. BaseLevelWorker");
            System.out.print("请选择: ");
            int choice = Integer.parseInt(scanner.nextLine());
            String employeeId;

            while (true) {

                System.out.print("请输入要删除的员工ID: ");
                employeeId = scanner.nextLine().trim();
                if (employeeId.isEmpty()) {
                    System.out.println("员工ID不能为空！");
                    return;
                }
                try {
                    String checkSql = "";
                    String tableName = "";
                    String idColumn = "";

                    switch (choice) {
                        case 1:
                            tableName = "executive_officer";
                            idColumn = "EO_ID";
                            checkSql = String.format("SELECT 1 FROM %s WHERE %s = '%s'", tableName, idColumn, employeeId);
//                        flag = false;
                            break;
                        case 2:
                            tableName = "mid_level_manager";
                            idColumn = "MLM_ID";
                            checkSql = String.format("SELECT 1 FROM %s WHERE %s = '%s'", tableName, idColumn, employeeId);
//                        flag = false;
                            break;
                        case 3:
                            tableName = "base_level_worker";
                            idColumn = "BLW_ID";
                            checkSql = String.format("SELECT 1 FROM %s WHERE %s = '%s'", tableName, idColumn, employeeId);
//                        flag = false;
                            break;
                        default:
                            System.out.println("无效选择");
                            continue; // 终止操作
                    }

                    // 执行查询并判断结果
                    try (ResultSet rs = checkStmt.executeQuery(checkSql);) {
                        if (!rs.next()) {
                            System.err.println("错误：" + tableName + " 表中不存在ID为 [" + employeeId + "] 的员工！");
                        } else {
                            break;
                        }
                    }
                } catch (SQLException e) {
                    System.err.println("检查员工ID存在性时出错：" + e.getMessage());
                    e.printStackTrace();
                }
            }


            try {
                switch (choice) {
                    case 1:
                        String sql = "UPDATE mid_level_manager " +
                                "SET EO_ID = NULL " + "WHERE EO_ID = '" + employeeId + "'";
                        stmt.executeUpdate(sql);
                        String sql1 = "DELETE FROM executive_officer WHERE EO_ID = '" + employeeId + "'";
                        int rowsAffected1 = stmt.executeUpdate(sql1);
                        if (rowsAffected1 > 0) {
                            System.out.println("删除成功");
                        } else {
                            System.out.println("删除失败");
                        }
                        break;
                    case 2:
                        String sql2 = "UPDATE base_level_worker " +
                                "SET MLM_ID = NULL " + "WHERE MLM_ID = '" + employeeId + "'";
                        stmt.executeUpdate(sql2);
                        String sql3 = "UPDATE building " +
                                "SET MLM_ID = NULL " + "WHERE MLM_ID = '" + employeeId + "'";
                        stmt.executeUpdate(sql3);
                        String sql4 = "DELETE FROM mid_level_manager WHERE MLM_ID = '" + employeeId + "'";
                        int rowsAffected2 = stmt.executeUpdate(sql4);
                        if (rowsAffected2 > 0) {
                            System.out.println("删除成功");
                        } else {
                            System.out.println("删除失败");
                        }
                        break;
                    case 3:
//                    String sql7 = "UPDATE activity " +
//                        "SET BLW_ID = NULL " + "WHERE BLW_ID = '" + employeeId + "'";
//                    stmt.executeUpdate(sql7);
                        String sql6 = "DELETE FROM base_level_worker WHERE BLW_ID = '" + employeeId + "'";
                        int rowsAffected3 = stmt.executeUpdate(sql6);
                        if (rowsAffected3 > 0) {
                            System.out.println("删除成功");
                        } else {
                            System.out.println("删除失败");
                        }
                    default:
                        System.out.println("无效选择");
                }
            } catch (SQLException e) {
                System.err.println("删除员工时出错：" + e.getMessage());
                e.printStackTrace();
            }

        } catch (NumberFormatException e) {
            System.out.println("输入错误，请输入数字");
        } finally {
            // 🔧 修复：关闭scanner和conn，避免资源泄漏
            if (scanner != null) {
                scanner.close();
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private static void updateEmployee() throws SQLException {
        try {
            scanner = new Scanner(System.in);
            System.out.println("\n=== 员工级别 ===");
            System.out.println("1. ExecutiveOfficers");
            System.out.println("2. MidLevelManager");
            System.out.println("3. BaseLevelWorker");
            System.out.print("请选择: ");
            int choice = Integer.parseInt(scanner.nextLine());

            String employeeId;
            while (true) {
                System.out.print("请输入要updata的员工ID: ");
                employeeId = scanner.nextLine().trim();
                if (employeeId.isEmpty()) {
                    System.out.println("员工ID不能为空！");
                    return;
                }
                try {
                    String checkSql = "";
                    String tableName = "";
                    String idColumn = "";

                    switch (choice) {
                        case 1:
                            tableName = "executive_officer";
                            idColumn = "EO_ID";
                            checkSql = String.format("SELECT 1 FROM %s WHERE %s = '%s'", tableName, idColumn, employeeId);
                            break;
                        case 2:
                            tableName = "mid_level_manager";
                            idColumn = "MLM_ID";
                            checkSql = String.format("SELECT 1 FROM %s WHERE %s = '%s'", tableName, idColumn, employeeId);
                            break;
                        case 3:
                            tableName = "base_level_worker";
                            idColumn = "BLW_ID";
                            checkSql = String.format("SELECT 1 FROM %s WHERE %s = '%s'", tableName, idColumn, employeeId);
                            break;
                        default:
                            System.out.println("无效选择");
                            continue;
                    }

                    try (ResultSet rs = checkStmt.executeQuery(checkSql);) {
                        if (!rs.next()) {
                            System.err.println("错误：" + tableName + " 表中不存在ID为 [" + employeeId + "] 的员工！");
                        } else {
                            break;
                        }
                    }
                } catch (SQLException e) {
                    System.err.println("检查员工ID存在性时出错：" + e.getMessage());
                    e.printStackTrace();
                }
            }


            try {
                switch (choice) {
                    case 1:
                        System.out.print("输入员工ID: ");
                        String eoid = scanner.nextLine();
                        System.out.print("输入姓名: ");
                        String eoname = scanner.nextLine();
                        System.out.print("输入联系方式: ");
                        String eocontact = scanner.nextLine();
                        System.out.print("输入邮箱: ");
                        String eoemail = scanner.nextLine();

                        String sql1 = "UPDATE mid_level_manager " +
                                "SET EO_ID ='" + eoid +
                                "'WHERE EO_ID = '" + employeeId + "'";
                        stmt.executeUpdate(sql1);
                        String sql = "UPDATE executive_officer " +
                                "SET EO_ID ='" + eoid + "'," +
                                "Name ='" + eoname + "'," +
                                "Contact ='" + eocontact + "'," +
                                "Email ='" + eoemail +
                                "'WHERE EO_ID = '" + employeeId + "'";
                        int rowsAffected1 = stmt.executeUpdate(sql);
                        if (rowsAffected1 > 0) {
                            System.out.println("update成功");
                        } else {
                            System.out.println("update nothing");
                        }
                        break;

                    case 2:
                        System.out.print("输入员工ID: ");
                        String midid = scanner.nextLine();
                        System.out.print("输入姓名: ");
                        String midname = scanner.nextLine();
                        System.out.print("输入联系方式: ");
                        String midcontact = scanner.nextLine();
                        System.out.print("输入邮箱: ");
                        String midemail = scanner.nextLine();
                        System.out.print("输入上级ID: ");
                        String midupper = scanner.nextLine();

                        String sql2 = "UPDATE base_level_worker " +
                                "SET MLM_ID ='" + midid +
                                "'WHERE MLM_ID = '" + employeeId + "'";
                        stmt.executeUpdate(sql2);
                        String sql4 = "UPDATE building " +
                                "SET MLM_ID ='" + midid +
                                "'WHERE MLM_ID = '" + employeeId + "'";
                        stmt.executeUpdate(sql4);
                        String sql3 = "UPDATE mid_level_manager " +
                                "SET MLM_ID ='" + midid + "'," +
                                "Name ='" + midname + "'," +
                                "Contact ='" + midcontact + "'," +
                                "Email ='" + midemail + "'," +
                                "EO_ID ='" + midupper +
                                "'WHERE MLM_ID = '" + employeeId + "'";
                        int rowsAffected2 = stmt.executeUpdate(sql3);
                        if (rowsAffected2 > 0) {
                            System.out.println("update成功");
                        } else {
                            System.out.println("update nothing");
                        }
                        break;

                    case 3:
//                    String sql7 = "UPDATE activity " +
//                        "SET BLW_ID = NULL " + "WHERE BLW_ID = '" + employeeId + "'";
//                    stmt.executeUpdate(sql7);
                        System.out.print("输入员工ID: ");
                        String bidid = scanner.nextLine();
                        System.out.print("输入姓名: ");
                        String bidname = scanner.nextLine();
                        System.out.print("输入联系方式: ");
                        String bidcontact = scanner.nextLine();
                        System.out.print("输入邮箱: ");
                        String bidemail = scanner.nextLine();
                        System.out.print("输入上级ID: ");
                        String bidupper = scanner.nextLine();

                        String sql5 = "UPDATE base_level_worker " +
                                "SET BLW_ID ='" + bidid + "'," +
                                "Name ='" + bidname + "'," +
                                "Contact ='" + bidcontact + "'," +
                                "Email ='" + bidemail + "'," +
                                "MLM_ID ='" + bidupper +
                                "' WHERE BLW_ID = '" + employeeId + "'";
                        int rowsAffected3 = stmt.executeUpdate(sql5);
                        if (rowsAffected3 > 0) {
                            System.out.println("update成功");
                        } else {
                            System.out.println("update nothing");
                        }
                        break;

                    default:
                        System.out.println("无效选择");
                }
            } catch (SQLException e) {
                System.err.println("update员工时出错：" + e.getMessage());
                e.printStackTrace();
            }

        } catch (NumberFormatException e) {
            System.out.println("输入错误，请输入数字");
        }
//        finally {
//            // 关闭所有资源
//            if (rs != null) rs.close();
//            if (checkPstmt != null) checkPstmt.close();
//            if (updateMidPstmt != null) updateMidPstmt.close();
//            if (updateEOPstmt != null) updateEOPstmt.close();
//            if (conn != null) conn.close();
//            // 不关闭scanner（全局复用）
//        }
    }

    private static void handleFacilityManagement() {
        while (true) {
            System.out.println("\n=== 设施管理 ===");
            System.out.println("1. 查询设施");
            System.out.println("2. 返回上一级");
            System.out.print("请选择: ");
            int choice = Integer.parseInt(scanner.nextLine());

            if (choice == 2) break;

            switch (choice) {
                case 1:
                    queryFacilities();
                    break;
                default:
                    System.out.println("无效选择");
            }
        }
    }

    private static void queryFacilities() {
        facilityService.displayAllBuildings();
        try {
            boolean hasResultSet = stmt.execute(
                    "SELECT * FROM building "
            );
            System.out.println("add successful");
            if (hasResultSet) {
                ResultSet rs = stmt.getResultSet();
                ResultSetMetaData meta = rs.getMetaData();
                int cols = meta.getColumnCount();

                // 步骤1: 收集所有行数据
                List<List<String>> rows = new ArrayList<>();
                while (rs.next()) {
                    List<String> row = new ArrayList<>();
                    for (int i = 1; i <= cols; i++) {
                        String val = rs.getString(i);
                        row.add(val != null ? val : "NULL");
                    }
                    rows.add(row);
                }

                // 步骤2: 计算每列最大宽度 + 4 padding（更宽松）
                int[] colWidths = new int[cols];
                for (int i = 1; i <= cols; i++) {
                    colWidths[i - 1] = meta.getColumnName(i).length() + 4;
                }
                for (List<String> row : rows) {
                    for (int i = 0; i < cols; i++) {
                        colWidths[i] = Math.max(colWidths[i], row.get(i).length() + 4);
                    }
                }

                // 步骤3: 打印表头（左对齐）
                for (int i = 1; i <= cols; i++) {
                    System.out.print(String.format("%-" + colWidths[i - 1] + "s", meta.getColumnName(i)));
                }
                System.out.println();

                // 打印分隔线（每个─段匹配宽度，每列间加1空格，整个左对齐）
                for (int width : colWidths) {
                    System.out.print("─".repeat(width - 1) + " ");
                }
                System.out.println();

                // 步骤4: 打印每一行（左对齐）
                for (List<String> row : rows) {
                    for (int i = 0; i < cols; i++) {
                        System.out.print(String.format("%-" + colWidths[i] + "s", row.get(i)));
                    }
                    System.out.println();
                }
            } else {
                // 是 INSERT/UPDATE/DELETE → 打印影响行数
                System.out.println("Done! " + stmt.getUpdateCount() + " lines is revised!");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
