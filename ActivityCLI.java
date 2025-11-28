package main.java.utils;
import main.java.config.DatabaseConfig;
import main.java.dao.EmployeeDAO;
import main.java.entities.ActivityType;
import main.java.entities.CMMSActivity;
import main.java.service.FacilityService;
import main.java.service.EmployeeService;
import main.java.service.FacilityService;
import main.java.dao.CMMSActivityDAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.sql.*;
/**
 *
 */
public class ActivityCLI {
    private static List<CMMSActivity> activities = new ArrayList<>();
    private static List<ActivityType> activityTypes = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static CMMSActivityDAO cmmsActivityDAO = new CMMSActivityDAO();
    private static FacilityService facilityService = new FacilityService();
    private static Connection conn;
    private static Statement stmt;
    private static Statement checkStmt;
    private static PreparedStatement updateTypePstmt;

    public static void main(String[] args) throws SQLException {
//       PreparedStatement updateTypePstmt;
        conn = DatabaseConfig.getInstance().getConnection();
        stmt = conn.createStatement();
        checkStmt = conn.createStatement();
        int choice;
        do {
            printMenu();
            choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    addActivity();
                    break;
                case 2:
                    deleteActivity();
                    break;
                case 3:
                    updateActivity();
                    break;
                case 4:
                    System.out.println("Goodbye! Exiting program...");
                    break;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        } while (choice != 4);
    }

    private static void printMenu() {
        System.out.println("\n=== CMMS Activity Management System ===");
        System.out.println("1. Add Activity");
        System.out.println("2. Delete Activity");
        System.out.println("3. Update Activity");
        System.out.println("4. Exit");
        System.out.print("Choose an option: ");
    }

//    CMMSActivity activity;
//    activity = new CMMSActivity(activityId, type, typeId, buildingid, levelnumber, roomnumber, startTime, endTime);

    private static void addActivity() {
        System.out.println("\n--- Add New Activity ---");
        System.out.print("Enter Activity ID (e.g., A001): ");
        String activityId = scanner.nextLine();
        System.out.print("Available Activity Types: ");
        String type = scanner.nextLine();
        System.out.print("Enter Type ID (from list above): ");
        String typeId = scanner.nextLine();
        System.out.print("Enter buildingid: ");
        String buildingid = scanner.nextLine();
        System.out.print("Enter levelnumber: ");
        String levelnumber = scanner.nextLine();
        System.out.print("Enter roomnumber: ");
        String roomnumber = scanner.nextLine();
        System.out.print("Enter startTime:yyyy-MM-dd ");
        String startTime = scanner.nextLine();
        System.out.print("Enter endTime:yyyy-MM-dd ");
        String endTime = scanner.nextLine();
        cmmsActivityDAO.addCMMSActivity(new CMMSActivity(activityId, type, typeId, buildingid, levelnumber, roomnumber, startTime, endTime));
        try {
            boolean hasResultSet = stmt.execute(
                    "SELECT * FROM cmms_activity WHERE Activity_ID = '" + activityId + "'"
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
        System.out.println("add successful");
    }


    private static void deleteActivity() {
        String activityId;

        while (true) {
            System.out.print("请输入要删除的activityId: ");
            activityId = scanner.nextLine().trim();
            if (activityId.isEmpty()) {
                System.out.println("activityId不能为空！");
                return;
            }
            try {
                String checkSql = "";
                String tableName = "";
                String idColumn = "";

                tableName = "cmms_activity";
                idColumn = "Activity_ID";
                checkSql = String.format("SELECT 1 FROM %s WHERE %s = '%s'", tableName, idColumn, activityId);
//

                // 执行查询并判断结果
                try (ResultSet rs = checkStmt.executeQuery(checkSql);) {
                    if (!rs.next()) {
                        // ID不存在时抛出明确错误
                        System.err.println("错误：" + tableName + " 表中不存在ID为 [" + activityId + "] 的员工！");
                    } else {
                        break;
                    }
                }
            } catch (SQLException e) {
                System.err.println("检查员工ID存在性时出错：" + e.getMessage());
                e.printStackTrace();
                }
        }
        try{
            String sql3 = "UPDATE activity_type " +
                    "SET Type_ID = NULL, TypeName = NULL " +
                    "WHERE Type_ID IN (" +
                    "SELECT type_ID FROM cmms_activity WHERE Activity_ID = ?" +
                    ")";
            updateTypePstmt = conn.prepareStatement(sql3);
            updateTypePstmt.setString(1, activityId);
            updateTypePstmt.executeUpdate();

        String sql4 = "UPDATE activity_assigns_worker " +
                "SET Activity_ID = NULL " +
                "WHERE Activity_ID = '" + activityId + "'";
        stmt.executeUpdate(sql4);

        String sql5 = "UPDATE activity_uses_chemical " +
                "SET Activity_ID = NULL " +
                "WHERE Activity_ID = '" + activityId + "'";
        stmt.executeUpdate(sql5);

        String sql6 = "UPDATE activity_uses_facility " +
                "SET Activity_ID = NULL " +
                "WHERE Activity_ID = '" + activityId + "'";
        stmt.executeUpdate(sql6);

        String sql7 = "UPDATE activity_outsources_to " +
                "SET Activity_ID = NULL " +
                "WHERE Activity_ID = '" + activityId + "'";
        stmt.executeUpdate(sql7);

        String sql1 = "DELETE FROM cmms_activity WHERE Activity_ID = '" + activityId + "'";
        int rowsAffected1 = stmt.executeUpdate(sql1);
        if (rowsAffected1 > 0) {
            System.out.println("update成功");
        } else {
            System.out.println("update nothing");
        }
        } catch (SQLException e) {
            System.err.println("删除员工时出错：" + e.getMessage());
            e.printStackTrace();
        }
    }

private static void updateActivity() {
            String activityId;
        while (true) {
            System.out.print("请输入要update的activityId: ");
            activityId = scanner.nextLine().trim();
            if (activityId.isEmpty()) {
                System.out.println("activityId不能为空！");
                return;
            }
            try {
                String checkSql = "";
                String tableName = "";
                String idColumn = "";

                tableName = "cmms_activity";
                idColumn = "Activity_ID";
                checkSql = String.format("SELECT 1 FROM %s WHERE %s = '%s'", tableName, idColumn, activityId);
//

                // 执行查询并判断结果
                try (ResultSet rs = checkStmt.executeQuery(checkSql);) {
                    if (!rs.next()) {
                        // ID不存在时抛出明确错误
                        System.err.println("错误：" + tableName + " 表中不存在ID为 [" + activityId + "] 的activity！");
                    } else {
                        break;
                    }
                }
            } catch (SQLException e) {
                System.err.println("检查activityId存在性时出错：" + e.getMessage());
                e.printStackTrace();
                }
        }
        try{
            System.out.println("\n--- Update New Activity ---");
            System.out.print("Enter Activity ID (e.g., A001): ");
            String newid = scanner.nextLine();
            System.out.print("Available Activity Types: ");
            String type = scanner.nextLine();
            System.out.print("Enter Type ID (from list above): ");
            String typeId = scanner.nextLine();
            System.out.print("Enter buildingid: ");
            String buildingid = scanner.nextLine();
            System.out.print("Enter levelnumber: ");
            String levelnumber = scanner.nextLine();
            System.out.print("Enter roomnumber: ");
            String roomnumber = scanner.nextLine();
            System.out.print("Enter startTime: ");
            String startTime = scanner.nextLine();
            System.out.print("Enter endTime: ");
            String endTime = scanner.nextLine();
            String sql3 = "UPDATE activity_type " +
                    "SET type_ID = ? " +
                    "WHERE type_ID = (" +
                    "   SELECT type_ID FROM cmms_activity WHERE Activity_ID = ?" +
                    ")";
            updateTypePstmt = conn.prepareStatement(sql3);
            updateTypePstmt.setString(1, typeId); // 新的类型ID
            updateTypePstmt.setString(2, activityId); // 活动ID（用于子查询）
            int rowsAffected3 = updateTypePstmt.executeUpdate();

        String sql2 = "UPDATE activity_assigns_worker " +
                "SET Activity_ID ='" + newid +
                "'WHERE Activity_ID = '" + activityId + "'";
        stmt.executeUpdate(sql2);

        String sql4 = "UPDATE activity_uses_chemical " +
                "SET Activity_ID ='" + newid +
                "'WHERE Activity_ID = '" + activityId + "'";
        stmt.executeUpdate(sql4);

        String sql5 = "UPDATE activity_uses_facility " +
                "SET Activity_ID ='" + newid +
                "'WHERE Activity_ID = '" + activityId + "'";
        stmt.executeUpdate(sql5);

        String sql6 = "UPDATE activity_outsources_to " +
                "SET Activity_ID ='" + newid +
                "'WHERE Activity_ID = '" + activityId + "'";
        stmt.executeUpdate(sql6);

        String sql1 = "UPDATE cmms_activity " +
                "SET Activity_ID ='" + newid + "'," +
                "type ='" + type + "'," +
                "type_ID ='" + typeId + "'," +
                "Building_ID ='" + buildingid + "'," +
                "LevelNumber ='" + levelnumber + "'," +
                "RoomNumber ='" + roomnumber + "'," +
                "Start_Date ='" + startTime + "'," +
                "End_Date ='" + endTime +
                "'WHERE Activity_ID = '" + activityId + "'";
        int rowsAffected1 = stmt.executeUpdate(sql1);
        if (rowsAffected1 > 0) {
            System.out.println("update成功");
        } else {
            System.out.println("update nothing");
        }
        } catch (SQLException e) {
            System.err.println("删除员工时出错：" + e.getMessage());
            e.printStackTrace();
        }
    }



    // Helper methods
    private static CMMSActivity findActivityById(String id) {
        for (CMMSActivity act : activities) {
            if (act.getActivityId().equalsIgnoreCase(id)) {
                return act;
            }
        }
        return null;
    }

    private static ActivityType findActivityTypeById(String typeId) {
        for (ActivityType at : activityTypes) {
            if (at.getTypeId().equalsIgnoreCase(typeId)) {
                return at;
            }
        }
        return null;
    }

    private static String getInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

}
