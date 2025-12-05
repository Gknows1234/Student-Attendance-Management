package com.attendance.exception;

/**
 * Thrown when a student is not found in the system.
 * Demonstrates: Custom exception extending a base exception.
 */
public class StudentNotFoundException extends AttendanceException {
    public StudentNotFoundException(String studentId) {
        super("Student not found with ID: " + studentId);
    }
}
