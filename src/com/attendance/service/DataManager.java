package com.attendance.service;

import com.attendance.exception.AttendanceException;
import com.attendance.model.*;

import java.util.List;

/**
 * Interface for data persistence operations.
 * Demonstrates: Interfaces, abstraction.
 */
public interface DataManager {
    void saveStudents(List<Student> students) throws AttendanceException;

    List<Student> loadStudents() throws AttendanceException;

    void saveAttendance(List<AttendanceRecord> records) throws AttendanceException;

    List<AttendanceRecord> loadAttendance() throws AttendanceException;

    void saveSubjects(List<Subject> subjects) throws AttendanceException;

    List<Subject> loadSubjects() throws AttendanceException;

    void saveTimeSlots(List<TimeSlot> slots) throws AttendanceException;

    List<TimeSlot> loadTimeSlots() throws AttendanceException;

    void saveTimetable(List<TimetableEntry> entries) throws AttendanceException;

    List<TimetableEntry> loadTimetable() throws AttendanceException;
}
