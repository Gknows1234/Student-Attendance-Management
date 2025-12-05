package com.attendance.service;

import com.attendance.exception.AttendanceException;
import com.attendance.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements DataManager using Java Object Serialization.
 * Demonstrates: ObjectInputStream, ObjectOutputStream, try-with-resources,
 * checked exceptions, synchronized methods (concurrency).
 */
public class SerializedDataManager implements DataManager {
    private static final String STUDENTS_FILE = "students.dat";
    private static final String ATTENDANCE_FILE = "attendance.dat";
    private static final String SUBJECTS_FILE = "subjects.dat";
    private static final String TIMESLOTS_FILE = "timeslots.dat";
    private static final String TIMETABLE_FILE = "timetable.dat";

    // --- Students ---
    @Override
    public synchronized void saveStudents(List<Student> students) throws AttendanceException {
        saveList(STUDENTS_FILE, students);
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized List<Student> loadStudents() throws AttendanceException {
        return loadList(STUDENTS_FILE);
    }

    // --- Attendance ---
    @Override
    public synchronized void saveAttendance(List<AttendanceRecord> records) throws AttendanceException {
        saveList(ATTENDANCE_FILE, records);
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized List<AttendanceRecord> loadAttendance() throws AttendanceException {
        return loadList(ATTENDANCE_FILE);
    }

    // --- Subjects ---
    @Override
    public synchronized void saveSubjects(List<Subject> subjects) throws AttendanceException {
        saveList(SUBJECTS_FILE, subjects);
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized List<Subject> loadSubjects() throws AttendanceException {
        return loadList(SUBJECTS_FILE);
    }

    // --- Time Slots ---
    @Override
    public synchronized void saveTimeSlots(List<TimeSlot> slots) throws AttendanceException {
        saveList(TIMESLOTS_FILE, slots);
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized List<TimeSlot> loadTimeSlots() throws AttendanceException {
        return loadList(TIMESLOTS_FILE);
    }

    // --- Timetable ---
    @Override
    public synchronized void saveTimetable(List<TimetableEntry> entries) throws AttendanceException {
        saveList(TIMETABLE_FILE, entries);
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized List<TimetableEntry> loadTimetable() throws AttendanceException {
        return loadList(TIMETABLE_FILE);
    }

    // --- Generic helpers ---
    private <T> void saveList(String filename, List<T> list) throws AttendanceException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(new ArrayList<>(list));
        } catch (IOException e) {
            throw new AttendanceException("Failed to save to " + filename, e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> loadList(String filename) throws AttendanceException {
        File file = new File(filename);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<T>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new AttendanceException("Failed to load from " + filename, e);
        }
    }
}
