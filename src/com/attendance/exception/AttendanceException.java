package com.attendance.exception;

/**
 * Base checked exception for attendance system errors.
 * Demonstrates: Custom exceptions, exception hierarchy.
 */
public class AttendanceException extends Exception {
    public AttendanceException(String message) {
        super(message);
    }

    public AttendanceException(String message, Throwable cause) {
        super(message, cause);
    }
}
