package com.attendance.model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Record representing an attendance entry with subject.
 * Demonstrates: Records, LocalDate (Date/Time API), Serializable.
 */
public record AttendanceRecord(String studentId, String subjectCode, LocalDate date, int slotNumber, boolean present)
        implements Serializable {
    private static final long serialVersionUID = 2L;
}
