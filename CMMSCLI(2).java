package main.java;

import main.java.dao.EmployeeDAO;
import main.java.service.EmployeeService;
import main.java.service.FacilityService;
import main.java.entities.ExecutiveOfficer;
import main.java.entities.MidLevelManager;
import main.java.config.DatabaseConfig;
import main.java.entities.BaseLevelWorker;
import main.java.entities.Building;

import java.util.List;
import java.util.Scanner;

import java.sql.*;

public class CMMSCLI {
    private static Scanner scanner = new Scanner(System.in);
    private static EmployeeDAO employeeDAO = new EmployeeDAO();
    private static FacilityService facilityService = new FacilityService();
    private static Connection conn;
    private static Statement stmt;
    private static Statement checkStmt;

    public static void main(String[] args) throws SQLException {
        stmt = conn.createStatement();
        checkStmt = conn.createStatement();
        conn = DatabaseConfig.getInstance().getConnection();
        System.out.println("=== CMMS系统 ===");
        System.out.println("1. 员工管理");
        System.out.println("2. 设施管理");
        System.out.println("3. 活动管理"); // 新增活动管理选项
        System.out.println("4. 退出");
//        Scanner scanner; // 🔧 修复：声明在try外面，让finally能访问

        while (true) {
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
    }

    private static void handleEmployeeManagement() {
        while (true) {
            System.out.println("1. 添加员工");      //在这里区分高管还是其他
            System.out.println("2. 查询员工");
            System.out.println("3. 更新员工信息");
            System.out.println("4. 删除员工");
            System.out.println("5. 返回上一级");

            System.out.print("请选择: ");
            int choice1 = Integer.parseInt(scanner.nextLine());

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
        }
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
        System.out.println("1. 查看所有高管");
        System.out.println("2. 查看所有经理");
        System.out.println("3. 查看所有员工");
        System.out.print("请选择: ");
        int choice = Integer.parseInt(scanner.nextLine());
        
        switch (choice) {
            case 1:
                System.out.print("输入高管ID: ");
                String eoId = scanner.nextLine();
                employeeDAO.getManagersByExecutive(eoId).forEach(System.out::println);
                break;
            case 2:
                System.out.print("输入经理ID: ");
                String midId = scanner.nextLine();
                employeeDAO.getManagersByExecutive(midId).forEach(System.out::println);
                break;
            case 3:
                System.out.print("输入员工ID: ");
                String bidid = scanner.nextLine();
                employeeDAO.getWorkersByManager(bidid).forEach(System.out::println);
                break;
            default:
                System.out.println("无效选择");
        }
    }

    private static void deleteEmployee() {
        //Connection conn = null;
        try{
            //conn = DatabaseConfig.getInstance().getConnection();
            scanner = new Scanner(System.in);
            System.out.println("\n=== 员工级别 ===");
            System.out.println("1. ExecutiveOfficers");
            System.out.println("2. MidLevelManager");
            System.out.println("3. BaseLevelWorker");
            System.out.print("请选择: ");
            int choice = Integer.parseInt(scanner.nextLine());

            System.out.print("请输入要删除的员工ID: ");
            String employeeId = scanner.nextLine().trim();
            if (employeeId.isEmpty()) {
            System.out.println("员工ID不能为空！");
            return;
        }
            try {
                String checkSql = "";
                String tableName = "";
                String idColumn = "";

                // 根据选择拼接查询SQL
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
                        return; // 终止操作
                }

                // 执行查询并判断结果
                try (ResultSet rs = checkStmt.executeQuery(checkSql);
    ) {
                    if (!rs.next()) {
                        // ID不存在时抛出明确错误
                        System.err.println("错误：" + tableName + " 表中不存在ID为 [" + employeeId + "] 的员工！");
                        return; // 终止后续删除操作
                    }
                }
            } catch (SQLException e) {
                System.err.println("检查员工ID存在性时出错：" + e.getMessage());
                e.printStackTrace();
                return; // 终止操作
            }
            try {
            switch (choice) {
                case 1:
                    String sql = "UPDATE mid_level_manager " +
                        "SET EO_ID = NULL " + "WHERE EO_ID = '" + employeeId + "'";
                    stmt.executeUpdate(sql);
                    String sql1 = "DELETE FROM executive_officers WHERE EO_ID = '" + employeeId + "'";
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
                        break;
                default:
                    System.out.println("无效选择");
            }
            } catch (SQLException e) {
                System.err.println("删除员工时出错：" + e.getMessage());
                e.printStackTrace();
            }

    } catch (NumberFormatException e) {
        System.out.println("输入错误，请输入数字");
    } catch (SQLException e) { // 🔧 修复：捕获所有SQL异常（连接+执行）
        System.err.println("删除失败：" + e.getMessage());
        e.printStackTrace();
    }
    finally {
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
private static void updateEmployee() {
    // 更新员工实现
    System.out.println("=== 员工更新功能 ===");
    try {
        Connection conn = DatabaseConfig.getInstance().getConnection();
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n=== 员工级别 ===");
        System.out.println("1. ExecutiveOfficers");
        System.out.println("2. MidLevelManager");
        System.out.println("3. BaseLevelWorker");
        System.out.print("请选择要更新的员工级别: ");

        String choiceStr = scanner.nextLine().trim();
        if (choiceStr.isEmpty() || !choiceStr.matches("\\d+")) {
            System.out.println("输入错误，请输入数字1-3");
            scanner.close();
            return;
        }
        int choice = Integer.parseInt(choiceStr);
        
        System.out.print("请输入要更新的员工ID: ");
        String oldId = scanner.nextLine().trim();
        if (oldId.isEmpty()) {
            System.out.println("员工ID不能为空");
            scanner.close();
            return;
        }

        try (Statement stmt = conn.createStatement()) {
            switch (choice) {           
                case 1:
                    System.out.print("请输入新的高管姓名（不修改请直接回车）: ");
                    String newEoName = scanner.nextLine().trim();
                    System.out.print("请输入新的高管邮箱（不修改请直接回车）: ");
                    String newEoEmail = scanner.nextLine().trim();
                    
                    StringBuilder eoSql = new StringBuilder("UPDATE executive_officers SET ");
                    boolean hasEoField = false;
                    if (!newEoName.isEmpty()) {
                        eoSql.append("name = '").append(newEoName).append("', ");
                        hasEoField = true;
                    }
                    if (!newEoEmail.isEmpty()) {
                        eoSql.append("email = '").append(newEoEmail).append("', ");
                        hasEoField = true;
                    }
                    
                    if (!hasEoField) {
                        System.out.println("未输入任何更新内容");
                        break;
                    }
                    eoSql.setLength(eoSql.length() - 2);
                    eoSql.append(" WHERE EO_ID = '").append(oldId).append("'");
                    
                    int rowsEo = stmt.executeUpdate(eoSql.toString());
                    System.out.println(rowsEo > 0 ? "高管信息更新成功" : "未找到该高管，更新失败");
                    break;

                case 2:
                    System.out.print("请输入新的中层姓名（不修改请直接回车）: ");
                    String newMlmName = scanner.nextLine().trim();
                    System.out.print("请输入新的部门（不修改请直接回车）: ");
                    String newMlmDept = scanner.nextLine().trim();
                    System.out.print("请输入新的关联高管ID（EO_ID，不修改请直接回车）: ");
                    String newEoId = scanner.nextLine().trim();
                    
                    StringBuilder mlmSql = new StringBuilder("UPDATE mid_level_manager SET ");
                    boolean hasMlmField = false;
                    if (!newMlmName.isEmpty()) {
                        mlmSql.append("name = '").append(newMlmName).append("', ");
                        hasMlmField = true;
                    }
                    if (!newMlmDept.isEmpty()) {
                        mlmSql.append("department = '").append(newMlmDept).append("', ");
                        hasMlmField = true;
                    }
                    if (!newEoId.isEmpty()) {
                        if (newEoId.equalsIgnoreCase("null")) {
                            mlmSql.append("EO_ID = NULL, ");
                        } else {
                            mlmSql.append("EO_ID = '").append(newEoId).append("', ");
                        }
                        hasMlmField = true;
                    }
                    
                    if (!hasMlmField) {
                        System.out.println("未输入任何更新内容");
                        break;
                    }
                    mlmSql.setLength(mlmSql.length() - 2);
                    mlmSql.append(" WHERE MLM_ID = '").append(oldId).append("'");
                    
                    int rowsMlm = stmt.executeUpdate(mlmSql.toString());
                    System.out.println(rowsMlm > 0 ? "中层管理者信息更新成功" : "未找到该中层，更新失败");
                    break;

                case 3:
                    System.out.print("请输入新的基层员工姓名（不修改请直接回车）: ");
                    String newBlwName = scanner.nextLine().trim();
                    System.out.print("请输入新的薪资（不修改请直接回车）: ");
                    String newBlwSalary = scanner.nextLine().trim();
                    System.out.print("请输入新的关联中层ID（MLM_ID，不修改请直接回车，设为NULL请输入null）: ");
                    String newMlmId = scanner.nextLine().trim();
                    
                    StringBuilder blwSql = new StringBuilder("UPDATE base_level_worker SET ");
                    boolean hasBlwField = false;
                    if (!newBlwName.isEmpty()) {
                        blwSql.append("name = '").append(newBlwName).append("', ");
                        hasBlwField = true;
                    }
                    if (!newBlwSalary.isEmpty() && newBlwSalary.matches("\\d+(\\.\\d+)?")) {
                        blwSql.append("salary = ").append(newBlwSalary).append(", ");
                        hasBlwField = true;
                    } else if (!newBlwSalary.isEmpty()) {
                        System.out.println("薪资输入格式错误，跳过薪资更新");
                    }
                    if (!newMlmId.isEmpty()) {
                        if (newMlmId.equalsIgnoreCase("null")) {
                            blwSql.append("MLM_ID = NULL, ");
                        } else {
                            blwSql.append("MLM_ID = '").append(newMlmId).append("', ");
                        }
                        hasBlwField = true;
                    }
                    
                    if (!hasBlwField) {
                        System.out.println("未输入任何有效更新内容");
                        break;
                    }
                    blwSql.setLength(blwSql.length() - 2);
                    blwSql.append(" WHERE BLW_ID = '").append(oldId).append("'");
                    
                    int rowsBlw = stmt.executeUpdate(blwSql.toString());
                    System.out.println(rowsBlw > 0 ? "基层员工信息更新成功" : "未找到该基层员工，更新失败");
                    break;

                default:
                    System.out.println("无效选择");
            }
        } catch (SQLException e) {
            System.err.println("更新失败：" + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
            if (conn != null && !conn.isClosed()) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    } catch (NumberFormatException e) {
        System.out.println("输入错误，请输入数字");
    } catch (SQLException e) {
        System.err.println("数据库连接失败：" + e.getMessage());
        e.printStackTrace();
    } catch (Exception e) {
        System.err.println("系统异常：" + e.getMessage());
        e.printStackTrace();
    }
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
    }

}
