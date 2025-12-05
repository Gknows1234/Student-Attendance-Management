package com.attendance;

import com.attendance.model.*;
import com.attendance.service.*;
import com.attendance.exception.*;

public class TestDriver {
    public static void main(String[] args) {
        System.out.println("Running Validation Tests...");
        try {
            DataManager dm = new SerializedDataManager();
            AttendanceService service = new AttendanceService(dm);

            // Test 1: Invalid Student (Empty ID)
            try {
                service.addStudent(new Student("", "Test", "123"));
                System.out.println("FAIL: Expected ValidationException for empty ID");
            } catch (ValidationException e) {
                System.out.println("PASS: Caught expected error: " + e.getMessage());
            }

            // Test 2: Invalid Subject (Null Name)
            try {
                service.addSubject(new Subject("TEST01", ""));
                System.out.println("FAIL: Expected ValidationException for empty Name");
            } catch (ValidationException e) {
                System.out.println("PASS: Caught expected error: " + e.getMessage());
            }

            System.out.println("Validation Tests Complete.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
