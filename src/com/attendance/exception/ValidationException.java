package com.attendance.exception;

/**
 * Thrown when input validation fails.
 */
public class ValidationException extends AttendanceException {
    public ValidationException(String message) {
        super(message);
    }
}
