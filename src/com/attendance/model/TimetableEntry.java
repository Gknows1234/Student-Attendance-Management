package com.attendance.model;

import java.io.Serializable;
import java.time.DayOfWeek;

/**
 * Record representing a timetable entry.
 * Demonstrates: Records, Enums (DayOfWeek from java.time).
 */
public record TimetableEntry(DayOfWeek day, int slotNumber, String subjectCode) implements Serializable {
    private static final long serialVersionUID = 1L;
}
