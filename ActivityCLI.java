// Main.java;

package main.java.utils;
import main.java.entities.ActivityType;
import main.java.entities.CMMSActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 */
public class ActivityCLI {
    private static List<CMMSActivity> activities = new ArrayList<>();
    private static List<ActivityType> activityTypes = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Seed some sample ActivityType

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
                    System.out.println("Goodbye! Exiting program...");
                    break;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        } while (choice != 3);

        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n=== CMMS Activity Management System ===");
        System.out.println("1. Add Activity");
        System.out.println("2. Delete Activity");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");
    }

    private static void addActivity() {
        System.out.println("\n--- Add New Activity ---");

        String activityId = getInput("Enter Activity ID (e.g., A001): ");
        if (findActivityById(activityId) != null) {
            System.out.println("Error: Activity with ID " + activityId + " already exists!");
            return;
        }

        System.out.println("Available Activity Types:");
        for (ActivityType at : activityTypes) {
            System.out.println("  " + at.getTypeId() + " - " + at.getTypeName());
        }

        String typeId = getInput("Enter Type ID (from list above): ");
        ActivityType selectedType = findActivityTypeById(typeId);
        if (selectedType == null) {
            System.out.println("Invalid Type ID!");
            return;
        }

        String type = getInput("Enter Type (from list above): ");
        ActivityType selectedType = findActivityTypeById(type);
        if (selectedType == null) {
            System.out.println("Invalid Type!");
            return;
        }

        String startTime = getInput("Enter startTime:");
        String endTime = getInput("Enter endTime:");
        String status = getInput("Enter status: ");


        
        CMMSActivity activity;
        activity = new CMMSActivity(
              activityId, type, typeId, startTime, endTime, status);


        activities.add(activity);
        System.out.println("Activity added successfully!");
        System.out.println(activity);
    }

    private static void deleteActivity() {
        System.out.println("\n--- Delete Activity ---");
        if (activities.isEmpty()) {
            System.out.println("No activities to delete.");
            return;
        }

        System.out.println("Current Activities:");
        for (CMMSActivity act : activities) {
            System.out.println("  " + act.getActivityId() + " | " + act.getType() + " | " + act.getBuildingId() +
                    " | " + act.getRoomNumber() + " | " + act.getStartDate() + " to " + act.getEndDate());
        }

        String activityId = getInput("Enter Activity ID to delete: ");
        CMMSActivity activity = findActivityById(activityId);

        if (activity != null) {
            activities.remove(activity);
            System.out.println("Activity " + activityId + " deleted successfully.");
        } else {
            System.out.println("Activity with ID " + activityId + " not found.");
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