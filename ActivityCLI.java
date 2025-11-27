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

    public static void main(String[] args) {
        // Seed some sample ActivityType
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

        scanner.close();
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
        try{

        System.out.println("\n--- Add New Activity ---");
        System.out.print("Enter Activity ID (e.g., A001): ");
        String activityId = scanner.nextLine();
        System.out.print("Available Activity Types: ");
        String type = scanner.nextLine();
        System.out.print("Enter Type ID (from list above): ");
        String typeId = scanner.nextLine();
        System.out.print("Enter typeId: ");
        String buildingid = scanner.nextLine();
        System.out.print("Enter buildingid: ");
        String levelnumber = scanner.nextLine();
        System.out.print("Enter roomnumber: ");
        String roomnumber = scanner.nextLine();
        System.out.print("Enter startTime: ");
        String startTime = scanner.nextLine();
        System.out.print("Enter endTime: ");
        String endTime = scanner.nextLine();
        cmmsActivityDAO.addCMMSActivity(new CMMSActivity(activityId, type, typeId, buildingid, levelnumber, roomnumber, startTime, endTime));
        System.out.println("add successful");
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

//        String activityId = getInput("Enter Activity ID (e.g., A001): ");
//        if (findActivityById(activityId) != null) {
//            System.out.println("Error: Activity with ID " + activityId + " already exists!");
//            return;
//        }
//
//        System.out.println("Available Activity Types:");
//        for (ActivityType at : activityTypes) {
//            System.out.println("  " + at.getTypeId() + " - " + at.getTypeName());
//        }
//
//        String typeId = getInput("Enter Type ID (from list above): ");
//        ActivityType selectedTypeID = findActivityTypeById(typeId);
//        if (selectedTypeID == null) {
//            System.out.println("Invalid Type ID!");
//            return;
//        }
//
//        String type = getInput("Enter Type (from list above): ");
//        ActivityType selectedType = findActivityTypeById(type);
//        if (selectedType == null) {
//            System.out.println("Invalid Type!");
//            return;
//        }
//
//        String startTime = getInput("Enter startTime:");
//        String endTime = getInput("Enter endTime:");
//
//        String buildingid = getInput("Enter Type (from list above): ");
//        String levelnumber = getInput("Enter Type (from list above): ");
//        String roomnumber = getInput("Enter Type (from list above): ");
//
//
//
//        activities.add(activity);
//        System.out.println("Activity added successfully!");
//        System.out.println(activity);


    private static void deleteActivity() {
        try{
        scanner = new Scanner(System.in);
        String activityId;

        while (true) {
            System.out.print("请输入要删除的activityId: ");
            activityId = scanner.nextLine().trim();
            if (activityId.isEmpty()) {
                System.out.println("员工ID不能为空！");
                return;
            }
            try {
                String checkSql = "";
                String tableName = "";
                String idColumn = "";

                tableName = "cmms_activity";
                idColumn = "activityId";
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
                "SET Type_ID = NULL " +
                "SET TypeName = NULL " +
                "WHERE Type_ID = (SELECT type_ID FROM cmms_activity WHERE Activity_ID) = '" + activityId + "'";
        stmt.executeUpdate(sql3);

        String sql2 = "UPDATE cmms_activity " +
                "SET Activity_ID = NULL " +
                "WHERE Activity_ID = '" + activityId + "'";
        stmt.executeUpdate(sql2);

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

private static void updateActivity() {
        try{
            System.out.println("\n--- Update New Activity ---");
            System.out.print("Enter Activity ID (e.g., A001): ");
            String activityId = scanner.nextLine();
            System.out.print("Available Activity Types: ");
            String type = scanner.nextLine();
            System.out.print("Enter Type ID (from list above): ");
            String typeId = scanner.nextLine();
            System.out.print("Enter typeId: ");
            String buildingid = scanner.nextLine();
            System.out.print("Enter buildingid: ");
            String levelnumber = scanner.nextLine();
            System.out.print("Enter roomnumber: ");
            String roomnumber = scanner.nextLine();
            System.out.print("Enter startTime: ");
            String startTime = scanner.nextLine();
            System.out.print("Enter endTime: ");
            String endTime = scanner.nextLine();
            scanner = new Scanner(System.in);
            String activityId;

        while (true) {
            if (activityId.isEmpty()) {
                System.out.println("activityId不能为空！");
                return;
            }
            try {
                String checkSql = "";
                String tableName = "";
                String idColumn = "";

                tableName = "cmms_activity";
                idColumn = "activityId";
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
                "SET type_ID = NULL " +
                "SET typeName = NULL " +
                "WHERE type_ID = (SELECT type_ID FROM cmms_activity WHERE Activity_ID) = '" + activityId + "'";
        stmt.executeUpdate(sql3);

        String sql2 = "UPDATE cmms_activity " +
                "SET Activity_ID '" + activityId + "'," +
                "WHERE Activity_ID = '" + activityId + "'";
        stmt.executeUpdate(sql2);

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

        String sql1 = "UPDATE cmms_activity " +
                "SET Activity_ID ='" + activityId + "'," +
                "type ='" + type + "'," +
                "type_ID ='" + type_ID + "'," +
                "Building_ID ='" + buildingid + "'," +
                "LevelNumber ='" + levelnumber + "'," +
                "RoomNumber ='" + roomnumber + "'," +
                "Start_Date ='" + startTimes + "'," +
                "End_Date ='" + endTime + "'," +
                "WHERE Activity_ID = '" + activityId + "'";
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
//    private static String getInput(String prompt) {
//        System.out.print(prompt);
//        return scanner.nextLine().trim();
//    }
//
//    private static int getIntInput(String prompt) {
//        while (true) {
//            System.out.print(prompt);
//            String input = scanner.nextLine().trim();
//            try {
//                return Integer.parseInt(input);
//            } catch (NumberFormatException e) {
//                System.out.println("Please enter a valid number.");
//            }
//        }
//    }
}