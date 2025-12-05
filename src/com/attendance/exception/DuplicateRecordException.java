package com.attendance.exception;

/**
 * Thrown when attempting to add a duplicate record.
 */
public class DuplicateRecordException extends AttendanceException {
    public DuplicateRecordException(String message) {
        super(message);
    }
}
